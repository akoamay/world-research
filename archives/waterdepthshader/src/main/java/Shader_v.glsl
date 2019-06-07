#version 330 core

uniform mat4 vpMat;
uniform mat4 modelMat;

layout(location=0)in vec3 loc;
layout(location=1)in vec3 prop;
layout(location=2)in vec3 nrm;

out vec3 Loc;
out mat4 VMat;
out vec3 Prop;
out vec3 Norm;

void main(){
    //float normal = round(prop/1000);
    //Prop = vec2(prop-normal*1000,normal);
    Prop = prop;
    Loc =  loc;
    Norm = nrm;
    vec4 modelLoc = modelMat * vec4(loc.xyz,1.0);
    gl_Position = vpMat*modelLoc;
    VMat = modelMat;

//    ModelLoc = vec3(modelLoc.xyz);
}