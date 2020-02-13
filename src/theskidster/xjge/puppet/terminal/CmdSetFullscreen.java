package theskidster.xjge.puppet.terminal;

import java.util.List;
import theskidster.xjge.main.App;
import theskidster.xjge.util.Color;

/**
 * @author J Hoffman
 * Created: Jan 28, 2020
 */

class CmdSetFullscreen extends TerminalCommand {
    
    public CmdSetFullscreen() {
        super("Changes the apps window between fullscreen and windowed modes.",

              useGenericShowing("fullscreen mode"),

              "setFullscreen [true|false]");
    }

    @Override
    public void execute(List<String> args) {
        output = null;
        
        if(!args.isEmpty()) {
            String parameter = args.get(0);

            if(parameter.equals("true") || parameter.equals("false")) {
                boolean value = Boolean.parseBoolean(parameter);
                setOutput("Fullscreen changed: (" + value + ")", Color.WHITE);
                App.setFullscreen(value);
            } else {
                setOutput(errorInvalidArg(parameter, "(true) or (false)"), Color.RED);
            }
        } else {
            App.setFullscreen(!App.getFullscreen());
            setOutput("Fullscreen changed: (" + App.getFullscreen() + ")", Color.WHITE);
        }
    }
    
}