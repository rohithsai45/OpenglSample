uniform mat4 uMVPMatrix;
uniform mat4 uSTMatrix;
attribute vec4 aPosition;

attribute vec4 aTextureCoord;

varying vec2 v2TextureCoord;

void main() {
    gl_Position =   aPosition;
    v2TextureCoord = (aTextureCoord).xy;
}