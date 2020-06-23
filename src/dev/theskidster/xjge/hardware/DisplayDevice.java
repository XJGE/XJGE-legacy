package dev.theskidster.xjge.hardware;

import java.util.NavigableMap;
import java.util.TreeMap;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetVideoModes;
import org.lwjgl.glfw.GLFWVidMode;
import dev.theskidster.xjge.util.LogLevel;
import dev.theskidster.xjge.util.Logger;

/**
 * @author J Hoffman
 * Created: Jan 14, 2020
 */

/**
 * Represents a visual display device such as a monitor or television.
 */
public final class DisplayDevice {
    
    public final int id;
    
    public final long handle;
    
    public String info;
    public String aspect;
    public GLFWVidMode videoMode;
    public NavigableMap<String, GLFWVidMode> videoModes = new TreeMap<>(); 
    
    /**
     * Provides the information parsed from a display device as an object.
     * 
     * @param id     the unique number used to identify the device in other parts of the engine
     * @param handle the unique handle of the display device as recognized by GLFW
     */
    public DisplayDevice(int id, long handle) {
        this.id     = id;
        this.handle = handle;
        
        videoMode   = glfwGetVideoMode(handle);
        aspect      = findAspect(videoMode);
        info        = findInfo(videoMode);
        
        findVideoModes();
    }
    
    /**
     * Finds the aspect ratio of a display devices current video mode.
     * 
     * @param mode the video mode to parse information from
     * @return an aspect ratio represented as a string
     */
    public String findAspect(GLFWVidMode mode) {
        int gcd = findDivisor(mode.width(), mode.height());
        return mode.width() / gcd + ":" + mode.height() / gcd;
    }
    
    /**
     * Finds the greatest common divisor given two numbers.
     * 
     * @param width  the width of the video mode.
     * @param height the height of the video mode.
     * @return the greatest common divisor.
     */
    private int findDivisor(int width, int height) {
        return (height == 0) ? width : findDivisor(height, width % height);
    }
    
    /**
     * Generates a string which provides information about the display devices current video mode.
     * 
     * @param mode the video mode to parse information from.
     * @return A string detailing the video modes resolution, aspect ratio, and refresh rate.
     */
    private String findInfo(GLFWVidMode mode) { 
        return mode.width() + "x" + mode.height() + " " + findAspect(mode) + " " + mode.refreshRate() + "hz";
    }
    
    /**
     * Finds every video mode available to this device that's supported by the engine.
     */
    private void findVideoModes() {
        GLFWVidMode.Buffer modeBuf = glfwGetVideoModes(handle);
        
        if(modeBuf != null) {
            modeBuf.forEach(mode -> {
                switch(findAspect(mode)) {
                    case "4:3": case "16:9": case "85:48": case "683:384":
                        if(mode.refreshRate() <= 60 && mode.refreshRate() >= 29) {
                            videoModes.put(findInfo(mode), mode);
                        }
                        break;
                }
            });
            
            //We'll supply the default video mode of the current display device in the 
            //off chance it doesnt support one of our preferred aspect ratios.
            
            if(!videoModes.containsKey(info)) {
                videoModes.put(info, glfwGetVideoMode(handle));
            }
        } else {
            Logger.log(LogLevel.WARNING, 
                    "Unable to find additional video modes for " +
                    "display device " + id + " (" + info + ")");
        }
    }
    
}