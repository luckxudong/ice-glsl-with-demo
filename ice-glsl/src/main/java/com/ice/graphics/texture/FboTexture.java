package com.ice.graphics.texture;

import static android.opengl.GLES20.*;

/**
 * User: jason
 * Date: 13-3-14
 */
public class FboTexture extends Texture {

    private int width, height;
    private int attachment = GL_COLOR_ATTACHMENT0;

    private int type = GL_UNSIGNED_BYTE;
    private int format = GL_RGBA;
    private int internalformat = GL_RGBA;

    public FboTexture(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public FboTexture(Params params, int width, int height) {
        super(params);
        this.width = width;
        this.height = height;
    }

    public void setDataStorage(int internalFormat, int format, int type) {
        this.internalformat = internalFormat;
        this.format = format;
        this.type = type;
    }

    @Override
    protected void onLoadTextureData() {
        glTexImage2D(
                GL_TEXTURE_2D,
                0,
                internalformat,
                width, height, 0,
                format,
                type,
                null
        );
    }

}
