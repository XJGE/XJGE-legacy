package dev.theskidster.xjge.level;

import java.util.ArrayList;
import java.util.List;
import dev.theskidster.xjge.entities.Entity;

/**
 * @author J Hoffman
 * Created: Jan 14, 2020
 */

/**
 * Represents a single game state.
 */
public abstract class Level {
    
    /**
     * Contains every entity present in the level.
     */
    public List<Entity> entityList = new ArrayList<>();
    
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
    public abstract void render();
    
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
    
}