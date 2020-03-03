package dev.theskidster.xjge.util;

/**
 * @author J Hoffman
 * Created: Jan 14, 2020
 */

/**
 * Immutable object used to represent color. 
 */
public final class Color {
    
    public static final Color WHITE     = new Color(1);
    public static final Color GRAY      = new Color(0.5f);
    public static final Color BLACK     = new Color();
    public static final Color RED       = new Color(255, 0, 0);
    public static final Color ORANGE    = new Color(255, 153, 0);
    public static final Color YELLOW    = new Color(255, 255, 0);
    public static final Color GREEN     = new Color(0, 255, 0);
    public static final Color CYAN      = new Color(0, 255, 255);
    public static final Color BLUE      = new Color(0, 0, 255);
    public static final Color PURPLE    = new Color(136, 0, 152);
    public static final Color PINK      = new Color(255, 0, 255);
    public static final Color BROWN     = new Color(70, 45, 10);
    public static final Color NAVY      = new Color(0, 0, 128);
    public static final Color SOFT_BLUE = new Color(92, 148, 252);
    public static final Color TEAL      = new Color(0, 128, 128);
    
    public final float r; 
    public final float g; 
    public final float b; 
    
    /**
     * Creates the color black.
     */
    private Color() {
        r = g = b = 0;
    }
    
    /**
     * Creates a new colorless shade (grays) using the scalar value specified
     * 
     * @param scalar the value all color components of this color will be set to. Between 0 and 1.
     */
    private Color(float scalar) {
        r = g = b = scalar;
    }
    
    /**
     * Creates a new color using the three RGB components supplied. Values supplied are expected to be between 0 and 255.
     * 
     * @param r the red color component
     * @param g the green color component
     * @param b the blue color component
     */
    private Color(int r, int g, int b) {
        this.r = (r / 255f);
        this.g = (g / 255f);
        this.b = (b / 255f);
    }
    
    /**
     * Creates a new color using the three RGB components supplied. Values supplied are expected to be between 0 and 255.
     * 
     * @param r the red color component
     * @param g the green color component
     * @param b the blue color component
     * @return  the new color generated with these components
     */
    public static Color create(int r, int g, int b) {
        return new Color(r, g, b);
    }
    
    /**
     * Creates a random color.
     * 
     * @return a random color
     */
    public static Color random() {
        return new Color(
                (int) (Math.random() * 255),
                (int) (Math.random() * 255),
                (int) (Math.random() * 255));
    }
    
}