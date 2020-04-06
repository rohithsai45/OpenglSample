#extension GL_OES_EGL_image_external : require

precision mediump float;// highp here doesn't seem to matter

varying vec2 v2TextureCoord;
uniform samplerExternalOES sTexture;

uniform vec2 u_resolution;
uniform float u_time;

void main() {
    vec4 pixel  = texture2D(sTexture, v2TextureCoord);
    gl_FragColor = pixel;
}