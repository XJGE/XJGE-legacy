package dev.theskidster.xjge.puppets;

import dev.theskidster.xjge.puppet.freecam.Freecam;
import dev.theskidster.xjge.puppet.terminal.Terminal;

/**
 * @author J Hoffman
 * Created: Jan 16, 2020
 */

/**
 * Contains objects that use {@linkplain Puppet puppets} to facilitate their input (with exception to the {@link Terminal} and {@link Freecam} objects, which 
 * instead override access to the {@link dev.theskidster.xjge.hardware.Keyboard keyboard} while active). 
 */
public final class Puppets {
    
    public static final Terminal TERMINAL = new Terminal();
    public static final Freecam FREECAM   = new Freecam();
    
}