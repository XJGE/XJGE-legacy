package theskidster.xjge.util;

import theskidster.xjge.audio.AudioService;

/**
 * @author J Hoffman
 * Created: Jan 17, 2020
 */

/**
 * Provides a global point of access to a service without coupling users to the class which implements it.
 */
public final class ServiceLocator {
    
    private static AudioService audio;
    
    public static AudioService getAudio() { return audio; }
    
    public static void setAudio(AudioService value) {
        audio = value;
    }
    
}