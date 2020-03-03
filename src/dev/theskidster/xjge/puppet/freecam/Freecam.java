package dev.theskidster.xjge.puppet.freecam;

import org.joml.Vector3f;
import dev.theskidster.xjge.util.Camera;
import dev.theskidster.xjge.shader.core.ShaderCore;

/**
 * @author J Hoffman
 * Created: Jan 14, 2020
 */

/**
 * Represents a camera which is free to roam about the level. Set as the initial camera of all viewports, typically used for debugging purposes. If debugging is 
 * permitted, F2 can be used to enable the camera on viewport 0.
 * 
 * @see theskidster.xjge.main.App#setFreecamEnabled(boolean)
 */
public class Freecam extends Camera {

    private int speed = 1;
    
    private float pitch;
    private float yaw         = -90f;
    private float sensitivity = 0.10f;
    
    public double prevX;
    public double prevY;
    
    public boolean[] pressed = new boolean[4];
    
    private Vector3f tempFront = new Vector3f();
    private Vector3f tempRight = new Vector3f();
    private Vector3f tempDirec = new Vector3f();
    
    /**
     * Creates a new freely moving camera.
     */
    public Freecam() {
        super("persp");
    }

    @Override
    public void update() {
        if(pressed[0]) position.add(direction.mul(speed, tempDirec));
        if(pressed[1]) position.sub(direction.cross(up, tempRight).normalize().mul(speed));
        if(pressed[2]) position.sub(direction.mul(speed, tempDirec));
        if(pressed[3]) position.add(direction.cross(up, tempRight).normalize().mul(speed));
    }

    @Override
    public void render() {
        ShaderCore.getPrograms().forEach(name -> {
            ShaderCore.use(name);
            
            view.setLookAt(position, position.add(direction, tempFront), up);
            ShaderCore.setMat4("uView", false, view);
        });
    }
    
    /**
     * Calculates how quickly the yaw/pitch of the camera should change given the sensitivity of an input devices axis.
     * 
     * @param currValue the current value of the input axis
     * @param prevValue the previous value of the input axis to compare
     * @return the intensity of the movement
     */
    private float getChangeIntensity(double currValue, double prevValue) {
        return (float) (currValue - prevValue) * sensitivity;
    }
    
    /**
     * Enables faster camera movement while active.
     * 
     * @param enable if true, the camera will move at 3 times normal speed
     */
    public void setSpeedBoostEnabled(boolean enable) {
        speed = (enable) ? 3 : 1;
    }
    
    /**
     * Changes the current direction of the camera relative to some input device axes.
     * 
     * @param xpos the x-position of the input axis
     * @param ypos the y-position of the input axis
     */
    public void setDirection(double xpos, double ypos) {
        if(xpos != prevX || ypos != prevY) {
            yaw   += getChangeIntensity(xpos, prevX) * 2;
            pitch += getChangeIntensity(ypos, prevY) * 2;
            
            if(pitch > 89f)  pitch = 89;
            if(pitch < -89f) pitch = -89;
            
            direction.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
            direction.y = (float) Math.sin(Math.toRadians(pitch)) * -1;
            direction.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
            
            prevX = xpos;
            prevY = ypos;
        }
    }
    
}