

uniform mat4 u_MVPMatrix;
attribute vec4 a_Position;
attribute vec4 a_Color;
attribute float a_PointSize;

varying vec4 v_Color;

void main()                    
{
    v_Color=a_Color;
	gl_Position = u_MVPMatrix * a_Position;   
    gl_PointSize = a_PointSize;
}                              