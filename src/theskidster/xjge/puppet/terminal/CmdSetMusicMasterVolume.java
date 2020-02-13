package theskidster.xjge.puppet.terminal;

import java.util.List;
import theskidster.xjge.util.Color;
import theskidster.xjge.util.ServiceLocator;

/**
 * @author J Hoffman
 * Created: Jan 28, 2020
 */

class CmdSetMusicMasterVolume extends TerminalCommand {
    
    public CmdSetMusicMasterVolume() {
        super("Sets the master volume of the games music.", 

              "Requires a float between 0 and 1.",

              "setMusicMasterVolume (<float>)");
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
                    float value = Float.parseFloat(args.get(0));

                    if(value >= 0 && value <= 1) {
                        ServiceLocator.getAudio().setMusicMasterVolume(value);
                    } else {
                        setOutput("ERROR: Value out of bounds, must be between 1 and 0.", Color.RED);
                    }
                } catch(NumberFormatException e) {
                    setOutput(errorInvalidArg(args.get(0), "(float)"), Color.RED);
                }
            }
        }
    }
    
}