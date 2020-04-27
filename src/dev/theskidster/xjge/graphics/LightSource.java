package dev.theskidster.xjge.graphics;

import dev.theskidster.xjge.util.Color;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: Apr 27, 2020
 */

public class LightSource {
    
    private Light light;
    private Graphics g      = new Graphics();
    private Texture texture = new Texture("img_light");
    
    public LightSource(Light light, LightSource source) {
        
    }
    
    public LightSource(float brightness, float contrast, Vector3f position, Color ambientColor, Color diffuseColor) {
        
    }
    
    public void update() {
        
    }
    
    public void render(Vector3f camPos, Vector3f camDir, Vector3f camUp) {
        
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
    
    
}