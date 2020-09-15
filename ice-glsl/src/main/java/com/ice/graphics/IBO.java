package com.ice.graphics;

import android.util.Log;
import com.ice.graphics.state_controller.GlStateController;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.*;
import static com.ice.model.Constants.BYTES_PER_SHORT;
import static javax.microedition.khronos.opengles.GL11.GL_ELEMENT_ARRAY_BUFFER;
import static javax.microedition.khronos.opengles.GL11.GL_STATIC_DRAW;

/**
 * User: jason
 * Date: 13-2-16
 */
public class IBO extends AutoManagedGlRes implements GlStateController {

    private static final String TAG = "IBO";

    private int usage;
    private Buffer indicesData;

    private final int size;
    private final int type;

    public IBO(Buffer data) {
        this(data, GL_STATIC_DRAW);
    }

    public IBO(Buffer data, int usage) {
        if (data instanceof ByteBuffer) {
            type = GL_UNSIGNED_BYTE;
            size = data.limit();
            Log.i(TAG, "GL_UNSIGNED_BYTE");
        }
        else if (data instanceof ShortBuffer) {
            type = GL_UNSIGNED_SHORT;
            size = data.limit() * BYTES_PER_SHORT;
            Log.i(TAG, "GL_UNSIGNED_SHORT");
        }
        else {
            throw new IllegalArgumentException();
        }

        indicesData = data;
        this.usage = usage;
    }

    public int getType() {
        return type;
    }

    @Override
    protected int onPrepare() {
        int[] temp = new int[1];
        glGenBuffers(1, temp, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, temp[0]);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, size, indicesData, usage);
        return temp[0];
    }

    @Override
    protected void onRelease(int glRes) {
        glDeleteBuffers(1, new int[]{glRes}, 0);
    }

    @Override
    public void attach() {
        if (!isPrepared()) {
            prepare();
        }
        else {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, glRes());
        }
    }

    @Override
    public void detach() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

}
