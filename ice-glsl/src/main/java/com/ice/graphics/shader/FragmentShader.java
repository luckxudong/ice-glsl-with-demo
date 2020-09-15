package com.ice.graphics.shader;

import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.glCreateShader;

/**
 * User: jason
 * Date: 13-2-17
 */
public class FragmentShader extends Shader {

    public FragmentShader(String shaderSrc) {
        super(shaderSrc);
    }

    @Override
    protected int createGlShader() {
        return glCreateShader(GL_FRAGMENT_SHADER);
    }

}
