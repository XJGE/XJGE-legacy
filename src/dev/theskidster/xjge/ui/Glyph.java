package dev.theskidster.xjge.ui;

import org.joml.Vector3i;
import dev.theskidster.xjge.util.Color;

/**
 * @author J Hoffman
 * Created: Jan 23, 2020
 */

/**
 * Data structure which represents a single symbol used to comprise words of a language. These structures are used by a {@link BitmapFont} to permit uniqueness among 
 * its individual instances.
 */
class Glyph {
    
    char c;
    
    Vector3i position;
    Color color;
    
    Glyph(char c, Vector3i position, Color color) {
        this.c        = c;
        this.position = position;
        this.color    = color;
    }
    
}