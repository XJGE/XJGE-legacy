package dev.theskidster.xjge.graphics;

/**
 * @author J Hoffman
 * Created: Jan 23, 2020
 */

/**
 * Data structure which represents the dimensions of a single {@link SpriteSheet} cell in pixels.
 */
public class Cell {
    
    public final int width;
    public final int height;
    
    /**
     * Defines the dimensions of a sprite sheet cell that will be used to split a {@link Texture} image evenly. A cell essentially represents any single sprite or 
     * frame of an animation.
     * 
     * @param width  the width of the cell in pixels
     * @param height the height of the cell in pixels
     */
    public Cell(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
}