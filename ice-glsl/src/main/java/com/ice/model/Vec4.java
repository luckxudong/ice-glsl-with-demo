package com.ice.model;

/**
 * User: jason
 * Date: 13-2-22
 */
public class Vec4 {
    public float x, y, z, w;

    public Vec4() {
    }

    public Vec4(float x, float y, float z, float w) {
        setXYZW(x, y, z, w);
    }

    public void setXYZW(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public void setRGBA(float r, float g, float b, float a) {
        setXYZW(r, g, b, a);
    }

}
