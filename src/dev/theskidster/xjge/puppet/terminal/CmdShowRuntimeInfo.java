package dev.theskidster.xjge.puppet.terminal;

import java.util.List;
import dev.theskidster.xjge.main.App;
import dev.theskidster.xjge.util.Color;

/**
 * @author J Hoffman
 * Created: Jan 28, 2020
 */

class CmdShowRuntimeInfo extends TerminalCommand {
    
    public CmdShowRuntimeInfo() {
        super("Provides information pertaining to the current state of the engine at runtime.", 

              useGenericShowing("component visibility"),

              "showRuntimeInfo [true|false]");
    }

    @Override
    public void execute(List<String> args) {
        output = null;

        if(!args.isEmpty()) {
            String parameter = args.get(0);

            if(parameter.equals("true") || parameter.equals("false")) {
                boolean value = Boolean.parseBoolean(parameter);
                App.setShowRuntimeInfo(value);
            } else {
                setOutput(errorInvalidArg(parameter, "(true) or (false)"), Color.RED);
            }
        } else {
            App.setShowRuntimeInfo(!App.getShowRuntimeInfo());
        }
    }
    
}