package dev.theskidster.xjge.graphics;

import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: Apr 27, 2020
 */

class Light {
    
    Vector3f position;
    Vector3f ambient;
    Vector3f diffuse;
    
    Light() {
        position = new Vector3f(0, 100, 0);
        ambient  = new Vector3f(0.3f);
        diffuse  = new Vector3f(0.6f);
    }
    
}