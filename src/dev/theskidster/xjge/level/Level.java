package dev.theskidster.xjge.level;

import java.util.ArrayList;
import java.util.List;
import dev.theskidster.xjge.entities.Entity;
import dev.theskidster.xjge.graphics.Light;
import dev.theskidster.xjge.graphics.LightSource;
import dev.theskidster.xjge.main.App;
import dev.theskidster.xjge.util.LogLevel;
import dev.theskidster.xjge.util.Logger;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: Jan 14, 2020
 */

/**
 * Represents a single game state.
 */
public abstract class Level {
    
    private int index;
    private int numLights = 1;
    
    /**
     * Contains every entity present in the level.
     */
    public List<Entity> entityList = new ArrayList<>();
    
    private LightSource[] lights = new LightSource[App.MAX_LIGHTS];
    
    Level() {
        lights[0] = new LightSource(Light.DAYLIGHT);
    }
    
    /**
     * Called once when the level object is initialized. Intended to be used to set application state and load any resources required by the level including entities, 
     * game world maps, etc.
     * 
     * @see dev.theskidster.xjge.main.Game#setLevel(Level)
     */
    public abstract void init();
    
    /**
     * Updates the game logic of the world and every entity object inhabiting it.
     * 
     * @see dev.theskidster.xjge.main.Game#loop()
     */
    public abstract void update();
    
    /**
     * Organizes calls to the graphics API made by various objects in the game world.
     * 
     * @see dev.theskidster.xjge.main.Game#loop()
     */
    public abstract void render(Vector3f camPos, Vector3f camDir, Vector3f camUp);
    
    /**
     * Used to free any resources used by this level before changing to a new one.
     * 
     * @see dev.theskidster.xjge.main.Game#setLevel(Level)
     */
    public abstract void exit();
    
    /**
     * Used in {@link update()} to remove <a>{@link Entity entities}</a> from the entity list safely.
     * 
     * @see dev.theskidster.xjge.entities.Entity#getRemoveRequest()
     */
    protected void resolveRemoveRequest() {
        entityList.removeIf(e -> e.getRemoveRequest());
    }
    
    protected void freeEntities() {
        entityList.forEach(e -> e.remove());
        resolveRemoveRequest();
    }
    
    protected void freeLightSources() {
        for(LightSource light : lights) {
            if(light != null && light != lights[0]) {
                light.destroy();
            }
        }
    }
    
    protected int getNumLights()              { return numLights; }
    protected LightSource[] getLightSources() { return lights; }
    
    public void addLightSource(Light light) {
        boolean search = true;
        
        for(int i = 1; search; i++) {
            if(i < App.MAX_LIGHTS) {
                if(lights[i] != null) {
                    if(!lights[i].enabled) {
                        lights[i] = new LightSource(light, lights[i]);
                        search    = false;
                    }
                } else {
                    lights[i] = new LightSource(light);
                    
                    search = false;
                }
            } else {
                //Like sound sources, we're gonna steal a light source if we have to.
                index = (index == App.MAX_LIGHTS - 1) ? 1 : index + 1;
                
                lights[index] = new LightSource(light, lights[index]);
                search        = false;
            }
        }
        
        findNumLights();
    }
    
    private void findNumLights() {
        numLights = 1;
        
        for(LightSource light : lights) {
            if(light != null) numLights++;
        }
    }
    
    protected void setWorldLight(Light light) {
        if(light != null) {
            lights[0] = new LightSource(light, lights[0]);
        } else {
            Logger.log(LogLevel.WARNING, "World light source may not be null.");
        }
    }
    
    protected void updateLightSources() {
        for(LightSource light : lights) {
            if(light != null) light.update();
        }
    }
    
    protected void renderLightSources(Vector3f camPos, Vector3f camDir, Vector3f camUp) {
        if(App.getShowLightSources()) {
            for(LightSource light : lights) {
                if(light != null) light.render(camPos, camDir, camUp);
            }
        }
    }
    
}