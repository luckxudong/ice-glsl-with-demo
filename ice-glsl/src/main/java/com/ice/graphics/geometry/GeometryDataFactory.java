package com.ice.graphics.geometry;

import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import com.ice.graphics.shader.ShaderBinder;
import com.ice.model.Point3F;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static android.graphics.Color.*;
import static android.opengl.GLES20.*;
import static com.ice.graphics.geometry.GeometryData.Descriptor;
import static com.ice.graphics.geometry.GeometryDataFactory.Grid.normalTrianglesIndices;
import static com.ice.graphics.geometry.GeometryDataFactory.Grid.stripTrianglesIndices;
import static com.ice.graphics.shader.ShaderBinder.*;
import static com.ice.model.Constants.MAX_UNSIGNED_BYTE_VALUE;
import static com.ice.model.Constants.MAX_UNSIGNED_SHORT_VALUE;
import static com.ice.util.BufferUtil.*;

/**
 * User: jason
 * Date: 13-2-17
 */
public class GeometryDataFactory {

    public static GeometryData createTriangleData(float radius) {
        Descriptor descriptor = new Descriptor(GL_TRIANGLES, 3);

        descriptor.addComponent(POSITION, 3);
        descriptor.addComponent(COLOR, 4);
        descriptor.addComponent(ShaderBinder.TEXTURE_COORD, 2);
        descriptor.addComponent(ShaderBinder.NORMAL, 3);

        FloatBuffer data = wrap(new Triangle(radius).vertexes);

        return new GeometryData(data, descriptor);
    }

    public static GeometryData createCubeData(float length) {
        if (length == 0) throw new IllegalArgumentException("length " + length);

        Cube cube = new Cube(length);

        int[] indices = Cube.indices;

        Descriptor descriptor = new Descriptor(GL_TRIANGLES, 6 * 2 * 3);

        descriptor.addComponent(POSITION, 3);
        descriptor.addComponent(COLOR, 4);
        descriptor.addComponent(ShaderBinder.NORMAL, 3);
        descriptor.addComponent(ShaderBinder.TEXTURE_COORD, 2);

        int componentCount = 3 + 4 + 3 + 2;

        float[] data = new float[descriptor.getStride() * descriptor.getCount()];

        for (int i = 0; i < indices.length; i++) {
            int vertexIndex = indices[i];

            int index = 0;

            for (int j = 0; j < 3; j++) {
                data[i * componentCount + index++] = cube.positions[vertexIndex * 3 + j];
            }

            for (int j = 0; j < 4; j++) {
                data[i * componentCount + index++] = Cube.colors[vertexIndex * 4 + j];
            }

            for (int j = 0; j < 3; j++) {
                data[i * componentCount + index++] = Cube.normals[vertexIndex * 3 + j];
            }

            for (int j = 0; j < 2; j++) {
                data[i * componentCount + index++] = Cube.textureCoord[vertexIndex * 2 + j];
            }

        }

        FloatBuffer buffer = wrap(data);

        return new GeometryData(buffer, descriptor);
    }

    public static GeometryData createPointData(float[] point, int argb, float size) {
        Point3F point3f = new Point3F(point[0], point[1], point[2]);

        return createPointData(point3f, argb, size);
    }

    public static GeometryData createPointData(Point3F point, int argb, float size) {
        Descriptor descriptor = new Descriptor(GL_POINTS, 1);
        descriptor.addComponent(POSITION, 3);
        descriptor.addComponent(COLOR, 4);
        descriptor.addComponent(POINT_SIZE, 1);

        float red = red(argb) / 255f;
        float green = green(argb) / 255f;
        float blue = blue(argb) / 255f;
        float alpha = alpha(argb) / 255f;

        FloatBuffer floatBuffer = wrap(
                point.x, point.y, point.z,
                red, green, blue, alpha,
                size
        );

        return new GeometryData(floatBuffer, descriptor);
    }

    public static IndexedGeometryData createGridData(float width, float height, int stepX, int stepY) {
        Buffer gridTrianglesIndices = normalTrianglesIndices(stepX, stepY);

        Descriptor descriptor = new Descriptor(GL_TRIANGLES, gridTrianglesIndices.limit());

        descriptor.addComponent(POSITION, 3);
        descriptor.addComponent(ShaderBinder.TEXTURE_COORD, 2);
        descriptor.addComponent(ShaderBinder.NORMAL, 3);

        Buffer gridVertex = new Grid(width, height, stepX, stepY).vertexs;

        return new IndexedGeometryData(gridVertex, gridTrianglesIndices, descriptor);
    }

    public static IndexedGeometryData createStripGridData(float width, float height, int stepX, int stepY) {
        return createStripGridData(width, height, stepX, stepY, new PointF(-width / 2, height / 2), new Point(1, 1));
    }

    public static IndexedGeometryData createStripGridData(float width, float height, int stepX, int stepY, PointF leftTop, Point coordinateSystem) {
        Buffer stripIndices = stripTrianglesIndices(stepX, stepY);

        Descriptor descriptor = new Descriptor(GL_TRIANGLE_STRIP, stripIndices.capacity());

        descriptor.addComponent(POSITION, 3);
        descriptor.addComponent(TEXTURE_COORD, 2);
        descriptor.addComponent(NORMAL, 3);

        Buffer gridVertex = new Grid(width, height, stepX, stepY, leftTop, coordinateSystem).vertexs;

        return new IndexedGeometryData(gridVertex, stripIndices, descriptor);
    }

    /**
     * 0       1      2
     * |-------|------|
     * |       |      |
     * 3       4      5
     * |-------|------|
     * |       |      |
     * |       |      |
     * |-------|------|
     * 6       7      8
     */
    public static class Grid {
        private static final int[] SUB_INDICES = new int[4];

        private static final int[] ORDERS = {
                0, 2, 1,
                3, 1, 2
        };

        /**
         * In order CCW
         */
        public static Buffer normalTrianglesIndices(int stepX, int stepY) {
            int vertexCount = (stepX + 1) * (stepY + 1);
            int maxIndex = vertexCount - 1;
            int indicesCount = stepX * stepY * (3 + 3);

            Buffer buffer;

            if (maxIndex <= 0) {
                throw new IllegalArgumentException();
            } else if (maxIndex <= MAX_UNSIGNED_BYTE_VALUE) {
                buffer = byteBuffer(indicesCount);
                fillByteIndices(stepX, stepY, (ByteBuffer) buffer);
            } else if (maxIndex <= MAX_UNSIGNED_SHORT_VALUE) {
                buffer = shortBuffer(indicesCount);
                fillShortIndices(stepX, stepY, (ShortBuffer) buffer);
            } else {
                throw new IllegalArgumentException("too big index " + maxIndex);
            }

            if (buffer.position() != buffer.capacity()) {
                throw new IllegalStateException();
            }

            buffer.position(0);

            return buffer;
        }

        private static void fillShortIndices(int stepX, int stepY, ShortBuffer buffer) {
            for (int j = 0; j < stepY; j++) {
                for (int i = 0; i < stepX; i++) {

                    SUB_INDICES[0] = (j + 1) * (stepX + 1) + i;     //0   LeftTop
                    SUB_INDICES[1] = SUB_INDICES[0] + 1;             //1   RightTop
                    SUB_INDICES[2] = j * (stepX + 1) + i;           //2   LeftBottom
                    SUB_INDICES[3] = SUB_INDICES[2] + 1;             //3   RightBottom

                    for (int order : ORDERS) {
                        buffer.put((short) SUB_INDICES[order]);
                    }
                }
            }
        }

        private static void fillByteIndices(int stepX, int stepY, ByteBuffer buffer) {
            for (int j = 0; j < stepY; j++) {
                for (int i = 0; i < stepX; i++) {

                    SUB_INDICES[0] = (j + 1) * (stepX + 1) + i;     //0   LeftTop
                    SUB_INDICES[1] = SUB_INDICES[0] + 1;             //1   RightTop
                    SUB_INDICES[2] = j * (stepX + 1) + i;           //2   LeftBottom
                    SUB_INDICES[3] = SUB_INDICES[2] + 1;             //3   RightBottom

                    for (int order : ORDERS) {
                        buffer.put((byte) SUB_INDICES[order]);
                    }
                }
            }
        }

        /**
         * In order CCW
         */
        public static Buffer stripTrianglesIndices(int stepX, int stepY) {
            int vertexCount = (stepX + 1) * (stepY + 1);
            int maxIndex = vertexCount - 1;
            int indicesCount = vertexCount * 2 - (stepX + 1) * 2 + (stepY / 2) * 4;

            Buffer buffer;
            if (maxIndex <= 0) {
                throw new IllegalArgumentException();
            } else if (maxIndex <= MAX_UNSIGNED_BYTE_VALUE) {
                buffer = byteBuffer(indicesCount);
                fillByteStripIndices(stepX, stepY, (ByteBuffer) buffer);
            } else if (maxIndex <= MAX_UNSIGNED_SHORT_VALUE) {
                buffer = shortBuffer(indicesCount);
                fillShortStripIndices(stepX, stepY, (ShortBuffer) buffer);
            } else {
                throw new IllegalArgumentException("too big index " + maxIndex);
            }

            if (buffer.position() != buffer.capacity()) {
                throw new IllegalStateException();
            }

            buffer.position(0);

            return buffer;
        }

        private static void fillByteStripIndices(int stepX, int stepY, ByteBuffer buffer) {
            int upLineStartIndex;
            int downLineStartIndex;

            for (int i = 0; i < stepY; i++) {
                upLineStartIndex = i * (stepX + 1);
                downLineStartIndex = (i + 1) * (stepX + 1);

                if (i % 2 == 0) {
                    for (int j = 0; j <= stepX; j++) {
                        buffer.put((byte) (upLineStartIndex + j));
                        buffer.put((byte) (downLineStartIndex + j));
                    }
                } else {
                    buffer.put((byte) (upLineStartIndex + stepX));
                    buffer.put((byte) (downLineStartIndex + stepX));

                    for (int j = stepX; j >= 0; j--) {
                        buffer.put((byte) (downLineStartIndex + j));
                        buffer.put((byte) (upLineStartIndex + j));
                    }

                    buffer.put((byte) upLineStartIndex);
                    buffer.put((byte) downLineStartIndex);
                }
            }
        }

        private static void fillShortStripIndices(int stepX, int stepY, ShortBuffer buffer) {
            int upLineStartIndex;
            int downLineStartIndex;

            for (int i = 0; i < stepY; i++) {
                upLineStartIndex = i * (stepX + 1);
                downLineStartIndex = (i + 1) * (stepX + 1);

                if (i % 2 == 0) {
                    for (int j = 0; j <= stepX; j++) {
                        buffer.put((short) (upLineStartIndex + j));
                        buffer.put((short) (downLineStartIndex + j));
                    }
                } else {
                    buffer.put((short) (upLineStartIndex + stepX));
                    buffer.put((short) (downLineStartIndex + stepX));

                    for (int j = stepX; j >= 0; j--) {
                        buffer.put((short) (downLineStartIndex + j));
                        buffer.put((short) (upLineStartIndex + j));
                    }

                    buffer.put((short) upLineStartIndex);
                    buffer.put((short) downLineStartIndex);
                }
            }
        }

        private Buffer vertexs;

        private Grid(float width, float height, int stepX, int stepY) {
            this(width, height, stepX, stepY, new PointF(-width / 2, height / 2), new Point(1, 1));
        }

        private Grid(float width, float height, int stepX, int stepY, PointF leftTop, Point coordinateSystem) {
            float eachW = width / stepX;
            if (coordinateSystem.x < 0) {
                eachW *= -1;
            }

            float eachH = -height / stepY;
            if (coordinateSystem.y < 0) {
                eachH *= -1;
            }

            int size = (stepX + 1) * (stepY + 1) * (3 + 3 + 2);

            FloatBuffer buffer = floatBuffer(size);

            for (int j = 0; j < stepY + 1; j++) {
                for (int i = 0; i < stepX + 1; i++) {

                    buffer.put(leftTop.x + i * eachW);     //x
                    buffer.put(leftTop.y + j * eachH);     //y
                    buffer.put(0);                         //z

                    buffer.put(i / (float) stepX);        //u
                    buffer.put(j / (float) stepY);        //v

                    buffer.put(0);        //nx
                    buffer.put(0);        //ny
                    buffer.put(1);        //nz
                }

            }

            buffer.position(0);

            vertexs = buffer;
        }

        public Buffer getVertexs() {
            return vertexs;
        }

    }

    public static class Triangle {
        private static final float Z = 0;
        public final float[] vertexes;

        public Triangle(float radius) {
            float[] top = new float[]{0, radius};
            float[] left = new float[2];
            float[] right = new float[2];

            Matrix matrix = new Matrix();
            matrix.postRotate(120, 0, 0);
            matrix.mapPoints(left, top);

            matrix.postRotate(120, 0, 0);
            matrix.mapPoints(right, top);

            vertexes = new float[]{
                    top[0], top[1], Z, //pos
                    1.0f, 0.0f, 0.0f, 1.0f,//color
                    0.5f, 1.0f,//texture
                    0f, 0f, 1.0f, //normal

                    left[0], left[1], Z, //pos
                    0.0f, 1.0f, 0.0f, 1.0f,//color
                    0.0f, 0.0f,//texture
                    0f, 0f, 1.0f, //normal

                    right[0], right[1], Z,//pos
                    0.0f, 0.0f, 1.0f, 1.0f,//color
                    1.0f, 0.0f,//texture
                    0f, 0f, 1.0f, //normal
            };
        }

    }

    /**
     * v6----- v5
     * /|      /|
     * v1------v0|
     * | |     | |
     * | |v7---|-|v4
     * |/      |/
     * v2------v3
     */
    public static class Cube {
        float length;
        private final float[] positions;

        public Cube(float length) {
            this.length = length;

            float halfLength = length / 2;

            Point3F v0 = new Point3F(halfLength, halfLength, halfLength);
            Point3F v1 = new Point3F(-halfLength, halfLength, halfLength);
            Point3F v2 = new Point3F(-halfLength, -halfLength, halfLength);
            Point3F v3 = new Point3F(halfLength, -halfLength, halfLength);

            Point3F v4 = new Point3F(halfLength, -halfLength, -halfLength);
            Point3F v5 = new Point3F(halfLength, halfLength, -halfLength);
            Point3F v6 = new Point3F(-halfLength, halfLength, -halfLength);
            Point3F v7 = new Point3F(-halfLength, -halfLength, -halfLength);

            // X, Y, Z
            positions = new float[]{
                    v0.x, v0.y, v0.z,
                    v1.x, v1.y, v1.z,
                    v2.x, v2.y, v2.z,
                    v3.x, v3.y, v3.z,        // v0-v1-v2-v3

                    v0.x, v0.y, v0.z,
                    v3.x, v3.y, v3.z,
                    v4.x, v4.y, v4.z,
                    v5.x, v5.y, v5.z,        // v0-v3-v4-v5

                    v0.x, v0.y, v0.z,
                    v5.x, v5.y, v5.z,
                    v6.x, v6.y, v6.z,
                    v1.x, v1.y, v1.z,        // v0-v5-v6-v1

                    v1.x, v1.y, v1.z,
                    v6.x, v6.y, v6.z,
                    v7.x, v7.y, v7.z,
                    v2.x, v2.y, v2.z,    // v1-v6-v7-v2

                    v7.x, v7.y, v7.z,
                    v4.x, v4.y, v4.z,
                    v3.x, v3.y, v3.z,
                    v2.x, v2.y, v2.z,    // v7-v4-v3-v2

                    v4.x, v4.y, v4.z,
                    v7.x, v7.y, v7.z,
                    v6.x, v6.y, v6.z,
                    v5.x, v5.y, v5.z,   // v4-v7-v6-v5
            };
        }

        // R, G, B, A
        public static final float[] colors = {
                1, 1, 1, 1,
                1, 1, 0, 1,
                1, 0, 0, 1,
                1, 0, 1, 1,              // v0-v1-v2-v3
                1, 1, 1, 1,
                1, 0, 1, 1,
                0, 0, 1, 1,
                0, 1, 1, 1,              // v0-v3-v4-v5
                1, 1, 1, 1,
                0, 1, 1, 1,
                0, 1, 0, 1,
                1, 1, 0, 1,              // v0-v5-v6-v1
                1, 1, 0, 1,
                0, 1, 0, 1,
                0, 0, 0, 1,
                1, 0, 0, 1,              // v1-v6-v7-v2
                0, 0, 0, 1,
                0, 0, 1, 1,
                1, 0, 1, 1,
                1, 0, 0, 1,              // v7-v4-v3-v2
                0, 0, 1, 1,
                0, 0, 0, 1,
                0, 1, 0, 1,
                0, 1, 1, 1             // v4-v7-v6-v5
        };

        // X, Y, Z
        // The normal is used in light calculations and is a vector which points
        // orthogonal to the plane of the surface. For a cube model, the normals
        // should be orthogonal to the points of each face.
        public static final float[] normals = {
                0, 0, 1,
                0, 0, 1,
                0, 0, 1,
                0, 0, 1,             // v0-v1-v2-v3
                1, 0, 0,
                1, 0, 0,
                1, 0, 0,
                1, 0, 0,              // v0-v3-v4-v5
                0, 1, 0,
                0, 1, 0,
                0, 1, 0,
                0, 1, 0,              // v0-v5-v6-v1
                -1, 0, 0,
                -1, 0, 0,
                -1, 0, 0,
                -1, 0, 0,          // v1-v6-v7-v2
                0, -1, 0,
                0, -1, 0,
                0, -1, 0,
                0, -1, 0,         // v7-v4-v3-v2
                0, 0, -1,
                0, 0, -1,
                0, 0, -1,
                0, 0, -1        // v4-v7-v6-v5
        };

        public static final float[] textureCoord = new float[]{
                1, 1,
                0, 1,
                0, 0,
                1, 0,                    // v0-v1-v2-v3
                0, 1,
                0, 0,
                1, 0,
                1, 1,              // v0-v3-v4-v5
                1, 0,
                1, 1,
                0, 1,
                0, 0,              // v0-v5-v6-v1 (top)
                1, 1,
                0, 1,
                0, 0,
                1, 0,              // v1-v6-v7-v2
                1, 1,
                0, 1,
                0, 0,
                1, 0,              // v7-v4-v3-v2 (bottom)
                0, 0,
                1, 0,
                1, 1,
                0, 1             // v4-v7-v6-v5
        };

        public static final int[] indices = new int[]{
                0, 1, 2,
                0, 2, 3,
                4, 5, 6,
                4, 6, 7,
                8, 9, 10,
                8, 10, 11,
                12, 13, 14,
                12, 14, 15,
                16, 17, 18,
                16, 18, 19,
                20, 21, 22,
                20, 22, 23
        };
    }

}
