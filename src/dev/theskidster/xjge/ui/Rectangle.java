package dev.theskidster.xjge.ui;

import org.joml.Vector2f;
import dev.theskidster.xjge.util.Color;

/**
 * @author J Hoffman
 * Created: Jan 24, 2020
 */

/**
 * Data structure used by {@link RectangleBatch} objects to draw single color rectangles.
 */
public class Rectangle {
    
    public float width;
    public float height;
    
    public Vector2f position;
    public Color color;
    
    public Rectangle(Vector2f position, float width, float height, Color color) {
        this.position = new Vector2f(position);
        this.width    = width;
        this.height   = height;
        this.color    = color;
    }
    
}