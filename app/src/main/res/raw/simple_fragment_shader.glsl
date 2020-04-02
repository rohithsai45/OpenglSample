precision mediump float;

uniform sampler2D u_Texture;
varying lowp vec2 frag_TexCoord;
//uniform vec2 u_resolution;
uniform float u_time;

float start_time = 1000.0;
float end_time = 5000.0;
float delta = 1000.0;


void main() {
    float mixRatio = smoothstep(start_time, start_time + delta, u_time) - smoothstep(end_time - delta, end_time, u_time);
    vec4 title = texture2D(u_Texture, vec2(frag_TexCoord.x , frag_TexCoord.y));
    vec4 pixel = title + vec4(0.0, 1.0, 1.0, 0.0);
    gl_FragColor = mix(vec4(0.0, 1.0, 1.0, 0.0), pixel, mixRatio);

}
