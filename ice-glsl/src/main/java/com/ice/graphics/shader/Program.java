package com.ice.graphics.shader;

import com.ice.exception.FailException;
import com.ice.graphics.AutoManagedGlRes;
import com.ice.graphics.state_controller.GlStateController;

import static android.opengl.GLES20.*;

/**
 * User: jason
 * Date: 13-2-5
 */
public class Program extends AutoManagedGlRes implements GlStateController {

    public static Program using;

    private boolean linked;
    private VertexShader vsh;
    private FragmentShader fsh;

    public Program() {
        prepare();
    }

    public void attachShader(VertexShader vsh, FragmentShader fsh) {
        int glProgram = glRes();

        glAttachShader(glProgram, vsh.glRes());
        glAttachShader(glProgram, fsh.glRes());

        this.vsh = vsh;
        this.fsh = fsh;

        vsh.onAttachToProgram(this);
        fsh.onAttachToProgram(this);
    }

    public VertexShader getVertexShader() {
        return vsh;
    }

    public FragmentShader getFragmentShader() {
        return fsh;
    }

    public void link() {
        // Link the attachedProgram
        int glProgram = glRes();

        glLinkProgram(glProgram);

        // Check the link status
        int[] link = new int[1];
        glGetProgramiv(glProgram, GL_LINK_STATUS, link, 0);

        if (link[0] == GL_FALSE) {
            String info = glGetProgramInfoLog(glProgram);
            glDeleteProgram(glProgram);
            throw new FailException("Link failed ! " + info);
        }

        // Free up no longer needed shader resources
        glDeleteShader(vsh.glRes());
        glDeleteShader(fsh.glRes());

        vsh.onProgramLinked(this);
        fsh.onProgramLinked(this);

        linked = true;
    }

    public boolean isLinked() {
        return linked;
    }

    @Override
    protected int onPrepare() {
        int glProgram = glCreateProgram();

        if (glProgram == 0) {
            throw new FailException("Create attachedProgram failed !");
        }

        return glProgram;
    }

    @Override
    public void release() {
    }

    @Override
    protected void onRelease(int glRes) {
        //TODO
    }

    public boolean isActive() {
        return this == using;
    }

    @Override
    public void attach() {
        if (vsh == null || fsh == null) {
            throw new IllegalStateException();
        }

        glUseProgram(glRes());

        using = this;
    }

    @Override
    public void detach() {
        glUseProgram(0);
    }

}
