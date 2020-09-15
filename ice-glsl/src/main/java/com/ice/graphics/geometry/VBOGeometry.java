package com.ice.graphics.geometry;

import com.ice.graphics.VBO;
import com.ice.graphics.shader.Attribute;
import com.ice.graphics.shader.FragmentShader;
import com.ice.graphics.shader.VertexShader;

import java.util.List;

import static android.opengl.GLES20.glDrawArrays;
import static com.ice.graphics.geometry.GeometryData.Component;
import static com.ice.graphics.geometry.GeometryData.Descriptor;

/**
 * User: Jason
 * Date: 13-2-16
 */
public class VBOGeometry extends Geometry {

    private VBO vbo;

    public VBOGeometry(GeometryData data) {
        this(data, null, null);
    }

    public VBOGeometry(GeometryData data, VertexShader vsh) {
        this(data, vsh, null);
    }

    public VBOGeometry(GeometryData data, VertexShader vsh, FragmentShader fsh) {
        super(data, vsh, fsh);

        vbo = new VBO(data.getVertexData());

        setBinder(
                new EasyBinder(getGeometryData().getFormatDescriptor())
        );
    }

    @Override
    protected void bindShaderData(GeometryData data, VertexShader vsh, FragmentShader fsh) {
        vbo.attach();

        super.bindShaderData(data, vsh, fsh);
    }

    @Override
    protected void unbindShaderData(GeometryData data, VertexShader vsh, FragmentShader fsh) {
        vbo.detach();

        super.unbindShaderData(data, vsh, fsh);
    }

    public VBO getVbo() {
        return vbo;
    }

    @Override
    public void draw() {
        Descriptor formatDescriptor = getGeometryData().getFormatDescriptor();
        glDrawArrays(formatDescriptor.getMode(), 0, formatDescriptor.getCount());
    }

    public static class EasyBinder implements Geometry.Binder {

        private boolean errorPrinted;
        private Descriptor descriptor;

        public EasyBinder(Descriptor descriptor) {
            this.descriptor = descriptor;
        }

        @Override
        public void bind(GeometryData data, VertexShader vsh, FragmentShader fsh) {

            List<GeometryData.Component> components = descriptor.getComponents();

            boolean error = false;

            for (Component component : components) {

                Attribute attribute = vsh.findAttribute(component.name);

                if (attribute == null) {
                    error = true;

//                    if (!errorPrinted) {
//                        Log.e(TAG, "attribute " + component.name + " not found in vertex shader ");
//                    }

                }
                else {

                    attribute.pointer(
                            component.dimension,
                            component.type,
                            component.normalized,
                            descriptor.getStride(),
                            component.offset
                    );


                }
            }

            errorPrinted = error;

        }

        @Override
        public void unbind(GeometryData data, VertexShader vsh, FragmentShader fsh) {

        }

    }

}
