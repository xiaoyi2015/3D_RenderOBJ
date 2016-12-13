
uniform mat4 mvMatrix;
uniform mat4 mvpMatrix;

attribute vec4 position;
attribute vec4 normal;


varying vec3 Normal;
varying vec2 TexCoord;
varying vec4 Position;

void main() {
    vec4 p = vec4(position.xyz, 1);

    //根据总变换矩阵计算此次绘制此顶点位置
    gl_Position = mvpMatrix*p;

    Normal = normalize((mvMatrix * vec4(normal.xyz, 0)).xyz);
    Position = mvMatrix * p;
    TexCoord = vec2(position.w, normal.w);
}