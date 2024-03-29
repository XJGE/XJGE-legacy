#version 330 core

//Value should match the variable of the same name in the App class.
#define MAX_LIGHTS 32

in vec2 ioTexCoords;
in vec3 ioColor;
in vec3 ioNormal;
in vec3 ioFragPos;
in vec3 ioSkyTexCoords;

struct Light {
    float brightness;
    float contrast;
    vec3 position;
    vec3 ambient;
    vec3 diffuse;
};

uniform int uType;
uniform sampler2D uTexture;
uniform samplerCube uSkyTexture;
uniform Light uLights[MAX_LIGHTS];
uniform int uNumLights;

out vec4 ioResult;

/**
 * Enables the framebuffer texture attachments to better suit unorthodox screen 
 * resolutions while maintaining a consistent pixelated look.
 */
float sharpen(float pixArray) {
    float normal  = (fract(pixArray) - 0.5) * 2.0;
    float normal2 = normal * normal;

    return floor(pixArray) + normal * pow(normal2, 2.0) / 2.0 + 0.5;
}

/**
 * Allows texture transparency by discarding the fragments produced by its alpha channel.
 */
void makeTransparent(float a) {
    if(a == 0) discard;
}

/**
 * Calculates the output of the single world light all entities using models will be 
 * illuminated by.
 */
vec3 calcWorldLight(Light light, vec3 normal) {
    vec3 direction = normalize(light.position);
    float diff     = max(dot(normal, direction), -light.contrast);
    vec3 diffuse   = diff * light.ambient * light.diffuse;

    return (light.ambient + diffuse) * light.brightness;
}

/**
 * Calculates the output of individual point lights located throughout the gameworld. 
 * These are attenuated to have a sphere of influence on nearby models that decreases 
 * over distance.
 */
vec3 calcPointLight(Light light, vec3 normal, vec3 fragPos) {
    vec3 ambient = light.ambient;

    vec3 direction = normalize(light.position - ioFragPos);
    float diff     = max(dot(normal, direction), -light.contrast);
    vec3 diffuse   = diff * light.diffuse;

    float linear    = 0.0014f / light.brightness;
    float quadratic = 0.000007f / light.brightness;
    float dist      = length(light.position - ioFragPos);
    float attenuate = 1.0f / (1.0f + linear * dist + quadratic * (dist * dist));

    ambient *= attenuate;
    diffuse *= attenuate;

    return (ambient + diffuse) * light.brightness;
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

        case 2: case 3: case 9: //Used for rectangles, polygons, and testing.
            ioResult = vec4(ioColor, 0);
            break;

        case 4: case 7: //Used for icons and animated 2D sprites.
            makeTransparent(texture(uTexture, ioTexCoords).a);
            ioResult = texture(uTexture, ioTexCoords);
            break;

        case 5: //Used for 3D models.
            vec3 normal = normalize(ioNormal);
            vec3 result = calcWorldLight(uLights[0], normal);

            for(int i = 1; i < uNumLights; i++) {
                result += calcPointLight(uLights[i], normal, ioFragPos);
            }
            
            makeTransparent(texture(uTexture, ioTexCoords).a);
            ioResult = texture(uTexture, ioTexCoords) * vec4(result * ioColor, 1.0);
            break;

        case 6: //Used for light source icons.
            makeTransparent(texture(uTexture, ioTexCoords).a);
            ioResult = texture(uTexture, ioTexCoords) * vec4(ioColor, 1);
            break;

        case 8: //Used for skyboxes.
            makeTransparent(texture(uSkyTexture, ioSkyTexCoords).a);
            ioResult = texture(uSkyTexture, ioSkyTexCoords);
            break;
    }
}