package dev.theskidster.xjge.main;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import static org.lwjgl.openal.ALC11.*;
import static org.lwjgl.openal.ALUtil.*;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryUtil.NULL;
import dev.theskidster.xjge.audio.Audio;
import dev.theskidster.xjge.util.Camera;
import dev.theskidster.xjge.hardware.AudioDevice;
import dev.theskidster.xjge.hardware.Controller;
import dev.theskidster.xjge.hardware.DisplayDevice;
import dev.theskidster.xjge.hardware.InputDevice;
import static dev.theskidster.xjge.hardware.InputDevice.*;
import dev.theskidster.xjge.hardware.Keyboard;
import dev.theskidster.xjge.level.Level;
import dev.theskidster.xjge.puppet.freecam.Freecam;
import dev.theskidster.xjge.puppets.Puppet;
import dev.theskidster.xjge.puppets.Puppets;
import dev.theskidster.xjge.shader.core.BufferType;
import dev.theskidster.xjge.shader.core.ShaderCore;
import dev.theskidster.xjge.shader.core.ShaderProgram;
import dev.theskidster.xjge.shader.core.ShaderSource;
import dev.theskidster.xjge.ui.Component;
import dev.theskidster.xjge.ui.InputInfo;
import dev.theskidster.xjge.ui.RectangleBatch;
import dev.theskidster.xjge.ui.RuntimeInfo;
import dev.theskidster.xjge.ui.SystemInfo;
import dev.theskidster.xjge.util.Color;
import dev.theskidster.xjge.util.ScreenSplitType;
import static dev.theskidster.xjge.util.ScreenSplitType.*;
import dev.theskidster.xjge.util.ServiceLocator;

/**
 * @author J Hoffman
 * Created: Jan 14, 2020
 */

/**
 * Manages application state including the graphics pipeline, audio engine, viewports for
 * split screen functionality, window, debugging utilities, and peripheral devices. 
 */
public final class App {
    
    private static int fbo;
    
    private static boolean fullscreen;
    private static boolean vsync = true;
    private static boolean showInputInfo;
    private static boolean showLightSources;
    private static boolean showRuntimeInfo;
    private static boolean showSystemInfo;
    private static boolean terminalEnabled;
    private static boolean freecamEnabled;
    
    public static final int MAX_WEIGHTS       = 4;
    public static final int MAX_TEXTURES      = 4;
    public static final int MAX_LIGHTS        = 32;
    public static final int MAX_BONES         = 128;
    public static final int MAX_ANIM_SPEED    = 5;
    public static final int ALL_VIEWPORTS     = -1;
    public static final boolean DEBUG_ALLOWED = true; //TODO change this to false before building distributions.
    public static final String DOMAIN         = "xjge";
    public static final String ENGINE_VERSION = "1.5.3";
    public static final String GAME_VERSION   = "0";
    
    private static final Viewport[] viewports = new Viewport[4];
    private static Color clearColor           = Color.BLACK;
    private static ScreenSplitType split;
    
    private static AudioDevice audioDevice;
    private static DisplayDevice displayDevice;
    private static Window window;
    
    private static final NavigableMap<Integer, AudioDevice> audioDevices     = new TreeMap<>();
    private static final NavigableMap<Integer, DisplayDevice> displayDevices = new TreeMap<>();
    private static final Map<Integer, InputDevice> inputDevices              = new HashMap<>();
    
    /**
     * Initializes utilities required by the application then enters the {@link Game#loop()}.
     */
    void start() {
        if((System.getProperty("java.version")).compareTo("15.0.2") < 0) {
            Logger.logSevere( 
                    "Unsupported java version. required 15.0.2, " +
                    "found " + System.getProperty("java.version"),
                    null);
        }
        
        if(!glfwInit()) Logger.logSevere("Failed to initialize GLFW.", null);
        
        findAudioDevices();
        findDisplayDevices();
        
        audioDevice   = audioDevices.get(0);
        displayDevice = displayDevices.get(0);
        window        = new Window("Extensible Java Game Engine (" + ENGINE_VERSION + ")"); //TODO change window title.
        
        findInputDevices();
        
        alInit();
        glInit();
        
        inputDevices.put(KEYBOARD, new Keyboard(KEYBOARD));
        Logger.logSystemInfo();
        window.show();
        
        new Game().loop();
        
        audioDevices.forEach((id, device) -> alcCloseDevice(device.handle));
        ShaderCore.deleteAll();
        GL.destroy();
        glfwTerminate();
    }
    
    /**
     * Establishes the audio engine and sets the current OpenAL context to the default audio device found in {@link start()}.
     * 
     * @see dev.theskidster.xjge.audio
     */
    private void alInit() {
        ServiceLocator.setAudio(new Audio());
        audioDevice.setContextCurrent();
    }
    
    /**
     * Establishes the graphics pipeline. Any shader programs required by the implementation should be defined here.
     * <p>
     * If you wish to provide additional shader programs do so following the Model View Projection structure of the default program otherwise {@link Camera} 
     * objects will fail. Remember to clear the shaderSources collection between program definitions.
     * 
     * @see dev.theskidster.xjge.shader.core
     */
    private void glInit() {
        glfwMakeContextCurrent(window.handle);
        GL.createCapabilities();
        
        for(int i = 0; i < viewports.length; i++) {
            viewports[i] = new Viewport(i);
        }
        
        fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, viewports[0].texHandle, 0);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, viewports[1].texHandle, 0);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT2, GL_TEXTURE_2D, viewports[2].texHandle, 0);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT3, GL_TEXTURE_2D, viewports[3].texHandle, 0);
            createRenderbuffer(GL_DEPTH_COMPONENT, GL_DEPTH_ATTACHMENT);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        
        ErrorUtil.checkFBStatus(GL_FRAMEBUFFER);
        
        var shaderSources  = new ArrayList<ShaderSource>();
        var shaderPrograms = new HashMap<String, ShaderProgram>();
        
        //TODO (OPTIONAL) Define additional shaders in a scope here.
        
        {
            shaderSources.add(new ShaderSource("defaultVertex.glsl",   GL_VERTEX_SHADER));
            shaderSources.add(new ShaderSource("defaultFragment.glsl", GL_FRAGMENT_SHADER));
            
            ShaderProgram program = new ShaderProgram(shaderSources);
            
            shaderPrograms.put("default", program);
            glUseProgram(program.handle);
            
            program.addUniform(BufferType.MAT4, "uModel");
            program.addUniform(BufferType.MAT4, "uView");
            program.addUniform(BufferType.MAT4, "uProjection");
            program.addUniform(BufferType.INT,  "uType");
            program.addUniform(BufferType.VEC2, "uTexCoords");
            program.addUniform(BufferType.MAT3, "uNormal");
            program.addUniform(BufferType.VEC3, "uColor");
            program.addUniform(BufferType.INT, "uNumLights");
            program.addUniform(BufferType.MAT4, "uBoneTransforms");
            
            for(int i = 0; i < MAX_LIGHTS; i++) {
                program.addUniform(BufferType.FLOAT, "uLights[" + i + "].brightness");
                program.addUniform(BufferType.FLOAT, "uLights[" + i + "].contrast");
                program.addUniform(BufferType.VEC3, "uLights[" + i + "].position");
                program.addUniform(BufferType.VEC3, "uLights[" + i + "].ambient");
                program.addUniform(BufferType.VEC3, "uLights[" + i + "].diffuse");
            }
        }
        
        ShaderCore.init(shaderPrograms);
        ShaderCore.use("default");
    }
    
    /**
     * Generates a new Renderbuffer object using the format provided and attaches it to the location specified on the Framebuffer.
     * 
     * @param internalformat the internal format to use for the Renderbuffer objects image. Must be a color-renderable, depth-renderable, or stencil-renderable 
     *                       format.
     * @param attachment     the attachment point of the Framebuffer.
     */
    private void createRenderbuffer(int internalformat, int attachment) {
        int rbo = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rbo);
        
        glRenderbufferStorage(GL_RENDERBUFFER, internalformat, window.width, window.height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, attachment, GL_RENDERBUFFER, rbo);
        
        ErrorUtil.checkGLError();
    }
    
    /**
     * Finds every available audio device currently connected to the system and adds it to a collection of {@link AudioDevice} objects which can be used for 
     * audio output. 
     */
    static void findAudioDevices() {
        audioDevices.forEach((id, device) -> alcCloseDevice(device.handle));
        audioDevices.clear();
        
        var deviceList = getStringList(NULL, ALC_ALL_DEVICES_SPECIFIER);
        
        if(deviceList != null) {
            for(int i = 0; i < deviceList.size(); i++) {
                audioDevices.put(i, new AudioDevice(i, deviceList.get(i)));
            }
        } else {
            Logger.logSevere("No available audio devices found.", null);
        }
    }
    
    /**
     * Finds every available display device currently connected to the system and adds it to a collection of  {@link DisplayDevice} objects which can be used 
     * for viewing the output of the applications graphics pipeline.
     */
    static void findDisplayDevices() {
        PointerBuffer displayBuf = glfwGetMonitors();
        
        if(displayBuf != null) {
            for(int i = 0; i < displayBuf.limit(); i++) {
                displayDevices.put(i, new DisplayDevice(i, displayBuf.get(i)));
            }
        } else {
            Logger.logSevere("No available display devices found.", null);
        }
    }
    
    /**
     * Finds every available controller currently connected to the system and adds it to a collection of {@link Controller} objects which may be used for player 
     * input.
     */
    static void findInputDevices() {
        for(int i = 0; i < GLFW_JOYSTICK_5; i++) {
            if(inputDevices.containsKey(i)) {
                if(glfwJoystickPresent(i)) {
                    inputDevices.put(i, new Controller((Controller) inputDevices.get(i)));
                }
            } else {
                if(glfwJoystickPresent(i)) {
                    inputDevices.put(i, new Controller(i));
                    if(i < GLFW_JOYSTICK_5) window.connected[i] = true;
                }
            }
        }
    }
    
    /**
     * {@linkplain InputDevice#poll() Polls} the input of each connected input device.
     */
    static void pollInput() {
        inputDevices.forEach((deviceID, device) -> {
            if(device.enabled) device.poll();
        });
    }
    
    /**
     * Generates new viewport objects using their previous states. Called whenever some operation performed by the application invalidates the current state of 
     * the viewports. 
     */
    private static void resetViewports() {
        for(int i = 0; i < viewports.length; i++) {
            viewports[i] = new Viewport(viewports[i]);
        }
        
        setSplitType(split);
    }
    
    /**
     * Updates each viewports UI components and current {@link Camera} object.
     * 
     * @see Viewport
     * @see dev.theskidster.xjge.ui
     * @see dev.theskidster.xjge.ui.Component
     */
    static void updateViewports() {
        for(Viewport viewport : viewports) {
            if(viewport.active && viewport.currCamera != null) {
                viewport.currCamera.update();
                viewport.ui.forEach((name, component) -> component.update());
                
                ServiceLocator.getAudio().setViewportCamData(viewport.id, viewport.currCamera.position, viewport.currCamera.direction);
            }
        }
        
        ServiceLocator.getAudio().updateSourcePositions();
    }
    
    /**
     * Renders the perspective of the game from each active viewport.
     * 
     * @param level the current level of the game to render
     * @param proj  an additional projection matrix used to align the Framebuffer image produced by the viewport
     */
    static void renderViewports(Level level, Matrix4f proj) {
        for(Viewport viewport : viewports) {
            if(viewport.active) {
                if(viewport.id == 0) {
                    glClearColor(0, 0, 0, 0);
                    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                }
                
                glBindFramebuffer(GL_FRAMEBUFFER, fbo);
                    glViewport(0, 0, viewport.width, viewport.height);
                    glClearColor(clearColor.r, clearColor.g, clearColor.b, 0);
                    switch(viewport.id) {
                        case 0 -> glDrawBuffer(GL_COLOR_ATTACHMENT0);
                        case 1 -> glDrawBuffer(GL_COLOR_ATTACHMENT1);
                        case 2 -> glDrawBuffer(GL_COLOR_ATTACHMENT2);
                        case 3 -> glDrawBuffer(GL_COLOR_ATTACHMENT3);
                    }
                    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                    
                    viewport.resetCamera();
                    
                    viewport.render("camera");
                    level.renderSkybox(viewport.currCamera.viewMatrix);
                    level.render(viewport.currCamera);
                    level.renderLightSources(viewport.currCamera.position, viewport.currCamera.direction, viewport.currCamera.up);
                    viewport.render("ui");
                glBindFramebuffer(GL_FRAMEBUFFER, 0);
                
                glViewport(viewport.botLeft.x, viewport.botLeft.y, viewport.topRight.x, viewport.topRight.y);
                proj.setOrtho(viewport.width, 0, 0, viewport.height, 0, 1);
                ShaderCore.setMat4("uProjection", false, proj);
                viewport.render("texture");
            }
        }
    }
    
    /**
     * Ceases application execution and exits gracefully.
     */
    public static void terminate() {
        glfwSetWindowShouldClose(window.handle, true);
    }
    
    public static boolean getFullscreen()        { return fullscreen; }
    public static boolean getVSync()             { return vsync; }
    public static boolean getShowRuntimeInfo()   { return showRuntimeInfo; }
    public static boolean getShowSystemInfo()    { return showSystemInfo; }
    public static boolean getShowInputInfo()     { return showInputInfo; }
    public static boolean getShowLightSources()  { return showLightSources; }
    public static boolean getTerminalEnabled()   { return terminalEnabled; }
    public static boolean getFreecamEnabled()    { return freecamEnabled; }
    public static String getAudioDeviceName()    { return audioDevice.name; }
    public static int getNumAudioDevices()       { return audioDevices.size(); }
    public static long getDisplayHandle()        { return displayDevice.handle; }
    public static String getDisplayInfo()        { return displayDevice.info; }
    public static int getDisplayID()             { return displayDevice.id; }
    public static String getAspectRatio()        { return displayDevice.aspect; }
    public static int getNumDisplayDevices()     { return displayDevices.size(); }
    public static GLFWVidMode getVideoMode()     { return displayDevice.videoMode; }
    public static long getWindowHandle()         { return window.handle; }
    public static int getWindowWidth()           { return window.width; }
    public static int getWindowHeight()          { return window.height; }
    public static Vector2i getResolution()       { return window.resolution; }
    public static Vector2i getWindowPos()        { return window.position; }
    public static ScreenSplitType getSplitType() { return split; }
    public static Color getClearColor()          { return clearColor; }
    public static int getNumInputDevices()       { return inputDevices.size(); }
    public static boolean getInputDevicePresent(int id)   { return (id != KEYBOARD) ? inputDevices.containsKey(id) && window.connected[id] : true; }
    public static float getInputDeviceSensitivity(int id) { return (getInputDevicePresent(id)) ? inputDevices.get(id).sensitivity : 0; }
    public static boolean getInputDeviceEnabled(int id)   { return (getInputDevicePresent(id)) ? inputDevices.get(id).enabled : false; }
    public static String getInputDeviceName(int id)       { return (getInputDevicePresent(id)) ? inputDevices.get(id).name : "N/A"; }
    public static Puppet getInputDevicePuppet(int id)     { return (getInputDevicePresent(id)) ? inputDevices.get(id).puppets.peek() : null; }
    public static boolean getViewportActive(int id)       { return viewports[id].active; }
    
    /**
     * Sets the application to use fullscreen or windowed mode.
     * 
     * @param value if true, the application will enter fullscreen mode. Supplying false will change it to windowed mode.
     */
    public static void setFullscreen(boolean value) {
        fullscreen = value;
        window.update();
        resetViewports();
        Logger.logInfo("Fullscreen changed: (" + fullscreen + ")");
    }
    
    /**
     * Determines whether the application will take advantage of vertical sync (or VSync). VSync is enabled by default on startup.
     * 
     * @param value if true, VSync will be enabled. Supplying false will disable it.
     */
    public static void setVSync(boolean value) {
        vsync = value;
        
        if(vsync) glfwSwapInterval(1);
        else      glfwSwapInterval(0);
        
        Logger.logInfo("VSync changed: (" + vsync + ")");
    }
    
    /**
     * Provides information pertaining to the current state of the engine at runtime. Calling this will disable 
     * {@link setShowSystemInfo(boolean) setShowSystemInfo()} or {@link setShowInputInfo(boolean) setShowInputInfo()} if either is active.
     * 
     * @param value if true, the component will be visible. Supplying false will hide it.
     */
    public static void setShowRuntimeInfo(boolean value) {
        showRuntimeInfo = value;
        
        if(showRuntimeInfo) {
            if(showSystemInfo) setShowSystemInfo(false);
            if(showInputInfo)  setShowInputInfo(false);
            
            addUIComponent(0, "runtime info", new RuntimeInfo());
        } else {
            removeUIComponent(0, "runtime info");
        }
    }
    
    /**
     * Provides information about the architecture on which the engine is currently running. Calling this will disable 
     * {@link setShowRuntimeInfo(boolean) setShowRuntimeInfo()} or {@link setShowInputInfo(boolean) setShowInputInfo()} if either is active.
     * 
     * @param value if true, the component will be visible. Supplying false will hide it.
     */
    public static void setShowSystemInfo(boolean value) {
        showSystemInfo = value;
        
        if(showSystemInfo) {
            if(showRuntimeInfo) setShowRuntimeInfo(false);
            if(showInputInfo)   setShowInputInfo(false);
            
            addUIComponent(0, "system info", new SystemInfo());
        } else {
            removeUIComponent(0, "system info");
        }
    }
    
    /**
     * Provides information regarding connected input devices. Calling this will disable {@link setShowRuntimeInfo(boolean) setShowRuntimeInfo()} or
     * {@link setShowSystemInfo(boolean) setShowSystemInfo()} if either is active.
     * 
     * @param value if true, the component will be visible. Supplying false will hide it.
     */
    public static void setShowInputInfo(boolean value) {
        showInputInfo = value;
        
        if(showInputInfo) {
            if(showRuntimeInfo) setShowRuntimeInfo(false);
            if(showSystemInfo)  setShowSystemInfo(false);
            
            addUIComponent(0, "input info", new InputInfo());
        } else {
            removeUIComponent(0, "input info");
        }
    }
    
    /**
     * Exposes the locations of all {@link dev.theskidster.xjge.graphics.LightSource LightSource} objects inhabiting the current {@link Level}.
     * 
     * @param value true to expose the light source locations or false to hide them
     */
    public static void setShowLightSources(boolean value) {
        showLightSources = value;
        
        if(showLightSources) Logger.logInfo("Light source locations visible.");
        else                 Logger.logInfo("Light source locations hidden.");
    }
    
    /**
     * Provides access to the engines debugging utilities via the {@linkplain dev.theskidster.xjge.puppet.terminal.Terminal command line terminal}. While open, 
     * the command terminal will disable free roaming camera movement.
     * 
     * @param value if true, the command terminal will be opened. Supplying false will close it.
     */
    public static void setTerminalEnabled(boolean value) {
        terminalEnabled    = value;
        InputDevice device = inputDevices.get(KEYBOARD);
        
        if(terminalEnabled) {
            Puppets.TERMINAL.setSplitPosition();
            Puppets.TERMINAL.rectBatch = new RectangleBatch(1);
            addUIComponent(0, "terminal", Puppets.TERMINAL);
            device.setEnabled(false);
        } else {
            Puppets.TERMINAL.rectBatch.destroy();
            removeUIComponent(0, "terminal");
            
            try {
                device.enableStates.pop();
                device.enabled = device.enableStates.peek();
            } catch(EmptyStackException e) {
                device.enabled = true;
            }
        }
    }
    
    /**
     * Sets the camera of viewport 0 to one which allows free exploration of the current level. Cannot be used while the command terminal is open.
     * 
     * @param value if true, viewport 0 will use a free roaming camera. Supplying false will return the viewport to its previously bound camera object.
     */
    public static void setFreecamEnabled(boolean value) {
        InputDevice device = inputDevices.get(KEYBOARD);
        
        if(!terminalEnabled) {
            freecamEnabled = value;
                
            if(freecamEnabled) {
                glfwSetInputMode(window.handle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                setViewportCamera(0, Puppets.FREECAM);
                device.setEnabled(false);
            } else {
                glfwSetInputMode(window.handle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                setViewportCameraPrev(0);
                
                try {
                    device.enableStates.pop();
                    device.enabled = device.enableStates.peek();
                } catch(EmptyStackException e) {
                    device.enabled = true;
                }
            }
        } else {
            Logger.logInfo(
                    "Freecam access denied, command terminal is currently " + 
                    "in use. Close the command terminal and try again.");
        }
    }
    
    /**
     * Sets the current display device of the application. Doing so will move the window to that screen.
     * 
     * @param operation the method of traversal either explicitly as the ID number of the device or "prev"/"next" to move to the previous or next device in the 
     *                  list
     * @param safely    an additional argument used to indicate whether this action should be performed in a platform agnostic manner. Required only when the 
     *                  change is a result of a display device being abruptly disconnected.
     */
    public static void setDisplayDevice(String operation, boolean safely) {
        boolean wasFullscreen = fullscreen;
        
        if(safely) {
            if(wasFullscreen) setFullscreen(false);
            
            try {
                //Added to fix the display change bug on Linux.
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Logger.logSevere(e.getMessage(), e);
            }
        }
        
        findDisplayDevices();
        
        if(displayDevices.size() > 0) {
            switch(operation) {
                case "next" -> {
                    if(!displayDevices.ceilingKey(displayDevices.lastKey()).equals(displayDevice.id)) {
                        displayDevice = displayDevices.higherEntry(displayDevice.id).getValue();
                    } else {
                        displayDevice = displayDevices.firstEntry().getValue();
                    }
                    window.update();
                    resetViewports();
                    Logger.logInfo("Set current display device to " + displayDevice.id);
                }

                case "prev" -> {
                    if(!displayDevices.floorKey(displayDevices.firstKey()).equals(displayDevice.id)) {
                        displayDevice = displayDevices.lowerEntry(displayDevice.id).getValue();
                    } else {
                        displayDevice = displayDevices.lastEntry().getValue();
                    }
                    window.update();
                    resetViewports();
                    Logger.logInfo("Set current display device to " + displayDevice.id);
                }

                default -> {
                    try {
                        int index = Integer.parseInt(operation);
                        
                        if(displayDevices.containsKey(index)) {
                            displayDevice = displayDevices.get(index);
                            window.update();
                            resetViewports();
                            Logger.logInfo("Set current display device to " + displayDevice.id);
                        } else {
                            Logger.logWarning( 
                                    "Failed to set display device. Could not find device at index " + index + ".",
                                    null);
                        }
                    } catch(NumberFormatException e) {
                        Logger.logWarning("Failed to set video mode. Invalid index value passed.", null);
                    }
                }
            }
        } else {
            Logger.logWarning("No display devices currently connected.", null);
        }
        
        if(safely && wasFullscreen) setFullscreen(true);
    }
    
    /**
     * Sets the video mode of the current display device.
     * 
     * @param operation the method of traversal either explicitly as the index number of the video mode or "prev"/"next" to move to the previous or next mode in 
     *                  the list
     */
    public static void setVideoMode(String operation) {
        switch(operation) {
            case "next" -> {
                if(!displayDevice.videoModes.ceilingKey(displayDevice.videoModes.lastKey()).equals(displayDevice.info)) {
                    displayDevice.videoMode = displayDevice.videoModes.higherEntry(displayDevice.info).getValue();
                    displayDevice.info      = displayDevice.videoModes.higherEntry(displayDevice.info).getKey();
                } else {
                    displayDevice.videoMode = displayDevice.videoModes.firstEntry().getValue();
                    displayDevice.info      = displayDevice.videoModes.firstEntry().getKey();
                }
                Logger.logInfo(
                        "Set current video mode to " + displayDevice.info +
                        " for display device " + displayDevice.id);
            }

            case "prev" -> {
                if(!displayDevice.videoModes.floorKey(displayDevice.videoModes.firstKey()).equals(displayDevice.info)) {
                    displayDevice.videoMode = displayDevice.videoModes.lowerEntry(displayDevice.info).getValue();
                    displayDevice.info      = displayDevice.videoModes.lowerEntry(displayDevice.info).getKey();
                } else {
                    displayDevice.videoMode = displayDevice.videoModes.lastEntry().getValue();
                    displayDevice.info      = displayDevice.videoModes.lastEntry().getKey();
                }
                Logger.logInfo(
                        "Set current video mode to " + displayDevice.info +
                        " for display device " + displayDevice.id);
            }

            default -> {
                try {
                    int index = Integer.parseInt(operation);
                    
                    var tempInfo  = new ArrayList<String>();
                    var tempModes = new ArrayList<GLFWVidMode>();

                    displayDevice.videoModes.forEach((info, mode) -> {
                        tempInfo.add(info);
                        tempModes.add(mode);
                    });
                    
                    if(tempInfo.get(index) != null && tempModes.get(index) != null) {
                        displayDevice.videoMode = tempModes.get(index);
                        displayDevice.info      = tempInfo.get(index);
                        Logger.logInfo(
                                "Set current video mode to " + displayDevice.info +
                                        " for display device " + index);
                    } else {
                        Logger.logWarning(
                                "Failed to set video mode. Could not find video " +
                                        "mode at index " + index + ".",
                                null);
                    }
                } catch(NumberFormatException | IndexOutOfBoundsException e) {
                    Logger.logWarning("Failed to set video mode. Invalid index value passed.", null);
                }
            }
        }
        
        displayDevice.aspect = displayDevice.findAspect(displayDevice.videoMode);
        window.update();
        resetViewports();
        updateViewports();
    }
    
    /**
     * Sets the icon image of the window. Images should be at least 32x32 pixels large, but no larger than 64x64.
     * 
     * @param filename the name of the file to load. Expects the file extension to be included.
     */
    public static void setWindowIcon(String filename) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            InputStream file = App.class.getResourceAsStream("/dev/theskidster/" + DOMAIN + "/assets/" + filename);
            byte[] data      = file.readAllBytes();
            
            IntBuffer widthBuf   = stack.mallocInt(1);
            IntBuffer heightBuf  = stack.mallocInt(1);
            IntBuffer channelBuf = stack.mallocInt(1);
            
            ByteBuffer icon = stbi_load_from_memory(
                    stack.malloc(data.length).put(data).flip(),
                    widthBuf,
                    heightBuf,
                    channelBuf,
                    STBI_rgb_alpha);
            
            glfwSetWindowIcon(window.handle, GLFWImage.mallocStack(1, stack)
                    .width(widthBuf.get())
                    .height(heightBuf.get())
                    .pixels(icon));
            
            stbi_image_free(icon);
            
        } catch(IOException e) {
            Logger.logWarning("Failed to set window icon: \"" + filename + "\"", null);
        }
    }
    
    /**
     * Sets the type of {@linkplain ScreenSplitType Split} to be used by the viewports during split screen.
     * 
     * @param value the type of split to use. One of {@link ScreenSplitType#NO_SPLIT NO_SPLIT}, {@link ScreenSplitType#VERTICAL VERTICAL}, 
     *              {@link ScreenSplitType#HORIZONTAL HORIZONTAL}, {@link ScreenSplitType#TRIPLE TRIPLE}, or {@link ScreenSplitType#QUADRUPLE QUADRUPLE}.
     */
    public static void setSplitType(ScreenSplitType value) {
        split = value;
        
        for(Viewport viewport : viewports) {
            switch(split) {
                case NO_SPLIT -> {
                    viewport.active = (viewport.id == 0);
                    viewport.setBounds(
                            window.resolution.x, window.resolution.y,
                            0, 0,
                            window.width, window.height);
                }
                   
                case VERTICAL -> {
                    viewport.active = (viewport.id == 0 || viewport.id == 1);
                    switch(viewport.id) {
                        case 0 -> viewport.setBounds(
                                    window.resolution.x / 2, window.resolution.y,
                                    0, 0, 
                                    window.width / 2, window.height);
                            
                        case 1 -> viewport.setBounds(
                                    window.resolution.x / 2, window.resolution.y,
                                    window.width / 2, 0, 
                                    window.width / 2, window.height);
                    }
                }
                    
                case HORIZONTAL -> {
                    viewport.active = (viewport.id == 0 || viewport.id == 1);
                    switch(viewport.id) {
                        case 0 -> viewport.setBounds(
                                    window.resolution.x, window.resolution.y / 2,
                                    0, window.height / 2, 
                                    window.width, window.height / 2);
                            
                        case 1 -> viewport.setBounds(
                                    window.resolution.x, window.resolution.y / 2,
                                    0, 0, 
                                    window.width, window.height / 2);
                    }
                }
                    
                case TRIPLE -> {
                    viewport.active = (viewport.id != 3);
                    switch(viewport.id) {
                        case 0 -> viewport.setBounds(
                                    window.resolution.x / 2, window.resolution.y / 2,
                                    0, window.height / 2, 
                                    window.width / 2, window.height / 2);
                            
                        case 1 -> viewport.setBounds(
                                    window.resolution.x / 2, window.resolution.y / 2,
                                    window.width / 2, window.height / 2, 
                                    window.width / 2, window.height / 2);
                            
                        case 2 -> viewport.setBounds(
                                    window.resolution.x / 2, window.resolution.y / 2,
                                    window.width / 4, 0, 
                                    window.width / 2, window.height / 2);
                    }
                }
                    
                case QUADRUPLE -> {
                    viewport.active = true;
                    switch(viewport.id) {
                        case 0 -> viewport.setBounds(
                                    window.resolution.x / 2, window.resolution.y / 2,
                                    0, window.height / 2, 
                                    window.width / 2, window.height / 2);
                            
                        case 1 -> viewport.setBounds(
                                    window.resolution.x / 2, window.resolution.y / 2,
                                    window.width / 2, window.height / 2, 
                                    window.width / 2, window.height / 2);
                            
                        case 2 -> viewport.setBounds(
                                    window.resolution.x / 2, window.resolution.y / 2,
                                    0, 0, 
                                    window.width / 2, window.height / 2);
                            
                        case 3 -> viewport.setBounds(
                                    window.resolution.x / 2, window.resolution.y / 2,
                                    window.width / 2, 0, 
                                    window.width / 2, window.height / 2);
                    }
                }
            }
        }
    }
    
    /**
     * Sets the current camera of the viewport specified to the camera provided.
     * 
     * @param id     the unique number used to identify the viewport in other parts of the engine. Can be a 
     *               {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK} value or ALL_VIEWPORTS to effect each viewport.
     * @param camera the {@link Camera} object to be used by the viewport
     */
    public static void setViewportCamera(int id, Camera camera) {
        if(camera == null) camera = new Freecam();
        
        switch(id) {
            case 0, 1, 2, 3 -> {
                viewports[id].prevCamera = viewports[id].currCamera;
                viewports[id].currCamera = camera;
            }
            
            case ALL_VIEWPORTS -> {
                for(Viewport viewport : viewports) {
                    viewport.prevCamera = viewport.currCamera;
                    viewport.currCamera = camera;
                }
            }
        }
    }
    
    /**
     * Convenience method provided to revert a viewport back to its previously bound camera object.
     * 
     * @param id the unique number used to identify the viewport in other parts of the engine. Can be a {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK} 
     *           value or ALL_VIEWPORTS to effect each viewport.
     */
    public static void setViewportCameraPrev(int id) {
        switch(id) {
            case 0, 1, 2, 3 -> viewports[id].currCamera = viewports[id].prevCamera;
            
            case ALL_VIEWPORTS -> {
                for(Viewport viewport : viewports) {
                    viewport.currCamera = viewport.prevCamera;
                }
            }
        }
    }
    
    /**
     * Used to update the vectors of a viewport camera.
     * 
     * @param id  the unique number used to identify the viewport in other parts of the engine
     * @param pos the position of the camera in the game world
     * @param dir the direction in which the camera's pointing
     * @param up  the direction that's considered upwards relative to the camera
     * @see Camera
     */
    public static void setViewportCameraVectors(int id, Vector3f pos, Vector3f dir, Vector3f up) {
        viewports[id].currCamera.position  = pos;
        viewports[id].currCamera.direction = dir;
        viewports[id].currCamera.up        = up;
    }
    
    /**
     * Adds a {@linkplain Component UI Component} to the viewport(s) specified.
     * 
     * @param id        the unique number used to identify the viewport in other parts of the engine. Can be a 
     *                  {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK} value or ALL_VIEWPORTS to effect each viewport.
     * @param name      the name that will be used to identify and remove the component later
     * @param component the component object to use
     * @see Viewport#addUIComponent(String, Component) 
     */
    public static void addUIComponent(int id, String name, Component component) {
        switch(id) {
            case 0, 1, 2, 3 -> viewports[id].addUIComponent(name, component);
            
            case ALL_VIEWPORTS -> {
                for(Viewport viewport : viewports) {
                    viewport.addUIComponent(name, component);
                }
            }
        }
    }
    
    /**
     * Removes a {@linkplain Component UI Component} from the viewport specified.
     * 
     * @param id   the unique number used to identify the viewport in other parts of the engine. Or {@link ALL_VIEWPORTS} to effect every viewport.
     * @param name the name specified in {@link addUIComponent(int, String, Component) addUIComponent()}
     */
    public static void removeUIComponent(int id, String name) {
        switch(id) {
            case 0, 1, 2, 3 -> viewports[id].removeUIComponent(name);
            
            case ALL_VIEWPORTS -> {
                for(Viewport viewport : viewports) {
                    viewport.removeUIComponent(name);
                }
            }
        }
    }
    
    /**
     * Changes the clear color of the graphics pipeline. Often used to set background or sky colors.
     * 
     * @param color the color we want empty space to be filled with
     */
    public static void setClearColor(Color color) {
        clearColor = color;
    }
    
    /**
     * Changes the movement sensitivity of the specified input device.
     * 
     * @param id          the unique number used to identify the input device in other parts of the engine. Either a 
     *                    {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK} value or KEYBOARD for the keyboard. ALL_DEVICES is not accepted here.
     * @param sensitivity the desired sensitivity, for controllers this refers to the sensitivity of the analog sticks.
     */
    public static void setInputDeviceSensitivity(int id, float sensitivity) {
        if(inputDevices.containsKey(id)) {
            inputDevices.get(id).sensitivity = sensitivity;
        } else {
            Logger.logWarning(
                    "Failed to set sensitivity of input device " + id + 
                    ". No such device exists.",
                    null);
        }
    }
    
    /**
     * Sets the enabled state of input devices. This state determines whether a device will react to state changes in their interactive components. PREV_STATE 
     * should not be supplied anywhere outside of {@link Game#pauseEvents()}.
     * 
     * @param id      the unique id of the input device (specified with {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK}). Or a value to perform this 
     *                operation on multiple devices such as  {@link InputDevice#ALL_EXCEPT_1 ALL_EXCEPT_X} and {@link InputDevice#ALL_DEVICES ALL_DEVICES}.
     * @param enabled if true, the input device(s) will be enabled. Supplying false will disable the specified device(s).
     * @see InputDevice
     */
    public static void setInputDeviceEnabled(int id, boolean enabled) {
        switch(id) {
            case KEYBOARD:
            case GLFW_JOYSTICK_1: case GLFW_JOYSTICK_2:
            case GLFW_JOYSTICK_3: case GLFW_JOYSTICK_4:
                inputDevices.get(id).setEnabled(enabled);
                break;
                
            case ALL_EXCEPT_1: case ALL_EXCEPT_2:
            case ALL_EXCEPT_3: case ALL_EXCEPT_4:
                inputDevices.forEach((deviceID, device) -> {
                    if(deviceID == Math.abs(id + 1)) {
                        device.setEnabled(!enabled);
                    } else {
                        if(deviceID != KEYBOARD) device.setEnabled(enabled);
                    }
                });
                break;
                
            /*
            Do NOT call PREV_STATE manually. The event queue will automatically revert to 
            its previous state once an event has been resolved.
            */
                
            case PREV_STATE:
                inputDevices.forEach((deviceID, device) -> {
                    try {
                        if(deviceID != KEYBOARD) {
                            device.enableStates.pop();
                            device.enabled = device.enableStates.peek();
                        } else {
                            if(!(terminalEnabled || freecamEnabled)) {
                                device.enableStates.pop();
                                device.enabled = device.enableStates.peek();
                            }
                        }
                    } catch(EmptyStackException e) {
                        device.enabled = true;
                    }
                });
                break;
                
            case ALL_DEVICES: default:
                inputDevices.forEach((deviceID, device) -> device.setEnabled(enabled));
                break;
        }
    }
    
    /**
     * Changes the current {@link Puppet} object to be used by the input device specified.
     * 
     * @param id     the unique number used to identify the input device in other parts of the engine. Either a 
     *               {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK} value or KEYBOARD for the keyboard. ALL_DEVICES is not accepted here.
     * @param puppet the puppet to use
     */
    public static void setInputDevicePuppet(int id, Puppet puppet) {
        if(inputDevices.containsKey(id)) {
            inputDevices.get(id).setPuppet(puppet);
        } else {
            Logger.logWarning( 
                    "Failed to set new puppet: \"" + puppet.getClass().getSimpleName() + 
                    "\" of input device " + id + ". No such device exists.",
                    null);
        }
    }
    
    /**
     * You'll never guess what this does!
     * 
     * @param id the unique number used to identify the input device in other parts of the engine. Either a 
     *           {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK} value or KEYBOARD for the keyboard. ALL_DEVICES is not accepted here.
     * @see dev.theskidster.xjge.hardware.InputDevice#setPrevPuppet()
     */
    public static void setInputDevicePuppetPrev(int id) {
        if(inputDevices.containsKey(id)) {
            inputDevices.get(id).setPrevPuppet();
        } else {
            Logger.logWarning(
                    "Failed to set previous puppet of input device " + id + 
                    ". No such device exists.",
                    null);
        }
    }
    
    /**
     * Sets the current audio device of the application. Doing so will change the {@linkplain AudioDevice#setContextCurrent() AL Context} to that device.
     * 
     * @param operation the method of traversal either explicitly as the ID number of the device or "prev"/"next" to move to the previous or next device in the 
     *                  list
     */
    public static void setAudioDevice(String operation) {
        ServiceLocator.getAudio().findSourceStates();
        
        findAudioDevices();
        
        if(audioDevices.size() > 0) {
            switch(operation) {
                case "next" -> {
                    if(!audioDevices.ceilingKey(audioDevices.lastKey()).equals(audioDevice.id)) {
                        audioDevice = audioDevices.higherEntry(audioDevice.id).getValue();
                    } else {
                        audioDevice = audioDevices.firstEntry().getValue();
                    }
                    audioDevice.setContextCurrent();
                    Logger.logInfo(
                            "Set current audio device to " + audioDevice.id + " \"" +
                            audioDevice.name.substring(15) + "\".");
                }

                case "prev" -> {
                    if(!audioDevices.floorKey(audioDevices.firstKey()).equals(audioDevice.id)) {
                        audioDevice = audioDevices.lowerEntry(audioDevice.id).getValue();
                    } else {
                        audioDevice = audioDevices.lastEntry().getValue();
                    }
                    audioDevice.setContextCurrent();
                    Logger.logInfo(
                            "Set current audio device to " + audioDevice.id + " \"" +
                            audioDevice.name.substring(15) + "\".");
                }

                default -> {
                    try {
                        int index = Integer.parseInt(operation);
                        
                        if(audioDevices.containsKey(index)) {
                            audioDevice = audioDevices.get(index);
                            audioDevice.setContextCurrent();
                            Logger.logInfo(
                                    "Set current audio device to " + audioDevice.id + " \"" +
                                            audioDevice.name.substring(15) + "\".");
                        } else {
                            Logger.logWarning(
                                    "Failed to set audio device. Could not find device at index " + index + ".", null);
                        }
                    } catch(NumberFormatException e) {
                        Logger.logWarning("Failed to set audio device. Invalid index value passed.", null);
                    }
                }
            }
        } else {
            Logger.logWarning("No audio devices currently connected.", null);
        }
    }
    
}