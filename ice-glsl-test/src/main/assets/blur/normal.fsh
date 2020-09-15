precision mediump float;

uniform sampler2D u_Texture;
varying vec2 v_Texture;
  
void main()                    		
{                              
    gl_FragColor = texture2D(u_Texture, v_Texture);
}

