package com.ice.graphics.geometry;

import com.ice.graphics.shader.FragmentShader;
import com.ice.graphics.shader.VertexShader;

import java.util.ArrayList;
import java.util.List;

import static com.ice.graphics.geometry.Geometry.Binder;

/**
 * User: jason
 * Date: 13-2-22
 */
public class GroupBinder implements Binder {
    private List<Geometry.Binder> binders;

    public GroupBinder() {
        binders = new ArrayList<Geometry.Binder>();
    }

    public void addBinder(Binder binder) {
        binders.add(binder);
    }

    public void removeBinder(Binder binder) {
        binders.add(binder);
    }

    public List<Binder> getBinders() {
        return binders;
    }

    @Override
    public void bind(GeometryData data, VertexShader vsh, FragmentShader fsh) {
        for (Binder binder : binders) {
            binder.bind(data, vsh, fsh);
        }
    }

    @Override
    public void unbind(GeometryData data, VertexShader vsh, FragmentShader fsh) {
        for (Binder binder : binders) {
            binder.unbind(data, vsh, fsh);
        }
    }

}
