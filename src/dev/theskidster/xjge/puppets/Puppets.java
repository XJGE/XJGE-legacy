package dev.theskidster.xjge.puppets;

import dev.theskidster.xjge.puppet.freecam.Freecam;
import dev.theskidster.xjge.puppet.terminal.Terminal;

/**
 * @author J Hoffman
 * Created: Jan 16, 2020
 */

/**
 * Contains objects that use <a>{@link Puppet puppets}</a> to facilitate their input (with exception to the {@link dev.theskidster.xjge.puppet.terminal.Terminal Terminal} 
 * and {@link dev.theskidster.xjge.puppet.freecam.Freecam Freecam} objects, which instead override access to the {@link theskidster.xjge.hardware.Keyboard keyboard}
 * while active). 
 */
public final class Puppets {
    
    public static final Terminal TERMINAL = new Terminal();
    public static final Freecam FREECAM   = new Freecam();
    
}