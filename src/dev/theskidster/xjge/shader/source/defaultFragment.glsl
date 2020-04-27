#version 330 core

//Value should match the variable of the same name in the App class.
#define MAX_LIGHTS 32

in vec2 ioTexCoords;
in vec3 ioColor;
in vec3 ioNormal;
in vec3 ioFragPos;

struct Light {
    float brightness;
    float contrast;
    vec3 position;
    vec3 ambient;
    vec3 diffuse;
};

uniform int uType;
uniform sampler2D uTexture;
uniform Light uLights[];

out vec4 ioResult;

float sharpen(float pixArray) {
    float normal  = (fract(pixArray) - 0.5) * 2.0;
    float normal2 = normal * normal;

    return floor(pixArray) + normal * pow(normal2, 2.0) / 2.0 + 0.5;
}

void makeTransparent(float a) {
    if(a == 0) discard;
}

void main() {
    switch(uType) {
        case 0: //Used for the framebuffer texture attachment.
            vec2 vRes = textureSize(uTexture, 0);
            
            ioResult = texture(uTexture, vec2(
                sharpen(ioTexCoords.x * vRes.x) / vRes.x,
                sharpen(ioTexCoords.y * vRes.y) / vRes.y
            ));
            break;

        case 1: //Used for bitmap fonts.
            makeTransparent(texture(uTexture, ioTexCoords).a);
            ioResult = texture(uTexture, ioTexCoords) * vec4(ioColor, 0);
            break;

        case 2: case 3: //Used for rectangles and testing.
            ioResult = vec4(ioColor, 0);
            break;

        case 4: //Used for icons.
            makeTransparent(texture(uTexture, ioTexCoords).a);
            ioResult = texture(uTexture, ioTexCoords);
            break;

        case 5: //Used for 3D models.
            vec3 ambient = uLights[0].ambient;

            vec3 norm     = normalize(ioNormal);
            vec3 lightDir = normalize(uLights[0].position - ioFragPos);
            float diff    = max(dot(norm, lightDir), -0.5);
            vec3 diffuse  = uLights[0].diffuse * diff;

            ioResult = texture(uTexture, ioTexCoords) * vec4(ambient + diffuse, 1.0);
            break;
    }
}