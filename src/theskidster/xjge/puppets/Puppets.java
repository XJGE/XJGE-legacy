package theskidster.xjge.puppets;

import theskidster.xjge.puppet.freecam.Freecam;
import theskidster.xjge.puppet.terminal.Terminal;

/**
 * @author J Hoffman
 * Created: Jan 16, 2020
 */

/**
 * Contains objects that use <a>{@link Puppet puppets}</a> to facilitate their input (with exception to the {@link theskidster.xjge.puppet.terminal.Terminal Terminal} 
 * and {@link theskidster.xjge.puppet.freecam.Freecam Freecam} objects, which instead override access to the {@link theskidster.xjge.hardware.Keyboard keyboard}
 * while active). 
 */
public final class Puppets {
    
    public static final Terminal TERMINAL = new Terminal();
    public static final Freecam FREECAM   = new Freecam();
    
}