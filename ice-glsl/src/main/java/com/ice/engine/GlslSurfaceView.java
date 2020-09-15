package com.ice.engine;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * User: Jason
 * Date: 13-2-22
 */
public class GlslSurfaceView extends GLSurfaceView {

    public GlslSurfaceView(Context context) {
        super(context);
        init();
    }

    public GlslSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        //setDebugFlags(DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS);
    }

}
