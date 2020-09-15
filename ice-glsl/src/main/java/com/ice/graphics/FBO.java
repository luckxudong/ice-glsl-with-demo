package com.ice.graphics;

import android.opengl.GLES20;
import com.ice.graphics.state_controller.GlStateController;

import static android.opengl.GLES20.*;

/**
 * User: jason
 * Date: 13-3-14
 */
public class FBO extends AutoManagedGlRes implements GlStateController {

    @Override
    protected int onPrepare() {
        int[] temp = new int[1];
        GLES20.glGenFramebuffers(temp.length, temp, 0);
        return temp[0];
    }

    @Override
    protected void onRelease(int glRes) {
        glDeleteFramebuffers(1, new int[]{glRes}, 0);
    }

    @Override
    public void attach() {
        if (!isPrepared()) {
            prepare();
        }

        glBindFramebuffer(GL_FRAMEBUFFER, glRes());
    }

    @Override
    public void detach() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

}
