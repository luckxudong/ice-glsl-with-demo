package com.ice.graphics.texture;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import javax.microedition.khronos.opengles.GL10;

/**
 * User: jason
 * Date: 12-3-30
 * Time: 下午3:46
 */
public class BitmapTexture extends Texture {
    private static final String TAG = "BitmapTexture";

    private boolean invalidateAll = true;
    private boolean invalidateSub;

    public BitmapTexture(Bitmap bitmap) {
        this(bitmap, Params.LINEAR_CLAMP_TO_EDGE);
    }

    public BitmapTexture(Bitmap bitmap, Params params) {
        super(params);

        if (bitmap == null) {
            Log.w(TAG, "build with bitmap null !");
        }

        this.bitmap = bitmap;
    }

    @Override
    public void attach() {
        super.attach();

        if (invalidateAll) {
            invalidateAll = false;
            onLoadTextureData();
        }

        if (invalidateSub) {
            synchronized (this) {
                invalidateSub = false;
                GLUtils.texSubImage2D(GL10.GL_TEXTURE_2D, 0, xOffset, yOffset, bitmap);
            }
        }

    }

    @Override
    protected void onLoadTextureData() {
        if (bitmap == null) {
            Log.w(TAG, "bitmap null !");
            return;
        }

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            throw new IllegalArgumentException();
        }

        if (this.bitmap == null) {
            this.bitmap = bitmap;
            invalidateSub = false;
            invalidateAll = true;
        } else {
            if (this.bitmap.getWidth() != bitmap.getWidth() || this.bitmap.getHeight() != bitmap.getHeight()) {
                this.bitmap = bitmap;
                invalidateSub = false;
                invalidateAll = true;
            } else {
                postSubData(0, 0, bitmap);
            }
        }
    }

    public synchronized void postSubData(int xoffset, int yoffset, Bitmap subPixel) {
        this.xOffset = xoffset;
        this.yOffset = yoffset;
        bitmap = subPixel;
        invalidateSub = true;
    }

    private int xOffset, yOffset;

    private Bitmap bitmap;
}
