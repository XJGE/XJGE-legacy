package dev.theskidster.xjge.shader.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.system.MemoryStack;
import static dev.theskidster.xjge.shader.core.BufferType.*;
import dev.theskidster.xjge.main.Logger;

/**
 * @author J Hoffman
 * Created: Jan 14, 2020
 */

/**
 * Represents a completed shader program comprised of multiple {@link ShaderSource} objects that will specify how data will be processed by the GPU while this 
 * program is active.
 */
public class ShaderProgram {
    
    public final int handle;
    
    private Map<String, UniformVariable> uniforms = new HashMap<>();
    private Map<BufferType, Integer> bufferSizes  = new HashMap<>();
    
    /**
     * Creates a new shader program with the code supplied from the compiled .glsl source files.
     * 
     * @param shaders the objects representing .glsl source code describing various stages of rendering
     * @see ShaderSource
     */
    public ShaderProgram(List<ShaderSource> shaders) {
        handle = glCreateProgram();
        
        shaders.forEach(shader -> glAttachShader(handle, shader.handle));
        
        glLinkProgram(handle);
        
        bufferSizes.put(VEC2, 2);
        bufferSizes.put(VEC3, 3);
        bufferSizes.put(MAT3, 3);
        bufferSizes.put(MAT4, 4);
    }
    
    public UniformVariable getUniform(String name) { return uniforms.get(name); }
    
    /**
     * Adds a new uniform variable to the shader program.
     * 
     * @param type the data type of the uniform variable
     * @param name the name of the uniform variable exactly as it's found in the .glsl source files this program is comprised from
     */
    public void addUniform(BufferType type, String name) {
        if(glGetUniformLocation(handle, name) == -1) {
            Logger.logSevere(
                    "Uniform variable \"" + name + "\" returned -1, check " + 
                    "variable name or shader in which it is declared.",
                    null);
        } else if(uniforms.containsKey(name)) {
            Logger.logWarning("Uniform variable \"" + name + "\" already exists.", null);
        } else {
            try(MemoryStack stack = MemoryStack.stackPush()) {
                switch(type) {
                    case INT -> {
                        uniforms.put(name, new UniformVariable(
                                glGetUniformLocation(handle, name),
                                stack.mallocInt(1)));
                    }
                        
                    case FLOAT -> {
                        uniforms.put(name, new UniformVariable(
                                glGetUniformLocation(handle, name),
                                stack.mallocFloat(1)));
                    }
                        
                    case VEC2, VEC3 -> {
                        uniforms.put(name, new UniformVariable(
                                glGetUniformLocation(handle, name),
                                stack.mallocFloat(bufferSizes.get(type))));
                    }
                    
                    case MAT3, MAT4 -> {
                        uniforms.put(name, new UniformVariable(
                                glGetUniformLocation(handle, name),
                                stack.mallocFloat(bufferSizes.get(type) * Float.BYTES)));
                    }
                }
            }
        }
    }
    
}