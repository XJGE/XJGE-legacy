package dev.theskidster.xjge.util;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import dev.theskidster.xjge.main.App;
import dev.theskidster.xjge.shader.core.ShaderCore;

/**
 * @author J Hoffman
 * Created: Jan 14, 2020
 */

/**
 * Abstract class which can be used to create specialized camera objects for viewports. Subclasses of this object use a combination of matrices to alter how the game 
 * world is perceived.
 * 
 * @see Camera(String)
 */
public abstract class Camera {
    
    public final String type;
    
    /**
     * The position of the camera in the game world.
     */
    public Vector3f position = new Vector3f();
    
    /**
     * The direction in which the camera is pointing.
     */
    public Vector3f direction = new Vector3f(0, 0, -1);
    
    /**
     * The direction that's considered upwards relative to the camera.
     */
    public Vector3f up = new Vector3f(0, 1, 0);
    
    public Matrix4f viewMatrix    = new Matrix4f();
    protected Matrix4f projMatrix = new Matrix4f();
    
    /**
     * Creates a new camera object that will use the projection type specified. The engine provides two projection types by default; orthographic projection and 
     * perspective projection, which are identified with "ortho" and "persp" respectively.
     * 
     * @param type the type of projection the camera will use
     */
    protected Camera(String type) {
        this.type = type;
        
        //All shaders should use model view projection matrices.
        ShaderCore.getPrograms().forEach(name -> {
            ShaderCore.use(name);
            setType(type, App.getWindowWidth(), App.getWindowHeight());
        });
    }
    
    /**
     * Updates the cameras internal logic. This typically includes supplying the cameras matrices with the appropriate values.
     */
    public abstract void update();
    
    /**
     * Used to organize calls to the graphics API made by the camera.
     */
    public abstract void render();
    
    /**
     * Sets the projection type of the camera. The engine provides both orthographic and perspective projection types by default which is sufficient enough for most 
     * cases. This functionality can be extended by the implementation to support more fringe projection types if needed.
     * <ul>
     * <li>orthographic - Objects rendered in the scene will look flat and not appear smaller regardless of physical distance.</li>
     * <li>perspective - Objects located closer to the camera will appear larger than those located further away.</li>
     * </ul>
     * 
     * @param type   the type of projection the camera will use
     * @param width  the width of the viewport this camera is attached to
     * @param height the height of the viewport this camera is attached to
     */
    public void setType(String type, int width, int height) {        
        switch(type) {
            case "ortho":
                projMatrix.setOrtho(0, width, 0, height, 0, Integer.MAX_VALUE);
                ShaderCore.setMat4("uProjection", false, projMatrix);
                break;
                
            case "persp":
                projMatrix.setPerspective((float) Math.toRadians(45), (float) width / height, 0.1f, Float.POSITIVE_INFINITY);
                ShaderCore.setMat4("uProjection", false, projMatrix);
                break;
        }
    }
    
}