package com.ice.graphics.geometry;

import com.ice.graphics.IBO;
import com.ice.graphics.shader.FragmentShader;
import com.ice.graphics.shader.VertexShader;

import static android.opengl.GLES20.glDrawElements;

/**
 * User: jason
 * Date: 13-2-17
 */
public class IBOGeometry extends VBOGeometry {

    private IBO ibo;

    public IBOGeometry(IndexedGeometryData data) {
        this(data, null);
    }

    public IBOGeometry(IndexedGeometryData data, VertexShader vsh) {
        this(data, vsh, null);
    }

    public IBOGeometry(IndexedGeometryData data, VertexShader vsh, FragmentShader fsh) {
        super(data, vsh, fsh);

        ibo = new IBO(data.getIndexData());
    }

    @Override
    protected void bindShaderData(GeometryData data, VertexShader vsh, FragmentShader fsh) {
        ibo.attach();
        super.bindShaderData(data, vsh, fsh);
    }

    @Override
    protected void unbindShaderData(GeometryData data, VertexShader vsh, FragmentShader fsh) {
        ibo.detach();
        super.unbindShaderData(data, vsh, fsh);
    }

    @Override
    public void draw() {
        GeometryData.Descriptor formatDescriptor = getGeometryData().getFormatDescriptor();
        glDrawElements(formatDescriptor.getMode(), formatDescriptor.getCount(), ibo.getType(), 0);
    }

}
