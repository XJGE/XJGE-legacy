package dev.theskidster.xjge.graphics;

import dev.theskidster.xjge.shader.core.ShaderCore;
import dev.theskidster.xjge.util.ErrorUtil;
import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;

/**
 * @author J Hoffman
 * Created: Jun 2, 2020
 */

/**
 * Enables a 3D scene (such as a {@link dev.theskidster.xjge.level.Level Level}) to exhibit a greater level of detail in its environment by projecting 
 * {@link Cubemap} texture onto the corresponding faces of a cuboid mesh.
 */
public class Skybox {
    
    private final Graphics g;
    private final Cubemap cubemap;
    
    private Matrix3f tempView = new Matrix3f();
    private Matrix4f newView  = new Matrix4f();
    
    public static final Skybox NOON = new Skybox(
            "sky_noon_top.png", 
            "sky_noon_center.png", 
            "sky_noon_bottom.png");
    
    public static final Skybox SUNSET = new Skybox(
            "sky_sunset_center.png", 
            "sky_sunset_center.png", 
            "sky_sunset_top.png",
            "sky_sunset_bottom.png", 
            "sky_sunset_center.png", 
            "sky_sunset_back.png");
    
    public static final Skybox MIDNIGHT = new Skybox(
            "sky_midnight_center.png", 
            "sky_midnight_center.png", 
            "sky_midnight_top.png",
            "sky_midnight_bottom.png", 
            "sky_midnight_front.png", 
            "sky_midnight_center.png");
    
    /**
     * Creates a new skybox using the images specified. These images should all exhibit the same width/height dimensions in pixels and may exhibit transparency. 
     * 
     * @param topFilename    the filename of the image to use for the top of the skybox
     * @param centerFilename the filename of the image to use for the sides of the skybox
     * @param bottomFilename the filename of the image to use for the bottom of the skybox
     */
    public Skybox(String topFilename, String centerFilename, String bottomFilename) {
        Map<Integer, String> images = new HashMap<>();
        
        for(int i = 0; i < 6; i++) {
            switch(i) {
                case 0 -> images.put(GL_TEXTURE_CUBE_MAP_POSITIVE_X, centerFilename);
                case 1 -> images.put(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, centerFilename);
                case 2 -> images.put(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, topFilename);
                case 3 -> images.put(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, bottomFilename);
                case 4 -> images.put(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, centerFilename);
                case 5 -> images.put(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, centerFilename);
            }
        }
        
        cubemap = new Cubemap(images);
        g       = new Graphics();
        
        genMesh();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        glEnableVertexAttribArray(0);
        
        MemoryUtil.memFree(g.vertices);
        MemoryUtil.memFree(g.indices);
    }
    
    /**
     * Overloaded version of {@link Skybox(String, String, String)}. This variant permits more variation between faces of the skybox.
     * 
     * @param rightFilename  the filename of the image to use for the right side of the skybox
     * @param leftFilename   the filename of the image to use for the left side of the skybox
     * @param topFilename    the filename of the image to use for the top of the skybox
     * @param bottomFilename the filename of the image to use for the bottom of the skybox
     * @param frontFilename  the filename of the image to use for the front of the skybox
     * @param backFilename   the filename of the image to use for the back of the skybox
     */
    public Skybox(String rightFilename, String leftFilename, String topFilename, String bottomFilename, String frontFilename, String backFilename) {
        Map<Integer, String> images = new HashMap<>();
        
        for(int i = 0; i < 6; i++) {
            switch(i) {
                case 0 -> images.put(GL_TEXTURE_CUBE_MAP_POSITIVE_X, rightFilename);
                case 1 -> images.put(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, leftFilename);
                case 2 -> images.put(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, topFilename);
                case 3 -> images.put(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, bottomFilename);
                case 4 -> images.put(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, frontFilename);
                case 5 -> images.put(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, backFilename);
            }
        }
        
        cubemap = new Cubemap(images);
        g       = new Graphics();
        
        genMesh();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        glEnableVertexAttribArray(0);
        
        MemoryUtil.memFree(g.vertices);
        MemoryUtil.memFree(g.indices);
    }
    
    /**
     * Generates the mesh of a cube to project the specified images onto.
     */
    private void genMesh() {
        g.vertices = MemoryUtil.memAllocFloat(192);
        g.indices  = MemoryUtil.memAllocInt(36);
        
        //Front
        g.vertices.put(-1) .put(1).put(-1); //0
        g.vertices .put(1) .put(1).put(-1); //1
        g.vertices .put(1).put(-1).put(-1); //2
        g.vertices.put(-1).put(-1).put(-1); //3
        
        //Back
        g.vertices .put(1) .put(1).put(1);  //4
        g.vertices.put(-1) .put(1).put(1);  //5
        g.vertices.put(-1).put(-1).put(1);  //6
        g.vertices .put(1).put(-1).put(1);  //7
        
        //Top
        g.vertices.put(-1).put(1) .put(1);  //8
        g.vertices .put(1).put(1) .put(1);  //9
        g.vertices .put(1).put(1).put(-1);  //10
        g.vertices.put(-1).put(1).put(-1);  //11
        
        //Bottom
        g.vertices.put(-1).put(-1).put(-1); //12
        g.vertices .put(1).put(-1).put(-1); //13
        g.vertices .put(1).put(-1) .put(1); //14
        g.vertices.put(-1).put(-1) .put(1); //15
        
        //Left
        g.vertices.put(-1) .put(1) .put(1); //16
        g.vertices.put(-1) .put(1).put(-1); //17
        g.vertices.put(-1).put(-1).put(-1); //18
        g.vertices.put(-1).put(-1) .put(1); //19
        
        //Right
        g.vertices.put(1) .put(1).put(-1);  //20
        g.vertices.put(1) .put(1) .put(1);  //21
        g.vertices.put(1).put(-1) .put(1);  //22
        g.vertices.put(1).put(-1).put(-1);  //23
        
        g.indices.put(0).put(1).put(2).put(2).put(3).put(0);       //Front
        g.indices.put(4).put(5).put(6).put(6).put(7).put(4);       //Back
        g.indices.put(8).put(9).put(10).put(10).put(11).put(8);    //Top
        g.indices.put(12).put(13).put(14).put(14).put(15).put(12); //Bottom
        g.indices.put(16).put(17).put(18).put(18).put(19).put(16); //Left
        g.indices.put(20).put(21).put(22).put(22).put(23).put(20); //Right
        
        g.vertices.flip();
        g.indices.flip();
        
        g.bindBuffers();
    }
    
    /**
     * Renders the skybox using the images provided through its constructor. The view matrix of the camera currently rendering the scene is included here to 
     * create the illusion of distance.
     * 
     * @param viewMatrix the view matrix of the viewport camera currently rendering the level
     */
    public void render(Matrix4f viewMatrix) {
        ShaderCore.use("default");
        
        glDepthMask(false);
        glBindTexture(GL_TEXTURE_CUBE_MAP, cubemap.handle);
        glBindVertexArray(g.vao);
        
        viewMatrix.get3x3(tempView);
        newView.set(tempView);
        
        ShaderCore.setInt("uType", 8);
        ShaderCore.setMat4("uView", false, newView);
        
        glDrawElements(GL_TRIANGLES, g.indices.limit(), GL_UNSIGNED_INT, 0);
        glDepthMask(true);
        
        ShaderCore.setMat4("uView", false, viewMatrix);
        
        ErrorUtil.checkGLError();
    }
    
    /**
     * Frees all resources used by this skybox.
     */
    public void destroy() {
        g.freeBuffers();
        cubemap.freeCubemap();
    }
    
}