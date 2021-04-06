package dev.theskidster.xjge.graphics;

import dev.theskidster.xjge.main.App;
import dev.theskidster.xjge.util.ErrorUtil;
import dev.theskidster.xjge.main.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Map;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.stb.STBImage.STBI_rgb_alpha;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 * @author J Hoffman
 * Created: Jun 2, 2020
 */

/**
 * A Cubemap is similar in function to a {@link Texture} object, but instead uses the data supplied through multiple image files to generate a new
 * three-dimensional texture object that can be used to map several individual images onto different faces of a mesh.
 */
class Cubemap {
    
    final int handle;
    
    /**
     * Creates a new cubemap texture object from the image files provided. Like conventional {@link Texture} objects, the cubemap will provide a fallback texture 
     * in place of missing images. 
     * <br><br>
     * <b>*All images used by the cubemap must exhibit the same width/height dimensions in pixels otherwise it will appear black.*</b>
     * 
     * @param images the names of each image file to parse texture data from
     */
    Cubemap(Map<Integer, String> images) {
        handle = glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, handle);
        
        images.forEach((target, filename) -> {
            try(InputStream file = Cubemap.class.getResourceAsStream("/dev/theskidster/" + App.DOMAIN + "/assets/" + filename)) {
                loadCubemap(target, file);
            } catch(Exception e) {
                Logger.logWarning("Failed to load cubemap image: \"" + filename + "\"", e);
                
                loadCubemap(target, Cubemap.class.getResourceAsStream("/dev/theskidster/" + App.DOMAIN + "/assets/img_null.png"));
            }
        });
        
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        
        ErrorUtil.checkGLError();
    }
    
    /**
     * Parses the data of the image file specified and uses it to generate a larger OpenGL cubemap texture object.
     * 
     * @param target the OpenGL texture target, this will be specified automatically though the constructor 
     * @param file   the file to extract texture data from
     */
    private void loadCubemap(int target, InputStream file) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            byte[] data = file.readAllBytes();
            
            ByteBuffer imageBuf  = MemoryUtil.memAlloc(data.length).put(data).flip();
            IntBuffer widthBuf   = stack.mallocInt(1);
            IntBuffer heightBuf  = stack.mallocInt(1);
            IntBuffer channelBuf = stack.mallocInt(1);
            
            ByteBuffer texture = stbi_load_from_memory(imageBuf, widthBuf, heightBuf, channelBuf, STBI_rgb_alpha);
            
            if(texture != null) {
                glTexImage2D(target, 0, GL_RGBA, widthBuf.get(), heightBuf.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, texture);
            } else {
                throw new NullPointerException("STBI failed to parse texture data.");
            }
            
            stbi_image_free(texture);
            MemoryUtil.memFree(imageBuf);
            
        } catch(IOException e) {
            Logger.logSevere("Failed to load fallback texture.", e);
        }
    }
    
    /**
     * Frees the OpenGL cubemap texture used by this object.
     * 
     * @see org.lwjgl.opengl.GL11#glDeleteTextures(int)
     */
    public void freeCubemap() {
        glDeleteTextures(handle);
    }
    
}