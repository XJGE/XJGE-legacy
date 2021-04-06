package dev.theskidster.xjge.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector3i;
import dev.theskidster.xjge.puppet.terminal.TerminalOutput;
import dev.theskidster.xjge.util.Color;

/**
 * @author J Hoffman
 * Created: Jan 23, 2020
 */

/**
 * Provides utilities for drawing text to the screen. For reasons pertaining to performance, text objects will look for changes in their draw methods arguments to 
 * determine whether a strings data needs to be updated. Because of this, it is preferred that objects implementing text use multiple instances of the object to draw 
 * separate strings.
 * <p>
 * The text class can be supplied with additional draw methods as needed by the implementation.
 * </p>
 */
public class Text {
    
    private String prevText  = "";
    private Vector3i prevPos = new Vector3i();
    private Color prevCol    = Color.WHITE;
    private BitmapFont font;
    
    private Map<Integer, Glyph> glyphs = new HashMap<>();
    
    /**
     * Creates a new text object that will use the default font provided by the engine.
     * @see BitmapFont#BitmapFont()
     */
    public Text() {
        font = new BitmapFont();
    }
    
    /**
     * Creates a new text object that will use the font specified in the .xml file provided.
     * 
     * @param filename the name of the file to load. Expects the file extension to be included.
     * @see BitmapFont#BitmapFont(String)
     */
    public Text(String filename) {
        font = new BitmapFont(filename);
    }
    
    /**
     * Draws text to the screen.
     * 
     * @param text     the string to draw. This will be broken into a collection of glyphs when supplied to {@link BitmapFont}.
     * @param position the position on the screen to start drawing the text
     * @param color    the color to draw the text in
     * @see Glyph
     */
    public void draw(String text, Vector3i position, Color color) {
        boolean changed = !prevText.equals(text) || !prevPos.equals(position.x, position.y, position.z) || !prevCol.equals(color);
        
        if(changed) {
            glyphs.clear();
            
            int span = 0;
            int drop = 0;
            
            for(int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                
                if(c != '\n') {
                    Vector3i pos = new Vector3i(
                            position.x + span, 
                            position.y + (drop + font.getGlyphDescent(c)), 
                            position.z);
                    
                    glyphs.put(i, new Glyph(c, pos, color));
                    
                    span += font.getGlyphLeading(c);
                } else {
                    span = 0;
                    drop -= font.getCellHeight();
                }
            }
        }
        
        font.draw(glyphs, changed);
        
        prevText = text;
        prevPos.set(position);
        prevCol = color;
    }
    
    /**
     * Draws a string following the syntax highlighting of a command in the engines command terminal.
     * 
     * @param text     the string to draw. This will be broken into a collection of glyphs when supplied to {@link BitmapFont}.
     * @param position the position on the screen to start drawing the text
     */
    public void drawCommand(String text, Vector3i position) {
        boolean changed = !prevText.equals(text) || !prevPos.equals(position.x, position.y, position.z);
        
        if(changed) {
            glyphs.clear();
            
            int span  = 0;
            int drop  = 0;
            int start = 0;
            
            for(int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if(c == ' ') start = i;
                
                Vector3i pos = new Vector3i(
                        position.x + span, 
                        position.y + drop,
                        position.z);
                
                Color col = (start != 0 && i > start) ? Color.YELLOW : Color.CYAN;
                
                switch(c) {
                    case '(', ')', ',', '<', '>' -> col = Color.WHITE;
                }
                
                glyphs.put(i, new Glyph(c, pos, col));

                span += font.getGlyphLeading(c);
            }
        }
        
        font.draw(glyphs, changed);
        
        prevText = text;
        prevPos.set(position);
    }
    
    /**
     * Draws the output of the engines command terminal.
     * 
     * @param o1       the output objects generated from previous commands
     * @param o2       the most recent output generated
     * @param index    the index used to find the y position offset of an output object
     * @param executed if true, the command has been executed
     */
    public void drawOutput(TerminalOutput[] o1, TerminalOutput o2, int index, boolean executed) {
        if(executed) {
            glyphs.clear();
            
            int span    = 0;
            int drop    = 0;
            int yOffset = (font.getCellHeight() * (charOccurences(o2.text, '\n', 0))) + 2;
            
            if(index != 0) {
                String composite = "";
                for(int i = 0; i < index; i++) composite += o1[i].text;
                yOffset += (font.getCellHeight() * (charOccurences(composite, '\n', 0)));
            }
            
            for(int i = 0; i < o2.text.length(); i++) {
                char c = o2.text.charAt(i);
                
                if(c != '\n') {
                    Vector3i pos = new Vector3i(
                            2 + span, 
                            yOffset + drop, 
                            -2);
                    
                    glyphs.put(i, new Glyph(c, pos, o2.color));
                    
                    span += font.getGlyphLeading(c);
                } else {
                    span = 0;
                    drop -= font.getCellHeight();
                }
            }
        }
        
        font.draw(glyphs, executed);
    }
    
    /**
     * Attempts to wrap a string inside of the width specified. Will not break words.
     * 
     * @param text  the string of text we want to wrap
     * @param width the width that the string may not exceed
     * @return a string formatted to fit inside the width
     */
    public String wrap(String text, int width) {
        var words        = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        
        for(int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if(i != text.length() - 1) {
                if(c != ' ') {
                    sb.append(c);
                } else {
                    words.add(sb.toString());
                    sb.delete(0, sb.length());
                }
            } else {
                sb.append(c);
                words.add(sb.toString());
                sb.delete(0, sb.length());
            }
        }
        
        int wordLength = 0;
        text = "";

        for(int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            wordLength += lengthInPixels(word + " ");
            
            if(i != words.size() - 1 && wordLength + lengthInPixels(words.get(i + 1)) > width) {
                text += words.get(words.indexOf(word)).concat("\n");
                wordLength = 0;
            } else {
                if(words.indexOf(word) != words.size() - 1) {
                    text += words.get(words.indexOf(word)).concat(" ");
                } else {
                    text += words.get(words.indexOf(word));
                }
            }
        }
        
        return text;
    }
    
    /**
     * Finds the length of a string in pixels.
     * 
     * @param text the text we want to measure
     * @return the length of the string in pixels
     */
    public int lengthInPixels(String text) {
        int length = 0;
        
        for(char c : text.toCharArray()) {
            if(c != '\n') {
                length += font.getGlyphLeading(c);
            }
        }
        
        return length;
    }
    
    /**
     * Finds the number of times a character appears in a string from some point.
     * 
     * @param text  the string of text to examine
     * @param c     the character to search for
     * @param index the index to offset the search by. Any index preceding the one specified will be omitted from the search. 
     * @return the number of times the character was found
     */
    public static int charOccurences(String text, char c, int index) {
        if(index >= text.length()) return 0;
        int count = (text.charAt(index) == c) ? 1 : 0;
        
        return count + charOccurences(text, c, index + 1);
    }
    
}