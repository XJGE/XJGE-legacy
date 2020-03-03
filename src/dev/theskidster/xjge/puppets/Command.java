package dev.theskidster.xjge.puppets;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import dev.theskidster.xjge.hardware.InputDevice;
import static dev.theskidster.xjge.hardware.InputDevice.KEYBOARD;

/**
 * @author J Hoffman
 * Created: Jan 16, 2020
 */

/**
 * Defines how input data supplied from a users {@link dev.theskidster.xjge.hardware.InputDevice InputDevice} should be utilized in relation to the control of the 
 * implementing object.
 * 
 * @see Puppet
 */
public abstract class Command {
    
    public float value;
    protected boolean request = true;
    public String action;
    
    /**
     * Creates a new command with no extra functionality.
     */
    protected Command() {}
    
    /**
     * Creates a new command which provides additional means of interaction. This is useful whenever a command is coupled to an interactive component that requires 
     * a greater level of granularity over its action (such as the axis of a controllers analog stick used).
     * 
     * @param action the specific action to be performed with this command
     * @see Puppet#commands
     */
    protected Command(String action) {
        this.action = action;
    }
    
    /**
     * Executes the command defined by the subclass.
     * 
     * @param device the input device that executed the command
     * @param value  the value of the input state returned from the input device. This is helpful for instances where the interactive component is fluid in its 
     *               action (see {@link pressed(InputDevice, String, float) pressed()}).
     * @see theskidster.xjge.hardware.InputDevice#poll()
     */
    public abstract void execute(InputDevice device, float value);
    
    /**
     * Used to see if the state of an interactive component can be determined in relation to how components of that nature typically behave.
     * 
     * @param device the input device in question
     * @param type   the type of interactive component to compare against. One of "button", "axis", or "trigger".
     * @param value  a value representing the input state of the input devices interactive component
     * @return true if the devices interactive component can be considered pressed
     */
    protected boolean pressed(InputDevice device, String type, float value) {
        switch(type) {
            case "button":
                return value == GLFW_PRESS;
                
            case "axis":
                return Math.abs(value) > GLFW_RELEASE;
                
            case "trigger":
                if(device.id == KEYBOARD) return value > 0;
                else                      return value > -1;
                
            default:
                return false;
        }
    }
    
}