package dev.theskidster.xjge.hardware;

import java.util.HashMap;
import java.util.Map;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWGamepadState;
import org.lwjgl.system.MemoryStack;
import dev.theskidster.xjge.main.Logger;

/**
 * @author J Hoffman
 * Created: Jan 16, 2020
 */

/**
 * Represents a generic game controller. Loosely modeled after the layout of an XBOX&trade; controller.
 */
public class Controller extends InputDevice {

    private GLFWGamepadState state;
    
    private Map<String, Integer> buttons = new HashMap<>();
    private Map<String, Integer> axes    = new HashMap<>();
    
    /**
     * Creates a new controller object and sets the default configuration for the buttons.
     * 
     * @param id the unique number used to identify the device in other parts of the engine
     */
    public Controller(int id) {
        super(id);
        
        validate();
        
        //These could be loaded via XML file if needed.
        buttons.put("a button",     GLFW_GAMEPAD_BUTTON_A);
        buttons.put("b button",     GLFW_GAMEPAD_BUTTON_B);
        buttons.put("x button",     GLFW_GAMEPAD_BUTTON_X);
        buttons.put("y button",     GLFW_GAMEPAD_BUTTON_Y);
        buttons.put("left bumper",  GLFW_GAMEPAD_BUTTON_LEFT_BUMPER);
        buttons.put("right bumper", GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER);
        buttons.put("back button",  GLFW_GAMEPAD_BUTTON_BACK);
        buttons.put("start button", GLFW_GAMEPAD_BUTTON_START);
        buttons.put("guide button", GLFW_GAMEPAD_BUTTON_GUIDE);
        buttons.put("left thumb",   GLFW_GAMEPAD_BUTTON_LEFT_THUMB);
        buttons.put("right thumb",  GLFW_GAMEPAD_BUTTON_RIGHT_THUMB);
        buttons.put("dpad up",      GLFW_GAMEPAD_BUTTON_DPAD_UP);
        buttons.put("dpad right",   GLFW_GAMEPAD_BUTTON_DPAD_RIGHT);
        buttons.put("dpad down",    GLFW_GAMEPAD_BUTTON_DPAD_DOWN);
        buttons.put("dpad left",    GLFW_GAMEPAD_BUTTON_DPAD_LEFT);
        
        axes.put("left x",          GLFW_GAMEPAD_AXIS_LEFT_X);
        axes.put("left y",          GLFW_GAMEPAD_AXIS_LEFT_Y);
        axes.put("right x",         GLFW_GAMEPAD_AXIS_RIGHT_X);
        axes.put("right y",         GLFW_GAMEPAD_AXIS_RIGHT_Y);
        axes.put("left trigger",    GLFW_GAMEPAD_AXIS_LEFT_TRIGGER);
        axes.put("right trigger",   GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER);
    }
    
    /**
     * Transfers the state of a controller.
     * 
     * @param controller the previous controller object used to transfer its state into the new instance
     */
    public Controller(Controller controller) {
        super(controller);
        
        validate();
        
        buttons = controller.buttons;
        axes    = controller.axes;
    }
    
    /**
     * Determines if this controller is eligible for use by the engine. GLFW recognizes Joysticks which exhibit predictable button layouts as Gamepads. Gamepads 
     * often include mappings for buttons which correspond well to GLFWs definitions.
     */
    private void validate() {
        if(glfwJoystickIsGamepad(id)) {
            name = glfwGetGamepadName(id);
            
            try(MemoryStack stack = MemoryStack.stackPush()) {
                state = new GLFWGamepadState(stack.malloc(40));
                
                state.buttons(stack.malloc(15));
                state.axes(stack.mallocFloat(6));
            }
        } else {
            name = glfwGetJoystickName(id);
            Logger.logWarning("Unsupported controller: \"" + name + "\" connected.", null);
        }
    }

    @Override
    public void poll() {
        if(glfwGetGamepadState(id, state) && !puppets.empty() && puppets.peek() != null) {
            puppets.peek().commands.forEach((action, command) -> {
                switch(action) {
                    case "left x", "left y", "right x", "right y" -> {
                        if(Math.abs(state.axes(axes.get(action))) >= sensitivity) {
                            command.execute(this, state.axes(axes.get(action)));
                        }
                    }
                    
                    case "left trigger", "right trigger" -> command.execute(this, state.axes(axes.get(action)));
                    
                    default -> command.execute(this, state.buttons(buttons.get(action)));
                }
            });
        }
        
        resolvePuppetSetRequest();
    }

    @Override
    public void configure() {
        //TODO This is left open to the implementation to define.
    }
    
}