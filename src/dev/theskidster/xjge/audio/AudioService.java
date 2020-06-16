package dev.theskidster.xjge.audio;

import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: Jan 23, 2020
 */

/**
 * Provides the {@link dev.theskidster.xjge.util.ServiceLocator ServiceLocator} class access to the audio engine without exposing implementation details.
 */
public interface AudioService {
    
    /**
     * Loads {@link Sound Sounds} and {@link Song Songs} then initializes the  
     * {@link Source Sound Sources} to be used by the audio engine. If sources existed 
     * previously their state is transferred to ensure a (relatively) smooth transition 
     * between {@link dev.theskidster.xjge.hardware.AudioDevice Audio Devices}.
     */
    void init();
    
    /**
     * Saves the state of each source in the last 
     * {@link dev.theskidster.xjge.hardware.AudioDevice#setContextCurrent() AL Context} to be 
     * transferred over to this context once {@link init()} is called.
     */
    void findSourceStates();
    
    /**
     * Plays a {@link Sound} from a {@link Source} object.
     * 
     * @param sound    the sound to play
     * @param position the position from which the source object will play the sound. If null is passed, the sound will be played from the origin point of the 
     *                 game world.
     * @param loop     if true, the source will loop the sound provided until stopped
     * @return the handle of the source set to play. Included so you can stop the source if its set to loop with {@link setSourceState(int, int) setSourceState()}.
     */
    int playSound(String sound, Vector3f position, boolean loop);
    
    /**
     * Plays music from the beginning. If the song provided contains an intro, {@link checkIntroFinished()} must be called from the current levels 
     * {@link dev.theskidster.xjge.level.Level#update() update()} method.
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
     * {@link dev.theskidster.xjge.level.Level#update() update()} method, but only if the current song contains an intro.
     */
    void checkIntroFinished();
    
    /**
     * Because OpenAL only permits one listener object per device context, a custom 3D audio positioning solution was developed to enable individual viewports 
     * to experience stereo sound effects during split screen play. 
     * <br><br>
     * First, the solution collects information about the orientation of each active viewports {@link dev.theskidster.xjge.util.Camera camera} object. Then it 
     * positions each {@link Source} object around the single OpenAL listener object located at the origin point of the game world relative to the nearest camera.
     * 
     * @see setViewportCamData(int, Vector3f, Vector3f)
     * @see Audio#findClosestViewport(Vector3f)
     * @see Source#setSourcePosition(Vector3f, Vector3f)
     */
    void updateSourcePositions();
    
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
     * Explicitly sets the state of the source object.
     * 
     * @param handle the unique handle of the source object or {@link Audio#ALL_SOURCES ALL_SOURCES}
     * @param state  the state to set the source(s). One of {@link org.lwjgl.openal.AL10#AL_PAUSED AL_PLAYING}, {@link org.lwjgl.openal.AL10#AL_PAUSED AL_PAUSED}, or 
     *               {@link org.lwjgl.openal.AL10#AL_PAUSED AL_STOPPED}. 
     */
    void setSourceState(int handle, int state);
    
    /**
     * Captures information regarding the position and direction of a viewports camera object to be used during 3D sound source positioning calculations.
     * 
     * @param id        the id number of the viewport
     * @param position  the current position of the viewports camera
     * @param direction the direction the viewports camera is currently facing
     */
    void setViewportCamData(int id, Vector3f position, Vector3f direction);
    
}