package com.ice.graphics;

import com.ice.engine.GlResManager;

/**
 * User: Jason
 * Date: 13-3-19
 */
public abstract class AutoManagedGlRes implements GlRes {
    protected int glRes;
    private boolean prepared;

    @Override
    public void prepare() {
        if (!prepared) {
            GlResManager.regist(this);

            glRes = onPrepare();

            prepared = true;
        }
    }

    protected abstract int onPrepare();

    @Override
    public int glRes() {
        return glRes;
    }

    @Override
    public void release() {
        onRelease(glRes);
        glRes = NULL;
        prepared = false;
    }

    protected abstract void onRelease(int glRes);

    @Override
    public void onEGLContextLost() {
        prepared = false;
    }

    public boolean isPrepared() {
        return prepared;
    }

}
