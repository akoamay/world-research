#version 330 core

in vec3 Loc;
in mat4 VMat;
in vec3 Prop;
in vec3 Norm;

layout(location=0) out vec4 outcol;
layout(location=1) out vec4 normalBright;

float rnd( vec2 p ){
    return fract(sin(dot(p.xy,vec2(12.9898,78.233)))*43758.5453);
}

void main(){
    /*
    vec3 light=vec3(0.0,1.0,0.0);
    vec3 NV = Norm*mat3(VMat);
    float diff = clamp( dot(NV,light), 0.0, 1.0 );
    outcol = vec4(Prop.xyz,1.0)*vec4(vec3(diff),1.0);
    */

/*
    vec3 light=vec3(0.0,1.0,0.0);
    mat4 invMat = inverse(VMat);
    vec3 invL = normalize(light*mat3(invMat));
    float diff = clamp( dot(Norm,invL), 0.1, 1.0 );
    outcol = vec4(Prop.xyz,1.0)*vec4(vec3(diff),1.0);
    */
    vec3 light=vec3(0.0,1.0,0.0);
    mat4 invMat = transpose(inverse(VMat));
    vec3 invL = normalize(light*mat3(invMat));
    float diff = clamp( dot(Norm,invL), 0.1, 1.0 );

    float depth = gl_FragCoord.z;
//    outcol = vec4(Prop.xyz,1.0)*vec4(vec3(diff)*depth,1.0);

    outcol = vec4( vec3(depth), 1.0 );


}
