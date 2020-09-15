package com.ice.model.light;

import com.ice.model.Vec4;

/**
 * User: jason
 * Date: 13-2-22
 */
public class Material {

    public static class MaterialColor extends Color {
        public Vec4 emission;

        public MaterialColor() {
            emission = new Vec4();
        }

    }

    public static Material createNormal() {
        Material material = new Material();

        material.color.ambient.setRGBA(1.0f, 1.0f, 1.0f, 1.0f);
        material.color.diffuse.setRGBA(1.0f, 1.0f, 1.0f, 1.0f);
        material.color.specular.setRGBA(1.0f, 1.0f, 1.0f, 1.0f);

        return material;
    }

    public float shininess;
    public MaterialColor color;

    public Material() {
        color = new MaterialColor();
    }

}
