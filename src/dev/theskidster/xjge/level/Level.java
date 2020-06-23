package dev.theskidster.xjge.level;

import java.util.ArrayList;
import java.util.List;
import dev.theskidster.xjge.entities.Entity;
import dev.theskidster.xjge.graphics.Light;
import dev.theskidster.xjge.graphics.LightSource;
import dev.theskidster.xjge.graphics.Skybox;
import dev.theskidster.xjge.main.App;
import dev.theskidster.xjge.util.Camera;
import dev.theskidster.xjge.util.LogLevel;
import dev.theskidster.xjge.util.Logger;
import org.joml.Matrix4f;
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
     * Collection that contains every {@link Entity} currently present in the game world.
     */
    public List<Entity> entityList = new ArrayList<>();
    
    private Skybox skybox;
    private LightSource[] lights = new LightSource[App.MAX_LIGHTS];
    
    Level() {
        lights[0] = new LightSource(Light.NOON);
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
     * @param camera the {@link Camera Camera} object of the {@link dev.theskidster.xjge.main.Viewport Viewport} currently being rendered
     * @see dev.theskidster.xjge.main.Game#loop()
     */
    public abstract void render(Camera camera);
    
    /**
     * Used to free any resources used by this level before changing to a new one.
     * 
     * @see dev.theskidster.xjge.main.Game#setLevel(Level)
     */
    public abstract void exit();
    
    /**
     * Used in {@link update()} to remove {@linkplain Entity entities} from the {@linkplain entityList entity list} safely.
     * 
     * @see Entity#getRemoveRequest()
     */
    protected void resolveRemoveRequest() {
        entityList.removeIf(e -> e.getRemoveRequest());
    }
    
    /**
     * Frees all resources allocated by every entity in the level.
     */
    protected void freeEntities() {
        entityList.forEach(e -> e.remove());
        resolveRemoveRequest();
    }
    
    /**
     * Frees all resources allocated by every light source in the level.
     */
    protected void freeLightSources() {
        for(LightSource light : lights) {
            if(light != null && light != lights[0]) {
                light.destroy();
            }
        }
    }
    
    protected int getNumLights()              { return numLights; }
    protected LightSource[] getLightSources() { return lights; }
    
    /**
     * Adds a new light source to the level. If the maximum number of allowed light sources is exceeded, it will hijack an already existing one in place of a 
     * new instance.
     * 
     * @param light the light data to use in the fragment shader
     */
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
    
    /**
     * Calculates the number of light source objects currently inhabiting the level. This includes light sources which are disabled. Used to prematurely 
     * terminate a loop in the default fragment shader.
     */
    private void findNumLights() {
        numLights = 1;
        
        for(LightSource light : lights) {
            if(light != null) numLights++;
        }
    }
    
    /**
     * Sets the world light that will illuminate all entities effected by light in the current level regardless of their positions within the scene. Predefined 
     * values such as {@link Light#DAYLIGHT DAYLIGHT} are available through the {@link Light} class, otherwise custom values may be passed.
     * 
     * @param light the light data to use as the world light.
     */
    protected void setWorldLight(Light light) {
        if(light != null) {
            lights[0] = new LightSource(light, lights[0]);
        } else {
            Logger.log(LogLevel.WARNING, "World light source may not be null.");
        }
    }
    
    /**
     * Sets the {@link Skybox} to use for this level. If a skybox is used, the {@link renderSkybox(Matrix4f) renderSkybox()} method must be called first in the 
     * render method of this level.
     * 
     * @param skybox the skybox object to render
     */
    protected void setSkybox(Skybox skybox) {
        if(skybox != null) {
            this.skybox = skybox;
        } else {
            Logger.log(LogLevel.WARNING, "Level skybox may not be null.");
        }
    }
    
    /**
     * Updates each {@link LightSource} object that currently exists in the level.
     */
    protected void updateLightSources() {
        for(LightSource light : lights) {
            if(light != null) light.update();
        }
    }
    
    /**
     * <b>*For internal use only*</b> 
     * <br><br>
     * Renders the levels current {@link Skybox Skybox}. Called automatically by the {@link App} class before this levels {@link render(Camera) render()} method.
     * 
     * @param viewMatrix the view matrix of the viewport camera currently rendering the level
     */
    public void renderSkybox(Matrix4f viewMatrix) {
        if(skybox != null) skybox.render(viewMatrix);
    }
    
    /**
     * <b>*For internal use only*</b> 
     * <br><br>
     * Renders each {@link LightSource} object in the level. Light source objects can be exposed or hidden through the use of the 
     * {@link App#setShowLightSources(boolean) setShowLightSoures()} method in the App class. Or during runtime with the <i>showLightSources</i> terminal command.
     * <br><br>
     * Additional debug information that requires objects to be displayed within the game world (such as displaying collision boxes, entity names, etc.) should 
     * be included by the implementation in a similar manner to this. 
     * 
     * @param camPos the position of the viewports camera in the game world
     * @param camDir the direction in which the viewports camera is facing
     * @param camUp  the direction considered upwards relative to the viewports camera
     */
    public void renderLightSources(Vector3f camPos, Vector3f camDir, Vector3f camUp) {
        if(App.getShowLightSources()) {
            for(LightSource light : lights) {
                if(light != null) light.render(camPos, camDir, camUp);
            }
        }
    }
    
}