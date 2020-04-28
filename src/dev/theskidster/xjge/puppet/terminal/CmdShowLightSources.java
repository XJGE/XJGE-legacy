package dev.theskidster.xjge.puppet.terminal;

import dev.theskidster.xjge.main.App;
import dev.theskidster.xjge.util.Color;
import java.util.List;

/**
 * @author J Hoffman
 * Created: Apr 27, 2020
 */

public class CmdShowLightSources extends TerminalCommand {

    public CmdShowLightSources() {
        super("Exposes the locations of all light sources in the game world.", 
                
              useGenericShowing("light source visibility"), 
              
              "showLightSources [true|false]");
    }

    @Override
    public void execute(List<String> args) {
        output = null;
        
        if(!args.isEmpty()) {
            String parameter = args.get(0);

            if(parameter.equals("true") || parameter.equals("false")) {
                boolean value = Boolean.parseBoolean(parameter);
                response(value);
                App.setShowLightSources(value);
            } else {
                setOutput(errorInvalidArg(parameter, "(true) or (false)"), Color.RED);
            }
        } else {
            App.setShowLightSources(!App.getShowLightSources());
            response(App.getShowLightSources());
        }
    }
    
    private void response(boolean value) {
        if(value) {
            setOutput("Light source locations visible.", Color.WHITE);
        } else {
            setOutput("Light source locations hidden.", Color.WHITE);
        }
    }
    
}