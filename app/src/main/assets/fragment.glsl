
precision mediump float;

uniform sampler2D uTexture;

vec4 uLightPosition = vec4(-20f, 10.0f, 20f, 1.0f);
vec4 uAmbient = vec4(0.3f, 0.3f, 0.3f, 1.0f);
vec4 uDiffuse = vec4(0.6f, 0.6f, 0.6f, 1.0f);

//data from vertex shader
varying vec4 Position;
varying vec3 Normal;
varying vec2 TexCoord;

void main(){

    float dis = length(uLightPosition - Position);
    vec4 lightVector = normalize(uLightPosition - Position);
    vec4 diffuse = uDiffuse * max(dot(vec3(lightVector), Normal), 0.0);

    vec4 color;
    color = texture2D(uTexture, TexCoord) * (diffuse + uAmbient);

    //color = vec4(0.0,1.0,0.0,1.0);
    gl_FragColor = color;

}