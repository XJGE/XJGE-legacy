package dev.theskidster.xjge.graphics;

import java.util.HashMap;
import java.util.Map;
import org.joml.Vector2f;
import org.joml.Vector2i;

/**
 * @author J Hoffman
 * Created: Jan 23, 2020
 */

/**
 * A sprite sheet consists of a single {@link Texture} image split into smaller sub-images according to the pixel dimensions specified by its {@link Cell}. More 
 * specifically, this object provides data generated from this process that can be used by other parts of the engine. 
 * <p>The data provided by this class includes: </p>
 * <ul>
 * <li>The number of rows and columns the texture was divided into.</li>
 * <li>The number of sub-images this sheet contains.</li>
 * <li>The dimensions of a single sub-image as texture coordinates.</li>
 * <li>A mapping of every sub-images cell positions to texture coordinates.</li>
 * </ul>
 */
public class SpriteSheet {
    
    public final int rows;
    public final int columns;
    public final int imgCount;
    
    /**
     * The dimensions (as texture coordinates) of a single sub-image.
     * 
     * @see texOffsets
     */
    public final float imgWidth, imgHeight;
    
    public Vector2f texCoords = new Vector2f();
    
    /**
     * A collection sprite sheet data that joins sub-image cell locations to their corresponding texture coordinates.
     */
    public Map<Vector2i, Vector2f> imgOffsets = new HashMap<>();
    
    /**
     * Creates a new sprite sheet and supplies the data it generates from the texture and 
     * cell provided. The width and height of the texture should be evenly 
     * divisible by the cell dimensions.
     * 
     * @param texture the texture to use.
     * @param cell    the dimensions to split the texture by.
     */
    public SpriteSheet(Texture texture, Cell cell) {
        imgWidth  = (float) cell.width / texture.getWidth();
        imgHeight = (float) cell.height / texture.getHeight();
        rows      = texture.getWidth() / cell.width;
        columns   = texture.getHeight() / cell.height;
        imgCount  = rows * columns;
        
        for(int x = 0; x < rows; x++) {
            for(int y = 0; y < columns; y++) {
                imgOffsets.put(new Vector2i(x, y), new Vector2f(imgWidth * x, imgHeight * y));
            }
        }
    }
    
}