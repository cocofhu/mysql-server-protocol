package com.cocofhu.server.mysql;

public enum StringSelfDataType {
    /**
     * Protocol::NulTerminatedString
     * Strings that are terminated by a [00] byte.
     */
    STRING_TERM,

    /**
     * Protocol::LengthEncodedString
     * A length encoded string is a string that is prefixed with length encoded integer describing the length of the string.
     * It is a special case of Protocol::VariableLengthString
     */
    STRING_LENENC,

    /**
     * Protocol::RestOfPacketString
     * If a string is the last component of a packet, its length can be calculated from the overall packet length minus the current position.
     */
    STRING_EOF;
}
