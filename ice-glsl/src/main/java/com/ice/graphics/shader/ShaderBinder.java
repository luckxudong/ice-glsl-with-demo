package com.ice.graphics.shader;

/**
 * User: jason
 * Date: 13-2-18
 */
public interface ShaderBinder<T extends Shader> {

    String POSITION = "a_Position";
    String COLOR = "a_Color";
    String NORMAL = "a_Normal";
    String TEXTURE_COORD = "a_Texture";
    String POINT_SIZE = "a_PointSize";

    String M_MATRIX = "u_MMatrix";
    String M_V_MATRIX = "u_MVMatrix";
    String M_V_P_MATRIX = "u_MVPMatrix";

    void bind(T shader);

}
