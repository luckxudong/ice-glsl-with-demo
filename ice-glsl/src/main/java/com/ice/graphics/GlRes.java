package com.ice.graphics;


/**
 * User: ice
 * Date: 11-11-15
 * Time: 下午3:26
 */
public interface GlRes {
    public static final int NULL = -1;

    void prepare();

    //void recycle(GL10 gl);

    int glRes();

    void release();

    void onEGLContextLost();

}
