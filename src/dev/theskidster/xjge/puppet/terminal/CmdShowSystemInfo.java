package dev.theskidster.xjge.puppet.terminal;

import java.util.List;
import dev.theskidster.xjge.main.App;
import dev.theskidster.xjge.util.Color;

/**
 * @author J Hoffman
 * Created: Jan 28, 2020
 */

class CmdShowSystemInfo extends TerminalCommand {
    
    public CmdShowSystemInfo() {
        super("Provides information about the architecture on which the engine is currently running.", 

              useGenericShowing("component visibility"),

              "showSystemInfo [true|false]");
    }

    @Override
    public void execute(List<String> args) {
        output = null;

        if(!args.isEmpty()) {
            String parameter = args.get(0);

            if(parameter.equals("true") || parameter.equals("false")) {
                boolean value = Boolean.parseBoolean(parameter);
                App.setShowSystemInfo(value);
            } else {
                setOutput(errorInvalidArg(parameter, "(true) or (false)"), Color.RED);
            }
        } else {
            App.setShowSystemInfo(!App.getShowSystemInfo());
        }
    }
    
}