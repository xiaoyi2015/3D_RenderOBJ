
precision mediump float;

uniform sampler2D uTexture;

uniform vec3 uDiffuse;
uniform vec3 uSpecular;

varying vec3 vNormal;
varying vec2 vTexel;

void main(){

        // Material
        vec3 ka = vec3(0.05);
        vec3 kd = uDiffuse;
        vec3 ks = uSpecular;
        float alpha = 1.0;

        // Light
        vec3 ia = vec3(1.0);
        vec3 id = vec3(1.0);
        vec3 is = vec3(1.0);

        // Vectors
        vec3 L = normalize(vec3(1.0, 1.0, 1.0));
        vec3 N = normalize(vNormal);
        vec3 V = normalize(vec3(0.0, 0.0, 1.0));
        vec3 R = reflect(L, N);

        // Illumination factors
        float df = max(0.0, dot(L, N));
        float sf = pow(max(0.0, dot(R, V)), alpha);

        // Phong reflection equation
        vec3 Ip = ka*ia + kd*id*df + ks*is*sf;

        vec2 tex = vTexel;
        while(tex.x < 0.0) tex.x = tex.x + 1.0;
        while(tex.x > 1.0) tex.x = tex.x - 1.0;
        while(tex.y < 0.0) tex.y = tex.y + 1.0;
        while(tex.y > 1.0) tex.y = tex.y - 1.0;

        // Decal
        vec4 decal = texture2D(uTexture, tex);

        // Surface
        vec3 surface;
        if(decal.a > 0.0)
            surface = decal.rgb*df + decal.rgb*sf;
        else
            surface = Ip;

        gl_FragColor = vec4(surface, 1.0);

}