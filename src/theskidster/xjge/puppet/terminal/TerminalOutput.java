package theskidster.xjge.puppet.terminal;

import theskidster.xjge.util.Color;

/**
 * @author J Hoffman
 * Created: Jan 25, 2020
 */

/**
 * Data structure which contains information pertaining to a commands response when executed.
 */
public class TerminalOutput {
    
    public final String text;
    public final Color color;
    
    /**
     * Creates an empty response.
     */
    public TerminalOutput() {
        text  = "";
        color = Color.WHITE;
    }
    
    /**
     * Creates a response to show the user with the data provided.
     * 
     * @param text  the text to show the user
     * @param color a pretty color to draw the text in
     */
    public TerminalOutput(String text, Color color) {
        this.text  = text;
        this.color = color;
    }
    
}