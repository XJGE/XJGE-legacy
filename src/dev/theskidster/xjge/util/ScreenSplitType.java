package dev.theskidster.xjge.util;

/**
 * @author J Hoffman
 * Created: Jan 15, 2020
 */

/**
 * Used to determine the method in which the screen will be divided among each {@link dev.theskidster.xjge.main.Viewport Viewport}.
 * 
 * @see NO_SPLIT
 * @see VERTICAL
 * @see HORIZONTAL
 * @see TRIPLE
 * @see QUADRUPLE
 */
public enum ScreenSplitType {
    
    /**
     * The screen will not be split. Only viewport 0 will be visible.
     * <br><br>
     * EXAMPLE:
     * <br>
     * |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
     * |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;0&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
     * |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
     * 
     * @see ScreenSplitType
     */
    NO_SPLIT,
    
    /**
     * The screen will be split vertically. Only viewports 0 and 1 will be visible.
     * <br><br>
     * EXAMPLE:
     * <br>
     * |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
     * |&nbsp;&nbsp;0&nbsp;&nbsp;|&nbsp;&nbsp;1&nbsp;&nbsp;|<br>
     * |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
     * 
     * @see ScreenSplitType
     */
    VERTICAL,
    
    /**
     * The screen will be split horizontally. Only viewports 0 and 1 will be visible.
     * <br><br>
     * EXAMPLE:
     * <br>
     * |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;0&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
     * |-----------|<br>
     * |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
     * 
     * @see ScreenSplitType
     */
    HORIZONTAL,
    
    /**
     * The screen will be split three times. Viewports 0, 1, and 2 will be visible.
     * <br><br>
     * EXAMPLE:
     * <br>
     * |&nbsp;&nbsp;0&nbsp;&nbsp;|&nbsp;&nbsp;1&nbsp;&nbsp;|<br>
     * |-----------|<br>
     * |XX|&nbsp;&nbsp;2&nbsp;&nbsp;|XX|
     * 
     * @see ScreenSplitType
     */
    TRIPLE,
    
    /**
     * The screen will be split evenly four times. All viewports will be visible.
     * <br><br>
     * EXAMPLE:
     * <br>
     * |&nbsp;&nbsp;0&nbsp;&nbsp;|&nbsp;&nbsp;1&nbsp;&nbsp;|<br>
     * |-----+-----|<br>
     * |&nbsp;&nbsp;2&nbsp;&nbsp;|&nbsp;&nbsp;3&nbsp;&nbsp;|
     * 
     * @see ScreenSplitType
     */
    QUADRUPLE
}