package dev.theskidster.xjge.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Vector3i;
import dev.theskidster.xjge.hardware.InputDevice;
import dev.theskidster.xjge.puppets.Command;
import dev.theskidster.xjge.puppets.Puppet;

/**
 * @author J Hoffman
 * Created: Jan 23, 2020
 */

/**
 * Abstract class which can be used to create navigable menu systems.
 */
public abstract class Menu extends Component {
    
    public int choice;
    protected int count;

    public Puppet puppet = new Puppet(this);
    
    protected Map<String, SubMenu> menus = new HashMap<>();
    
    /**
     * A smaller menu object which can be populated with {@linkplain Option options} and used to comprise a single node of a larger menu hierarchy.
     */
    protected class SubMenu {
        public List<Option> options = new ArrayList<>();

        public SubMenu(List<Option> options) {
            this.options = options;
        }
    }
    
    /**
     * Data structure that represents a single option in a {@link SubMenu}. Can store additional information as needed through its "value" field.
     */
    protected class Option {
        public String name;
        public Object value;
        
        public Option(String name) {
            this.name = name;
        }
    }
    
    /**
     * Creates a new menu component object.
     * 
     * @param position the position of the component on the screen
     * @param width    the width of the component
     * @param height   the height of the component
     * @see Component#Component(Vector3i, int, int) 
     */
    public Menu(Vector3i position, int width, int height) {
        super(position, width, height);
    }
    
    @Override
    public abstract void update();
    @Override
    public abstract void render();
    @Override
    public abstract void setSplitPosition();
    
    /**
     * Used to define the means of traversal between submenus and their options.
     * 
     * @param device the input device using the menu component
     * @param value  a value representing the input state of the input devices interactive component
     * @param action additional data pertaining to the nature of the interaction
     */
    protected abstract void navigate(InputDevice device, float value, String action);
    
    /**
     * Exits the menu component.
     * 
     * @param device the input device using the menu component
     * @param value  a value representing the input state of the input devices interactive component
     * @param action additional data pertaining to the nature of the interaction
     */
    protected abstract void exit(InputDevice device, float value, String action);
    
    /**
     * Changes the current {@link SubMenu} of this menu component.
     * 
     * @param name the name of the submenu to change to
     */
    protected abstract void changeMenu(String name);
    
    /**
     * Used to supply the puppet used by menu component with a means of interaction.
     */
    protected class CommandNavigate extends Command {
        public CommandNavigate(String action) {
            super(action);
        }
        
        @Override
        public void execute(InputDevice device, float value) {
            if(pressed(device, "button", value) && !request) {
                navigate(device, value, action);
                
                request = true;
            } else if(!pressed(device, "button", value)) {
                request = false;
            }
        }
    }
    
}