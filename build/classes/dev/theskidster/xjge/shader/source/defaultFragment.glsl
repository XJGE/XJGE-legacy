#version 330 core

in vec2 ioTexCoords;
in vec3 ioColor;
in vec3 ioNormal;
in vec3 ioFragPos;

struct Light {
    vec3 position;
    vec3 ambient;
    vec3 diffuse;
};

uniform int uType;
uniform sampler2D uTexture;
uniform Light uLight;

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
            vec3 ambient = uLight.ambient;

            vec3 norm     = normalize(ioNormal);
            vec3 lightDir = normalize(uLight.position - ioFragPos);
            float diff    = max(dot(norm, lightDir), -0.5);
            vec3 diffuse  = uLight.diffuse * diff;

            ioResult = texture(uTexture, ioTexCoords) * vec4(ambient + diffuse, 1.0);
            break;
    }
}