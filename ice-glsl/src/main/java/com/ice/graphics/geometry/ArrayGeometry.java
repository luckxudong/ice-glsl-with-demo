package com.ice.graphics.geometry;

import com.ice.graphics.shader.FragmentShader;
import com.ice.graphics.shader.VertexShader;

/**
 * User: Jason
 * Date: 13-2-16
 */
public class ArrayGeometry extends Geometry {

    public ArrayGeometry(GeometryData data) {
        super(data);
    }

    public ArrayGeometry(GeometryData data, VertexShader vsh) {
        super(data, vsh);
    }

    public ArrayGeometry(GeometryData data, VertexShader vsh, FragmentShader fsh) {
        super(data, vsh, fsh);
    }

    @Override
    public void draw() {

    }

}
