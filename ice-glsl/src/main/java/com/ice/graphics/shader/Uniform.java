package com.ice.graphics.shader;

import static android.opengl.GLES20.*;
import static android.opengl.GLES20.glUniform3i;
import static android.opengl.GLES20.glUniform4i;

/**
 * User: Jason
 * Date: 13-2-23
 */
public class Uniform {

    private int type;
    private String name;
    private int glUniform;

    public Uniform(int glUniform, String name) {
        this(glUniform, name, 0);
    }

    public Uniform(int glUniform, String name, int type) {
        this.glUniform = glUniform;
        this.name = name;
        this.type = type;
    }

    public void upload(float... values) {
        switch (values.length) {
            case 1:
                glUniform1f(glUniform, values[0]);
                break;
            case 2:
                glUniform2f(glUniform, values[0], values[1]);
                break;
            case 3:
                glUniform3f(glUniform, values[0], values[1], values[2]);
                break;
            case 4:
                glUniform4f(glUniform, values[0], values[1], values[2], values[3]);
                break;
            case 16:
                glUniformMatrix4fv(glUniform, 1, false, values, 0);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void upload(int... values) {
        switch (values.length) {
            case 1:
                glUniform1i(glUniform, values[0]);
                break;
            case 2:
                glUniform2i(glUniform, values[0], values[1]);
                break;
            case 3:
                glUniform3i(glUniform, values[0], values[1], values[2]);
                break;
            case 4:
                glUniform4i(glUniform, values[0], values[1], values[2], values[3]);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

}
