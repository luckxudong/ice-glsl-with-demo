package com.ice.widget;

import android.graphics.Point;

/**
 * User: jason
 */
public interface CameraProxy {

    interface Listener {
        void onCameraOpened(boolean frontFace, Point previewSize);
    }

    void bindCameraListener(Listener listener);

}
