package dev.theskidster.xjge.puppet.terminal;

import java.util.List;
import dev.theskidster.xjge.main.App;
import dev.theskidster.xjge.util.Color;

/**
 * @author J Hoffman
 * Created: Jan 28, 2020
 */

class CmdSetAudioDevice extends TerminalCommand {
    
    public CmdSetAudioDevice() {
        super("Changes the current audio device.", 

              useGenericSetter("device"),

              "setAudioDevice (next|prev|<int>)");
    }

    @Override
    public void execute(List<String> args) {
        output = null;

        if(!args.isEmpty()) {
            try {
                int value = Integer.parseInt(args.get(0));

                if(value > -1 && value < App.getNumAudioDevices()) {
                    App.setAudioDevice(args.get(0));
                } else {
                    setOutput("ERROR: Could not find an audio device by the ID of " + value, Color.RED);
                }
            } catch(NumberFormatException e) {
                if(args.get(0).equals("next") || args.get(0).equals("prev")) {
                    App.setAudioDevice(args.get(0));
                } else {
                    setOutput(errorInvalidArg(args.get(0), "<int>, (next), or (prev)"), Color.RED);
                }
            }
        } else {
            setOutput(errorNotEnoughArgs(1), Color.RED);
        }
    }
    
}