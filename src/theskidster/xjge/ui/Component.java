package theskidster.xjge.ui;

import org.joml.Vector3i;

/**
 * @author J Hoffman
 * Created: Jan 15, 2020
 */

/**
 * Abstract class which can be used to define subclasses that will comprise individual elements of a user interface.
 */
public abstract class Component {
    
    protected int width;
    protected int height;
    
    public Vector3i position = new Vector3i();
    
    /**
     * Component objects often are comprised of smaller individual elements. The placement of these elements is comparatively easier when done relative to the 
     * position of the component in which they're located. As such, it is expected that subclasses of component supply this information to their elements if needed.
     * 
     * @param position the position of the component on the screen
     * @param width    the width of the component
     * @param height   the height of the component
     */
    public Component(Vector3i position, int width, int height) {
        this.position = position;
        this.width    = width;
        this.height   = height;
    }
    
    /**
     * Updates the internal logic of a component.
     */
    public abstract void update();
    
    /**
     * Organizes calls to the graphics API made by this component.
     */
    public abstract void render();
    
    /**
     * Called automatically anytime a change to the applications viewports occurs. Using this method, components can update the positions of their elements to better
     * suit the size of the viewport.
     */
    public abstract void setSplitPosition();
    
    /**
     * Compares the z-position of this component to another to determine the order in which they're to be rendered. Components with a higher z-index will be drawn 
     * further into the background.
     * 
     * @param component the component to compare with
     * @return the difference between this components z-position and the z-position of the component provided
     */
    public int compareTo(Component component) {
        return this.position.z - component.position.z;
    }
    
}