package com.ice.graphics.shader;

import java.util.HashMap;
import java.util.Map;

import static android.opengl.GLES20.*;

/**
 * User: jason
 * Date: 13-2-17
 */
public class VertexShader extends Shader {

    private Map<String, Attribute> attributes;

    public VertexShader(String shaderSrc) {
        super(shaderSrc);

        attributes = new HashMap<String, Attribute>();
    }

    @Override
    protected int createGlShader() {
        return glCreateShader(GL_VERTEX_SHADER);
    }

    @Override
    public void onProgramLinked(Program program) {
        super.onProgramLinked(program);

        if (this.attachedProgram == program) {
            initAttributes();
        }

    }

    private void initAttributes() {
        attributes.clear();

        int[] activeAttributeSize = new int[1];

        int glProgram = attachedProgram.glRes();

        glGetProgramiv(glProgram, GL_ACTIVE_ATTRIBUTES, activeAttributeSize, 0);

        int[] lengthContainer = new int[1];
        int[] sizeContainer = new int[1];
        int[] typeContainer = new int[1];
        int nameContainerSize = 64;
        byte[] nameContainer = new byte[nameContainerSize];

        for (int glAttribute = 0; glAttribute < activeAttributeSize[0]; glAttribute++) {
            glGetActiveAttrib(glProgram, glAttribute, nameContainerSize, lengthContainer, 0, sizeContainer, 0, typeContainer, 0, nameContainer, 0);

            int length = lengthContainer[0];

            String name = new String(nameContainer, 0, length);

            attributes.put(
                    name,
                    new Attribute(glAttribute, name, typeContainer[0])
            );
        }
    }

    public void preBindAttribute(Map<String, Integer> preBindAttributes) {
        validateProgram();

        if (preBindAttributes.size() > attributeCapacity) {
            throw new IllegalStateException("Too many attribute bound ! while attribute capacity is " + attributeCapacity);
        }

        int glProgram = attachedProgram.glRes();

        for (Map.Entry<String, Integer> entry : preBindAttributes.entrySet()) {
            String attributeName = entry.getKey();
            Integer location = entry.getValue();
            glBindAttribLocation(glProgram, location, attributeName);
        }

    }

    public Attribute findAttribute(String name) {
        validateProgram();

        return attributes.get(name);
    }

    public void uploadAttribute(String name, float... values) {
        Attribute attribute = findAttribute(name);

        if (attribute == null) {
            throw new IllegalAccessError("Attribute " + name + " not exist !");
        }

        attribute.upload(values);
    }

}
