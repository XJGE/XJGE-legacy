package dev.theskidster.xjge.entities;

import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;
import dev.theskidster.xjge.graphics.Graphics;
import dev.theskidster.xjge.graphics.Texture;
import dev.theskidster.xjge.shader.core.ShaderCore;
import dev.theskidster.xjge.util.ErrorUtil;

/**
 * @author J Hoffman
 * Created: Feb 13, 2020
 */

/**
 * A test entity that demonstrates the general structure of an object implementing {@link Entity}.
 */
public class EntityTest extends Entity {

    private float angle;
    
    private Graphics g = new Graphics();
    
    public EntityTest(Vector3f position) {
        super(position);
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(18);
            
            //(vec3 position), (vec3 color)
            g.vertices.put(-8).put(-8).put(0)   .put(1).put(0).put(0);
            g.vertices .put(0) .put(8).put(0)   .put(0).put(1).put(0);
            g.vertices .put(8).put(-8).put(0)   .put(0).put(0).put(1);
            
            g.vertices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (6 * Float.BYTES), 0);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, (6 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(2);
    }

    @Override
    public void update() {
        angle += 1f;
        
        g.modelMatrix.translation(position);
        g.modelMatrix.rotateY((float) Math.toRadians(-angle));
    }

    @Override
    public void render() {
        ShaderCore.use("default");
        glBindVertexArray(g.vao);
        
        ShaderCore.setMat4("uModel", false, g.modelMatrix);
        ShaderCore.setInt("uType", 3);
        
        glDrawArrays(GL_TRIANGLES, 0, 3);
        ErrorUtil.checkGLError();
    }

    @Override
    protected void destroy() {}
    
}