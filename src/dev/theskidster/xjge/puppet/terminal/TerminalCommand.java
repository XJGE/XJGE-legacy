package dev.theskidster.xjge.puppet.terminal;

import java.util.List;
import dev.theskidster.xjge.util.Color;

/**
 * @author J Hoffman
 * Created: Jan 24, 2020
 */

/**
 * Abstract class which can be used to define commands accepted by the terminal.
 */
abstract class TerminalCommand {
    
    private final String description;
    private final String usage;
    private final String syntax;
    
    protected TerminalOutput output;
    
    /**
     * Creates a new terminal command with metadata that can be parsed using the default help command.
     * 
     * @param description a description of what the command does
     * @param usage       a description of how to use the command from the terminal
     * @param syntax      the syntax structure of the command
     */
    public TerminalCommand(String description, String usage, String syntax) {
        this.description = description;
        this.usage       = usage;
        this.syntax      = syntax;
    }
    
    /**
     * Executes the command and returns an output string indicating its success or failure. Values parsed from the arguments passed by the command terminal must be 
     * validated individually by subclasses that contain this method.
     * 
     * @param args any arguments required by the command
     */
    public abstract void execute(List<String> args);
    
    public String getDescription()    { return description; }
    public String getUsage()          { return usage; }
    public String getSyntax()         { return syntax; }
    public TerminalOutput getOutput() { return output; }
    
    /**
     * Provides a response to the user upon executing a command.
     * 
     * @param text  the text to show the user
     * @param color a pretty color to draw the text in
     */
    protected void setOutput(String text, Color color) {
        output = new TerminalOutput(text + "\n", color);
    }
    
    /**
     * Provides a generic response for "setter" style commands with a similar signature.
     * 
     * @param diff the small difference used to better contextualize the response
     */
    public static String useGenericSetter(String diff) {
        return "Supplying prev or next will iterate backwards or forwards through each " + 
               "available " + diff + " whereas passing a number will explicitly set the " + 
                diff + " to whichever currently occupies that ID (if it exists).";
    }
    
    /**
     * Provides a generic response for "showing" style commands with a similar signature.
     * 
     * @param diff the small difference used to better contextualize the response
     */
    public static String useGenericShowing(String diff) {
        return "Passing no argument will toggle " + diff + ". Supplying either true or " + 
                "false will set it explicitly.";
    }
    
    /**
     * Provides a generic response for when users supply an insufficient number of arguments to a command.
     * 
     * @param required the minimum amount of arguments required by this command
     * @return the string to supply to {@link setOutput}
     */
    protected String errorNotEnoughArgs(int required) {
        return "ERROR: Invalid number of arguments. Required at least " + required + ".";
    }
    
    /**
     * Provides a generic response for when users supply more arguments than allowed by a command.
     * 
     * @param numPassed the number of arguments provided by the user
     * @param maxArgs   the maximum amount of arguments allowed by the command
     * @return the string to supply to {@link setOutput}
     */
    protected String errorTooManyArgs(int numPassed, int maxArgs) {
        return "ERROR: Invalid number of arguments. Found " + numPassed + ", max of " + maxArgs + " allowed.";
    }
    
    /**
     * Provides a generic response for when users supply an argument to a command which cannot be processed either due to differing data types or a syntax error.
     * 
     * @param found    the argument provided by the user
     * @param required the argument(s) or data types as accepted by the command
     * @return the string to supply to {@link setOutput}    
     */
    protected String errorInvalidArg(String found, String required) {
        return "ERROR: Invalid argument. Found " + found + ", required " + required + ".";
    }
    
}

