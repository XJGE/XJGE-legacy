package dev.theskidster.xjge.audio;

/**
 * @author J Hoffman
 * Created: Jan 23, 2020
 */

/**
 * Provides the {@link theskidster.xjge.util.ServiceLocator ServiceLocator} class access to the audio engine without exposing implementation details.
 */
public interface AudioService {
    
    /**
     * Saves the state of each source in the last 
     * {@link theskidster.xjge.hardware.AudioDevice#setContextCurrent() AL Context} to be 
     * transferred over to this context once {@link init()} is called.
     */
    void findSourceStates();
    
    /**
     * Loads {@link Sound Sounds} and {@link Song Songs} then initializes the  
     * {@link Source Sound Sources} to be used by the audio engine. If sources existed 
     * previously their state is transferred to ensure a (relatively) smooth transition 
     * between {@link theskidster.xjge.hardware.AudioDevice Audio Devices}.
     */
    void init();
    
    float getSoundMasterVolume();
    float getMusicMasterVolume();
    
    /**
     * Sets the master volume of all sources excluding the reserved music source.
     * 
     * @param masterVolume the master volume (between 0 and 1).
     */
    void setSoundMasterVolume(float masterVolume);
    
    /**
     * Sets the master volume of the reserved music source.
     * 
     * @param masterVolume the master volume (between 0 and 1).
     */
    void setMusicMasterVolume(float masterVolume);
    
    /**
     * Plays a {@link Sound} from a {@link Source} object.
     * 
     * @param sound the sound to play
     * @param loop  if true, the source will loop the sound provided until stopped
     * @return the handle of the source set to play. Included so you can stop the source if its set to loop with {@link setSourceState(int, int) setSourceState()}.
     */
    int playSound(String sound, boolean loop);
    
    /**
     * Explicitly sets the state of the source object.
     * 
     * @param handle the unique handle of the source object or {@link Audio#ALL_SOURCES ALL_SOURCES}
     * @param state  the state to set the source(s). One of {@link org.lwjgl.openal.AL10#AL_PAUSED AL_PLAYING}, {@link org.lwjgl.openal.AL10#AL_PAUSED AL_PAUSED}, or 
     *               {@link org.lwjgl.openal.AL10#AL_PAUSED AL_STOPPED}. 
     */
    void setSourceState(int handle, int state);
    
    /**
     * Plays music from the beginning. If the song provided contains an intro, {@link checkIntroFinished()} must be called from the current levels 
     * {@link theskidster.xjge.level.Level#update() update()} method.
     * 
     * @param song the song to start playing
     */
    void playMusic(String song);
    
    /**
     * Sets the reserved music source object to the {@link org.lwjgl.openal.AL10#AL_PAUSED AL_PAUSED} state.
     */
    void pauseMusic();
    
    /**
     * Sets the reserved music source object to the {@link org.lwjgl.openal.AL10#AL_PLAYING AL_PLAYING} state. Intended to be used when the music sources previous 
     * state was {@link org.lwjgl.openal.AL10#AL_PAUSED AL_PAUSED}.
     */
    void resumeMusic();
    
    /**
     * Sets the reserved music source object to the {@link org.lwjgl.openal.AL10#AL_STOPPED AL_STOPPED} state.
     */
    void stopMusic();
    
    /**
     * Checks whether or not the musics intro has finished to begin the body section which will loop until stopped. Intended to be called from the current levels 
     * {@link theskidster.xjge.level.Level#update() update()} method, but only if the current song contains an intro.
     */
    void checkIntroFinished();
    
}