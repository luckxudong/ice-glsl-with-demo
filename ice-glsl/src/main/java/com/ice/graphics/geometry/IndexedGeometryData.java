package com.ice.graphics.geometry;

import java.nio.Buffer;

/**
 * User: jason
 * Date: 13-2-17
 */
public class IndexedGeometryData extends GeometryData {

    private Buffer indexData;

    public IndexedGeometryData(Buffer vertexData, Buffer indexData, Descriptor formatDescriptor) {
        super(vertexData, formatDescriptor);

        this.indexData = indexData;
    }

    public Buffer getIndexData() {
        return indexData;
    }

}
