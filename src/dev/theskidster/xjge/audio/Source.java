package dev.theskidster.xjge.audio;

import static org.lwjgl.openal.AL11.*;
import dev.theskidster.xjge.util.ErrorUtil;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: Jan 23, 2020
 */

/**
 * Represents an OpenAL source object. These objects can be best conceptualized as invisible CD players that are located throughout the game world, with the CD 
 * itself representing the sources current {@link Sound} or {@link Song} object.
 */
class Source {
    
    public final int handle;
    
    private boolean loop;
    
    private Sound sound;
    private Vector3f position;
    private Vector3f tempPos = new Vector3f();
    
    /**
     * Generates a new source object.
     */
    Source() {
        handle = alGenSources();
    }
    
    /**
     * Used in {@link Audio#init() Audio.init()} to transfer the state of sources from the previous OpenAL context.
     * 
     * @param source       the previous source object used to transfer its state into the new instance
     * @param sound        the sound used by the previous source object to be used by the new instance
     * @param sourceSample the index of the sample. Used to continue playing sounds from where sources previously left off.
     * @param sourceState  the AL_SOURCE_STATE of the previous source. One of {@link org.lwjgl.openal.AL11#AL_INITIAL AL_INITIAL},
     *                     {@link org.lwjgl.openal.AL11#AL_PAUSED AL_PLAYING}, {@link org.lwjgl.openal.AL11#AL_PAUSED AL_PAUSED}, or 
     *                     {@link org.lwjgl.openal.AL11#AL_PAUSED AL_STOPPED}. 
     */
    Source(Source source, Sound sound, int sourceSample, int sourceState) {
        handle = alGenSources();
        
        setLooping(source.loop);
        setWorldPosition(source.position);
        
        if(sound != null) {
            setSound(sound);
            alSourcei(handle, AL_SAMPLE_OFFSET, sourceSample);
            
            switch(sourceState) {
                case AL_PLAYING: alSourcePlay(handle);  break;
                case AL_PAUSED:  alSourcePause(handle); break;
                case AL_STOPPED: alSourceStop(handle);  break;
            }
        }
        
        ErrorUtil.checkALError();
    }
    
    /**
     * Variant of the {@link Source(Source, Sound, int, int) Source()} constructor used to transfer the state of the music source.
     * 
     * @param source        the previous source object used to transfer its state into the new instance
     * @param sound         the sound used by the previous source object to be used by the new instance
     * @param sourceSample  the index of the sample. Used to continue playing sounds from where sources previously left off.
     * @param sourceState   the AL_SOURCE_STATE of the previous source. One of {@link org.lwjgl.openal.AL11#AL_INITIAL AL_INITIAL},
     *                      {@link org.lwjgl.openal.AL11#AL_PAUSED AL_PLAYING}, {@link org.lwjgl.openal.AL11#AL_PAUSED AL_PAUSED}, or 
     *                      {@link org.lwjgl.openal.AL11#AL_PAUSED AL_STOPPED}. 
     * @param introFinished indicates whether or not the songs intro has finished
     */
    Source(Source source, Song song, int sourceSample, int sourceState, boolean introFinished) {
        handle = alGenSources();
        
        if(song != null) {
            if(song.intro != null && !introFinished) {
                queueSound(song.intro);
                queueSound(song.body);
            } else {
                queueSound(song.body);
            }
            
            setLooping(source.loop);
            alSourcei(handle, AL_SAMPLE_OFFSET, sourceSample);
            
            switch(sourceState) {
                case AL_PLAYING: alSourcePlay(handle);  break;
                case AL_PAUSED:  alSourcePause(handle); break;
                case AL_STOPPED: alSourceStop(handle);  break;
            }
        }
        
        ErrorUtil.checkALError();
    }
    
    /**
     * Finds the current state of the source from which its called.
     * 
     * @param state the state we want to check for
     * @return the current state of the source object. One of {@link org.lwjgl.openal.AL11#AL_INITIAL AL_INITIAL}, {@link org.lwjgl.openal.AL11#AL_PAUSED AL_PLAYING}, 
     *         {@link org.lwjgl.openal.AL11#AL_PAUSED AL_PAUSED}, or {@link org.lwjgl.openal.AL11#AL_PAUSED AL_STOPPED}.
     */
    public boolean getState(int state) {
        return alGetSourcei(handle, AL_SOURCE_STATE) == state;
    }
    
    public Vector3f getPosition() { return position; }
    
    /**
     * Sets the position of the source object as its found in the game world. This vector will be used in conjunction with the position and direction vectors of
     * the nearest {@link dev.theskidster.xjge.util.Camera Camera} object to calculate its final position relative to the OpenAL listener object.
     * 
     * @param position the position to place the source at
     */
    public void setWorldPosition(Vector3f position) {
        if(position != null) this.position = position;
    }
    
    /**
     * Calculates the final position of the source object given its position in the game world relative to the position and direction of the nearest viewports 
     * {@link dev.theskidster.xjge.util.Camera Camera} object.
     * 
     * @param position  the current position of the viewports camera
     * @param direction the direction the viewports camera is currently facing
     */
    public void setSourcePosition(Vector3f position, Vector3f direction) {
        this.position.sub(position, tempPos);
        
        float dot   = tempPos.dot(direction);
        float det   = tempPos.x * direction.z - tempPos.z * direction.x; //determinant
        float angle = (float) Math.toDegrees(Math.atan2(det, dot)) - 90;
        
        if(angle < 0) {
            float offset = 180 + angle;
            angle = 180 + offset;
        }
        
        float dist = position.distance(this.position);
        
        float rad = (float) Math.toRadians(angle);
        float x   = (float) -(dist * Math.cos(rad));
        float z   = (float) (dist * Math.sin(rad));
        
        alSource3f(handle, AL_POSITION, x, tempPos.y, z);
    }
    
    /**
     * Sets this source to start or stop playing in a loop. Loops will continue indefinitely until specified otherwise or the source is stopped.
     */
    public void setLooping(boolean loop) {
        this.loop = loop;
        alSourcei(handle, AL_LOOPING, (loop) ? AL_TRUE : AL_FALSE);
    }
    
    /**
     * Sets the current {@link Sound} object that the source will use. Calling this method on a newly initialized source object will set its source type from
     * {@link org.lwjgl.openal.AL11#AL_UNDETERMINED AL_UNDETERMINED} to {@link org.lwjgl.openal.AL11#AL_STATIC AL_STATIC}. Which will prevent it from allowing 
     * additional sounds to be queued through {@link queueSound(Sound) queueSound()} and cause an 
     * {@link dev.theskidster.xjge.util.ErrorUtil#checkALError() AL Error}.
     * 
     * @param sound the sound to bind to this source
     */
    public void setSound(Sound sound) {
        this.sound = sound;
        alSourcei(handle, AL_BUFFER, sound.handle);
    }
    
    /**
     * Queues a sound to play on this source after prior sounds in the queue have finished. This method will cause an 
     * {@link dev.theskidster.xjge.util.ErrorUtil#checkALError() AL Error} if a source type is not of {@link org.lwjgl.openal.AL11#AL_STREAMING AL_STREAMING}. 
     * OpenAL requires a source to make an initial call to {@link org.lwjgl.openal.AL11#alSourceQueueBuffers(int, int) alSourceQueueBuffers()} prior to other 
     * operations to set its type, as its type cannot be set explicitly later. The reserved music source by default uses this type.
     * 
     * @param sound the sound to queue
     */
    public void queueSound(Sound sound) {
        alSourceQueueBuffers(handle, sound.handle);
    }
    
}