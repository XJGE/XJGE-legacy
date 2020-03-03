package dev.theskidster.xjge.graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author J Hoffman
 * Created: Jan 14, 2020
 */

/**
 * Component object that supplies implementing objects with the following:
 * <ul>
 * <li>A vertex buffer object - As a default buffer through which the implementing class may supply its vertex data to the 
 *                              <a>{@link theskidster.xjge.shader.core graphics pipeline}</a>.</li>
 * <li>A index buffer object - To discourage data redundancy by specifying which vertices to reuse for the objects model mesh.</li>
 * <li>A vertex array object - For providing convenient access to the default vertex buffer in addition to any the implementation may define.</li>
 * </ul>
 * <p>
 * Implementing objects should define their vertex data and vertex attribute layouts in their constructors using LWJGLs memory utilities such as 
 * {@link org.lwjgl.system.MemoryUtil MemoryUtil} or {@link org.lwjgl.system.MemoryStack MemoryStack} (the later of which only if the vertex data doesn't exceed the 
 * JVMs stack size). LWJGLs {@link org.lwjgl.assimp.Assimp Assimp} binding is also available in conjunction to the previously mentioned classes to load vertex data in 
 * the form of a 3D model.
 * </p>
 */
public class Graphics {
    
    public final int vao = glGenVertexArrays();
    public final int vbo = glGenBuffers();
    public final int ibo = glGenBuffers();
    
    public FloatBuffer vertices;
    public IntBuffer indices;
    
    public Matrix4f model = new Matrix4f();
    
    /**
     * Convenience method provided to bind the default buffers defined by this class. Implementing classes are expected to define vertex attribute layouts following 
     * this call in their constructors with methods like {@link org.lwjgl.opengl.GL30#glVertexAttribPointer(int, int, int, boolean, int, java.nio.ByteBuffer) 
     * glVertexAttribPointer()}.
     */
    public void bindBuffers() {
        glBindVertexArray(vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        
        if(indices != null) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        }
    }
    
    /**
     * Convenience method which frees the default buffer objects defined by this class. Additional buffers required by the implementing object will need to be freed 
     * individually. This method should be called in conjunction with {@link org.lwjgl.system.MemoryUtil#memFree(java.nio.Buffer) MemoryUtil.memFree()} if additional 
     * data was used during the implementing objects lifetime.
     */
    public void freeBuffers() {
        glDeleteBuffers(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ibo);
    }
    
}