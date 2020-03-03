package dev.theskidster.xjge.graphics;

/**
 * @author J Hoffman
 * Created: Jan 23, 2020
 */

/**
 * A sprite sheet consists of a single {@link Texture} image split into smaller sub-images (known as sprites) according to the dimensions specified by its 
 * {@link Cell}. More specifically this object provides the data generated from that process such as the amount of rows an columns the texture has been evenly divided 
 * into, the total number of sprite images, and the dimensions of each sprite in a format understood by the graphics pipeline.
 */
public class SpriteSheet {
    
    public final int rows;
    public final int cols;
    public final int cellCount;
    
    /**
     * Used in conjunction with a cells location on a sprite sheet to offset texture coordinates inside a shader.
     * 
     * @see theskidster.xjge.ui.BitmapFont#posOffsets
     * @see theskidster.xjge.ui.BitmapFont#init(Texture, Cell) 
     */
    public final float cellWidth, cellHeight;
    
    /**
     * Creates a new sprite sheet and supplies the data it generates from the texture and 
     * cell provided. The width and height of the texture should be evenly 
     * divisible by the cell dimensions.
     * 
     * @param texture the texture to use.
     * @param cell    the dimensions to split the texture by.
     */
    public SpriteSheet(Texture texture, Cell cell) {
        cellWidth  = (float) cell.width / texture.getWidth();
        cellHeight = (float) cell.height / texture.getHeight();
        rows       = texture.getWidth() / cell.width;
        cols       = texture.getHeight() / cell.height;
        cellCount  = rows * cols;
    }
    
}