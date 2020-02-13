package theskidster.xjge.audio;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_memory;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import theskidster.xjge.main.App;
import theskidster.xjge.util.ErrorUtil;
import theskidster.xjge.util.LogLevel;
import theskidster.xjge.util.Logger;

/**
 * @author J Hoffman
 * Created: Jan 17, 2020
 */

/**
 * Supplies data parsed from audio files, supports both 8-bit mono and 16-bit stereo samples. Vorbis .ogg is the preferred format of this engine, though its usage is 
 * not explicitly defined. Use alternate formats at your own risk.
 */
public final class Sound {
    
    public final int handle;
    private int channels;
    private int sampleRate;
    
    /**
     * Generates a new sound object to be used by a {@link Source} object. If the file cannot be found it will use a fallback sound.
     * 
     * @param filename the name of the file to load. Expects the file extension to be included.
     */
     Sound(String filename) {
        handle = alGenBuffers();
        
        try(InputStream file = Sound.class.getResourceAsStream("/theskidster/" + App.DOMAIN + "/assets/" + filename)) {
            loadSound(file);
        } catch(Exception e) {
            Logger.setStackTrace(e);
            Logger.log(LogLevel.WARNING, "Failed to load sound: \"" + filename + "\"");
            
            loadSound(Sound.class.getResourceAsStream("/theskidster/" + App.DOMAIN + "/assets/sfx_beep.ogg"));
        }
        
        ErrorUtil.checkALError();
    }
    
    private void loadSound(InputStream file) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            byte[] data         = file.readAllBytes();
            
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
            Logger.setStackTrace(e);
            Logger.log(LogLevel.SEVERE, "Failed to load fallback sound.");
        }
    }
    
    public int getChannels()   { return channels; }
    public int getSampleRate() { return sampleRate; }
    
    /**
     * Frees the sound buffer associated with this object. Sounds will be freed automatically between configuration changes to the applications current 
     * {@link theskidster.xjge.hardware.AudioDevice Audio Device} that requires a new OpenAL context to be created.
     */
    public void freeSound() {
        alDeleteBuffers(handle);
    }
    
}