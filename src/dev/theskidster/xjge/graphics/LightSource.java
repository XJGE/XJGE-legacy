package dev.theskidster.xjge.graphics;

import dev.theskidster.xjge.shader.core.ShaderCore;
import dev.theskidster.xjge.util.Color;
import dev.theskidster.xjge.util.ErrorUtil;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Apr 27, 2020
 */

/**
 * Encapsulates a source of visible light at some point in space. This can be best conceptualized as a physical object that emits light such as a light bulb
 */
public class LightSource {
    
    public boolean enabled = true;
    
    private final Light light;
    private final Graphics g;
    private final Texture texture;
    private final SpriteSheet sprite;
    
    /**
     * Creates a new object that represents a source of light, such as a light bulb.
     * 
     * @param light the light data to use in the fragment shader
     */
    public LightSource(Light light) {
        this.light = light;
        
        Cell cell = new Cell(20, 20);
        
        g       = new Graphics();
        texture = new Texture("spr_engineicons.png");
        sprite  = new SpriteSheet(texture, cell);
        
        glBindTexture(GL_TEXTURE_2D, texture.handle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glBindTexture(GL_TEXTURE_2D, 0);
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(20);
            g.indices  = stack.mallocInt(6);
            
            //(vec3 position), (vec2 texCoords)
            g.vertices.put(-8) .put(8).put(0)   .put(sprite.imgWidth)    .put(sprite.imgHeight);
            g.vertices .put(8) .put(8).put(0)   .put(sprite.imgWidth * 2).put(sprite.imgHeight);
            g.vertices .put(8).put(-8).put(0)   .put(sprite.imgWidth * 2).put(sprite.imgHeight * 2);
            g.vertices.put(-8).put(-8).put(0)   .put(sprite.imgWidth)    .put(sprite.imgHeight * 2);
            
            g.indices.put(0).put(1).put(2);
            g.indices.put(2).put(3).put(0);
            
            g.vertices.flip();
            g.indices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (5 * Float.BYTES), 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, (5 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
    }
    
    /**
     * Transfers the state of the light source object provided into this one. Called automatically from 
     * {@link dev.theskidster.xjge.level.Level#addLightSource(Light) Level.addLightSource()}.
     * 
     * @param light  the light data to use in the fragment shader
     * @param source the light source object that this instance will assume
     */
    public LightSource(Light light, LightSource source) {
        this.light = light;
        
        enabled    = source.enabled;
        g          = source.g;
        texture    = source.texture;
        sprite     = source.sprite;
    }
    
    /**
     * updates the position of the light source.
     */
    public void update() {
        g.modelMatrix.translation(light.position);
    }
    
    /**
     * Renders an icon representing the position of the light source that will be visible through every object in the game world. Light sources can be made 
     * visible by using the {@link dev.theskidster.xjge.main.App#setShowLightSources(boolean) App.setShowLightSources()} method. Or at runtime through the 
     * <i>showLightSources</i> terminal command.
     * 
     * @param camPos the position of the viewports camera in the game world
     * @param camDir the direction in which the viewports camera is facing
     * @param camUp  the direction considered upwards relative to the viewports camera
     */
    public void render(Vector3f camPos, Vector3f camDir, Vector3f camUp) {
        g.modelMatrix.billboardSpherical(light.position, camPos, camUp);

        ShaderCore.use("default");

        glBindTexture(GL_TEXTURE_2D, texture.handle);
        glBindVertexArray(g.vao);

        ShaderCore.setInt("uType", 6);
        ShaderCore.setMat4("uModel", false, g.modelMatrix);
        ShaderCore.setVec3("uColor", light.ambient);

        glDrawElements(GL_TRIANGLES, g.indices.limit(), GL_UNSIGNED_INT, 0);

        ErrorUtil.checkGLError();
    }
    
    /**
     * Frees all resources used by this light source object.
     */
    public void destroy() {
        g.freeBuffers();
        texture.freeTexture();
    }
    
    public float getBrightness()  { return light.brightness; }
    public float getContrast()    { return light.contrast; }
    public Vector3f getPosition() { return light.position; }
    public Vector3f getAmbient()  { return light.ambient; }
    public Vector3f getDiffuse()  { return light.diffuse; }
    
    /**
     * Sets the intensity and range of the sources light.
     * 
     * @param brightness the intensity of the light. Should be a non-negative value between 0 and 1.
     */
    public void setBrightness(float brightness) {
        light.brightness = brightness;
    }
    
    /**
     * Controls the contrast between the ambient and diffuse colors of this sources light.
     * 
     * @param contrast the contrast of this light source. Should be a non-negative value between 0 and 1.
     */
    public void setContrast(float contrast) {
        light.contrast = contrast;
    }
    
    /**
     * Sets the position from which the light will be emitted.
     * 
     * @param position the position to set this source to
     */
    public void setPosition(Vector3f position) {
        light.position = position;
    }
    
    /**
     * Sets the ambient color that will determine the brightness and hue of nearby entities edges facing away from the light source.
     * 
     * @param color the color of the ambient lighting
     */
    public void setAmbientColor(Color color) {
        light.ambientColor = color;
        light.ambient      = Color.convert(color);
    }
    
    /**
     * Sets the diffuse color that will be refactored by nearby entities relative to this light sources location.
     * 
     * @param color the color to reflect
     */
    public void setDiffuseColor(Color color) {
        light.diffuseColor = color;
        light.diffuse      = Color.convert(color);
    }
    
}