uniform highp mat4 u_ModelViewMatrix;

attribute vec4 a_Position;

void main(void) {
    gl_Position = u_ModelViewMatrix * a_Position;
}