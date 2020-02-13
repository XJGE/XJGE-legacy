package theskidster.xjge.ui;

import org.joml.Vector3i;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;
import theskidster.xjge.graphics.Graphics;
import theskidster.xjge.shader.core.ShaderCore;
import theskidster.xjge.util.Color;
import theskidster.xjge.util.ErrorUtil;

/**
 * @author J Hoffman
 * Created: Jan 24, 2020
 */

/**
 * Used to batch render rectangles. A batch should be done inside of a components render method like so:
 * <blockquote><pre>
 * batchStart();
 *     drawRectangle()
 *     drawRectangle()
 *     drawRectangle()
 *     ...
 * batchEnd();
 * </pre></blockquote>
 */
public class RectangleBatch {
    
    private int numVertices;
    private int numIndices;
    
    private Graphics g = new Graphics();
    
    /**
     * Establishes a system through which vertex data may be streamed to draw rectangles. 
     * 
     * @param numRectangles the maximum number of rectangles this batch is allowed to draw
     */
    public RectangleBatch(int numRectangles) {
        g.vertices = MemoryUtil.memAllocFloat(24 * numRectangles);
        g.indices  = MemoryUtil.memAllocInt(6 * numRectangles);
        
        glBindVertexArray(g.vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, g.vbo);
        glBufferData(GL_ARRAY_BUFFER, g.vertices.capacity() * Float.BYTES, GL_DYNAMIC_DRAW);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, g.ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, g.indices.capacity() * Float.BYTES, GL_DYNAMIC_DRAW);
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (6 * Float.BYTES), 0);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, (6 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(2);
    }
    
    /**
     * Renders every rectangle provided to the batch.
     */
    private void render() {
        ShaderCore.use("default");
        
        glBindVertexArray(g.vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, g.vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, g.vertices);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, g.ibo);
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, g.indices);
        
        ShaderCore.setInt("uType", 2);
        
        glDrawElements(GL_TRIANGLES, g.indices.limit() * (numVertices / 24), GL_UNSIGNED_INT, 0);
        ErrorUtil.checkGLError();
    }
    
    /**
     * Begins the batch rendering process.
     */
    public void batchStart() {
        numVertices = 0;
        numIndices  = 0;
    }
    
    /**
     * Finalizes the data and sends it to the GPU to be rendered.
     */
    public void batchEnd() {
        if(numVertices > 0) {
            g.vertices.flip();
            g.indices.flip();
            
            render();
            
            g.vertices.clear();
            g.indices.clear();
            
            numVertices = 0;
            numIndices  = 0;
        }
    }
    
    /**
     * Draws a rectangle using the data provided. the position shape will be drawn starts from its bottom left corner.
     * 
     * @param x      the x position to draw the rectangle from
     * @param y      the y position to draw the rectangle from
     * @param width  the width of the rectangle
     * @param height the height of the rectangle
     * @param color  the color to draw the rectangle
     */
    public void drawRectangle(float x, float y, float width, float height, Color color) {
        int startIndex = (numVertices / 24) * Float.BYTES;
        
        g.vertices.put(x)        .put(y + height).put(-100).put(color.r).put(color.g).put(color.b);
        g.vertices.put(x + width).put(y + height).put(-100).put(color.r).put(color.g).put(color.b);
        g.vertices.put(x + width).put(y)         .put(-100).put(color.r).put(color.g).put(color.b);
        g.vertices.put(x)        .put(y)         .put(-100).put(color.r).put(color.g).put(color.b);
        
        g.indices.put(startIndex)    .put(startIndex + 1).put(startIndex + 2);
        g.indices.put(startIndex + 3).put(startIndex + 2).put(startIndex);
        
        numVertices += 24;
        numIndices  += 6;
    }
    
    /**
     * Draws a rectangle using the data provided.
     * 
     * @param pos    the position to draw the rectangle from. Starts from the shapes bottom left corner.
     * @param width  the width of the rectangle
     * @param height the height of the rectangle
     * @param color  the color to draw the rectangle
     */
    public void drawRectangle(Vector3i pos, float width, float height, Color color) {
        drawRectangle(pos.x, pos.y, width, height, color);
    }
    
    /**
     * Draws a rectangle using the data provided.
     * 
     * @param rectangle the rectangle to draw
     */
    public void drawRectangle(Rectangle rectangle) {
        drawRectangle(
                rectangle.position.x,
                rectangle.position.y,
                rectangle.width,
                rectangle.height,
                rectangle.color);
    }
    
    /**
     * Frees the memory allocated by the rectangle batch.
     */
    public void destroy() {
        MemoryUtil.memFree(g.vertices);
        MemoryUtil.memFree(g.indices);
        g.freeBuffers();
    }
    
}