package dev.theskidster.xjge.graphics;

import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Jan 23, 2020
 */

/**
 * Using various constructors, instance objects will define a set of vertex data as needed by the class in which it is to be used. After sending this data to the GPU 
 * the implementing class can use it to draw multiple instances of its vertex data with little performance overhead. This is particularly advantageous whenever game 
 * objects are present in large numbers with little variation (trees, rocks, etc). Variation among instances can be achieved through supplying additional vertex 
 * attributes.
 */
public class Instance {
    
    public final int vao;
    
    public final Texture texture;
    public final Cell cell;
    public final SpriteSheet sprite;
   
     private Graphics g = new Graphics();
    
    /**
     * Constructs a rectangular instance that uses a sprite image as its texture. Useful for text, tiles, etc.
     * 
     * @param texture    the texture to be used as a sprite sheet
     * @param cell       the dimensions of the cells the texture will be split by
     * @param fromCenter determines whether the instance will be offset by its center or bottom left corner relative to its position
     * @see theskidster.xjge.ui.BitmapFont
     */
    public Instance(Texture texture, Cell cell, boolean fromCenter) {
        this.texture = texture;
        this.cell    = cell;
        
        glBindTexture(GL_TEXTURE_2D, texture.handle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glBindTexture(GL_TEXTURE_2D, 0);
        
        vao    = g.vao;
        sprite = new SpriteSheet(texture, cell);
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(20);
            g.indices  = stack.mallocInt(6);
            
            if(fromCenter) {
                float width  = cell.width / 2;
                float height = cell.height / 2;
                
                //        Position                          TexCoords
                g.vertices.put(-width) .put(height).put(0)  .put(0)               .put(0);
                g.vertices .put(width) .put(height).put(0)  .put(sprite.cellWidth).put(0);
                g.vertices .put(width).put(-height).put(0)  .put(sprite.cellWidth).put(sprite.cellHeight);
                g.vertices.put(-width).put(-height).put(0)  .put(0)               .put(sprite.cellHeight);
            } else {
                //        Position                                  TexCoords
                g.vertices.put(0)         .put(cell.height).put(0)  .put(0)               .put(0);
                g.vertices.put(cell.width).put(cell.height).put(0)  .put(sprite.cellWidth).put(0);
                g.vertices.put(cell.width).put(0)          .put(0)  .put(sprite.cellWidth).put(sprite.cellHeight);
                g.vertices.put(0)         .put(0)          .put(0)  .put(0)               .put(sprite.cellHeight);
            }
            
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
    
}