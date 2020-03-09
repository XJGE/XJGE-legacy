package dev.theskidster.xjge.hardware;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import dev.theskidster.xjge.puppets.Puppet;
import dev.theskidster.xjge.util.LogLevel;
import dev.theskidster.xjge.util.Logger;

/**
 * @author J Hoffman
 * Created: Jan 16, 2020
 */

/**
 * Standardizes the input operations of devices which exhibit varying layouts in their interactive components.
 * 
 * @see Controller
 * @see Keyboard
 * @see dev.theskidster.xjge.puppets
 */
public abstract class InputDevice {
    
    public final int id;
    public static final int ALL_EXCEPT_1 = -1;
    public static final int ALL_EXCEPT_2 = -2;
    public static final int ALL_EXCEPT_3 = -3;
    public static final int ALL_EXCEPT_4 = -4;
    public static final int ALL_DEVICES  = -5;
    public static final int PREV_STATE   = -6;
    public static final int KEYBOARD     = -7;
    
    public float sensitivity = 0.15f;
    
    public boolean enabled = true;
    
    public String name;
    
    public Stack<Boolean> enableStates = new Stack<>();
    public Stack<Puppet> puppets       = new Stack<>();
    private Queue<Puppet> pupSetEvents = new LinkedList<>();
    
    /**
     * Creates a new input device object.
     * 
     * @param id the unique number used to identify the device in other parts of the engine
     */
    public InputDevice(int id) {
        this.id = id;
    }
    
    /**
     * Transfers the state of an input device.
     * 
     * @param device the previous input device object used to transfer its state into the new instance
     */
    public InputDevice(InputDevice device) {
        id           = device.id;
        sensitivity  = device.sensitivity;
        enabled      = device.enabled;
        enableStates = device.enableStates;
        puppets      = device.puppets;
        pupSetEvents = device.pupSetEvents;
    }
    
    /**
     * Resolves the set event requested by {@link setPuppet(Puppet)} or {@link setPrevPuppet()}. Called in {@link poll()} after all input events have been resolved 
     * to avoid a {@link java.util.ConcurrentModificationException ConcurrentModificationException}.
     */
    protected void resolvePuppetSetRequest() {
        if(pupSetEvents.size() > 0) {
            puppets.push(pupSetEvents.poll());
        }
    }
    
    /**
     * Looks for any state changes in the input devices interactive components (buttons, switches, axes, etc) and resolves the input event according to the 
     * definition supplied by this devices current {@link dev.theskidster.xjge.puppets.Puppet Puppet} object.
     */
    public abstract void poll();
    
    /**
     * Maps the input devices interactive components according to the preferences of the user.
     */
    public abstract void configure();
    
    /**
     * Sets the current state of this input device. Disabled devices will ignore state changes in their interactive components.
     * 
     * @param value if true, the input device will acknowledge input events 
     * @see poll()
     */
    public void setEnabled(boolean value) {
        enableStates.add(value);
        enabled = enableStates.peek();
    }
    
    /**
     * Sets the input devices current {@link dev.theskidster.xjge.puppets.Puppet Puppet} object to the one specified here.
     * 
     * @param puppet the puppet object we want this input device to control
     */
    public void setPuppet(Puppet puppet) {
        pupSetEvents.add(puppet);
    }
    
    /**
     * Convenience method that will set the input devices current puppet to its previous binding if it had one.
     */
    public void setPrevPuppet() {
        if(puppets.size() > 1) {
            puppets.pop();
            pupSetEvents.add(puppets.peek());
        } else {
            Logger.log(LogLevel.WARNING, 
                    "Failed to set previous puppet of input device: \"" + name + "\" (" + 
                    id + "). This device has no prior puppet objects.");
        }
    }
    
}