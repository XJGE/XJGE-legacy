package dev.theskidster.xjge.puppet.terminal;

import java.util.List;
import dev.theskidster.xjge.main.App;
import dev.theskidster.xjge.util.Color;

/**
 * @author J Hoffman
 * Created: Jan 28, 2020
 */

class CmdTerminate extends TerminalCommand {
    
    public CmdTerminate() {
        super("Ceases execution and gracefully exits the application.",

              "Simply type terminate to use. This command contains no " +
              "additional parameters.", 

              "terminate");
    }

    @Override
    public void execute(List<String> args) {
        setOutput("cya!", Color.WHITE);
        App.terminate();
    }
    
}