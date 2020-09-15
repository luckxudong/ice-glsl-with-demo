package com.ice.util;

import android.opengl.Matrix;

/**
 * User: jason
 * Date: 13-2-18
 */
public class MathUtil {
    public static final float[] IDENTITY_MATRIX_4F;

    static {
        IDENTITY_MATRIX_4F = new float[4 * 4];
        Matrix.setIdentityM(IDENTITY_MATRIX_4F, 0);
    }

    public static void setIdentity(float[] matrix4f) {
        if (matrix4f.length != IDENTITY_MATRIX_4F.length) {
            throw new IllegalArgumentException();
        }

        System.arraycopy(IDENTITY_MATRIX_4F, 0, matrix4f, 0, 16);
    }

}
