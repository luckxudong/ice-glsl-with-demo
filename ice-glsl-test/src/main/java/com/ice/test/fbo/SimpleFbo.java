package com.ice.test.fbo;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import com.ice.engine.AbstractRenderer;
import com.ice.engine.TestCase;
import com.ice.graphics.FBO;
import com.ice.graphics.GlUtil;
import com.ice.graphics.geometry.*;
import com.ice.graphics.shader.FragmentShader;
import com.ice.graphics.shader.Program;
import com.ice.graphics.shader.ShaderBinder;
import com.ice.graphics.shader.VertexShader;
import com.ice.graphics.texture.BitmapTexture;
import com.ice.graphics.texture.FboTexture;
import com.ice.model.ObjLoader;
import com.ice.test.R;

import javax.microedition.khronos.egl.EGLConfig;
import java.util.HashMap;
import java.util.Map;

import static android.opengl.GLES20.*;
import static com.ice.engine.Res.*;
import static com.ice.graphics.geometry.CoordinateSystem.M_V_P_MATRIX;
import static com.ice.graphics.texture.Texture.Params.LINEAR_REPEAT;

/**
 * User: Jason
 * Date: 13-2-12
 */
public class SimpleFbo extends TestCase {
    private static final String VERTEX_SRC = "texture/vertex.glsl";
    private static final String FRAGMENT_SRC = "texture/fragment.glsl";

    @Override
    protected GLSurfaceView.Renderer buildRenderer() {
        return new Renderer();
    }

    private class Renderer extends AbstractRenderer {
        Program program;
        Geometry panle;
        Geometry geometryA;
        Geometry geometryB;
        FBO fbo;
        private FboTexture fboTexture;

        @Override
        protected void onCreated(EGLConfig config) {
            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

            glEnable(GL_DEPTH_TEST);

            VertexShader vsh = new VertexShader(assetSting(VERTEX_SRC));
            FragmentShader fsh = new FragmentShader(assetSting(FRAGMENT_SRC));

            program = new Program();
            program.attachShader(vsh, fsh);
            program.link();

            Map<String, String> nameMap = new HashMap<String, String>();
            nameMap.put(ShaderBinder.POSITION, "a_Position");
            nameMap.put(ShaderBinder.TEXTURE_COORD, "a_TexCoordinate");

            GeometryData geometryData = GeometryDataFactory.createCubeData(1);
            geometryData.getFormatDescriptor().namespace(nameMap);
            geometryA = new VBOGeometry(geometryData, vsh);
            geometryA.setTexture(
                    new BitmapTexture(bitmap(R.drawable.freshfruit2))
            );

            geometryData = ObjLoader.loadObj(openRaw(R.raw.teaport));
            geometryData.getFormatDescriptor().namespace(nameMap);
            geometryB = new VBOGeometry(geometryData, vsh);
            Bitmap bitmap = bitmap(R.drawable.mask1);
            geometryB.setTexture(
                    new BitmapTexture(bitmap, LINEAR_REPEAT)
            );

            geometryData = GeometryDataFactory.createStripGridData(4, 4.5f, 1, 1);
            //geometryData.getFormatDescriptor().setMode(GL_LINE_STRIP);
            geometryData.getFormatDescriptor().namespace(nameMap);
            panle = new VBOGeometry(geometryData, vsh);

            fboTexture = new FboTexture(768, 920);
            fboTexture.setDataStorage(GL_DEPTH_COMPONENT, GL_DEPTH_COMPONENT, GL_UNSIGNED_INT);

            panle.setTexture(fboTexture);

            fbo = new FBO();
            fbo.attach();
            fboTexture.attach();
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, fboTexture.glRes(), 0);

            GlUtil.checkFramebufferStatus();

            fbo.detach();
        }


        @Override
        protected void onChanged(int width, int height) {
            glViewport(0, 0, width, height);

            CoordinateSystem.Global global = CoordinateSystem.global();

            if (global == null) {
                global = new CoordinateSystem.SimpleGlobal();
                CoordinateSystem.buildGlobal(global);
            }

            CoordinateSystem.SimpleGlobal simpleGlobal = (CoordinateSystem.SimpleGlobal) global;
            simpleGlobal.eye(6);
            simpleGlobal.perspective(45, width / (float) height, 1, 10);

//            float[] viewMatrix = simpleGlobal.viewMatrix();
//
//            Matrix.rotateM(
//                    viewMatrix, 0,
//                    -60,
//                    1.0f, 0, 0
//            );
        }

        @Override
        protected void onFrame() {
            drawOffscreen();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            drawOtherGeometrys();

            drawPanel();
        }

        private void drawOffscreen() {
            fbo.attach();
            fboTexture.attach();

            glColorMask(false, false, false, false);
            glClear(GL_DEPTH_BUFFER_BIT);

            drawOtherGeometrys();

            glColorMask(true, true, true, true);

            fboTexture.detach();
            fbo.detach();
        }

        private void drawOtherGeometrys() {
            // Do a complete rotation every 10 seconds.
            long time = System.currentTimeMillis() % 10000L;
            float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

            geometryA.attach();
            styleA(angleInDegrees, geometryA);
            styleB(angleInDegrees, geometryA);
            geometryA.detach();

            geometryB.attach();
            styleC(angleInDegrees, geometryB);
            geometryB.detach();
        }

        private void drawPanel() {
            panle.attach();

            updateMVPMatrix(panle);
            panle.draw();

            panle.detach();
        }

        private void styleA(float angleInDegrees, Geometry geometry) {
            float[] modelMatrix = geometry.selfCoordinateSystem();

            Matrix.setIdentityM(modelMatrix, 0);
            Matrix.translateM(
                    modelMatrix, 0,
                    0, 0, 0.5f
            );
            Matrix.rotateM(
                    modelMatrix, 0,
                    angleInDegrees,
                    0f, 0f, 1.0f
            );
            updateMVPMatrix(geometry);
            geometry.draw();
        }

        private void styleB(float angleInDegrees, Geometry geometry) {
            float[] modelMatrix = geometry.selfCoordinateSystem();

            Matrix.setIdentityM(modelMatrix, 0);
            Matrix.translateM(modelMatrix, 0, 1.5f, -1, 0);
            Matrix.rotateM(
                    modelMatrix, 0,
                    angleInDegrees,
                    0f, 0f, 1.0f
            );
            updateMVPMatrix(geometry);
            geometry.draw();
        }

        private void styleC(float angleInDegrees, Geometry geometry) {
            float[] modelMatrix = geometry.selfCoordinateSystem();

            Matrix.setIdentityM(modelMatrix, 0);

            Matrix.rotateM(
                    modelMatrix, 0,
                    90,
                    1, 0f, 0
            );

            Matrix.rotateM(
                    modelMatrix, 0,
                    angleInDegrees,
                    0, 1.0f, 0
            );

            Matrix.translateM(modelMatrix, 0, 1.5f, 0, 0);

            Matrix.rotateM(
                    modelMatrix, 0,
                    angleInDegrees,
                    0, 1, 0
            );

            updateMVPMatrix(geometry);
            geometry.draw();
        }

        private void updateMVPMatrix(Geometry geometry) {
            CoordinateSystem coordinateSystem = geometry.getCoordinateSystem();

            coordinateSystem.modelViewProjectMatrix(M_V_P_MATRIX);

            program.getVertexShader().uploadUniform("u_MVPMatrix", M_V_P_MATRIX);
        }

    }

}
