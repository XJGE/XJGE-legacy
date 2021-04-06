package dev.theskidster.xjge.audio;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_memory;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import dev.theskidster.xjge.main.App;
import dev.theskidster.xjge.util.ErrorUtil;
import dev.theskidster.xjge.main.Logger;

/**
 * @author J Hoffman
 * Created: Jan 17, 2020
 */

/**
 * Supplies the data parsed from an audio file into a new sound object that can be used by a {@link Source} object to play sound effects. The engine supports two 
 * formats, 8-bit mono (for sound effects), and 16-bit stereo (for music). Vorbis&trade; .ogg is the preferred file format of this engine.
 */
public final class Sound {
    
    public final int handle;
    private int channels;
    private int sampleRate;
    
    /**
     * Creates a new sound object from the audio file specified. If the audio file cannot be found, the engine will instead use a fallback sound in its place.
     * 
     * @param filename the name of the file to load. Expects the file extension to be included.
     */
     Sound(String filename) {
        handle = alGenBuffers();
        
        try(InputStream file = Sound.class.getResourceAsStream("/dev/theskidster/" + App.DOMAIN + "/assets/" + filename)) {
            loadSound(file);
        } catch(Exception e) {
            Logger.logWarning("Failed to load sound: \"" + filename + "\"", e);
            
            loadSound(Sound.class.getResourceAsStream("/dev/theskidster/" + App.DOMAIN + "/assets/sfx_beep.ogg"));
        }
        
        ErrorUtil.checkALError();
    }
    
    /**
     * Parses the data of the sound file specified and generates a new data buffer from its contents that can be used by a {@link Source} object.
     * 
     * @param file the file to extract sound data from
     */
    private void loadSound(InputStream file) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            byte[] data = file.readAllBytes();
            
            ByteBuffer soundBuf   = MemoryUtil.memAlloc(data.length).put(data).flip();
            IntBuffer channelsBuf = stack.mallocInt(1);
            IntBuffer sampleBuf   = stack.mallocInt(1);
            
            ShortBuffer sound = stb_vorbis_decode_memory(soundBuf, channelsBuf, sampleBuf);
            
            channels   = channelsBuf.get();
            sampleRate = sampleBuf.get();
            
            if(channels == 1) {
                alBufferData(handle, AL_FORMAT_MONO16, sound, sampleRate);
            } else {
                alBufferData(handle, AL_FORMAT_STEREO16, sound, sampleRate);
            }
            
            MemoryUtil.memFree(soundBuf);
            
        } catch(IOException e) {
            Logger.logSevere("Failed to load fallback sound.", e);
        }
    }
    
    public int getChannels()   { return channels; }
    public int getSampleRate() { return sampleRate; }
    
    /**
     * Frees the sound buffer associated with this object. Sounds will be freed automatically between configuration changes to the applications current 
     * {@link dev.theskidster.xjge.hardware.AudioDevice Audio Device} that requires a new OpenAL context to be created.
     */
    public void freeSound() {
        alDeleteBuffers(handle);
    }
    
}