package com.cocofhu.server.mysql;

public interface CapabilityFlags {
    /** 使用增强版本的密码鉴权 4.11版本之后该标志位恒为1 */
    int CLIENT_LONG_PASSWORD = 0x00000001;
    /**  发送找到的行数而不是影响的行数，可以忽略 */
    int CLIENT_FOUND_ROWS = 0x00000002;
    /** 支持更长的flags(4bytes) */
    int CLIENT_LONG_FLAG = 0x00000004;
    /** 连接时可以指定DB名字 */
    int CLIENT_CONNECT_WITH_DB = 0x00000008;

    /** 不允许database.table.column，可以忽略 */
    int CLIENT_NO_SCHEMA = 0x00000010;
    /** 支持压缩 */
    int CLIENT_COMPRESS = 0x00000020;
    /** 特殊处理ODBC 3.22之后该标志位无特殊行为，可以忽略 */
    int CLIENT_ODBC = 0x00000040;
    /** 支持处理本地文件 */
    int CLIENT_LOCAL_FILES = 0x00000080;

    /** 处理SQL时忽略左括号前面的空格 */
    int CLIENT_IGNORE_SPACE = 0x00000100;
    /** 支持4.1协议返回*/
    int CLIENT_PROTOCOL_41 = 0x00000200; // for > 4.1.1
    /** 交互模式*/
    int CLIENT_INTERACTIVE = 0x00000400;
    /** 支持SSL，握手之后建立SSL*/
    int CLIENT_SSL = 0x00000800;

    /** Not used. */
    int CLIENT_IGNORE_SIGPIPE = 0x00001000;
    /** 支持事务 */
    int CLIENT_TRANSACTIONS = 0x00002000; // Client knows about transactions
    /** 废弃 */
    @Deprecated
    int CLIENT_RESERVED = 0x00004000; // for 4.1.0 only

    int CLIENT_SECURE_CONNECTION = 0x00008000;

    /** 多语句支持 */
    int CLIENT_MULTI_STATEMENTS = 0x00010000; // Enable/disable multiquery support
    /** 多结果支持 */
    int CLIENT_MULTI_RESULTS = 0x00020000; // Enable/disable multi-results
    int CLIENT_PS_MULTI_RESULTS = 0x00040000; // Enable/disable multi-results for server prepared statements
    int CLIENT_PLUGIN_AUTH = 0x00080000;

    int CLIENT_CONNECT_ATTRS = 0x00100000;
    int CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA = 0x00200000;
    int CLIENT_CAN_HANDLE_EXPIRED_PASSWORD = 0x00400000;
    int CLIENT_SESSION_TRACK = 0x00800000;

    // The column definitions part starts with a packet containing the column-count,
    // followed by as many Column Definition packets as there are columns and terminated
    // by an EOF_Packet if the CLIENT_DEPRECATE_EOF is not set.
    int CLIENT_DEPRECATE_EOF = 0x01000000;
    int CLIENT_OPTIONAL_RESULTSET_METADATA = 0x02000000;
    int CLIENT_ZSTD_COMPRESSION_ALGORITHM = 0x04000000;
    int CLIENT_QUERY_ATTRIBUTES = 0x08000000;

    int CLIENT_MULTI_FACTOR_AUTHENTICATION = 0x10000000;
    // there are 3 flags reserved.
    // CLIENT_CAPABILITY_EXTENSION
    // CLIENT_SSL_VERIFY_SERVER_CERT
    // CLIENT_REMEMBER_OPTIONS

}
