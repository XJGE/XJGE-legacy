package theskidster.xjge.ui;

import org.joml.Vector3i;
import theskidster.xjge.main.App;
import theskidster.xjge.main.Game;
import theskidster.xjge.util.Color;

/**
 * @author J Hoffman
 * Created: Jan 23, 2020
 */

/**
 * Provides information pertaining to the current state of the engine at runtime.
 */
public class RuntimeInfo extends Component {
    
    private Vector3i textPos = new Vector3i();
    private Text[] text      = new Text[6];
    
    public RuntimeInfo() {
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
        text[0].draw("FPS: " + Game.getFPS(),
                textPos.set(position.x, position.y, 0), Color.WHITE);
        
        text[1].draw("DELTA: " + Game.getDelta(),
                textPos.set(position.x, position.y - 14, 0), Color.WHITE);
        
        text[2].draw("TICKED: " + Game.getTicked(),
                textPos.set(position.x, position.y - (14 * 2), 0), Color.WHITE);
        
        text[3].draw("VSYNC: " + App.getVSync(),
                textPos.set(position.x, position.y - (14 * 3), 0), Color.YELLOW);
        
        text[4].draw("DISPLAY: " + App.getDisplayInfo(),
                textPos.set(position.x, position.y - (14 * 4), 0), Color.YELLOW);
        
        text[5].draw("MEM FREE: " + Runtime.getRuntime().freeMemory(),
                textPos.set(position.x, position.y - (14 * 5), 0), Color.CYAN);
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