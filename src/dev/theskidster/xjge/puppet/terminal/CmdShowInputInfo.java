package dev.theskidster.xjge.puppet.terminal;

import java.util.List;
import dev.theskidster.xjge.main.App;
import dev.theskidster.xjge.util.Color;

/**
 * @author J Hoffman
 * Created: Jan 30, 2020
 */

class CmdShowInputInfo extends TerminalCommand {

    public CmdShowInputInfo() {
        super("Provides information regarding connected input devices.", 
                
              useGenericShowing("component visibility"), 
              
              "showInputInfo [true|false]");
    }

    @Override
    public void execute(List<String> args) {
        output = null;

        if(!args.isEmpty()) {
            String parameter = args.get(0);

            if(parameter.equals("true") || parameter.equals("false")) {
                boolean value = Boolean.parseBoolean(parameter);
                App.setShowInputInfo(value);
            } else {
                setOutput(errorInvalidArg(parameter, "(true) or (false)"), Color.RED);
            }
        } else {
            App.setShowInputInfo(!App.getShowInputInfo());
        }
    }
    
}