package com.ice.graphics.geometry;

import android.util.Log;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.opengl.GLES20.GL_BYTE;
import static android.opengl.GLES20.GL_FLOAT;

/**
 * User: jason
 * Date: 13-2-17
 */
public class GeometryData {

    public static class Descriptor {
        private static final String TAG = "Descriptor";

        private int mode;
        private int count;
        private int stride;
        private List<Component> components;

        public Descriptor(int mode, int count) {
            this.mode = mode;
            this.count = count;
            components = new ArrayList<Component>();
        }

        public void namespace(Map<String, String> nameMap) {
            if (nameMap.size() > 0) {

                for (Component component : components) {
                    if (!nameMap.containsKey(component.name)) {
                        Log.w(TAG, "transformNames " + component.name + " not in map !");
                    }
                    else {
                        component.name = nameMap.get(component.name);
                    }
                }
            }

        }

        public Descriptor deepNamespace(Map<String, String> nameMap) {
            Descriptor clone = deepClone();

            clone.namespace(nameMap);

            return clone;
        }

        public void addComponent(String name, int dimension) {
            addComponent(name, dimension, false);
        }

        public void addComponent(String name, int dimension, boolean normalized) {
            for (Component component : components) {
                if (component.name.equals(name)) {
                    throw new IllegalStateException("Same name included !");
                }
            }

            components.add(new Component(name, dimension, normalized));

            update();
        }

        public List<Component> getComponents() {
            return components;
        }

        public Component find(String name) {
            for (Component component : components) {
                if (component.name.equals(name)) {
                    return component;
                }
            }

            return null;
        }

        public int getStride() {
            return stride;
        }

        public int getCount() {
            return count;
        }

        public int getMode() {
            return mode;
        }

        public void setMode(int mode) {
            this.mode = mode;
        }

        private void update() {
            int offset = 0;

            for (Component component : components) {
                component.offset = offset;

                int bytes = component.type == GL_BYTE ? 1 : 4;

                offset += component.dimension * bytes;
            }

            stride = offset;
        }

        public Descriptor deepClone() {
            Descriptor clone = new Descriptor(mode, count);
            clone.stride = stride;
            clone.components.addAll(components);
            return clone;
        }

    }

    public static class Component {
        public String name;
        public int dimension;
        public int type = GL_FLOAT;
        public boolean normalized;
        public int offset;

        public Component(String name, int dimension) {
            this(name, dimension, GL_FLOAT, false);
        }

        public Component(String name, int dimension, boolean normalized) {
            this(name, dimension, GL_FLOAT, normalized);
        }

        public Component(String name, int dimension, int type, boolean normalized) {
            this.name = name;
            this.dimension = dimension;
            this.type = type;
            this.normalized = normalized;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Component)) return false;

            Component component = (Component) o;

            if (name != null ? !name.equals(component.name) : component.name != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }

    }

    private Buffer vertexData;
    private Descriptor formatDescriptor;

    public GeometryData(Buffer vertexData, Descriptor formatDescriptor) {
        this.vertexData = vertexData;
        this.formatDescriptor = formatDescriptor;
    }

    public Buffer getVertexData() {
        return vertexData;
    }

    public Descriptor getFormatDescriptor() {
        return formatDescriptor;
    }

}
