package com.ice.graphics.state_controller;

import com.ice.graphics.state_controller.GlStateController;

/**
 * User: jason
 * Date: 13-2-20
 */
public abstract class SafeGlStateController implements GlStateController {
    private static final String GL_THREAD_NAME = "GLThread";
    private static final String WARNING = "Invalid operation ! A  GlStateController's operations should be executed in a Gl Thread !";

    public static boolean safeMode = true;

    @Override
    public final void attach() {
        if (safeMode) {
            ensureInGlThread();
        }

        onAttach();
    }

    @Override
    public final void detach() {
        if (safeMode) {
            ensureInGlThread();
        }

        onDetach();
    }

    protected abstract void onAttach();

    protected abstract void onDetach();

    private void ensureInGlThread() {
        boolean valid = Thread.currentThread().getName().contains(GL_THREAD_NAME);

        if (!valid) {
            throw new IllegalStateException(WARNING);
        }
    }

}
