package dev.theskidster.xjge.graphics;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import dev.theskidster.xjge.main.App;
import dev.theskidster.xjge.util.ErrorUtil;
import dev.theskidster.xjge.util.LogLevel;
import dev.theskidster.xjge.util.Logger;

/**
 * @author J Hoffman
 * Created: Jan 17, 2020
 */

/**
 * Supplies data parsed from image files and generates a new two-dimensional texture object on the GPU. RBGA encoded .png is the preferred format of this engine. 
 * OpenGL texture parameters are expected to be defined outside of this class following the texture objects initialization in the implementing classes constructor.
 */
public final class Texture {
    
    public final int handle;
    private int width;
    private int height;
    private int channels;
    
    /**
     * Generates a new texture object to be used by the graphics pipeline. If the file cannot be found it will use a fallback texture.
     * 
     * @param filename the name of the file to load. Expects the file extension to be included.
     */
    public Texture(String filename) {
        handle = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, handle);
        
        try(InputStream file = Texture.class.getResourceAsStream("/dev/theskidster/" + App.DOMAIN + "/assets/" + filename)) {
            loadTexture(file);
        } catch(Exception e) {
            Logger.setStackTrace(e);
            Logger.log(LogLevel.WARNING, "Failed to load texture: \"" + filename + "\"");
            
            loadTexture(Texture.class.getResourceAsStream("/dev/theskidster/" + App.DOMAIN + "/assets/img_null.png"));
        }
        
        ErrorUtil.checkGLError();
    }
    
    private void loadTexture(InputStream file) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            byte[] data = file.readAllBytes();
            
            ByteBuffer imageBuf  = MemoryUtil.memAlloc(data.length).put(data).flip();
            IntBuffer widthBuf   = stack.mallocInt(1);
            IntBuffer heightBuf  = stack.mallocInt(1);
            IntBuffer channelBuf = stack.mallocInt(1);
            
            ByteBuffer texture = stbi_load_from_memory(imageBuf, widthBuf, heightBuf, channelBuf, STBI_rgb_alpha);
            
            width    = widthBuf.get();
            height   = heightBuf.get();
            channels = channelBuf.get();
            
            if(texture != null) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, texture);
            } else {
                throw new NullPointerException("STBI failed to parse texture data.");
            }
            
            stbi_image_free(texture);
            MemoryUtil.memFree(imageBuf);
            
        } catch(IOException e) {
            Logger.setStackTrace(e);
            Logger.log(LogLevel.SEVERE, "Failed to load fallback texture.");
        }
    }
    
    public int getWidth()    { return width; }
    public int getHeight()   { return height; }
    public int getChannels() { return channels; }
    
    /**
     * Frees the OpenGL texture image associated with this object. Should be used when a texture is no longer needed.
     * 
     * @see dev.org.lwjgl.opengl.GL11#glDeleteTextures(int)
     */
    public void freeTexture() {
        glDeleteTextures(handle);
    }
    
}