package theskidster.xjge.audio;

/**
 * @author J Hoffman
 * Created: Jan 17, 2020
 */

/**
 * Represents some music, may or may not contain an intro which is played once before 
 * entering a looping body section.
 */
public class Song {
    
    public Sound intro;
    public Sound body;
    
    /**
     * Creates a new song object without an intro.
     * 
     * @param filename the name of the file to load. Expects the file extension to be included.
     */
    public Song(String filename) {
        body = new Sound(filename);
    }
    
    /**
     * Creates a new song object with an intro section.
     * 
     * @param introFilename the name of the file to load for the intro section. Expects the file extension to be included.
     * @param bodyFilename  the name of the file to load for the body section. Expects the file extension to be included.
     */
    public Song(String introFilename, String bodyFilename) {
        intro = new Sound(introFilename);
        body  = new Sound(bodyFilename);
    }
    
}