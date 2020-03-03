package dev.theskidster.xjge.ui;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.joml.Vector2f;
import org.joml.Vector2i;
import static org.lwjgl.opengl.GL33C.*;
import org.lwjgl.system.MemoryStack;
import dev.theskidster.xjge.graphics.Instance;
import dev.theskidster.xjge.graphics.Cell;
import dev.theskidster.xjge.graphics.Texture;
import dev.theskidster.xjge.main.App;
import dev.theskidster.xjge.shader.core.ShaderCore;
import dev.theskidster.xjge.util.ErrorUtil;
import dev.theskidster.xjge.util.LogLevel;
import dev.theskidster.xjge.util.Logger;

/**
 * @author J Hoffman
 * Created: Jan 23, 2020
 */

/**
 * Objects of this type represent custom fonts that can be used by a {@link Text} object to draw strings.
 */
public class BitmapFont {
    
    private final int vboPosOffset = glGenBuffers();
    private final int vboTexOffset = glGenBuffers();
    private final int vboColOffset = glGenBuffers();
    
    private boolean monospaced;
    
    private Instance data;
    
    private Map<Character, Vector2i> posOffsets = new HashMap<>();
    private Map<Character, Vector2f> texOffsets = new HashMap<>();
    
    /**
     * Creates a new bitmap font with the engines default monospaced font.
     */
    public BitmapFont() {
        monospaced = true;
        init(new Texture("spr_dosmono.png"), new Cell(8, 14));
    }
    
    /**
     * Creates a new bitmap font using the data from the .xml file provided. This file specifies glyph spacing as well as the image to use for the fonts 
     * {@link dev.theskidster.xjge.graphics.SpriteSheet SpriteSheet}.
     * 
     * @param filename the name of the file to load. Expects the file extension to be included.
     */
    public BitmapFont(String filename) {
        Texture texture = null;
        Cell cell       = null;
        
        try {
            XMLStreamReader xmlReader = XMLInputFactory.newInstance()
                    .createXMLStreamReader(BitmapFont.class.getResourceAsStream("/dev/theskidster/" + App.DOMAIN + "/assets/" + filename));
            
            int leading = 0;
            int descent = 0;
            
            while(xmlReader.hasNext()) {
                final int LEADING = leading;
                final int DESCENT = descent;
                
                switch(xmlReader.next()) {
                    case XMLStreamConstants.START_ELEMENT:
                        if(xmlReader.getName().getLocalPart().equals("font")) {
                            texture = new Texture(xmlReader.getAttributeValue(null, "texture"));
                            
                            int width  = Integer.parseInt(xmlReader.getAttributeValue(null, "width"));
                            int height = Integer.parseInt(xmlReader.getAttributeValue(null, "height"));
                            cell       = new Cell(width, height);
                            
                            monospaced = Boolean.parseBoolean(xmlReader.getAttributeValue(null, "monospaced"));
                            
                            posOffsets.put('\n', new Vector2i(width, height));
                        } else if(xmlReader.getName().getLocalPart().equals("group")) {
                            leading = Integer.parseInt(xmlReader.getAttributeValue(null, "leading"));
                            descent = Integer.parseInt(xmlReader.getAttributeValue(null, "descent"));
                        }
                        break;
                        
                    case XMLStreamConstants.END_ELEMENT:
                        if(xmlReader.getName().getLocalPart().equals("font")) {
                            xmlReader.close();
                        }
                        break;
                    
                    case XMLStreamConstants.CHARACTERS:
                        BufferedReader reader = new BufferedReader(new StringReader(xmlReader.getText().trim()));
                        
                        reader.lines().forEach(line -> {
                            for(String value : line.trim().split(",")) {
                                posOffsets.put((char) Integer.parseInt(value), new Vector2i(LEADING, DESCENT));
                            }
                        });
                }
            }
        } catch(XMLStreamException e) {
            Logger.setStackTrace(e);
            Logger.log(LogLevel.WARNING, "Failed to parse font file: \"" + filename + "\" using default font.");
            
            monospaced = true;
            texture    = new Texture("spr_dosmono.png");
            cell       = new Cell(8, 14);
        }
        
        init(texture, cell);
    }
    
    /**
     * Generates positions which will be used to offset glyph instances when drawing strings. The effects of this process are most apparent in strings that use fonts
     * which aren't monospaced.
     * 
     * @param texture the texture to be used as a sprite sheet
     * @param cell    the dimensions of the cells the texture will be split by
     */
    private void init(Texture texture, Cell cell) {
        data = new Instance(texture, cell, false);
        
        String charset = " !\"#$%&\'()*+,-./" + "\r" +
                         "0123456789:;<=>?"   + "\r" +
                         "@ABCDEFGHIJKLMNO"   + "\r" +
                         "PQRSTUVWXYZ[\\]^_"  + "\r" + 
                         "`abcdefghijklmno"   + "\r" +
                         "pqrstuvwxyz{|}~";
        
        float charPosX = 0;
        float charPosY = 0;
        
        for(char c : charset.toCharArray()) {
            if(c != '\r') {
                texOffsets.put(c, new Vector2f(charPosX, charPosY));
                charPosX += data.sprite.cellWidth;
            } else {
                charPosX = 0;
                charPosY += data.sprite.cellHeight;
            }
        }
    }
    
    /**
     * Offsets the position of each glyph according to its individual specifications.
     * 
     * @param glyphs the collection of glyphs that comprise the string being drawn
     * @see Glyph
     */
    private void offsetPosition(Map<Integer, Glyph> glyphs) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer positions = stack.mallocFloat(glyphs.size() * Float.BYTES);
            
            glyphs.forEach((index, glyph) -> {
                positions.put(glyph.position.x).put(glyph.position.y).put(glyph.position.z);
            });
            
            positions.flip();
            
            glBindBuffer(GL_ARRAY_BUFFER, vboPosOffset);
            glBufferData(GL_ARRAY_BUFFER, positions, GL_STATIC_DRAW);
        }
        
        glVertexAttribPointer(4, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        glEnableVertexAttribArray(4);
        glVertexAttribDivisor(4, 1);
    }
    
    /**
     * Offsets the fonts texture coordinates to correspond to that of each individual character.
     * 
     * @param glyphs the collection of glyphs that comprise the string being drawn
     * @see Glyph
     */
    private void offsetTexture(Map<Integer, Glyph> glyphs) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer cells = stack.mallocFloat(glyphs.size() * Float.BYTES);
            
            glyphs.forEach((index, glyph) -> {
                cells.put(texOffsets.get(glyph.c).x).put(texOffsets.get(glyph.c).y);
            });
            
            cells.flip();
            
            glBindBuffer(GL_ARRAY_BUFFER, vboTexOffset);
            glBufferData(GL_ARRAY_BUFFER, cells, GL_STATIC_DRAW);
        }
        
        glVertexAttribPointer(5, 2, GL_FLOAT, false, (2 * Float.BYTES), 0);
        glEnableVertexAttribArray(5);
        glVertexAttribDivisor(5, 1);
    }
    
    /**
     * Changes the color of each glyph instance according to its individual specifications.
     * 
     * @param glyphs the collection of glyphs that comprise the string being drawn
     * @see Glyph
     */
    private void offsetColor(Map<Integer, Glyph> glyphs) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer colors = stack.mallocFloat(glyphs.size() * Float.BYTES);
            
            glyphs.forEach((index, glyph) -> {
                colors.put(glyph.color.r).put(glyph.color.g).put(glyph.color.b);
            });
            
            colors.flip();
            
            glBindBuffer(GL_ARRAY_BUFFER, vboColOffset);
            glBufferData(GL_ARRAY_BUFFER, colors, GL_STATIC_DRAW);
        }
        
        glVertexAttribPointer(6, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        glEnableVertexAttribArray(6);
        glVertexAttribDivisor(6, 1);
    }
    
    public int getGlyphLeading(char c) { return (monospaced) ? data.cell.width : posOffsets.get(c).x; }
    public int getGlyphDescent(char c) { return (monospaced) ? 0 : posOffsets.get(c).y; }
    public int getCellWidth()          { return data.cell.width; }
    public int getCellHeight()         { return data.cell.height; }
    
    /**
     * Renders a string according to the specifications of the font.
     * 
     * @param glyphs  the collection of glyphs that comprise the string being drawn
     * @param changed if true, the glyphs will be updated to reflect the changes in the string of text
     */
    public void draw(Map<Integer, Glyph> glyphs, boolean changed) {
        ShaderCore.use("default");
        
        glBindTexture(GL_TEXTURE_2D, data.texture.handle);
        glBindVertexArray(data.vao);
        
        if(changed) {
            offsetPosition(glyphs);
            offsetTexture(glyphs);
            offsetColor(glyphs);
        }
        
        ShaderCore.setInt("uType", 1);
        
        glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, glyphs.size());
        ErrorUtil.checkGLError();
    }
    
}