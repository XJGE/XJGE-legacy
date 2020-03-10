package dev.theskidster.xjge.ui;

import dev.theskidster.xjge.graphics.Cell;
import static dev.theskidster.xjge.hardware.InputDevice.KEYBOARD;
import org.joml.Vector3i;
import dev.theskidster.xjge.main.App;
import dev.theskidster.xjge.util.Color;

/**
 * @author J Hoffman
 * Created: Jan 30, 2020
 */

/**
 * Provides information regarding connected input devices.
 */
public class InputInfo extends Component {
    
    private int maxLength;
    
    private Vector3i textPos = new Vector3i();
    private Text[] text      = new Text[9];
    private Icon[] icons     = new Icon[5];
    
    public InputInfo() {
        super(new Vector3i(), 0, 0);
        
        for(int i = 0; i < text.length; i++) {
            text[i] = new Text();
        }
        
        for(int i = 0; i < icons.length; i++) {
            icons[i] = new Icon("spr_icons_input.png", new Cell(18, 12));
        }
        
        icons[4].setSprite(3, 0);
        
        setSplitPosition();
    }

    @Override
    public void update() {
        for(int i = 0; i < 4; i++) {
            if(App.getInputDevicePresent(i)) {
                if(App.getInputDeviceEnabled(i)) icons[i].setSprite(2, 0);
                else                             icons[i].setSprite(1, 0);
            } else {
                icons[i].setSprite(0, 0);
            }
        }
    }

    @Override
    public void render() {
        for(Icon icon : icons) icon.render();
        
        text[0].draw(
                ((App.getInputDeviceEnabled(KEYBOARD) ? "Enabled" : "Disabled")), 
                textPos.set(position.x + 29, position.y - 4, position.z), 
                Color.WHITE);
        
        for(int i = 1; i < 5; i++) {
            text[i].draw(
                    (i - 1) + "",
                    textPos.set(position.x + 16, position.y - (16 * i) - 10, 0),
                    Color.WHITE);
        }
        
        for(int i = 5; i < text.length; i++) {
            text[i].draw(
                    getFormattedDeviceName(i - 5),
                    textPos.set(position.x + 29, position.y - (16 * (i - 4)) - 6, 0),
                    Color.WHITE);
        }
    }

    @Override
    public void setSplitPosition() {
        position.x = 4;
        
        switch(App.getSplitType()) {
            case NO_SPLIT: case VERTICAL:
                position.y = App.getResolution().y - 18;
                maxLength  = 56;
                updateIconPositions(20);
                break;
                
            case HORIZONTAL: case TRIPLE: case QUADRUPLE:
                position.y = (App.getResolution().y / 2) - 18;
                maxLength = 26;
                updateIconPositions(20);
                break;
        }
    }
    
    /**
     * Used to format the name of an input device to better fit the viewport.
     * 
     * @param id the unique number used to identify the input device in other parts of the engine. One of 
     *           {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK}.
     * @return a formatted string depending on its length.
     */
    private String getFormattedDeviceName(int id) {
        String deviceName = App.getInputDeviceName(id);
        return (deviceName.length() >= maxLength) ? deviceName.substring(0, maxLength - 3) + "..." : App.getInputDeviceName(id);
    }
    
    /**
     * Sets the positions of each input device icon relative to some offset along the y axis.
     * 
     * @param yOffset the initial offset that each icon will use following the first one.
     */
    private void updateIconPositions(int yOffset) {
        icons[4].setPosition(position);
        
        for(int i = 0; i < 4; i++) {
            icons[i].setPosition(position.x, position.y - yOffset, position.z);
            yOffset += 16;
        }
    }
    
}