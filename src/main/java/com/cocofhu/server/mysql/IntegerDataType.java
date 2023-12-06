package com.cocofhu.server.mysql;

public enum IntegerDataType {
    /**
     * 1 byte Protocol::FixedLengthInteger
     */
    INT1,

    /**
     * 2 byte Protocol::FixedLengthInteger
     */
    INT2,

    /**
     * 3 byte Protocol::FixedLengthInteger
     */
    INT3,

    /**
     * 4 byte Protocol::FixedLengthInteger
     */
    INT4,

    /**
     * 6 byte Protocol::FixedLengthInteger
     */
    INT6,

    /**
     * 8 byte Protocol::FixedLengthInteger
     */
    INT8,

    /**
     * Length-Encoded Integer Type
     */
    INT_LENENC;
}
