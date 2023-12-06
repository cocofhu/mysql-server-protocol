package com.cocofhu.server.mysql;

public enum SessionPhase {
    /** when connecting or handshaking */
    CONNECTION,
    /** after handshake success, wait for command of client */
    COMMAND,
    /** after client disconnected*/
    DISCONNECTED
}
