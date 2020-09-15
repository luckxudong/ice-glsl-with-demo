package com.ice.model.light;

import com.ice.model.Vec4;

/**
 * User: jason
 * Date: 13-2-22
 */
public class Color {

    public Vec4 ambient;
    public Vec4 diffuse;
    public Vec4 specular;

    public Color() {
        ambient = new Vec4();
        diffuse = new Vec4();
        specular = new Vec4();
    }

}
