package theskidster.xjge.puppet.terminal;

import java.util.List;
import theskidster.xjge.puppets.Puppets;
import theskidster.xjge.util.Color;

/**
 * @author J Hoffman
 * Created: Jan 28, 2020
 */

class CmdHelp extends TerminalCommand {
    
    public CmdHelp() {
        super("Supplies information about commands.",

              "Using help without arguments will provide basic information " + 
              "about the command terminal. Passing a command name will provide " + 
              "a description of that command. Use -u or -s to prior to the " + 
              "command name to view its usage or syntax.",

              "help [-u|-s], [<command name>]");
    }

    @Override
    public void execute(List<String> args) {
        String wildcard;
        String command;

        switch(args.size()) {
            case 0:
                String info = "Start typing to view command suggestions, press TAB to " + 
                              "autocomplete, ENTER to execute, and F1 to exit the " + 
                              "command terminal. A full list of commands can be found " + 
                              "in the engines documentation.";

                setOutput(info, Color.CYAN);
                break;

            case 1:
                command = args.get(0);

                if(Puppets.TERMINAL.commands.keySet().stream().anyMatch(name -> name.equals(command))) {
                    setOutput(command + " - " + Puppets.TERMINAL.commands.get(command).getDescription(), Color.WHITE);
                } else {
                    setOutput(commandNotFound(command), Color.RED);
                }
                break;

            case 2:
                wildcard = args.get(0);
                command  = args.get(1);

                switch (wildcard) {
                    case "-u":
                        if(Puppets.TERMINAL.commands.keySet().stream().anyMatch(name -> name.equals(command))) {
                            setOutput(Puppets.TERMINAL.commands.get(command).getUsage(), Color.WHITE);
                        } else {
                            setOutput(commandNotFound(command), Color.RED);
                        }
                        break;
                    case "-s":
                        if(Puppets.TERMINAL.commands.keySet().stream().anyMatch(name -> name.equals(command))) {
                            setOutput(Puppets.TERMINAL.commands.get(command).getSyntax(), Color.YELLOW);
                        } else {
                            setOutput(commandNotFound(command), Color.RED);
                        }
                        break;

                    default:
                        setOutput("ERROR: Unknown wildcard: \"" + wildcard + "\", use -u or -s.", Color.RED);
                        break;
                }
                break;


            default:
                setOutput(errorTooManyArgs(args.size(), 2), Color.RED);
                break;
        }
    }
    
    private String commandNotFound(String command) {
        return "ERROR: Unable to find command: \"" + command + "\".";
    }
    
}