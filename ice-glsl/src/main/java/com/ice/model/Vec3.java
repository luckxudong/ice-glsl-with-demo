package com.ice.model;

/**
 * User: jason
 * Date: 13-2-22
 */
public class Vec3 {
    public float x, y, z;

    public Vec3() {
    }

    public Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setXYZ(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setRGB(float r, float g, float b) {
        setXYZ(r, g, b);
    }

}
