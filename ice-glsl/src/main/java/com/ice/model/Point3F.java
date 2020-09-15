package com.ice.model;

/**
 * User: jason
 * Date: 13-2-17
 */
public class Point3F {
    public float x, y, z;

    public Point3F() {
    }

    public Point3F(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3F(Point3F p) {
        this.x = p.x;
        this.y = p.y;
        this.z = p.z;
    }

}
