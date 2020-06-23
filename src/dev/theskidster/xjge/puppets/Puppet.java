package dev.theskidster.xjge.puppets;

import java.util.HashMap;
import java.util.Map;

/**
 * @author J Hoffman
 * Created: Jan 16, 2020
 */

/**
 * A component object that enables the implementing object to make use of input captured from an {@link dev.theskidster.xjge.hardware.InputDevice InputDevice} by 
 * coupling an interactive component of the device (such as a button) to a {@linkplain Command meaningful action} defined by the implementation.  
 * 
 * @see dev.theskidster.xjge.puppets
 */
public class Puppet {
    
    public Object object;
    
    /**
     * A collection of {@linkplain Command commands}. Command definitions should exhibit the following structure:
     * <blockquote><pre>
     * puppet.commands.put("a button", myCommand1());
     * puppet.commands.put("left x",   myCommand2("x"));
     * puppet.commands.put("left y",   myCommand2("y"));
     * ...
     * </pre></blockquote>
     * Notice how the keys of the collection correspond with those defined in the {@link dev.theskidster.xjge.hardware.Controller Controller} and 
     * {@link dev.theskidster.xjge.hardware.Keyboard Keyboard} classes. Additionally some commands may be supplied twice with an action represented as a string. 
     * This is typically useful for actions which require the use of an axis such as changing the direction of a 3D camera.
     * <br><br>
     */
    public Map<String, Command> commands = new HashMap<>();
    
    /**
     * Creates a new puppet object. It is excepted that the implementing object will populate the puppets {@link commands} collection inside of its constructor 
     * following the puppet objects initialization.
     * 
     * @param object the implementing object to be controlled with this puppet
     */
    public Puppet(Object object) {
        this.object = object;
    }
    
}