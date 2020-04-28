#version 330 core

//Non-instanced attributes
layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec2 aTexCoords;
layout (location = 2) in vec3 aColor;
layout (location = 3) in vec3 aNormal;

//Instanced attributes
layout (location = 4) in vec3 aPosOffset;
layout (location = 5) in vec2 aTexOffset;
layout (location = 6) in vec3 aColOffset;

uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProjection;
uniform int uType;
uniform vec2 uTexCoords;
uniform mat3 uNormal;
uniform vec3 uColor;

out vec2 ioTexCoords;
out vec3 ioColor;
out vec3 ioNormal;
out vec3 ioFragPos;

void main() {
    switch(uType) {
        case 0: //Used for the framebuffer texture attachment.
            ioTexCoords = aTexCoords;
            gl_Position = uProjection * vec4(aPosition, 1);
            break;

        case 1: //Used for bitmap fonts.
            ioTexCoords = aTexCoords + aTexOffset;
            ioColor     = aColOffset;
            gl_Position = uProjection * vec4(aPosition + aPosOffset, 1);
            break;

        case 2: //Used for rectangles.
            ioColor     = aColor;
            gl_Position = uProjection * vec4(aPosition, 1);
            break;

        case 3: //Provided for reference and testing.
            ioColor     = aColor;
            gl_Position = uProjection * uView * uModel * vec4(aPosition, 1);
            break;

        case 4: //Used for icons.
            ioTexCoords = aTexCoords + uTexCoords;
            gl_Position = uProjection * uModel * vec4(aPosition, 1);
            break;

        case 5: //Used for 3D models.
            ioTexCoords = aTexCoords;
            ioNormal    = aNormal * uNormal;
            ioFragPos   = vec3(uModel * vec4(aPosition, 1));
            gl_Position = uProjection * uView * uModel * vec4(aPosition, 1);
            break;

        case 6: //Used for light source icons.
            ioTexCoords = aTexCoords;
            ioColor     = uColor;
            gl_Position = uProjection * uView * uModel * vec4(aPosition, 1);
            break;
    }
}