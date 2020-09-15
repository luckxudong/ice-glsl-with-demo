package com.ice.engine;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public abstract class TestCase extends Activity {
    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    private void init() {
        if (!supportOpenGLES20()) {
            throw new IllegalStateException("OpenGL ES 2.0 not supported on device !");
        }

        Res.build(this);

        setContentView(glSurfaceView = new GlslSurfaceView(this));

        glSurfaceView.setRenderer(buildRenderer());
    }

    protected abstract GLSurfaceView.Renderer buildRenderer();

    private boolean supportOpenGLES20() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return (info.reqGlEsVersion >= 0x20000);
    }

    @Override
    protected void onResume() {
        glSurfaceView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        glSurfaceView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Res.release();
        super.onDestroy();
    }

}
