package com.cocofhu.server.mysql;

//

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import static com.cocofhu.server.mysql.CapabilityFlags.*;


/**
 * <a href="https://dev.mysql.com/doc/dev/mysql-server/latest/PAGE_PROTOCOL.html">MySQL native protocol</a>
 * C/S 阻塞IO
 */
public class NativeServer implements Runnable{

    private static final byte[] SERVER_VERSION = "0.0.1(beta)".getBytes();
    private static final byte[] DEFAULT_AUTH_PLUGIN_NAME = "mysql_native_password".getBytes();
    public static final int DEFAULT_CAPABILITY_FLAGS = CLIENT_LONG_PASSWORD | CLIENT_FOUND_ROWS | CLIENT_LONG_FLAG | CLIENT_NO_SCHEMA | CLIENT_IGNORE_SPACE
            | CLIENT_PROTOCOL_41 | CLIENT_INTERACTIVE | CLIENT_PLUGIN_AUTH | CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA | CLIENT_SECURE_CONNECTION;
            // ignoring column eof
//            | CLIENT_DEPRECATE_EOF ;
    /**
     * mysql_native_password 鉴权使用的最小客户端兼容
     */
    private static final int MIN_CLIENT_FLAGS = CLIENT_LONG_PASSWORD | CLIENT_LONG_FLAG | CLIENT_PROTOCOL_41  | CLIENT_SECURE_CONNECTION |
            CLIENT_PLUGIN_AUTH | CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA  ;

    // 字符集和整理集都可以用255
    public static final byte DEFAULT_CHARSET = (byte) Charset.UTF_8;
    public static final byte DEFAULT_CHARSET_COLLATION = 33;
    private static final Random RANDOM_GENERATOR = new Random();
    /**
     * 随机密码长度，注意：最小值为20，建议使用20
     * 注意：这里没有算'\0', 在传输时，这里会+1
     **/
    public static final int DEFAULT_SCRAMBLE_LENGTH = 20;

    private volatile SessionPhase phase;
    private volatile boolean isRunning;
    private final Socket socket;
    // represent input of client
    private final InputStream in;
    private final OutputStream out;
    private final int connectionId;


    public NativeServer(Socket client, int connectionId) throws IOException {
        this.phase = SessionPhase.CONNECTION;
        this.socket = client;
        this.in = client.getInputStream();
        this.out = client.getOutputStream();
        this.connectionId = connectionId;
        this.isRunning = true;
        try {
            handshake();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //writeOKPacket(1);
        run();

    }

    @Override
    public void run() {
        try {
            while (isRunning){
                Payload payload = Payload.readPackage(in);
                if(payload.isQueryPacket()){
                    //System.out.println(payload.dumpAsHex());
                    payload.readBytes(StringLengthDataType.STRING_FIXED, 1);
                    System.out.println(payload.readString(StringSelfDataType.STRING_TERM, "UTF-8"));
                    System.out.println("query package recv");
                    writeDummyTable(payload.getSequenceId(), RANDOM_GENERATOR.nextInt(20) + 1);
                }else{
                    System.out.println(payload.dumpAsHex());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void writeDummyTable(int sequenceId, int n) throws IOException, InterruptedException {
        Payload columnCountPacket = new Payload(16, ++sequenceId);
        columnCountPacket.writeInteger(IntegerDataType.INT_LENENC, n);
        columnCountPacket.writePackage(out);
        System.out.println("SEND 1");
        for(int i = 0; i < n; ++i){
            Payload payload = new Payload(128, ++sequenceId);
            String name = "dummy" + i;
            // The catalog used. currently always "def"
            payload.writeBytes(StringSelfDataType.STRING_LENENC, "def".getBytes());
            // schema name
            payload.writeBytes(StringSelfDataType.STRING_LENENC, name.getBytes());
            // virtual table name
            payload.writeBytes(StringSelfDataType.STRING_LENENC, name.getBytes());
            // physical table name
            payload.writeBytes(StringSelfDataType.STRING_LENENC, name.getBytes());
            // virtual column name
            payload.writeBytes(StringSelfDataType.STRING_LENENC, name.getBytes());
            // physical column name
            payload.writeBytes(StringSelfDataType.STRING_LENENC, name.getBytes());
            // 	[0x0c]
            payload.writeInteger(IntegerDataType.INT_LENENC, 0x0c);
            // the column character set
            // note: 0x3f for binary
            // https://dev.mysql.com/doc/dev/mysql-server/latest/page_protocol_basic_character_set.html
            payload.writeInteger(IntegerDataType.INT2, DEFAULT_CHARSET);
            // 	maximum length of the field
            payload.writeInteger(IntegerDataType.INT4, 10);
            // type of the column as defined in enum_field_types
            payload.writeInteger(IntegerDataType.INT1, FieldType.FIELD_TYPE_STRING);
            // Flags as defined in Column Definition Flags
            payload.writeInteger(IntegerDataType.INT2, 0);
            // max shown decimal digits:
            // 0x00 for integers and static strings
            // 0x1f for dynamic strings, double, float
            // 0x00 to 0x51 for decimals
            payload.writeInteger(IntegerDataType.INT1, 0);
            /*
             * note currently there are still remain 2 bytes.
             * see mysql-server/sql/protocol_classic.cc:3337
             * pos[10] = 0;  // For the future
             * pos[11] = 0;  // For the future
             **/
            payload.writeInteger(IntegerDataType.INT1, 0);
            payload.writeInteger(IntegerDataType.INT1, 0);
            payload.writePackage(out);
        }
        writeEOFPacket(++sequenceId,0,0);
        for (int i = 0, len = RANDOM_GENERATOR.nextInt(10) - 1; i < len; ++i){
            Payload payload = new Payload(128, ++sequenceId);
            for(int j = 0; j < n; ++j){
                payload.writeBytes(StringSelfDataType.STRING_LENENC, ("dummy" + i + j).getBytes());
            }
            //Thread.sleep(1111);
            payload.writePackage(out);
        }
        //SERVER_STATUS_LAST_ROW_SENT
        writeEOFPacket(++sequenceId,0,0);
    }
    //
    private void handshake() throws IOException {
        assert phase == SessionPhase.CONNECTION;
        byte[] scramble = nextScramble();
        // Protocol::HandshakeV10
        // https://dev.mysql.com/doc/dev/mysql-server/latest/page_protocol_connection_phase_packets_protocol_handshake_v10.html
        Payload initPacket = new Payload(128, 0);
        // protocol version	always 10
        initPacket.writeInteger(IntegerDataType.INT1, 10);
        // human-readable status information
        initPacket.writeBytes(StringSelfDataType.STRING_TERM, SERVER_VERSION);
        // a.k.a. connection id
        initPacket.writeInteger(IntegerDataType.INT4, connectionId);
        // first 8 bytes of the plugin provided data (scramble)
        initPacket.writeBytes(StringLengthDataType.STRING_FIXED, scramble, 0, 8);
        // 0x00 byte, terminating the first part of a scramble
        initPacket.writeInteger(IntegerDataType.INT1,0);
        // The lower 2 bytes of the Capabilities Flags
        initPacket.writeInteger(IntegerDataType.INT2, DEFAULT_CAPABILITY_FLAGS);
        // default server a_protocol_character_set, only the lower 8-bits
        initPacket.writeInteger(IntegerDataType.INT1, DEFAULT_CHARSET);
        // server status_flags
        initPacket.writeInteger(IntegerDataType.INT2, 0);
        // The upper 2 bytes of the Capabilities Flags
        initPacket.writeInteger(IntegerDataType.INT2, DEFAULT_CAPABILITY_FLAGS>>>16);
        // length of the combined auth_plugin_data (scramble), if auth_plugin_data_len is > 0
        initPacket.writeInteger(IntegerDataType.INT1, scramble.length);
        // reserved. All 0s.
        initPacket.writeInteger(IntegerDataType.INT8,0);
        initPacket.writeInteger(IntegerDataType.INT2,0);
        // Rest of the plugin provided data (scramble), $len=MAX(13, length of auth-plugin-data - 8)
        // 注意： 这里我们包含了 '\0', 所以这里是对的！
        initPacket.writeBytes(StringLengthDataType.STRING_FIXED, scramble, 8, scramble.length - 8);
        // name of the auth_method that the auth_plugin_data belongs to
        initPacket.writeBytes(StringSelfDataType.STRING_TERM, DEFAULT_AUTH_PLUGIN_NAME);

        // Communication
        // step 1: 发送第一个数据包
        initPacket.writePackage(out);
        // step 2: 接受一个客户端的响应包
        Payload response = Payload.readPackage(in);
        int clientFlags = (int) response.readInteger(IntegerDataType.INT4);
        if ((MIN_CLIENT_FLAGS & clientFlags) != MIN_CLIENT_FLAGS) {
            throw new UnsupportedOperationException(String.format("minimum flag of capability are not satisfied, %d are required, but %d, missing %d.",
                    MIN_CLIENT_FLAGS, clientFlags, (MIN_CLIENT_FLAGS & clientFlags) ^ clientFlags));
        }
        // maximum packet size
        long maxPacketSize = response.readInteger(IntegerDataType.INT4);
        // client charset a_protocol_character_set, only the lower 8-bits
        int charsetCode = (int) response.readInteger(IntegerDataType.INT1);
        if (!Charset.UTF8_COLLATION_NAME.containsKey(charsetCode)) {
            throw new UnsupportedOperationException(String.format("only utf8 are supported, but %d.", charsetCode));
        }
        // filler to the size of the handhshake response packet. All 0s.
        response.readBytes(StringLengthDataType.STRING_FIXED, 23);
        // login username
        String username = response.readString(StringSelfDataType.STRING_TERM, "UTF-8");
        // opaque authentication response data generated by Authentication Method indicated by the plugin name field.
        byte[] authResponse = response.readBytes(StringSelfDataType.STRING_LENENC);
        String database = null;
        if ((clientFlags & CLIENT_CONNECT_WITH_DB) != 0) {
            // initial database for the connection. This string should be interpreted using the character set indicated by character set field.
            database = response.readString(StringSelfDataType.STRING_TERM, "UTF-8");
        }
        // the Authentication Method used by the client to generate auth-response value in this packet. This is a UTF-8 string.
        String pluginName = response.readString(StringSelfDataType.STRING_TERM, "UTF-8");
        // step 3: 鉴权
        byte[] authData = scramble411("password".getBytes(), scramble);
        // TODO check if authData==authResponse
        System.out.println(username);
        System.out.println(Arrays.toString(authData));
        System.out.println(Arrays.toString(authResponse));
        writeOKPacket(response.getSequenceId() + 1);
        System.out.println("=====");
        this.phase = SessionPhase.COMMAND;
    }

    private void writeEOFPacket(int sequenceId, int warnings, int flags) throws IOException{
        Payload packet = new Payload(32, sequenceId);
        packet.writeInteger(IntegerDataType.INT1, 0xfe);
        packet.writeInteger(IntegerDataType.INT2, warnings);
        packet.writeInteger(IntegerDataType.INT2, flags);
        packet.writePackage(out);
    }

    private void writeOKPacket(int sequenceId, int affectedRows, int lastInsertId, int status, int warnings) throws IOException {
        System.out.println("write ok " + sequenceId);
        Payload packet = new Payload(64, sequenceId);
        packet.writeInteger(IntegerDataType.INT1, 0);
        packet.writeInteger(IntegerDataType.INT_LENENC, affectedRows);
        packet.writeInteger(IntegerDataType.INT_LENENC, lastInsertId);
        packet.writeInteger(IntegerDataType.INT2, status);
        packet.writeInteger(IntegerDataType.INT2, warnings);
        packet.writePackage(out);
    }

    private void writeOKPacket(int sequenceId) throws IOException {
        writeOKPacket(sequenceId, 0, 0, 0, 0);
    }

    private byte[] nextScramble(){
        byte[] scrambleData = new byte[DEFAULT_SCRAMBLE_LENGTH + 1];
        for(int i = 0 ; i < DEFAULT_SCRAMBLE_LENGTH ; ++i){
            scrambleData[i] = (byte) RANDOM_GENERATOR.nextInt();
        }
        return scrambleData;
    }

    private static byte[] scramble411(byte[] password, byte[] seed) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException ex) {
            throw new UnsupportedOperationException("fatal error, unsupported digest algorithm sha-1.");
        }
        byte[] passwordHashStage1 = md.digest(password);
        md.reset();
        byte[] passwordHashStage2 = md.digest(passwordHashStage1);
        md.reset();
        // remove '\0'
        md.update(seed,0 , seed.length - 1);
        md.update(passwordHashStage2);
        byte[] toBeXor = md.digest();
        int numToXor = toBeXor.length;
        for (int i = 0; i < numToXor; i++) {
            toBeXor[i] = (byte) (toBeXor[i] ^ passwordHashStage1[i]);
        }
        return toBeXor;
    }
}
























