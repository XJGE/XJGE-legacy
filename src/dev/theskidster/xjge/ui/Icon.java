package dev.theskidster.xjge.ui;

import dev.theskidster.xjge.graphics.Cell;
import dev.theskidster.xjge.graphics.Graphics;
import dev.theskidster.xjge.graphics.SpriteSheet;
import dev.theskidster.xjge.graphics.Texture;
import dev.theskidster.xjge.shader.core.ShaderCore;
import dev.theskidster.xjge.util.ErrorUtil;
import dev.theskidster.xjge.util.LogLevel;
import dev.theskidster.xjge.util.Logger;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Mar 9, 2020
 */

/**
 * Represents a quickly comprehensible symbol included to help users better understand an interface. Icons make use of a {@link SpriteSheet} and as such, provide 
 * utilities for quickly switching between individual images as needed.
 */
public class Icon {
    
    private Graphics g = new Graphics();
    private Texture texture;
    private SpriteSheet sprite;
    private Vector2f currCell = new Vector2f();
    
    private Map<Vector2i, Vector2f> texOffsets = new HashMap<>();
    
    /**
     * Creates a new icon object which can be used to comprise part of a larger user interface.
     * 
     * @param filename the texture to use.
     * @param cell     the dimensions to split the texture by.
     */
    public Icon(String filename, Cell cell) {
        texture = new Texture(filename);
        
        glBindTexture(GL_TEXTURE_2D, texture.handle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glBindTexture(GL_TEXTURE_2D, 0);
        
        sprite = new SpriteSheet(texture, cell);
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(20);
            g.indices  = stack.mallocInt(6);
            
            //(vec3 position), (vec2 tex coords)
            g.vertices.put(0)         .put(cell.height).put(0)  .put(0)              .put(0);
            g.vertices.put(cell.width).put(cell.height).put(0)  .put(sprite.imgWidth).put(0);
            g.vertices.put(cell.width).put(0)          .put(0)  .put(sprite.imgWidth).put(sprite.imgHeight);
            g.vertices.put(0)         .put(0)          .put(0)  .put(0)              .put(sprite.imgHeight);
            
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
        
        float texPosX = 0;
        float texPosY = 0;
        int cellPosX  = 0;
        int cellPosY  = 0;
        
        for(int i = 0; i < sprite.imgCount; i++) {
            if(i % sprite.rows != 0 && i != 0) {
                texPosX += sprite.imgWidth;
                cellPosX++;
                
                texOffsets.put(new Vector2i(cellPosX, cellPosY), new Vector2f(texPosX, texPosY));
            } else if(i == 0) {
                texOffsets.put(new Vector2i(), new Vector2f());
            } else {
                texPosX  = 0;
                cellPosX = 0;
                texPosY += sprite.imgHeight;
                cellPosY++; 
                
                texOffsets.put(new Vector2i(cellPosX, cellPosY), new Vector2f(texPosX, texPosY));
            }
        }
    }
    
    /**
     * Sets the position of the icon.
     * 
     * @param position the position to set this icon to.
     */
    public void setPosition(Vector3i position) {
        g.modelMatrix.translation(new Vector3f(position.x, position.y, position.z));
    }
    
    /**
     * Alternate version of {@link setPosition(Vector3i)}.
     * 
     * @param x the x position of the icon.
     * @param y the y position of the icon.
     * @param z the z position of the icon.
     */
    public void setPosition(float x, float y, float z) {
        g.modelMatrix.translation(x, y, z);
    }
    
    /**
     * Sets the sprite image to be used by the icon.
     * 
     * @param cellX the x position of the cell as it appears in the sprite sheet.
     * @param cellY the y position of the cell as it appears in the sprite sheet.
     */
    public void setSprite(int cellX, int cellY) {
        Vector2i cell = new Vector2i(cellX, cellY);
        
        if(texOffsets.containsKey(cell)) {
            currCell = texOffsets.get(new Vector2i(cellX, cellY));
        } else {
            Logger.log(LogLevel.WARNING, 
                    "Failed to set icon sprite. The cell: (" + cellX + ", " + cellY + 
                    ") is out of bounds.");
        }
    }
    
    /**
     * Renders the icon image.
     */
    public void render() {
        ShaderCore.use("default");
        
        glBindTexture(GL_TEXTURE_2D, texture.handle);
        glBindVertexArray(g.vao);
        
        ShaderCore.setInt("uType", 4);
        ShaderCore.setMat4("uModel", false, g.modelMatrix);
        ShaderCore.setVec2("uTexCoords", currCell);
                
        glDrawElements(GL_TRIANGLES, g.indices.limit(), GL_UNSIGNED_INT, 0);
        ErrorUtil.checkGLError();
    }
    
}