package com.ice.engine;

import com.ice.graphics.GlRes;
import com.ice.graphics.state_controller.SafeGlStateController;

import java.util.ArrayList;
import java.util.List;

/**
 * User: jason
 * Date: 13-3-19
 */
public class GlResManager extends SafeGlStateController {

    private static List<GlRes> glReses = new ArrayList<GlRes>();
    private static List<GlRes> autoManaged = new ArrayList<GlRes>();

    public static void regist(GlRes glRes) {
        if (!glReses.contains(glRes)) {
            glReses.add(glRes);
        }
    }

    public static void NotifyEGLContextLost() {
        for (GlRes glRes : glReses) {
            glRes.onEGLContextLost();
        }

        glReses.clear();
    }

    public static void scheduleAutoManaged(GlRes res) {
        if (!autoManaged.contains(res)) {
            autoManaged.add(res);
        }
    }

    @Override
    protected void onAttach() {
        for (GlRes glRes : autoManaged) {
            glRes.prepare();
        }
    }

    @Override
    protected void onDetach() {

    }

}
