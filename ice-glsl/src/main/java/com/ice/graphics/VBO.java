package com.ice.graphics;

import com.ice.graphics.state_controller.GlStateController;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static android.opengl.GLES20.*;
import static com.ice.model.Constants.*;

public class VBO extends AutoManagedGlRes implements GlStateController {

    private int usage;
    private boolean invalidate;
    private Buffer verticesData;

    public VBO(Buffer data) {
        this(data, GL_STATIC_DRAW);
    }

    public VBO(Buffer data, int usage) {
        verticesData = data;
        this.usage = usage;
    }

    @Override
    protected int onPrepare() {
        int[] temp = new int[1];

        glGenBuffers(1, temp, 0);
        glBindBuffer(GL_ARRAY_BUFFER, temp[0]);

        int bytes = 0;

        if (verticesData instanceof ByteBuffer) {
            bytes = BYTES_PER_BYTE;
        } else if (verticesData instanceof IntBuffer) {
            bytes = BYTES_PER_INT;
        } else if (verticesData instanceof FloatBuffer) {
            bytes = BYTES_PER_FLOAT;
        }

        glBufferData(
                GL_ARRAY_BUFFER,
                verticesData.limit() * bytes,
                verticesData,
                usage
        );

        return temp[0];
    }

    @Override
    protected void onRelease(int glRes) {
        glDeleteBuffers(1, new int[]{glRes}, 0);
    }

    public void postSubData(float[] data) {
        if (!invalidate) {
            verticesData.position(0);
            ((FloatBuffer) verticesData).put(data);
            verticesData.position(0);
            invalidate = true;
        }
    }

    public Buffer getVerticesData() {
        return verticesData;
    }

    @Override
    public void attach() {
        if (!isPrepared()) {
            prepare();
        } else {
            glBindBuffer(GL_ARRAY_BUFFER, glRes());
        }

        if (invalidate) {
            int bytes = 0;

            if (verticesData instanceof ByteBuffer) {
                bytes = BYTES_PER_BYTE;
            } else if (verticesData instanceof IntBuffer) {
                bytes = BYTES_PER_INT;
            } else if (verticesData instanceof FloatBuffer) {
                bytes = BYTES_PER_FLOAT;
            }

            glBufferSubData(GL_ARRAY_BUFFER, 0, verticesData.limit() * bytes, verticesData);
            invalidate = false;
        }
    }

    @Override
    public void detach() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

}
