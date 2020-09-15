package com.ice.graphics.geometry;

import android.opengl.Matrix;
import com.ice.util.MathUtil;

import static android.opengl.Matrix.*;

/**
 * User: jason
 * Date: 13-2-18
 */
public class CoordinateSystem {
    private static final float[] TEMP_MATRIX = new float[4 * 4];

    public static final float[] M_V_MATRIX = new float[4 * 4];
    public static final float[] M_V_P_MATRIX = new float[4 * 4];

    public interface Global {
        float[] viewMatrix();

        float[] projectMatrix();
    }

    public static Global global;

    public static void buildGlobal(Global global) {
        CoordinateSystem.global = global;
    }

    public static Global global() {
        return global;
    }

    private float[] modelMatrix;

    public CoordinateSystem() {
        modelMatrix = new float[4 * 4];

        MathUtil.setIdentity(modelMatrix);
    }

    public float[] modelMatrix() {
        return modelMatrix;
    }

    public void modelViewMatrix(float[] output) {
        if (output.length != 4 * 4) {
            throw new IllegalArgumentException();
        }

        Matrix.multiplyMM(
                output, 0,
                global.viewMatrix(), 0,
                modelMatrix, 0
        );
    }

    public void modelViewProjectMatrix(float[] output) {
        if (output.length != 4 * 4) {
            throw new IllegalArgumentException();
        }

        Matrix.multiplyMM(
                TEMP_MATRIX, 0,
                global.viewMatrix(), 0,
                modelMatrix, 0
        );

        Matrix.multiplyMM(
                output, 0,
                global.projectMatrix(), 0,
                TEMP_MATRIX, 0
        );

    }

    public static class SimpleGlobal implements Global {
        private float[] viewMatrix, projectMatrix;

        public SimpleGlobal() {
            viewMatrix = new float[4 * 4];
            projectMatrix = new float[4 * 4];

            setIdentityM(viewMatrix, 0);
        }

        public void eye(float eyeZ) {
            eye(
                    0, 0, eyeZ,
                    0, 0, eyeZ - 10,
                    0, 1, 0
            );
        }

        public void eye(float eyeX, float eyeY, float eyeZ,
                        float centerX, float centerY, float centerZ,
                        float upX, float upY, float upZ) {
            setLookAtM(
                    viewMatrix, 0,
                    eyeX, eyeY, eyeZ,
                    centerX, centerY, centerZ,
                    upX, upY, upZ
            );
        }

        public void frustum(float left, float top, float near, float far) {
            frustum(left, -left, -top, top, near, far);
        }

        public void frustum(float left, float right, float bottom, float top,
                            float near, float far) {
            frustumM(projectMatrix, 0, left, right, bottom, top, near, far);
        }

        public void perspective(float fovy, float aspect, float zNear, float zFar) {
            perspectiveM(projectMatrix, 0, fovy, aspect, zNear, zFar);
        }

        public void ortho(float left, float right, float bottom, float top, float near, float far) {
            orthoM(
                    projectMatrix, 0,
                    left, right, bottom, top,
                    near, far
            );
        }

        @Override
        public float[] viewMatrix() {
            return viewMatrix;
        }

        @Override
        public float[] projectMatrix() {
            return projectMatrix;
        }

    }

}
