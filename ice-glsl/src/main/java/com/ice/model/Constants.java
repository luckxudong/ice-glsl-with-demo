package com.ice.model;

/**
 * User: ice
 * Date: 12-1-6
 * Time: 下午12:30
 */
public class Constants {
    public static final int BYTES_PER_FLOAT = Float.SIZE / Byte.SIZE;

    public static final int BYTES_PER_INT = Integer.SIZE / Byte.SIZE;

    public static final int BYTES_PER_SHORT = Short.SIZE / Byte.SIZE;

    public static final int BYTES_PER_BYTE = 1;
    public static final int MAX_UNSIGNED_BYTE_VALUE = (int) (Math.pow(2, 8) - 1);
    public static final int MAX_UNSIGNED_SHORT_VALUE = (int) (Math.pow(2, 16) - 1);
}
