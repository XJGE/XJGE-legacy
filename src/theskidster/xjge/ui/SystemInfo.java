package theskidster.xjge.ui;

import org.joml.Vector3i;
import static org.lwjgl.glfw.GLFW.glfwGetVersionString;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.opengl.GL11.*;
import theskidster.xjge.main.App;
import theskidster.xjge.util.Color;

/**
 * @author J Hoffman
 * Created: Jan 27, 2020
 */

/**
 * Provides information about the architecture on which the engine is currently running.
 */
public class SystemInfo extends Component {

    private Vector3i textPos = new Vector3i();
    private Text[] text      = new Text[5];
    
    public SystemInfo() {
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
        text[0].draw(System.getProperty("os.name"),
                textPos.set(position.x, position.y, 0), Color.GREEN);
        
        text[1].draw("Java " + System.getProperty("java.version"),
                textPos.set(position.x, position.y - 14, 0), Color.WHITE);
        
        text[2].draw("GLFW: " + glfwGetVersionString(),
                textPos.set(position.x, position.y - (14 * 2), 0), Color.WHITE);
        
        text[3].draw("OPENGL: " + glGetString(GL_VERSION),
                textPos.set(position.x, position.y - (14 * 3), 0), Color.WHITE);
        
        text[4].draw("OPENAL: " + alGetString(AL_VERSION),
                textPos.set(position.x, position.y - (14 * 4), 0), Color.WHITE);
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