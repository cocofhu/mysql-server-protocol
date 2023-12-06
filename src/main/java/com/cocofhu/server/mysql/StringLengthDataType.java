package com.cocofhu.server.mysql;

public enum StringLengthDataType {
    /**
     * Protocol::FixedLengthString
     * Fixed-length strings have a known, hardcoded length.
     */
    STRING_FIXED,

    /**
     * Protocol::VariableLengthString
     * The length of the string is determined by another field or is calculated at runtime
     */
    STRING_VAR;
}
