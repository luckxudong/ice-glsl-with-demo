package com.ice.graphics.state_controller;

import android.opengl.GLES20;

import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.*;

/**
 * User: jason
 * Date: 12-2-21
 * Time: 上午10:50
 */
public class BlendController extends SafeGlStateController {

    public static final BlendController BLEND_S_ONE_D_ONE = new BlendController(GL_ONE, GL_ONE);
    private boolean originalBlend;

    private int[] originalSrcRGB = new int[1];
    private int[] originalDesRGB = new int[1];
    private int[] originalSrcAlpha = new int[1];
    private int[] originalDesAlpha = new int[1];

    private boolean blend;
    private int[] factorS, factorD;

    public BlendController(boolean blend) {
        this.blend = blend;
    }

    /**
     * 开启混合
     *
     * @param blend_S
     * @param factor_D
     */
    public BlendController(int blend_S, int factor_D) {
        this(true);
        this.factorS = new int[]{blend_S};
        this.factorD = new int[]{factor_D};
    }


    @Override
    protected void onAttach() {
        originalBlend = GLES20.glIsEnabled(GL_BLEND);

        if (originalBlend != blend) {
            if (blend) {
                glEnable(GL_BLEND);

                if (factorS != null) {
                    glBlendFunc(factorS[0], factorD[0]);
                }
            } else {
                glDisable(GL_BLEND);
            }
        } else {
            if (blend) {
                //todo save original blend factor
//                glGetIntegerv(GL_BLEND_SRC_RGB, originalSrcRGB, 0);
//                glGetIntegerv(GL_BLEND_DST_RGB, originalDesRGB, 0);
//                glGetIntegerv(GL_BLEND_SRC_ALPHA, originalSrcAlpha, 0);
//                glGetIntegerv(GL_BLEND_DST_ALPHA, originalDesAlpha, 0);

                if (factorS != null) {
                    glBlendFunc(factorS[0], factorD[0]);
                }
            }
        }

    }

    @Override
    protected void onDetach() {
        if (originalBlend != blend) {
            if (originalBlend) {
                glEnable(GL_BLEND);
            } else {
                glDisable(GL_BLEND);
            }
        } else {
            if (originalBlend) {
                //todo restore original blend factor
            }
        }
    }

}
