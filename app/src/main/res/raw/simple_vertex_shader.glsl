uniform highp mat4 u_ModelViewMatrix;
uniform highp mat4 u_ProjectionMatrix;

attribute vec4 a_Position;
attribute vec2 a_TexCoord;

varying lowp vec2 frag_TexCoord;

void main(void) {
    frag_TexCoord = a_TexCoord;
    gl_Position = u_ProjectionMatrix * u_ModelViewMatrix * a_Position;
}