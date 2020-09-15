package com.ice.graphics.geometry;

import com.ice.graphics.shader.FragmentShader;
import com.ice.graphics.shader.VertexShader;
import com.ice.graphics.state_controller.GlStateController;
import com.ice.graphics.texture.Texture;

/**
 * User: jason
 * Date: 13-2-16
 */
public abstract class Geometry implements GlStateController {

    public interface Binder {

        void bind(GeometryData data, VertexShader vsh, FragmentShader fsh);

        void unbind(GeometryData data, VertexShader vsh, FragmentShader fsh);

    }

    private Binder binder;
    private Texture texture;
    private GeometryData geometryData;
    private VertexShader vertexShader;
    private FragmentShader fragmentShader;
    private CoordinateSystem coordinateSystem;

    public Geometry(GeometryData data) {
        this(data, null, null);
    }

    public Geometry(GeometryData data, VertexShader vsh) {
        this(data, vsh, null);
    }

    public Geometry(GeometryData data, VertexShader vsh, FragmentShader fsh) {
        this.geometryData = data;
        this.vertexShader = vsh;
        this.fragmentShader = fsh;

        coordinateSystem = new CoordinateSystem();
    }

    @Override
    public void attach() {
        vertexShader.attach();

        if (texture != null) {
            texture.attach();
        }

        bindShaderData(geometryData, vertexShader, fragmentShader);
    }

    @Override
    public void detach() {
        unbindShaderData(geometryData, vertexShader, fragmentShader);

        if (texture != null) {
            texture.detach();
        }
    }

    public abstract void draw();

    protected void bindShaderData(GeometryData data, VertexShader vsh, FragmentShader fsh) {
        if (binder != null) {
            binder.bind(data, vsh, fsh);
        }
    }

    protected void unbindShaderData(GeometryData data, VertexShader vsh, FragmentShader fsh) {
        if (binder != null) {
            binder.unbind(data, vsh, fsh);
        }
    }

    public float[] selfCoordinateSystem() {
        return coordinateSystem.modelMatrix();
    }

    public CoordinateSystem getCoordinateSystem() {
        return coordinateSystem;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public FragmentShader getFragmentShader() {
        return fragmentShader;
    }

    public void setFragmentShader(FragmentShader fragmentShader) {
        this.fragmentShader = fragmentShader;
    }

    public GeometryData getGeometryData() {
        return geometryData;
    }

    public void setGeometryData(GeometryData geometryData) {
        this.geometryData = geometryData;
    }

    public VertexShader getVertexShader() {
        return vertexShader;
    }

    public void setVertexShader(VertexShader vertexShader) {
        this.vertexShader = vertexShader;
    }

    public Binder getBinder() {
        return binder;
    }

    public void setBinder(Binder binder) {
        this.binder = binder;
    }

}
