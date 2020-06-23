package dev.theskidster.xjge.entities;

import dev.theskidster.xjge.graphics.LightSource;
import dev.theskidster.xjge.util.Camera;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: Jan 14, 2020
 */

/**
 * Abstract class which can be subclassed to represent dynamic game objects.
 */
public abstract class Entity {
    
    private boolean removeRequest;
    
    public Vector3f position;
    
    /**
     * Constructs a new Entity object. Most subclasses will likely overload this with their own arguments.
     * 
     * @param position the initial position of this entity in 3D space
     */
    public Entity(Vector3f position) {
        this.position = position;
    }
    
    /**
     * Used to organize entity game logic. Must be called exclusively through {@link dev.theskidster.xjge.level.Level#update() Level.update()}.
     */
    public abstract void update();
    
    /**
     * Used to organize calls to the graphics API made by this entity through its {@link dev.theskidster.xjge.graphics.Graphics Graphics} component if it has one. 
     * Must be called exclusively through {@link dev.theskidster.xjge.level.Level#render(Camera) Level.render()}.
     * 
     * @param camera    the {@link Camera} object of the {@link dev.theskidster.xjge.main.Viewport Viewport} currently being 
     *                  rendered
     * @param lights    the array of light source objects provided by the current level through 
     *                  {@link dev.theskidster.xjge.level.Level#getLightSources() getLightSources()}
     * @param numLights the number of lights the current level is using. This number will increase until 
     *                  {@link dev.theskidster.xjge.main.App#MAX_LIGHTS App.MAX_LIGHTS} has been reached, or the current level has been changed.
     */
    public abstract void render(Camera camera, LightSource[] lights, int numLights);
    
    /**
     * Used to free resources used by this entity once it is no longer needed. Calls like 
     * {@link dev.theskidster.xjge.graphics.Graphics#freeBuffers() Graphics.freeBuffers()} and 
     * {@link dev.theskidster.xjge.graphics.Texture#freeTexture() Texture.freeTexture()} should be made here.
     */
    protected abstract void destroy();
    
    /**
     * Finds if this entity has made a request for {@linkplain remove removal}. If it has, the entity is {@linkplain destroy destroyed} and subsequently removed 
     * from the current levels {@linkplain dev.theskidster.xjge.level.Level#entityList entity list}.
     * 
     * @return true if the entity has requested removal
     * @see dev.theskidster.xjge.level.Level#resolveRemoveRequest() 
     */
    public boolean getRemoveRequest() {
        if(removeRequest) destroy();
        return removeRequest;
    }
    
    /**
     * Requests the removal and destruction of this entity.
     */
    public void remove() { removeRequest = true; }
    
}