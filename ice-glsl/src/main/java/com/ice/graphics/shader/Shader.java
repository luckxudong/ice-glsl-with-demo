//
// Book:      OpenGL(R) ES 2.0 Programming Guide
// Authors:   Aaftab Munshi, Dan Ginsburg, Dave Shreiner
// ISBN-10:   0321502795
// ISBN-13:   9780321502797
// Publisher: Addison-Wesley Professional
// URLs:      http://safari.informit.com/9780321563835
//            http://www.opengles-book.com
//

// Shader
//
//    Utility functions for loading shaders and creating attachedProgram objects.
//

package com.ice.graphics.shader;

import android.util.Log;
import com.ice.exception.FailException;
import com.ice.graphics.AutoManagedGlRes;
import com.ice.graphics.state_controller.GlStateController;

import java.util.HashMap;
import java.util.Map;

import static android.opengl.GLES20.*;

public abstract class Shader extends AutoManagedGlRes implements GlStateController {
    protected static int attributeCapacity = 8;
    private static final String TAG = "Shader";

    public static void setAttributeCapacity(int attributeCapacity) {
        Shader.attributeCapacity = attributeCapacity;
        Log.i(TAG, "Attribute capacity = " + attributeCapacity);
    }

    public static int getAttributeCapacity() {
        return attributeCapacity;
    }

    private String shaderSrc;
    protected Program attachedProgram;
    private Map<String, Uniform> uniforms;

    protected Shader(String shaderSrc) {
        if (shaderSrc == null || shaderSrc.length() == 0) {
            throw new IllegalArgumentException(" src " + shaderSrc);
        }

        this.shaderSrc = shaderSrc;

        uniforms = new HashMap<String, Uniform>();

        prepare();
    }

    @Override
    public void attach() {
        validateProgram();

        if (!attachedProgram.isActive()) {
            attachedProgram.attach();
        }
    }

    @Override
    public void detach() {
        validateProgram();

        attachedProgram.detach();
    }

    @Override
    protected int onPrepare() {
        // Create the shader object
        int glShader = createGlShader();

        if (glShader == 0) {
            throw new FailException("Create shader failed !");
        }

        // Load the shader source
        glShaderSource(glShader, shaderSrc);

        // Compile the shader
        glCompileShader(glShader);

        // Check the compile status
        int[] compiled = new int[1];
        glGetShaderiv(glShader, GL_COMPILE_STATUS, compiled, 0);

        if (compiled[0] == GL_FALSE) {
            String log = glGetShaderInfoLog(glShader);
            glDeleteShader(glShader);
            throw new FailException("Compile failed ! " + log);
        }

        return glShader;
    }

    protected abstract int createGlShader();

    @Override
    protected void onRelease(int glRes) {
        //TODO
    }

    public void onAttachToProgram(Program attachedProgram) {
        this.attachedProgram = attachedProgram;
    }

    public void onProgramLinked(Program program) {
        if (this.attachedProgram == program) {
            initUniforms();
        }
    }

    protected void validateProgram() {
        if (attachedProgram == null) {
            throw new IllegalStateException("Not attached to a program yet !");
        }
    }

    public boolean isActive() {
        return attachedProgram != null && attachedProgram.isActive();
    }

    public Uniform findUniform(String name) {
        validateProgram();

        Uniform uniform = uniforms.get(name);

        if (uniform != null) return uniform;

        int glUniform = glGetUniformLocation(attachedProgram.glRes(), name);

        Uniform uniformNotActive = new Uniform(glUniform, name);

        uniforms.put(name, uniformNotActive);

        Log.w(TAG, "Found uniform " + name + " ,which is not active !");

        return uniformNotActive;
    }

    private void initUniforms() {
        uniforms.clear();

        int[] activeUniformSize = new int[1];

        int glProgram = attachedProgram.glRes();

        glGetProgramiv(glProgram, GL_ACTIVE_UNIFORMS, activeUniformSize, 0);

        int[] lengthContainer = new int[1];
        int[] sizeContainer = new int[1];
        int[] typeContainer = new int[1];
        int nameContainerSize = 64;
        byte[] nameContainer = new byte[nameContainerSize];

        for (int glUniform = 0; glUniform < activeUniformSize[0]; glUniform++) {
            glGetActiveUniform(glProgram, glUniform, nameContainerSize, lengthContainer, 0, sizeContainer, 0, typeContainer, 0, nameContainer, 0);

            int length = lengthContainer[0];

            String name = new String(nameContainer, 0, length);

            uniforms.put(
                    name,
                    new Uniform(glUniform, name, typeContainer[0])
            );
        }
    }

    public void uploadUniform(String name, float... values) {
        findUniform(name).upload(values);
    }

    public void uploadUniform(String name, int... values) {
        findUniform(name).upload(values);
    }

}
