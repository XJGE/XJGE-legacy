package dev.theskidster.xjge.audio;

/**
 * @author J Hoffman
 * Created: Jan 17, 2020
 */

/**
 * Represents a musical composition, may or may not contain an intro which is played once before entering a looping body section. All {@link Sound} objects used 
 * by this object should be formatted as 16-bit stereo.
 */
public class Song {
    
    public Sound intro;
    public Sound body;
    
    /**
     * Creates a new song object using the audio file specified. The file provided should be formatted in 16-bit stereo.
     * 
     * @param filename the name of the file to load. Expects the file extension to be included.
     */
    public Song(String filename) {
        body = new Sound(filename);
    }
    
    /**
     * Creates a new song object using the audio files specified. The files provided should be formatted in 16-bit stereo. 
     * {@link dev.theskidster.xjge.level.Level Levels} that use a song object with this constructor will need to call {@link Audio#checkIntroFinished()} otherwise
     * the body section will not play.
     * 
     * @param introFilename the name of the file to load for the intro section. Expects the file extension to be included.
     * @param bodyFilename  the name of the file to load for the body section. Expects the file extension to be included.
     */
    public Song(String introFilename, String bodyFilename) {
        intro = new Sound(introFilename);
        body  = new Sound(bodyFilename);
    }
    
}