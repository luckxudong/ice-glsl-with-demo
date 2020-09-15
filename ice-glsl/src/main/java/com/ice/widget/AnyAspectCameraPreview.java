package com.ice.widget;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;
import com.ice.engine.AbstractRenderer;
import com.ice.graphics.geometry.*;
import com.ice.graphics.shader.FragmentShader;
import com.ice.graphics.shader.Program;
import com.ice.graphics.shader.ShaderBinder;
import com.ice.graphics.shader.VertexShader;
import com.ice.graphics.texture.Texture;
import com.ice.util.MathUtil;

import javax.microedition.khronos.egl.EGLConfig;
import java.util.HashMap;
import java.util.Map;

import static android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
import static android.opengl.GLES20.*;
import static com.ice.graphics.geometry.CoordinateSystem.M_V_P_MATRIX;

/**
 * User: jason
 */
public class AnyAspectCameraPreview extends GLSurfaceView implements CameraProxy.Listener {

    private static final String TAG = "AnyAspectCameraPreview";

    public enum Rotation {
        ROTATION_0(0), ROTATION_90(-90), ROTATION_180(180), ROTATION_270(90);

        private Rotation(float angle) {
            this.angle = angle;
        }

        public float getAngle() {
            return angle;
        }

        private float angle;
    }

    public interface PreviewTexturePreparedListener {
        void onPreviewTexturePrepared(AnyAspectCameraPreview preview, SurfaceTexture previewTexture);
    }

    private boolean frontFace;
    private CameraRenderer renderer;
    private Rotation previewRotation;
    private PreviewTexturePreparedListener previewTexturePreparedListener;

    public AnyAspectCameraPreview(Context context) {
        super(context);
        init();
    }

    public AnyAspectCameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        setRenderer(renderer = onCreateRenderer());
    }

    protected CameraRenderer onCreateRenderer() {
        return new CameraRenderer();
    }

    public SurfaceTexture getPreviewTexture() {
        return previewTexture;
    }

    public void setPreviewTexturePreparedListener(PreviewTexturePreparedListener listener) {
        this.previewTexturePreparedListener = listener;
        if (previewTexture != null) {
            previewTexturePreparedListener.onPreviewTexturePrepared(this, previewTexture);
        }
    }


    @Override
    public void onCameraOpened(boolean frontFace, Point previewSize) {
        this.frontFace = frontFace;

        cameraPreviewSize = new PointF(previewSize);
        textureCrop = null;

        updateRotation();

        if (panel != null) {
            renderer.updatePanelOrientation();
        }
    }

    private void calTextureCrop() {
        PointF srcTextureSize = new PointF();

        float displayAspect = displaySize.y / displaySize.x;
        float previewAspect = cameraPreviewSize.y / cameraPreviewSize.x;

        if (displayAspect > previewAspect) {
            srcTextureSize.y = cameraPreviewSize.y;
            srcTextureSize.x = srcTextureSize.y / displayAspect;
        } else {
            srcTextureSize.x = cameraPreviewSize.x;
            srcTextureSize.y = srcTextureSize.x * displayAspect;
        }

        RectF cropBounds = new RectF(0, 0, srcTextureSize.x, srcTextureSize.y);
        cropBounds.offset(
                (cameraPreviewSize.x - cropBounds.width()) / 2,
                (cameraPreviewSize.y - cropBounds.height()) / 2
        );

        textureCrop = new float[4];
        textureCrop[0] = cropBounds.left / cameraPreviewSize.x;
        textureCrop[1] = cropBounds.right / cameraPreviewSize.x;
        textureCrop[2] = cropBounds.top / cameraPreviewSize.y;
        textureCrop[3] = cropBounds.bottom / cameraPreviewSize.y;

        for (float v : textureCrop) {
            Log.d(TAG, "textureCrop " + v);
        }
    }

    protected class CameraRenderer extends AbstractRenderer {
        Texture texture;
        Program program;
        Point viewSize;

        @Override
        protected void onCreated(EGLConfig config) {
            glEnable(GL_CULL_FACE);

            VertexShader vsh = new VertexShader(VERTEX_SRC);
            FragmentShader fsh = new FragmentShader(FRAGMENT_SRC);

            program = new Program();
            program.attachShader(vsh, fsh);
            program.link();

            texture = new Texture(GL_TEXTURE_EXTERNAL_OES) {
                @Override
                protected void onLoadTextureData() {
                }
            };
            texture.prepare();

            previewTexture = new SurfaceTexture(texture.glRes());
            previewTexturePreparedListener.
                    onPreviewTexturePrepared(AnyAspectCameraPreview.this, previewTexture);
        }

        @Override
        protected void onChanged(int width, int height) {
            viewSize = new Point(width, height);

            setupProjection(width, height);

            updateRotation();

            rebuildPanel();
        }

        private void rebuildPanel() {
            if (viewSize == null) return;

            float aspect = (float) viewSize.y / viewSize.x;

            if (previewRotation == Rotation.ROTATION_90 || previewRotation == Rotation.ROTATION_270) {
                displaySize = new PointF(2.0f * aspect, 2.0f);
            } else {
                displaySize = new PointF(2.0f, 2.0f * aspect);
            }

            Map<String, String> nameMap = new HashMap<String, String>();
            nameMap.put(ShaderBinder.POSITION, "a_Position");
            nameMap.put(ShaderBinder.TEXTURE_COORD, "a_TexCoordinate");

            IndexedGeometryData indexedGeometryData =
                    GeometryDataFactory.createStripGridData(displaySize.x, displaySize.y, 1, 1);
            indexedGeometryData.getFormatDescriptor().namespace(nameMap);
            panel = new IBOGeometry(indexedGeometryData, program.getVertexShader());
            panel.setTexture(texture);

            updatePanelOrientation();
        }

        private void updatePanelOrientation() {
            CoordinateSystem coordinateSystem = panel.getCoordinateSystem();
            float[] matrix = coordinateSystem.modelMatrix();

            MathUtil.setIdentity(matrix);

            if (frontFace) {
                Matrix.rotateM(matrix, 0, 180, 0, 1, 0);
            }

            Matrix.rotateM(matrix, 0, previewRotation.getAngle(), 0, 0, 1);
        }

        private void setupProjection(int width, int height) {
            glViewport(0, 0, width, height);

            CoordinateSystem.Global global = CoordinateSystem.global();

            if (global == null) {
                global = new CoordinateSystem.SimpleGlobal();
                CoordinateSystem.buildGlobal(global);
            }

            CoordinateSystem.SimpleGlobal simpleGlobal = (CoordinateSystem.SimpleGlobal) global;

            float left = -1.0f;
            float top = -left * height / (float) width;
            simpleGlobal.ortho(left, -left, -top, top, 0, 10);
        }

        @Override
        protected void onFrame() {
            if (!allInfoOk()) return;

            renderCameraPreview();
        }

        private void renderCameraPreview() {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            if (frontFace) {
                GLES20.glCullFace(GL_FRONT);
            } else {
                GLES20.glCullFace(GL_BACK);
            }

            panel.attach();
            previewTexture.updateTexImage();

            if (textureCrop == null) {
                calTextureCrop();
            }

            updateShaderParams();

            panel.draw();

            panel.detach();
        }

        private boolean allInfoOk() {
            return cameraPreviewSize != null;
        }

        private void updateShaderParams() {
            CoordinateSystem coordinateSystem = panel.getCoordinateSystem();

            coordinateSystem.modelViewProjectMatrix(M_V_P_MATRIX);

            VertexShader vsh = program.getVertexShader();
            vsh.uploadUniform("u_MVPMatrix", M_V_P_MATRIX);
            vsh.uploadUniform("u_TextureCrop", textureCrop);
        }

    }

    private void updateRotation() {
        WindowManager windowManager =
                (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay().getRotation();

        switch (rotation) {
            case Surface.ROTATION_0:
                Log.w(TAG, "View rotation ROTATION_0");
                previewRotation = Rotation.ROTATION_0;
                break;
            case Surface.ROTATION_90:
                Log.w(TAG, "View rotation ROTATION_90");
                previewRotation = Rotation.ROTATION_270;
                break;
            case Surface.ROTATION_180:
                Log.w(TAG, "View rotation ROTATION_180");
                previewRotation = Rotation.ROTATION_180;
                break;
            case Surface.ROTATION_270:
                Log.w(TAG, "View rotation ROTATION_270");
                previewRotation = frontFace ? Rotation.ROTATION_270 : Rotation.ROTATION_90;
                break;
        }
    }

    public float[] getTextureCrop() {
        return textureCrop;
    }

    public boolean isFrontFace() {
        return frontFace;
    }

    private SurfaceTexture previewTexture;
    private Geometry panel;
    private PointF displaySize;
    private PointF cameraPreviewSize;
    private float[] textureCrop;

    private static final String VERTEX_SRC = "#version 120\n" +
            "uniform mat4 u_MVPMatrix;" +
            "uniform vec4 u_TextureCrop;" +
            "attribute vec4 a_Position;" +
            "attribute vec2 a_TexCoordinate;" +
            "varying vec2 v_TexCoordinate;" +

            "void main(){" +
            "float croppedS=u_TextureCrop.x+ a_TexCoordinate.x*(u_TextureCrop.y-u_TextureCrop.x);" +
            "float croppedT=u_TextureCrop.z+ a_TexCoordinate.y*(u_TextureCrop.w-u_TextureCrop.z);" +
            "v_TexCoordinate= vec2(croppedS,croppedT);" +
            "gl_Position = u_MVPMatrix * a_Position;" +
            "}";

    private static final String FRAGMENT_SRC = "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;" +
            "uniform samplerExternalOES u_Texture;" +
            "varying vec2 v_TexCoordinate;" +

            "void main(){" +
            "gl_FragColor = texture2D(u_Texture, v_TexCoordinate);" +
            "}";
}
