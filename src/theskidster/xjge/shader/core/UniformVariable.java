package theskidster.xjge.shader.core;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * @author J Hoffman
 * Created: Jan 14, 2020
 */

/**
 * Provides a type-neutral structure in which uniform variable data may be stored and retrieved.
 */
class UniformVariable {
    
    final int location;
    private Buffer buffer;
    
    /**
     * Creates a new data structure to store a uniform variables data.
     * 
     * @param location the unique number used to identify the uniform variable supplied by OpenGL
     * @param buffer   a type-neutral buffer used to store the uniform data to be sent the the shader program currently active on the GPU
     */
    UniformVariable(int location, Buffer buffer) {
        this.location   = location;
        this.buffer     = buffer;
    }
    
    IntBuffer asIntBuffer()     { return (IntBuffer) buffer; }
    FloatBuffer asFloatBuffer() { return (FloatBuffer) buffer; }
    
}