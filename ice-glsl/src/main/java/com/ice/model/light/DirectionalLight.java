package com.ice.model.light;

import com.ice.model.Vec3;

/**
 * User: jason
 * Date: 13-2-22
 */
public class DirectionalLight {

    /**
     * normalized light direction in eye space
     */
    public Vec3 direction;

    /**
     * normalized half-plane vector
     */
    public Vec3 halfPlane;

    public Color color;

    public DirectionalLight() {
        direction = new Vec3();
        halfPlane = new Vec3();
        color = new Color();
    }

}
