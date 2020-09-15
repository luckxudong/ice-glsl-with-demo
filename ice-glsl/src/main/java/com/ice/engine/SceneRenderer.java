package com.ice.engine;

import com.ice.overlay.Scene;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * User: jason
 * Date: 13-2-22
 */
public class SceneRenderer extends AbstractRenderer {

    private Scene activeScene;

    public SceneRenderer(Scene scene) {
        activeScene = scene;
    }

    @Override
    protected void onCreated(EGLConfig config) {
        activeScene.onCreate();
    }

    @Override
    protected void onChanged(int width, int height) {
        activeScene.onSurfaceChanged(width, height);
    }

    @Override
    protected void onFrame() {
        activeScene.render();
    }

}
