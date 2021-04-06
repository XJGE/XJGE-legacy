package dev.theskidster.xjge.puppet.discon;

import org.joml.Vector3i;
import dev.theskidster.xjge.main.App;
import dev.theskidster.xjge.ui.Component;
import dev.theskidster.xjge.ui.RectangleBatch;
import dev.theskidster.xjge.ui.Text;
import dev.theskidster.xjge.util.Color;

/**
 * @author J Hoffman
 * Created: Feb 3, 2020
 */

/**
 * Short for Disconnected Controller. This component displays a message whenever a controller disconnection event is unresolved.
 */
public class DisCon extends Component {

    private final int jid;
    
    private RectangleBatch rect = new RectangleBatch(2);
    private Text[] text         = new Text[2];
    private Vector3i[] textPos  = new Vector3i[2];
    private String message;
    
    /**
     * Creates a new disconnected controller message component.
     * 
     * @param jid the joystick id. One of {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK}.
     */
    public DisCon(int jid) {
        super(new Vector3i(), 192, 64);
        
        this.jid = jid;
        
        setSplitPosition();
        
        for(int i = 0; i < text.length; i++) {
            text[i] = new Text();
        }
        
        textPos[0] = new Vector3i(position.x + 4, position.y + 39, 0);
        textPos[1] = new Vector3i(position.x + 4, position.y + 18, 0);
        
        message = text[1].wrap("Please reconnect your controller.", width);
    }

    @Override
    public void update() {}

    @Override
    public void render() {
        rect.batchStart();
            rect.drawRectangle(position.x, position.y, width, 36, Color.BLACK);
            rect.drawRectangle(position.x, position.y + 40, width, 16, Color.BLACK);
        rect.batchEnd();
        
        text[0].draw("CONTROLLER DISCONNECTED", textPos[0], Color.WHITE);
        text[1].draw(message, textPos[1], Color.WHITE);
    }

    @Override
    public void setSplitPosition() {
        switch(App.getSplitType()) {
            case NO_SPLIT -> {
                position.x = 144;
                position.y = 107;
            }
                
            case VERTICAL -> {
                position.x = 24;
                position.y = 107;
            }
                
            case HORIZONTAL -> {
                position.x = 144;
                position.y = 40;
            }
                
            case TRIPLE, QUADRUPLE -> {
                position.x = 24;
                position.y = 40;
            }
        }
    }
    
}