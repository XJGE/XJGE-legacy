package theskidster.xjge.ui;

import org.joml.Vector3i;
import static org.lwjgl.glfw.GLFW.glfwJoystickPresent;
import theskidster.xjge.main.App;
import theskidster.xjge.util.Color;

/**
 * @author J Hoffman
 * Created: Jan 30, 2020
 */

/**
 * Provides information regarding connected input devices.
 */
public class InputInfo extends Component {

    private Vector3i textPos = new Vector3i();
    private Text[] text      = new Text[4];
    
    public InputInfo() {
        super(new Vector3i(), 0, 0);
        
        setSplitPosition();
        
        for(int i = 0; i < text.length; i++) {
            text[i] = new Text();
        }
    }

    @Override
    public void update() {}

    @Override
    public void render() {
        for(int i = 0; i < 4; i++) {
            boolean present = glfwJoystickPresent(i);
            
            text[i].draw("Input Device " + i + ": " + ((present) ? "connected." : "disconnected."),
                    textPos.set(position.x, position.y - (14 * i), 0), 
                    ((present) ? Color.GREEN : Color.RED));
        }
    }

    @Override
    public void setSplitPosition() {
        position.x = 4;
        
        switch(App.getSplitType()) {
            case NO_SPLIT: case VERTICAL:
                position.y = App.getResolution().y - 18;
                break;
                
            case HORIZONTAL: case TRIPLE: case QUADRUPLE:
                position.y = (App.getResolution().y / 2) - 18;
                break;
        }
    }
    
}