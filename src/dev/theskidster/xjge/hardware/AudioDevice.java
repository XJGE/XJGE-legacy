package dev.theskidster.xjge.hardware;

import java.nio.IntBuffer;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import static org.lwjgl.openal.ALC10.*;
import org.lwjgl.openal.ALCCapabilities;
import dev.theskidster.xjge.util.LogLevel;
import dev.theskidster.xjge.util.Logger;
import dev.theskidster.xjge.util.ServiceLocator;

/**
 * @author J Hoffman
 * Created: Jan 17, 2020
 */

/**
 * Represents a peripheral audio output device such as a speaker, headset, or headphones.
 */
public class AudioDevice {
    
    public final int id;
    
    public long handle;
    private long context;
    
    public final String name;
    private ALCCapabilities capabilities;
    
    /**
     * Creates a new audio device.
     * 
     * @param id   the unique number used to identify the device in other parts of the engine
     * @param name the name of the device as understood by OpenAL
     */
    public AudioDevice(int id, String name) {
        this.id   = id;
        this.name = name;
        
        handle       = alcOpenDevice(name);
        capabilities = ALC.createCapabilities(handle);
        context      = alcCreateContext(handle, (IntBuffer) null);
    }
    
    /**
     * Before an audio device can start playing sounds from {@link dev.theskidster.xjge.audio.Source Source} objects, OpenAL must create a context. Using this method will 
     * create a new OpenAL context on the device from which its called.
     */
    public void setContextCurrent() {
        try {
            alcMakeContextCurrent(context);
            AL.createCapabilities(capabilities);
            ServiceLocator.getAudio().init();
        } catch(IllegalStateException e) {
            Logger.setStackTrace(e);
            Logger.log(LogLevel.SEVERE, "Failed to set AL context.");
        }
    }
    
}