package dev.theskidster.xjge.puppet.terminal;

import java.util.List;
import dev.theskidster.xjge.main.App;
import dev.theskidster.xjge.util.Color;

/**
 * @author J Hoffman
 * Created: Jan 28, 2020
 */

class CmdSetVSync extends TerminalCommand {
    
    public CmdSetVSync() {
        super("Changes whether or not vertical sync is enabled.",

              useGenericShowing("vsync"),

              "setVSync [true|false]");
    }

    @Override
    public void execute(List<String> args) {
        output = null;

        if(!args.isEmpty()) {
            String parameter = args.get(0);

            if(parameter.equals("true") || parameter.equals("false")) {
                boolean value = Boolean.parseBoolean(parameter);
                setOutput("VSync changed: (" + value + ")", Color.WHITE);
                App.setVSync(value);
            } else {
                setOutput(errorInvalidArg(parameter, "(true) or (false)"), Color.RED);
            }
        } else {
            App.setVSync(!App.getVSync());
            setOutput("VSync changed: (" + App.getVSync() + ")", Color.WHITE);
        }
    }
    
}