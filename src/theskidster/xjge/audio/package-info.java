/**
 * Encapsulates the OpenAL framework into a single extensible audio interface that can be accessed anywhere in the application through the 
 * {@link theskidster.xjge.util.ServiceLocator ServiceLocater} class.
 * 
 * <p>
 * The audio engine makes use of several {@link Source} objects which are initialized once during startup, and again every time the application experiences a 
 * configuration change in its current {@link theskidster.xjge.hardware.AudioDevice Audio Device} that requires a new OpenAL context to be created (typically 
 * whenever the application switches audio devices). Additionally, source objects will retain state information between audio devices and are accessed from an object 
 * pool based on availability, determined through the value returned by their current {@link org.lwjgl.openal.AL10#AL_SOURCE_STATE AL_SOURCE_STATE}. Sources may be
 * forcefully pulled from this pool if no sources are deemed available at the time of the request. Lastly, a single source independent of the source object pool is 
 * reserved and provided exclusively for the purposes of playing music.
 * </p>
 * <p>
 * This package provides some simple methods to manipulate the audio engines source objects through the {@link Audio} class. However, it is expected that the 
 * implementation will extend this functionality to better suit its individual requirements.
 * </p>
 */
package theskidster.xjge.audio;