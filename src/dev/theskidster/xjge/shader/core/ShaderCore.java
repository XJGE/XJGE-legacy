package dev.theskidster.xjge.shader.core;

import dev.theskidster.xjge.graphics.Model;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL20.*;
import dev.theskidster.xjge.main.Logger;
import java.nio.FloatBuffer;
import java.util.List;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Jan 14, 2020
 */

/**
 * Provides methods to interact with the applications graphics pipeline. 
 */
public final class ShaderCore {
    
    private static boolean initialized;
    
    private static ShaderProgram shaderProgram;
    private static Map<String, ShaderProgram> shaderPrograms = new HashMap<>();
    
    /**
     * Supplies the shader core with {@link ShaderProgram} objects.
     * 
     * @param programs the shader programs the core will be able to use
     */
    public static void init(Map<String, ShaderProgram> programs) {
        if(!initialized) {
            shaderPrograms.putAll(programs);
            
            initialized = true;
        } else {
            Logger.logWarning("Initialization failed, shader core is already initialized.", null);
        }
    }
    
    /**
     * Changes the current active shader program the shader core will use for subsequent rendering operations. Program names supplied must correspond to those 
     * defined in {@link dev.theskidster.xjge.main.App#glInit() App.glInit()}.
     * 
     * @param name the name used to identify the shader program
     */
    public static void use(String name) {
        if(shaderPrograms.containsKey(name)) {
            shaderProgram = shaderPrograms.get(name);
            glUseProgram(shaderProgram.handle);
        } else {
            Logger.logSevere("Shader program: \"" + name + "\" not found.", null);
        }
    }
    
    /**
     * Removes the shader program from the GPU. Program names supplied must correspond to those defined in 
     * {@link dev.theskidster.xjge.main.App#glInit() App.glInit()}. Should only be called once the application is exiting. 
     * 
     * @param name the name used to identify the shader program
     */
    public static void delete(String name) {
        glDeleteProgram(shaderPrograms.get(name).handle);
    }
    
    /**
     * Flushes the GPU of all shader programs. Should only be called once the application is exiting.
     */
    public static void deleteAll() {
        shaderPrograms.forEach((name, program) -> glDeleteProgram(program.handle));
    }
    
    /**
     * Returns the names shader programs available to the shader core.
     * 
     * @return the name of every shader as a set
     */
    public static Set<String> getPrograms() {
        var programs = new TreeSet<String>();
        shaderPrograms.forEach((name, program) -> programs.add(name));
        
        return programs;
    }
    
    /**
     * Specifies the value of an int uniform variable for the currently active {@link ShaderProgram}.
     * 
     * @param name  the name of the uniform variable exactly as it appears in the .glsl file in which it's defined
     * @param value the uniform data we want to pass as a value to the graphics pipeline
     */
    public static void setInt(String name, int value) {
        glUniform1i(
                shaderProgram.getUniform(name).location, 
                value);
    }
    
    /**
     * Specifies the value of an float uniform variable for the currently active {@link ShaderProgram}.
     * 
     * @param name  the name of the uniform variable exactly as it appears in the .glsl file in which it's defined
     * @param value the uniform data we want to pass as a value to the graphics pipeline
     */
    public static void setFloat(String name, float value) {
        glUniform1f(
                shaderProgram.getUniform(name).location, 
                value);
    }
    
    /**
     * Specifies the value of a two component vector uniform variable for the currently active {@link ShaderProgram}.
     * 
     * @param name  the name of the uniform variable exactly as it appears in the .glsl file in which it's defined
     * @param value the uniform data we want to pass as a value to the graphics pipeline
     */
    public static void setVec2(String name, Vector2f value) {
        glUniform2fv(
                shaderProgram.getUniform(name).location,
                value.get(shaderProgram.getUniform(name).asFloatBuffer()));
    }
    
    /**
     * Specifies the value of a three component vector uniform variable for the currently active {@link ShaderProgram}.
     * 
     * @param name  the name of the uniform variable exactly as it appears in the .glsl file in which it's defined
     * @param value the uniform data we want to pass as a value to the graphics pipeline
     */
    public static void setVec3(String name, Vector3f value) {
        glUniform3fv(
                shaderProgram.getUniform(name).location,
                value.get(shaderProgram.getUniform(name).asFloatBuffer()));
    }
    
    /**
     * Specifies the value of a three component matrix uniform variable for the currently active {@link ShaderProgram}.
     * 
     * @param name      the name of the uniform variable exactly as it appears in the .glsl file in which it's defined
     * @param transpose indicates whether or not to transpose the matrix as the values are loaded into the uniform variable
     * @param value     the uniform data we want to pass as a value to the graphics pipeline
     */
    public static void setMat3(String name, boolean transpose, Matrix3f value) {
        glUniformMatrix3fv(
                shaderProgram.getUniform(name).location,
                transpose,
                value.get(shaderProgram.getUniform(name).asFloatBuffer()));
    }
    
    /**
     * Specifies the value of a four component matrix uniform variable for the currently active {@link ShaderProgram}.
     * 
     * @param name      the name of the uniform variable exactly as it appears in the .glsl file in which it's defined
     * @param transpose indicates whether or not to transpose the matrix as the values are loaded into the uniform variable
     * @param value     the uniform data we want to pass as a value to the graphics pipeline
     */
    public static void setMat4(String name, boolean transpose, Matrix4f value) {
        glUniformMatrix4fv(
                shaderProgram.getUniform(name).location,
                transpose,
                value.get(shaderProgram.getUniform(name).asFloatBuffer()));
    }
    
    /**
     * Array version of {@link setMat4(String, boolean, Matrix4f) setMat4()}. Allows multiple values to be passed at once. Used in {@link Model} to upload 
     * the offset matrices of {@link dev.theskidster.xjge.graphics.Bone Bone} objects.
     * 
     * @param name      the name of the uniform variable exactly as it appears in the .glsl file in which it's defined
     * @param transpose indicates whether or not to transpose the matrix as the values are loaded into the uniform variable
     * @param values    the collection of values we want to pass to the graphics pipeline
     */
    public static void setMat4(String name, boolean transpose, List<Matrix4f> values) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer matBuf = stack.mallocFloat(16 * values.size() - 1);
            
            for(int i = 0; i < values.size() - 1; i++) values.get(i).get(16 * i, matBuf);
            
            glUniformMatrix4fv(
                    shaderProgram.getUniform(name).location,
                    transpose,
                    matBuf);
        }
    }
    
}