package theskidster.xjge.hardware;

import java.nio.DoubleBuffer;
import java.util.HashMap;
import java.util.Map;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.system.MemoryStack;
import theskidster.xjge.main.App;

/**
 * @author J Hoffman
 * Created: Jan 16, 2020
 */

/**
 * Represents a keyboard and mouse. Only one keyboard object should be present at any given time, implementations which make use of the engines local multiplayer 
 * features should instead use {@link Controller Controller} objects to handle player input.
 */
public class Keyboard extends InputDevice {

    private float prevX = App.getWindowWidth() / 2; 
    private float prevY = App.getWindowHeight() / 2; 
    
    private boolean firstMouse = true;
    
    private Map<String, Integer> keys  = new HashMap<>();
    private Map<String, Integer> mouse = new HashMap<>();
    private Map<String, AxisButtons> axes = new HashMap<>();
    
    private DoubleBuffer x;
    private DoubleBuffer y;
    
    /**
     * Data structure used to mimic the left analog stick of a {@link Controller}. Permitting two keys to be assigned to a single 
     * {@link theskidster.xjge.puppets.Command Command}.
     */
    private class AxisButtons {
        public int btn1;
        public int btn2;
        
        public AxisButtons(int btn1, int btn2) {
            this.btn1 = btn1;
            this.btn2 = btn2;
        }
    }
    
    /**
     * Creates a new Keyboard object and sets the default configuration of its keys.
     * 
     * @param id the unique number used to identify the device in other parts of the engine
     */
    public Keyboard(int id) {
        super(id);
        
        name = "keyboard";
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            x = stack.mallocDouble(1);
            y = stack.mallocDouble(1);
        }
        
        //The default configuration seen here is modeled after a 3D camera. Though the 
        //implementation can alter this as needed.
        keys.put("a button",     GLFW_KEY_SPACE);
        keys.put("b button",     GLFW_KEY_Q);
        keys.put("x button",     GLFW_KEY_R);
        keys.put("y button",     GLFW_KEY_E);
        keys.put("left bumper",  GLFW_KEY_1);
        keys.put("right bumper", GLFW_KEY_2);
        keys.put("back button",  GLFW_KEY_TAB);
        keys.put("start button", GLFW_KEY_ESCAPE);
        keys.put("guide button", GLFW_KEY_ENTER);
        keys.put("left thumb",   GLFW_KEY_LEFT_SHIFT);
        keys.put("right thumb",  GLFW_KEY_C);
        keys.put("dpad up",      GLFW_KEY_UP);
        keys.put("dpad right",   GLFW_KEY_RIGHT);
        keys.put("dpad down",    GLFW_KEY_DOWN);
        keys.put("dpad left",    GLFW_KEY_LEFT);
        
        axes.put("left x",          new AxisButtons(GLFW_KEY_A, GLFW_KEY_D));
        axes.put("left y",          new AxisButtons(GLFW_KEY_W, GLFW_KEY_S));
        mouse.put("right x",        0);
        mouse.put("right y",        0);
        mouse.put("left trigger",   GLFW_MOUSE_BUTTON_LEFT);
        mouse.put("right trigger",  GLFW_MOUSE_BUTTON_RIGHT);
    }

    @Override
    public void poll() {
        if(!puppets.empty() && puppets.peek() != null) {
            puppets.peek().commands.forEach((action, command) -> {
                switch(action) {
                    case "left x": case "left y":
                        if(glfwGetKey(App.getWindowHandle(), axes.get(action).btn1) == GLFW_PRESS) {
                            command.execute(this, -1);
                        } else if(glfwGetKey(App.getWindowHandle(), axes.get(action).btn2) == GLFW_PRESS) {
                            command.execute(this, 1);
                        }
                        break;
                        
                    case "right x":
                        glfwGetCursorPos(App.getWindowHandle(), x, y);
                        
                        if((float) x.get(0) != prevX) {
                            command.execute(this, findAxisValue((float) x.get(0), prevX));
                            prevX = (float) x.get(0);
                        }
                        break;
                        
                    case "right y":
                        glfwGetCursorPos(App.getWindowHandle(), x, y);
                        
                        if((float) y.get(0) != prevY) {
                            command.execute(this, findAxisValue((float) y.get(0), prevY));
                            prevY = (float) y.get(0);
                        }
                        break;
                        
                    case "left trigger": case "right trigger":
                        command.execute(this, glfwGetMouseButton(App.getWindowHandle(), mouse.get(action)));
                        break;
                        
                    default:
                        command.execute(this, glfwGetKey(App.getWindowHandle(), keys.get(action)));
                        break;
                }
            });
        }
        
        resolvePuppetSetRequest();
    }

    @Override
    public void configure() {
        //TODO This is left open to the implementation to define.
    }
    
    /**
     * Used to mimic the right analog stick of a controller by finding the difference between the current and previous positions of the mouse cursor.
     * 
     * @param currValue the current value of the mouse cursor
     * @param prevValue the previous value of the mouse cursor to compare
     * @return a number between -1 and 1 to supply to the puppet object representing the intensity of the movement
     */
    private float findAxisValue(float currValue, float prevValue) {
        if(firstMouse) {
            prevValue  = currValue;
            firstMouse = false;
        }
        
        return (currValue - prevValue) * sensitivity;
    }
    
}