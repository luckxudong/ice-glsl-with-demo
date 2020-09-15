package com.ice.model;

import static android.opengl.Matrix.setIdentityM;

/**
 * User: jason
 * Date: 13-2-5
 */
public class Matrix4F {

    private float[] data;

    public Matrix4F() {
        data = new float[4 * 4];
        setIdentityM(data, 0);
    }

    public float[] values() {
        return data;
    }

}
