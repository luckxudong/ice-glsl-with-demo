package com.ice.graphics.shader;

import static android.opengl.GLES20.*;

/**
 * User: Jason
 * Date: 13-2-23
 */
public class Attribute {

    private int type;
    private String name;
    private int glAttribute;

    public Attribute(int glAttribute) {
        this.glAttribute = glAttribute;
    }

    public Attribute(int glAttribute, String name, int type) {
        this.glAttribute = glAttribute;
        this.name = name;
        this.type = type;
    }

    public void upload(float... values) {

        switch (values.length) {
            case 1:
                glVertexAttrib1f(glAttribute, values[0]);
                break;
            case 2:
                glVertexAttrib2fv(glAttribute, values, 0);
                break;
            case 3:
                glVertexAttrib3fv(glAttribute, values, 0);
                break;
            case 4:
                glVertexAttrib4fv(glAttribute, values, 0);
                break;

            default:
                throw new IllegalArgumentException();
        }

        glDisableVertexAttribArray(glAttribute);
    }

    public void pointer(int dimension, int type, boolean normalized, int stride, int offset) {
        glVertexAttribPointer(
                glAttribute,
                dimension,
                type,
                normalized,
                stride,
                offset
        );

        glEnableVertexAttribArray(glAttribute);
    }

}
