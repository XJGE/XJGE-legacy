package dev.theskidster.xjge.main;

import dev.theskidster.xjge.util.Event;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import org.joml.Matrix4f;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.AL10.AL_PLAYING;
import static dev.theskidster.xjge.audio.Audio.ALL_SOURCES;
import dev.theskidster.xjge.entities.Entity;
import dev.theskidster.xjge.graphics.Light;
import static dev.theskidster.xjge.hardware.InputDevice.*;
import dev.theskidster.xjge.level.Level;
import dev.theskidster.xjge.level.LevelTest;
import dev.theskidster.xjge.util.ServiceLocator;

/**
 * @author J Hoffman
 * Created: Jan 14, 2020
 */

/**
 * Contains the game loop and event system. Provides utilities for managing the state of the game implementation.
 */
public final class Game {
    
    private static int tickCount = 0;
    private static int fps;
    
    private double delta = 0;
    private static double deltaMetric;
    
    private static boolean ticked;
    
    private static Level level;
    private static Event event;
    
    private static Queue<Event> events = new PriorityQueue<>(Comparator.comparing(Event::getPriority));
    
    /**
     * Creates a new game instance and sets the initial level state. Called once immediately following the applications startup sequence. 
     */
    public Game() {
        setLevel(new LevelTest()); //TODO set to custom level.
    }
    
    /**
     * Central game loop which decouples game time progression from processor speed and framerate.
     */
    public void loop() {
        int cycles = 0;
        
        final double TARGET_DELTA = 1 / 60.0;
        double currTime;
        double prevTime = glfwGetTime();
        
        Matrix4f proj = new Matrix4f();
        
        while(!glfwWindowShouldClose(App.getWindowHandle())) {
            glfwPollEvents();
            
            currTime = glfwGetTime();
            
            delta += currTime - prevTime;
            if(delta < TARGET_DELTA && App.getVSync()) delta = TARGET_DELTA;
            
            prevTime = currTime;
            ticked   = false;
            
            while(delta >= TARGET_DELTA) {
                App.pollInput();
                
                deltaMetric = delta;
                
                delta     -= TARGET_DELTA;
                ticked    = true;
                tickCount = (tickCount == Integer.MAX_VALUE) ? 0 : tickCount + 1;
                
                pauseEvents();
                App.updateViewports();
                
                if(tick(60)) {
                    fps = cycles;
                    cycles = 0;
                }
            }
            
            App.renderViewports(level, proj);
            glfwSwapBuffers(App.getWindowHandle());
            
            if(!ticked) {
                try {
                    Thread.sleep(1);
                } catch(InterruptedException e) {
                    Logger.logSevere(e.getMessage(), e);
                }
            } else {
                cycles++;
            }
        }
    }
    
    /**
     * Alters the path of execution away from the normal game update depending on which events are queued. Events are processed in the order of their priorities. 
     * If no events are present in the queue, the game will update normally.
     * 
     * @see Event
     */
    private void pauseEvents() {
        if(events.size() > 0) {
            event = events.peek();
            
            if(!event.resolved) {
                switch(event.getPriority()) {
                    case Event.JOYSTICK_1_DIS: case Event.JOYSTICK_2_DIS:
                    case Event.JOYSTICK_3_DIS: case Event.JOYSTICK_4_DIS:
                        event.resolved = glfwJoystickPresent(event.getPriority());
                        //TODO customize this to better suit the implementation.
                        break;
                    
                    case Event.PAUSE:
                        //TODO This is left open to the implementation to define.
                        break;
                }
            } else {
                switch(event.getPriority()) {
                    case Event.JOYSTICK_1_DIS: case Event.JOYSTICK_2_DIS:
                    case Event.JOYSTICK_3_DIS: case Event.JOYSTICK_4_DIS:
                        if((Boolean) event.getData()) {
                            App.removeUIComponent(event.getPriority(), "discon " + event.getPriority());
                        } else {
                            App.removeUIComponent(Event.JOYSTICK_1_DIS, "discon " + event.getPriority());
                        }
                        ServiceLocator.getAudio().resumeMusic();
                        ServiceLocator.getAudio().setSourceState(ALL_SOURCES, AL_PLAYING);
                        break;
                }
                
                events.poll();
                App.setInputDeviceEnabled(PREV_STATE, false);
            }
        } else {
            level.update();
        }
    }
    
    public static int getFPS()        { return fps; }
    public static float getDelta()    { return (float) deltaMetric; }
    public static boolean getTicked() { return ticked; }
    
    /**
     * Exits the current level and changes to the one specified through the argument passed. 
     * 
     * @param value the level we want to change to
     */
    public static void setLevel(Level value) {
        Logger.logInfo("Level changed to: \"" + value.getClass().getSimpleName() + "\"" + System.lineSeparator());
        
        if(level != null) value.exit();
        level = value;
        level.init();
    }
    
    /**
     * Adds an entity to the current levels {@linkplain Level#entityList entity list}. Typically reserved for testing purposes.
     * 
     * @param e the entity we want to add
     */
    public static void addEntity(Entity e) {
        level.entityList.add(e);
    }
    
    /**
     * Adds a light source to the current levels {@linkplain Level#lights lights} array. This is reserved for testing purposes only, if you wish to add a new 
     * light source to a level you should use the protected method of the same name located in the level class.
     * 
     * @param light the light data to use in the fragment shader
     */
    public static void addLightSource(Light light) {
        level.addLightSource(light);
    }
    
    /**
     * Ticks (returns true) whenever the number of cycles has been reached. Intended to be used in if statements for systems that don't require the decoupled 
     * precision of the {@link dev.theskidster.xjge.util.Timer Timer} class.
     * 
     * @param cycles the number of cycles until a tick occurs
     * @return true every time the number of cycles is reached
     */
    public static boolean tick(int cycles) {
        return tickCount % cycles == 0;
    }
    
    /**
     * Adds an event to the event queue.
     * 
     * @param priority the priority of the event
     * @param data     (optional) additional data required by the event
     */
    public static void addEvent(int priority, Object data) {
        events.add(new Event(priority, data));
    }
    
    /**
     * Resolves and removes an event from the event queue.
     * 
     * @param priority the priority of the event
     */
    public static void resolveEvent(int priority) {
        events.forEach(evt -> {
            evt.resolved = (evt.getPriority() == priority);
        });
    }
    
}