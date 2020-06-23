package dev.theskidster.xjge.util;

/**
 * @author J Hoffman
 * Created: Jan 15, 2020
 */

/**
 * Objects of this type represent a game or application event (such as a pause, cutscene, or error) that temporarily disrupts the normal flow of execution. Events 
 * should be used anytime two systems need to be decoupled in time, otherwise an {@linkplain Observable Oberserver} should be considered.
 */
public final class Event {
    
    private final int priority;
    
    public boolean resolved;
    
    private final Object data;
    
    public static final int JOYSTICK_1_DIS = 0;
    public static final int JOYSTICK_2_DIS = 1;
    public static final int JOYSTICK_3_DIS = 2;
    public static final int JOYSTICK_4_DIS = 3;
    public static final int PAUSE = 4;
    
    /**
     * Creates a new event that will alter the path of execution until resolved.
     * 
     * @param priority the priority of the event. Lower numbers indicate a higher priority.
     * @param data     option additional data that can be supplied to the event while its waiting to be resolved
     */
    public Event(int priority, Object data) {
        this.priority   = priority;
        this.data       = data;
    }
    
    public int getPriority() { return priority; }
    public Object getData()  { return data; }
    
}