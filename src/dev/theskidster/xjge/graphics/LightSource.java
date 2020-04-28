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

public class LightSource {
    
    public boolean enabled = true;
    
    private Light light;
    private Graphics g;
    private Texture texture;
    
    public LightSource(Light light) {
        this.light = light;
        
        g       = new Graphics();
        texture = new Texture("img_light.png");
        
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
            g.vertices.put(-5) .put(5).put(0)   .put(0).put(0);
            g.vertices .put(5) .put(5).put(0)   .put(1).put(0);
            g.vertices .put(5).put(-5).put(0)   .put(1).put(1);
            g.vertices.put(-5).put(-5).put(0)   .put(0).put(1);
            
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
    
    public LightSource(Light light, LightSource source) {
        this.light = light;
        
        enabled = source.enabled;
        g       = source.g;
        texture = source.texture;
    }
    
    public void update() {
        g.modelMatrix.translation(light.position);
    }
    
    public void render(Vector3f camPos, Vector3f camDir, Vector3f camUp) {
        g.modelMatrix.billboardSpherical(light.position, camPos, camUp);

        ShaderCore.use("default");

        glBindTexture(GL_TEXTURE_2D, texture.handle);
        glBindVertexArray(g.vao);

        ShaderCore.setMat4("uModel", false, g.modelMatrix);
        ShaderCore.setVec3("uColor", light.ambient);
        ShaderCore.setInt("uType", 6);

        glDrawElements(GL_TRIANGLES, g.indices.limit(), GL_UNSIGNED_INT, 0);

        ErrorUtil.checkGLError();
    }
    
    public void destroy() {
        g.freeBuffers();
        texture.freeTexture();
    }
    
    public float getBrightness()  { return light.brightness; }
    public float getContrast()    { return light.contrast; }
    public Vector3f getPosition() { return light.position; }
    public Vector3f getAmbient()  { return light.ambient; }
    public Vector3f getDiffuse()  { return light.diffuse; }
    
    public void setBrightness(float brightness) {
        light.brightness = brightness;
    }
    
    public void setContrast(float contrast) {
        light.contrast = contrast;
    }
    
    public void setPosition(Vector3f position) {
        light.position = position;
    }
    
    public void setAmbientColor(Color color) {
        light.ambientColor = color;
        light.ambient      = Color.convert(color);
    }
    
    public void setDiffuseColor(Color color) {
        light.diffuseColor = color;
        light.diffuse      = Color.convert(color);
    }
    
}