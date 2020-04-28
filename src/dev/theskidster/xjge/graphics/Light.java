package dev.theskidster.xjge.graphics;

import dev.theskidster.xjge.util.Color;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: Apr 27, 2020
 */

public class Light {
    
    public static final Light DAYLIGHT = new Light(0.59f, 0.225f, new Vector3f(-20, 40, 10), Color.WHITE, Color.WHITE);
    public static final Light BLOOD = new Light(0.59f, 0.225f, new Vector3f(-20, 40, 10), Color.RED, Color.RED);
    
    public float brightness;
    public float contrast;
    
    public Vector3f position;
    public Vector3f ambient;
    public Vector3f diffuse;
    
    public Color ambientColor;
    public Color diffuseColor;
    
    public Light(float brightness, float contrast, Vector3f position, Color ambientColor, Color diffuseColor) {
        this.brightness   = brightness;
        this.contrast     = contrast;
        this.position     = position;
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
        
        ambient = Color.convert(ambientColor);
        diffuse = Color.convert(diffuseColor);
    }
    
}