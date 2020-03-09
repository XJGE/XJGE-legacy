package dev.theskidster.xjge.main;

import java.nio.IntBuffer;
import org.joml.Vector2i;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.AL10.AL_PAUSED;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryUtil.NULL;
import static dev.theskidster.xjge.audio.Audio.ALL_SOURCES;
import static dev.theskidster.xjge.hardware.InputDevice.*;
import dev.theskidster.xjge.puppet.discon.DisCon;
import dev.theskidster.xjge.puppets.Puppets;
import dev.theskidster.xjge.util.Event;
import static dev.theskidster.xjge.util.Event.PAUSE;
import dev.theskidster.xjge.util.LogLevel;
import dev.theskidster.xjge.util.Logger;
import dev.theskidster.xjge.util.ServiceLocator;

/**
 * @author J Hoffman
 * Created: Jan 14, 2020
 */

/**
 * Represents the applications window. Processes input and events from GLFW.
 */
final class Window {
    
    public int width;
    public int height;
    
    public final long handle;
    
    public float scale;
    
    private boolean firstMouse = true;
    public boolean connected[] = new boolean[4];
    
    public String title;
    public Vector2i resolution;
    public Vector2i position;
    
    /**
     * Creates a new window. Called once during application startup.
     * 
     * @param title the title of the window
     */
    public Window(String title) {
        this.title = title;
        
        findScale();
        findSize();
        findResolution();
        findStartPosition();
        
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        
        handle = glfwCreateWindow(width, height, title, NULL, NULL);
    }
    
    /**
     * Determines the scale of the window. The scale will be 60% of the screen size of the display device if fullscreen mode is disabled.
     */
    private void findScale() {
        scale = (App.getFullscreen()) ? 1 : 0.6f;
    }
    
    /**
     * Finds an appropriate size for this window.
     */
    private void findSize() {
        width  = Math.round(App.getVideoMode().width() * scale);
        height = Math.round(App.getVideoMode().height() * scale);
    }
    
    /**
     * Finds the resolution of this window relative to the aspect ratio of the current display device.
     */
    private void findResolution() {
        if(App.getAspectRatio().equals("4:3")) {
            resolution = new Vector2i(480, 360);
        } else {
            resolution = new Vector2i(480, 270);
        }
    }
    
    /**
     * Finds the start (center) position of the window on a display device.
     */
    private void findStartPosition() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer xStartBuf = stack.mallocInt(1);
            IntBuffer yStartBuf = stack.mallocInt(1);
            
            glfwGetMonitorPos(App.getDisplayHandle(), xStartBuf, yStartBuf);
            
            position = new Vector2i(
                    Math.round((App.getVideoMode().width() - width) / 2) + xStartBuf.get(), 
                    Math.round((App.getVideoMode().height() - height) / 2) + yStartBuf.get());
        }
    }
    
    /**
     * Makes the window visible and establishes callback events.
     */
    void show() {
        App.setWindowIcon("img_null.png");
        update();
        glfwShowWindow(handle);
        
        glfwSetKeyCallback(handle, (window, key, scancode, action, mods) -> {
            /*
            This callback is intended to be used ONLY for interacting directly with the 
            engine. All other input pertaining to gameplay should instead use a puppet 
            object and input device.
            */
            
            if(App.DEBUG_ALLOWED && key == GLFW_KEY_F1 && action == GLFW_PRESS) {
                App.setTerminalEnabled(!App.getTerminalEnabled());
            }
            if(App.getTerminalEnabled()) Puppets.TERMINAL.processInput(key, action);
            
            if(App.DEBUG_ALLOWED && key == GLFW_KEY_F2 && action == GLFW_PRESS) {
                App.setFreecamEnabled(!App.getFreecamEnabled());
            }
            if(App.getFreecamEnabled() && !App.getTerminalEnabled()) {
                if(key == GLFW_KEY_W) Puppets.FREECAM.pressed[0] = (action != GLFW_RELEASE);
                if(key == GLFW_KEY_A) Puppets.FREECAM.pressed[1] = (action != GLFW_RELEASE);
                if(key == GLFW_KEY_S) Puppets.FREECAM.pressed[2] = (action != GLFW_RELEASE);
                if(key == GLFW_KEY_D) Puppets.FREECAM.pressed[3] = (action != GLFW_RELEASE);
                
                Puppets.FREECAM.setSpeedBoostEnabled(mods == GLFW_MOD_SHIFT);
            }
        });
        
        glfwSetCursorPosCallback(handle, (window, xpos, ypos) -> {
            if(App.getFreecamEnabled() && !App.getTerminalEnabled()) {
                if(firstMouse) {
                    Puppets.FREECAM.prevX = xpos;
                    Puppets.FREECAM.prevY = ypos;
                    firstMouse = false;
                }
                
                Puppets.FREECAM.setDirection(xpos, ypos);
            } else {
                firstMouse = true;
            }
        });
        
        glfwSetMonitorCallback((monHandle, event) -> {
            switch(event) {
                case GLFW_CONNECTED:
                    App.setDisplayDevice(App.getDisplayID() + "", false);
                    Logger.log(LogLevel.INFO, 
                            "Display: " + App.getDisplayID() + " (" + 
                            App.getDisplayInfo() + ") connected.");
                    break;
                    
                case GLFW_DISCONNECTED:
                    App.findDisplayDevices();
                    Logger.log(LogLevel.WARNING, 
                            "Display: " + App.getDisplayID() + " (" + 
                            App.getDisplayInfo() + ") disconnected.");
                    break;
            }
        });
        
        glfwSetJoystickCallback((jid, event) -> {
            switch(event) {
                case GLFW_CONNECTED:
                    App.findInputDevices();
                    if(jid < GLFW_JOYSTICK_5) connected[jid] = true;
                    Logger.log(LogLevel.INFO, 
                            "Controller: \"" + App.getInputDeviceName(jid) + 
                            "\" connected at position " + jid + ".");
                    break;
                    
                case GLFW_DISCONNECTED:
                    App.findInputDevices();
                    Logger.log(LogLevel.WARNING, 
                            "Controller: \"" + App.getInputDeviceName(jid) +
                            "\" disconnected at position " + jid + ".");
                    
                    if(jid < GLFW_JOYSTICK_5) {
                        DisCon discon  = new DisCon(jid);
                        connected[jid] = false;
                        
                        switch(jid) {
                            case GLFW_JOYSTICK_1:
                                App.setInputDeviceEnabled(ALL_EXCEPT_1, false);
                                App.addUIComponent(GLFW_JOYSTICK_1, "discon " + jid, discon);
                                break;

                            case GLFW_JOYSTICK_2:
                                App.setInputDeviceEnabled(ALL_EXCEPT_2, false);
                                addDisCon(GLFW_JOYSTICK_2, discon);
                                break;

                            case GLFW_JOYSTICK_3:
                                App.setInputDeviceEnabled(ALL_EXCEPT_3, false);
                                addDisCon(GLFW_JOYSTICK_3, discon);
                                break;

                            case GLFW_JOYSTICK_4:
                                App.setInputDeviceEnabled(ALL_EXCEPT_4, false);
                                addDisCon(GLFW_JOYSTICK_4, discon);
                                break;
                        }

                        ServiceLocator.getAudio().pauseMusic();
                        ServiceLocator.getAudio().setSourceState(ALL_SOURCES, AL_PAUSED);
                        
                        Game.addEvent(jid, (Boolean) App.getViewportActive(jid));
                    }
                    break;
            }
        });
    }
    
    /**
     * Updates the window depending on changes to the applications current state.
     */
    void update() {
        findScale();
        findSize();
        findResolution();
        findStartPosition();
        
        if(App.getFullscreen()) {
            glfwSetWindowMonitor(
                    handle,
                    App.getDisplayHandle(),
                    position.x,
                    position.y,
                    App.getVideoMode().width(),
                    App.getVideoMode().height(),
                    App.getVideoMode().refreshRate());
        } else {
            glfwSetWindowMonitor(
                    handle,
                    NULL,
                    position.x,
                    position.y,
                    width,
                    height,
                    App.getVideoMode().refreshRate());
        }
        
        glfwSetWindowPos(handle, position.x, position.y);
        
        if(App.getVSync()) glfwSwapInterval(1);
        else               glfwSwapInterval(0);
    }
    
    /**
     * Convenience method. If the viewport with which the controller is associated is active, then viewport 0 will be used to display the message instead.
     * 
     * @param jid    the id number of the controller
     * @param discon the object used to display a message on the viewport
     */
    private void addDisCon(int jid, DisCon discon) {
        if(!App.getViewportActive(jid)) {
            App.addUIComponent(GLFW_JOYSTICK_1, "discon " + jid, discon);
        } else {
            App.addUIComponent(jid, "discon " + jid, discon);
        }
    }
    
}