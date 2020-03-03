package dev.theskidster.xjge.shader.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static org.lwjgl.opengl.GL20.*;
import dev.theskidster.xjge.main.App;
import dev.theskidster.xjge.util.LogLevel;
import dev.theskidster.xjge.util.Logger;

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
     *                 {@link org.lwjgl.opengl.GL30#GL_FRAGMENT_SHADER GL_FRAGMENT_SHADER}, {@link org.lwjgl.opengl.GL32#GL_GEOMETRY_SHADER GL_GEOMETRY_SHADER}, etc.
     */
    public ShaderSource(String filename, int type) {
        StringBuilder builder = new StringBuilder();
        InputStream file      = ShaderSource.class.getResourceAsStream("/dev/theskidster/" + App.DOMAIN + "/shader/source/" + filename);
        
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(file, "UTF-8"));) {
            String line;
            while(((line) = reader.readLine()) != null) builder.append(line).append("\n");
        } catch(IOException e) {
            Logger.setStackTrace(e);
            Logger.log(LogLevel.SEVERE, "Failed to parse GLSL file: \"" + filename +"\" " + e);
        }
        
        CharSequence src = builder.toString();
        
        handle = glCreateShader(type);
        glShaderSource(handle, src);
        glCompileShader(handle);
        
        if(glGetShaderi(handle, GL_COMPILE_STATUS) != GL_TRUE) {
            Logger.log(LogLevel.SEVERE, "Failed to compile GLSL file: \"" + filename + "\" " + glGetShaderInfoLog(handle));
        }
    }
    
}