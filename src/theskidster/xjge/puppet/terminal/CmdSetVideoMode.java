package theskidster.xjge.puppet.terminal;

import java.util.List;
import theskidster.xjge.main.App;
import theskidster.xjge.util.Color;

/**
 * @author J Hoffman
 * Created: Jan 28, 2020
 */

class CmdSetVideoMode extends TerminalCommand {
    
    public CmdSetVideoMode() {
        super("Alters the video mode of the current visual display device.", 

              useGenericSetter("video mode"),

              "setVideoMode (next|prev|<int>)");
    }

    @Override
    public void execute(List<String> args) {
        output = null;

        if(args.isEmpty()) {
            setOutput(errorNotEnoughArgs(1), Color.RED);
        } else {
            if(args.size() > 1) {
                setOutput(errorTooManyArgs(args.size(), 1), Color.RED);
            } else {
                try {
                    int value = Integer.parseInt(args.get(0));
                    App.setVideoMode(args.get(0));
                } catch(NumberFormatException e) {
                    if(args.get(0).equals("next") || args.get(0).equals("prev")) {
                        App.setVideoMode(args.get(0));
                    } else {
                        setOutput(errorInvalidArg(args.get(0), "<int>, (next), or (prev)"), Color.RED);
                    }
                }
            }
        }
    }
    
}