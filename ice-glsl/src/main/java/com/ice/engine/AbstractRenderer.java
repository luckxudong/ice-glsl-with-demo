package com.ice.engine;

import android.opengl.GLSurfaceView;
import android.util.Log;
import com.ice.graphics.geometry.CoordinateSystem;
import com.ice.graphics.shader.Shader;
import com.ice.util.BufferUtil;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static android.opengl.GLES20.*;
import static com.ice.graphics.GlUtil.checkError;

/**
 * User: jason
 * Date: 13-2-5
 */
public abstract class AbstractRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "AbstractRenderer";

    private Fps fps;

    protected AbstractRenderer() {
        CoordinateSystem.buildGlobal(new CoordinateSystem.SimpleGlobal());
        fps = new Fps();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated");

        GlResManager.NotifyEGLContextLost();

        printInfo();

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        onCreated(config);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        Log.i(TAG, "onSurfaceChanged width =" + width + " height =" + height);

        onChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        fps.step();
        onFrame();
        checkError();
    }

    protected abstract void onCreated(EGLConfig config);

    protected abstract void onChanged(int width, int height);

    protected abstract void onFrame();

    private void printInfo() {
        Log.i(TAG, "GL_RENDERER = " + glGetString(GL_RENDERER));
        Log.i(TAG, "GL_VENDOR = " + glGetString(GL_VENDOR));
        Log.i(TAG, "GL_VERSION = " + glGetString(GL_VERSION));
        Log.i(TAG, "GL_EXTENSIONS = " + glGetString(GL_EXTENSIONS));

        IntBuffer intBuffer = BufferUtil.intBuffer(1);
        glGetIntegerv(GL_MAX_TEXTURE_SIZE, intBuffer);
        Log.i(TAG, "GL_MAX_TEXTURE_SIZE = " + intBuffer.get());

        FloatBuffer floatBuffer = BufferUtil.floatBuffer(1);
        glGetFloatv(GL_MAX_VERTEX_ATTRIBS, floatBuffer);
        int attributeCapacity = (int) floatBuffer.get(0);
        glGetFloatv(GL_MAX_VERTEX_UNIFORM_VECTORS, floatBuffer);
        int vertexUniformCapacity = (int) floatBuffer.get(0);

        Log.i(TAG, "GL_MAX_VERTEX_ATTRIBS = " + attributeCapacity);
        Log.i(TAG, "GL_MAX_VERTEX_UNIFORM_VECTORS = " + vertexUniformCapacity);

        Shader.setAttributeCapacity(attributeCapacity);
    }


    private class Fps {
        private int fps;
        private long lastUpdate;

        public void step() {
            fps++;

            long now = System.currentTimeMillis();

            if (now - lastUpdate > 1000) {
                System.out.println("fps = " + fps);
                fps = 0;
                lastUpdate = now;
            }
        }

    }

}
