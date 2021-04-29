package dev.theskidster.xjge.shader.core;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import static org.lwjgl.opengl.GL20.*;
import dev.theskidster.xjge.main.App;
import dev.theskidster.xjge.main.Logger;

/**
 * @author J Hoffman
 * Created: Jan 14, 2020
 */

/**
 * These objects define how data will be processed by a single stage of the graphics pipeline by parsing a .glsl source file and providing its compiled contents to a 
 * {@link ShaderProgram}.
 */
public class ShaderSource {
    
    public final int handle;
    
    /**
     * Parses a .glsl source file and provides it as an object to be used as part of a {@link ShaderProgram}.
     * 
     * @param filename the name of the file to load. Expects the file extension to be included.
     * @param type     the rendering stage this shader code is used for. One of {@link org.lwjgl.opengl.GL30#GL_VERTEX_SHADER GL_VERTEX_SHADER}, 
     *                 {@link org.lwjgl.opengl.GL30#GL_FRAGMENT_SHADER GL_FRAGMENT_SHADER}, 
     *                 {@link org.lwjgl.opengl.GL32#GL_GEOMETRY_SHADER GL_GEOMETRY_SHADER},
     *                 {@link org.lwjgl.opengl.GL40#GL_TESS_CONTROL_SHADER GL_TESS_CONTROL_SHADER}, 
     *                 {@link org.lwjgl.opengl.GL40#GL_TESS_EVALUATION_SHADER GL_TESS_EVALUATION_SHADER}, or 
     *                 {@link org.lwjgl.opengl.GL43#GL_COMPUTE_SHADER GL_COMPUTE_SHADER}. 
     */
    public ShaderSource(String filename, int type) {
        String filepath       = "/dev/theskidster/" + App.DOMAIN + "/shader/source/" + filename;
        StringBuilder builder = new StringBuilder();
        InputStream file      = ShaderSource.class.getResourceAsStream(filepath);
        
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(file, "UTF-8"))) {
            String line;
            while((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
        } catch(Exception e) {
            Logger.logSevere("Failed to parse GLSL file: \"" + filename + "\"", e);
        }
        
        CharSequence src = builder.toString();
        
        handle = glCreateShader(type);
        glShaderSource(handle, src);
        glCompileShader(handle);
        
        if(glGetShaderi(handle, GL_COMPILE_STATUS) != GL_TRUE) {
            Logger.logSevere("Failed to compile GLSL file: \"" + filename + "\" " + glGetShaderInfoLog(handle), null);
        }
    }
    
}