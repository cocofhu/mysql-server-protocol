package com.cocofhu.server.mysql;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import static com.cocofhu.server.mysql.IntegerDataType.*;
import static com.cocofhu.server.mysql.StringLengthDataType.STRING_FIXED;

public class Payload {

    // 如果可变长度整数的第一个字节是251(0xfb),这将是一个空的ProtocolText::ResultsetRow.
    public static final long NULL_LENGTH = -1;
    /* 通过数据的第一个字节可以判断数据包的基本类型. */
    public static final short TYPE_COM_QUERY = 0x03;
    /**
     * 当内部byteBuffer不够时的扩容因子
     */
    public static final float DEFAULT_RESIZE_FACTOR = 1.25f;
    /**
     * 数据实际大小
     */
    private int payloadLength;
    private byte[] byteBuffer;
    private int position = 0;

    @Getter
    private int sequenceId = 0;

    public Payload(byte[] buf, int sequenceId) {
        this.byteBuffer = buf;
        this.payloadLength = buf.length;
        this.sequenceId = sequenceId;
    }

    public Payload(int size, int sequenceId) {
        this(new byte[size], sequenceId);
    }

    // Packet-detect methods
    public boolean isQueryPacket(){
        return byteBuffer != null && byteBuffer.length > 0 &&  byteBuffer[0] == TYPE_COM_QUERY;
    }


    // Read-Write methods
    public void writeInteger(IntegerDataType type, long l) {
        byte[] b;
        int size = getSize(type);
        if (size != -1) {
            ensureCapacity(size);
            b = this.byteBuffer;
            for (int i = 0; i < size; ++i) {
                b[this.position + i] = (byte) ((l >>> (i * 8)) & 0xff);
            }
            this.position += size;
        } else {
            if (l < 251) {
                ensureCapacity(1);
                writeInteger(INT1, l);
            } else if (l < 65536L) {
                ensureCapacity(3);
                writeInteger(INT1, 252);
                writeInteger(INT2, l);
            } else if (l < 16777216L) {
                ensureCapacity(4);
                writeInteger(INT1, 253);
                writeInteger(INT3, l);
            } else {
                ensureCapacity(9);
                writeInteger(INT1, 254);
                writeInteger(INT8, l);
            }
        }
        adjustPayloadLength();
    }

    public final long readInteger(IntegerDataType type) {
        byte[] b = this.byteBuffer;
        int size = getSize(type);
        if (size != -1) {
            long num = 0;
            for (int i = 0; i < size; ++i) {
                num += (((long) b[i + this.position]) & 255) << (i << 3);
            }
            this.position += size;
            return num;
        }
        int sw = b[this.position++] & 0xff;
        switch (sw) {
            case 251:
                return NULL_LENGTH; // represents a NULL in a ProtocolText::ResultsetRow
            case 252:
                return readInteger(INT2);
            case 253:
                return readInteger(INT3);
            case 254:
                return readInteger(INT8);
            default:
                return sw;
        }
    }

    public final void writeBytes(StringSelfDataType type, byte[] b) {
        writeBytes(type, b, 0, b.length);
    }

    public final void writeBytes(StringLengthDataType type, byte[] b) {
        writeBytes(type, b, 0, b.length);
    }

    public void writeBytes(StringSelfDataType type, byte[] b, int offset, int len) {
        switch (type) {
            case STRING_EOF:
                writeBytes(STRING_FIXED, b, offset, len);
                break;
            case STRING_TERM:
                ensureCapacity(len + 1);
                writeBytes(STRING_FIXED, b, offset, len);
                this.byteBuffer[this.position++] = 0;
                break;
            case STRING_LENENC:
                ensureCapacity(len + 9);
                writeInteger(INT_LENENC, len);
                writeBytes(STRING_FIXED, b, offset, len);
                break;
        }
        adjustPayloadLength();
    }

    public void writeBytes(StringLengthDataType type, byte[] b, int offset, int len) {
        switch (type) {
            case STRING_FIXED:
            case STRING_VAR:
                ensureCapacity(len);
                System.arraycopy(b, offset, this.byteBuffer, this.position, len);
                this.position += len;
                break;
        }
        adjustPayloadLength();
    }

    public byte[] readBytes(StringSelfDataType type) {
        byte[] b;
        switch (type) {
            case STRING_TERM:
                int i = this.position;
                while ((i < this.payloadLength) && (this.byteBuffer[i] != 0)) ++i;
                b = readBytes(STRING_FIXED, i - this.position);
                this.position++; // skip terminating byte
                return b;
            case STRING_LENENC:
                long l = readInteger(INT_LENENC);
                return l == NULL_LENGTH ? null : (l == 0 ? new byte[0] : readBytes(STRING_FIXED, (int) l));
            case STRING_EOF:
                return readBytes(STRING_FIXED, this.payloadLength - this.position);
        }
        return null;
    }

    public byte[] readBytes(StringLengthDataType type, int len) {
        byte[] b;
        switch (type) {
            case STRING_FIXED:
            case STRING_VAR:
                b = new byte[len];
                System.arraycopy(this.byteBuffer, this.position, b, 0, len);
                this.position += len;
                return b;
        }
        return null;
    }

    public String readString(StringSelfDataType type, String encoding) {
        String res = null;
        switch (type) {
            case STRING_TERM:
                int i = this.position;
                while ((i < this.payloadLength) && (this.byteBuffer[i] != 0)) ++i;
                res = readString(STRING_FIXED, encoding, i - this.position);
                this.position++; // skip terminating byte
                break;
            case STRING_LENENC:
                long l = readInteger(INT_LENENC);
                return l == NULL_LENGTH ? null : (l == 0 ? "" : readString(STRING_FIXED, encoding, (int) l));
            case STRING_EOF:
                return readString(STRING_FIXED, encoding, this.payloadLength - this.position);
        }
        return res;
    }

    public String readString(StringLengthDataType type, String encoding, int len) {
        String res = null;
        switch (type) {
            case STRING_FIXED:
            case STRING_VAR:
                if ((this.position + len) > this.payloadLength) {
                    throw new IllegalArgumentException("not have enough size to read. ");
                }
                try {
                    res = new String(this.byteBuffer, this.position, len, encoding);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                this.position += len;
                break;
        }
        return res;
    }

    public void writePackage(OutputStream out) throws IOException {
        byte[] headerBytes = new byte[]{
                (byte) ( position & 255),
                (byte) ((position / 255) & 255),
                (byte) ((position / 255 / 255) & 255),
                (byte) sequenceId
        };
        out.write(headerBytes);
        for(int i = 0; i < position; ++i){
            out.write(byteBuffer[i]);
        }
    }

    //public void writeTableRow()

    /**
     * This method blocks until input data is available, end of file is detected, or an exception is thrown.
     **/
    public static Payload readPackage(InputStream in) throws IOException {
        byte[] header = new byte[4];
        if (in.read(header) != 4) {
            throw new IOException("read packet header failed, not has enough bytes to read.");
        }
        int payloadSize = (header[0] & 255) + ((header[1] & 255) << 8) + ((header[2] & 255) << 16);
        byte[] payload = new byte[payloadSize];
        if (in.read(payload) != payloadSize) {
            throw new IOException("read packet payload failed, not has enough bytes to read.");
        }
        // reset position
        return new Payload(payload, header[3] & 255);
    }

    /**
     * low-performance,debug only
     */
    public String dumpAsHex() {
        byte[] bytes = toBytes();
        int length = bytes.length;
        StringBuilder fullOutBuilder = new StringBuilder(length * 4);
        StringBuilder asciiOutBuilder = new StringBuilder(16);
        for (int p = 0, l = 0; p < length; l = 0) {
            for (; l < 8 && p < length; p++, l++) {
                int asInt = bytes[p] & 0xff;
                if (asInt < 0x10) {
                    fullOutBuilder.append("0");
                }
                fullOutBuilder.append(Integer.toHexString(asInt)).append(" ");
                asciiOutBuilder.append(" ").append(asInt >= 0x20 && asInt < 0x7f ? (char) asInt : ".");
            }
            for (; l < 8; l++) { // if needed, fill remaining of last line with spaces
                fullOutBuilder.append("   ");
            }
            fullOutBuilder.append("   ").append(asciiOutBuilder).append(System.lineSeparator());
            asciiOutBuilder.setLength(0);
        }
        return fullOutBuilder.toString();
    }


    // Private Method
    private static int getSize(IntegerDataType type) {
        int size = -1;
        switch (type) {
            case INT1: size = 1; break;
            case INT2: size = 2; break;
            case INT3: size = 3; break;
            case INT4: size = 4; break;
            case INT6: size = 6; break;
            case INT8: size = 8; break;
        }
        return size;
    }

    /**
     * 确保内部字节数组中存在指定的存储空间，如果不够则扩容
     * @param additionalData 指定存储空间大小
     */
    private void ensureCapacity(int additionalData) {
        if ((this.position + additionalData) > this.byteBuffer.length) {
            int newLength = (int) (this.byteBuffer.length * DEFAULT_RESIZE_FACTOR);
            if (newLength < (this.byteBuffer.length + additionalData)) {
                // 扩容一次大小还不够则安装additionalData扩容
                newLength = this.byteBuffer.length + (int) (additionalData * DEFAULT_RESIZE_FACTOR);
            }
            if (newLength < this.byteBuffer.length) {
                newLength = this.byteBuffer.length + additionalData;
            }
            byte[] newBytes = new byte[newLength];
            System.arraycopy(this.byteBuffer, 0, newBytes, 0, this.byteBuffer.length);
            this.byteBuffer = newBytes;
        }
    }
    /**
     * 当写入底层数组时，需要重新调整length
     */
    private void adjustPayloadLength() {
        if (this.position > this.payloadLength) {
            this.payloadLength = this.position;
        }
    }

    /** Pack this packet as bytes array. it will return the copy of underlying bytes. */
    private byte[] toBytes(){
        byte[] headerBytes = new byte[]{
                (byte) ( payloadLength & 255),
                (byte) ((payloadLength / 255) & 255),
                (byte) ((payloadLength / 255 / 255) & 255),
                (byte) sequenceId
        };
        byte[] messageBytes = byteBuffer;
        byte[] newBytes = new byte[headerBytes.length + payloadLength];
        System.arraycopy(headerBytes, 0, newBytes, 0, headerBytes.length);
        System.arraycopy(messageBytes, 0, newBytes, headerBytes.length, payloadLength);
        return newBytes;
    }
}
