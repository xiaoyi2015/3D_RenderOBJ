
uniform mat4 uMVMatrix;
uniform mat4 uMVPMatrix;

attribute vec4 aPosition;
attribute vec4 aNormal;

varying vec3 vNormal;
varying vec2 vTexel;

void main() {

    vec4 p = vec4(aPosition.xyz, 1);
    gl_Position = uMVPMatrix * p;

    vNormal = normalize((uMVMatrix * vec4(aNormal.xyz, 0)).xyz);
    vTexel = vec2(aPosition.w, aNormal.w);
}