package dev.theskidster.xjge.ui;

import dev.theskidster.xjge.graphics.Graphics;
import dev.theskidster.xjge.shader.core.ShaderCore;
import dev.theskidster.xjge.util.Color;
import dev.theskidster.xjge.util.ErrorUtil;
import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Nov 29, 2020
 */

/**
 * Objects of this class can be used as part of a larger {@linkplain Component UI component} to represent a regular 2D shape such as a pentagon, hexagon, or 
 * circle by utilizing the number of sides specified through its {@linkplain Polygon(int, float, Color, Vector2f) constructor}.
 */
public final class Polygon {
    
    private final int numSides;
    
    private final Vector3f color;
    private final Graphics g = new Graphics();
    
    /**
     * Creates a new n-sided polygon object which can be used to represent regular shapes and circles. 
     * 
     * @param numSides the number of sides this shape will exhibit, (5 for a pentagon, 6 for a hexagon, etc.)
     * @param radius   the radius used to determine the size of the polygon
     * @param color    the color in which this shape will initially be rendered
     * @param position the position at which the shape will be rendered initially
     */
    public Polygon(int numSides, float radius, Color color, Vector2f position) {
        this.numSides = (numSides < 3) ? 3 : numSides;
        this.color    = new Vector3f(color.r, color.g, color.b);
        
        float doublePI = (float) (Math.PI * 2f);
        float[] vertX  = new float[this.numSides];
        float[] vertY  = new float[this.numSides];
        
        for(int v = 0; v < this.numSides; v++) {
            vertX[v] = (float) (radius * Math.cos(v * doublePI / this.numSides));
            vertY[v] = (float) (radius * Math.sin(v * doublePI / this.numSides));
        }
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(this.numSides * 3);
            for(int v = 0; v < this.numSides; v++) g.vertices.put(vertX[v]).put(vertY[v]).put(-100);
            g.vertices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        glEnableVertexAttribArray(0);
        
        setPosition(position);
    }
    
    /**
     * Translates the polygon to the position specified. NOTE: polygons are positioned around their centerpoints.
     * 
     * @param position the position where the shape will be places
     */
    public void setPosition(Vector2f position) {
        g.modelMatrix.translation(position.x, position.y, -100);
    }
    
    /**
     * Alternate version of {@link setPosition(Vector2f)}, provided out of convenience.
     * 
     * @param x the point along the x-axis at which this shape will be positioned
     * @param y the point along the y-axis at which this shape will be positioned
     */
    public void setPosition(float x, float y) {
        g.modelMatrix.translation(x, y, -100);
    }
    
    /**
     * Sets the color of this polygon.
     * 
     * @param color the color to draw this shape in
     */
    public void setColor(Color color) {
        this.color.set(color.r, color.g, color.b);
    }
    
    /**
     * Rotates the polygon according to the angle specified.
     * 
     * @param angle the value indicating the rotation of the shape
     */
    public void rotate(float angle) {
        g.modelMatrix.rotateZ((float) Math.toRadians(angle * -1f));
    }
    
    /**
     * Draws the polygon using the data specified by the constructor.
     * 
     * @param fill if true, the shape will be filled with the color of its edges
     */
    public void render(boolean fill) {
        ShaderCore.use("default");
        
        glBindVertexArray(g.vao);
        
        ShaderCore.setInt("uType", 9);
        ShaderCore.setMat4("uModel", false, g.modelMatrix);
        ShaderCore.setVec3("uColor", color);
        
        glDrawArrays((fill) ? GL_TRIANGLE_FAN : GL_LINE_LOOP, 0, numSides);
        
        ErrorUtil.checkGLError();
    }
    
    /**
     * Frees the memory allocated by the polygon object.
     */
    public void destroy() {
        g.freeBuffers();
    }
    
}