package theskidster.xjge.puppet.terminal;

import java.util.List;
import theskidster.xjge.main.App;
import theskidster.xjge.util.Color;

/**
 * @author J Hoffman
 * Created: Jan 28, 2020
 */

class CmdSetDisplayDevice extends TerminalCommand {
    
    public CmdSetDisplayDevice() {
        super("Changes the current visual display device.", 

              useGenericSetter("device"),

              "setDisplayDevice (next|prev|<int>)");
    }

    @Override
    public void execute(List<String> args) {
        output = null;

        if(!args.isEmpty()) {
            try {
                int value = Integer.parseInt(args.get(0));

                if(value > -1 && value < App.getNumDisplayDevices()) {
                    App.setDisplayDevice(args.get(0), true);
                } else {
                    setOutput("ERROR: Could not find a display device by the ID of " + value, Color.RED);
                }
            } catch(NumberFormatException e) {
                if(args.get(0).equals("next") || args.get(0).equals("prev")) {
                    App.setDisplayDevice(args.get(0), true);
                } else {
                    setOutput(errorInvalidArg(args.get(0), "<int>, (next), or (prev)"), Color.RED);
                }
            }
        } else {
            setOutput(errorNotEnoughArgs(1), Color.RED);
        }
    }
    
}