package com.ice.graphics.texture;

import com.ice.graphics.AutoManagedGlRes;
import com.ice.graphics.state_controller.GlStateController;

import java.util.HashMap;
import java.util.Map;

import static android.opengl.GLES20.*;

public abstract class Texture extends AutoManagedGlRes implements GlStateController {

    public static class Params {
        public static final Params LINEAR_REPEAT;

        public static final Params LINEAR_CLAMP_TO_EDGE;

        static {
            LINEAR_REPEAT = new Params();
            LINEAR_REPEAT.add(GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            LINEAR_REPEAT.add(GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            LINEAR_REPEAT.add(GL_TEXTURE_WRAP_S, GL_REPEAT);
            LINEAR_REPEAT.add(GL_TEXTURE_WRAP_T, GL_REPEAT);

            LINEAR_CLAMP_TO_EDGE = new Params();
            LINEAR_CLAMP_TO_EDGE.add(GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            LINEAR_CLAMP_TO_EDGE.add(GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            LINEAR_CLAMP_TO_EDGE.add(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            LINEAR_CLAMP_TO_EDGE.add(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        }

        public Params() {
            paramMap = new HashMap<Integer, Integer>();
        }

        public void add(int pName, int value) {
            paramMap.put(pName, value);
        }

        public Map<Integer, Integer> getParamMap() {
            return paramMap;
        }

        private Map<Integer, Integer> paramMap;
    }

    private int target;
    private Params params;

    public Texture() {
        this(Params.LINEAR_CLAMP_TO_EDGE);
    }

    public Texture(int target) {
        this(target, Params.LINEAR_CLAMP_TO_EDGE);
    }

    public Texture(Params params) {
        this(GL_TEXTURE_2D, params);
    }

    public Texture(int target, Params params) {
        this.target = target;
        this.params = params;
    }

    public int getTarget() {
        return target;
    }

    @Override
    public void attach() {
        if (!isPrepared()) {
            prepare();
        } else {
            glBindTexture(target, glRes());
        }
    }

    @Override
    public void detach() {
        glBindTexture(target, 0);
    }

    @Override
    protected int onPrepare() {
        int[] temp = new int[1];

        glGenTextures(1, temp, 0);

        int glTexture = temp[0];

        glBindTexture(target, glTexture);

        /*
         * Always set any texture parameters before loading texture data,
         *By setting the parameters first,
         *OpenGL ES can optimize the texture data it provides to the graphics hardware to match your settings.
         */
        bindTextureParams(params);

        onLoadTextureData();

        return glTexture;
    }

    @Override
    protected void onRelease(int glRes) {
        glDeleteTextures(1, new int[]{glRes}, 0);
    }

    protected abstract void onLoadTextureData();

    private void bindTextureParams(Params params) {
        for (Map.Entry<Integer, Integer> entry : params.getParamMap().entrySet()) {
            glTexParameterf(
                    target,
                    entry.getKey(),
                    entry.getValue()
            );
        }
    }

}
