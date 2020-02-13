package theskidster.xjge.puppet.terminal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import theskidster.xjge.level.Level;
import theskidster.xjge.main.App;
import theskidster.xjge.main.Game;
import theskidster.xjge.util.Color;

/**
 * @author J Hoffman
 * Created: Jan 28, 2020
 */

class CmdSetLevel extends TerminalCommand {
    public CmdSetLevel() {
        super("Changes the current level of the game.", 

              "Pass the class name of the desired level, should not include parentheses.",

              "setLevel (<string>)");
    }

    @Override
    public void execute(List<String> args) {
        output = null;

        if(args.isEmpty()) {
            setOutput(errorNotEnoughArgs(1), Color.RED);
        } else {
            if(args.size() > 1) {
                setOutput(errorTooManyArgs(args.size(), 1), Color.RED);
            } else {
                try {
                    Class<?> c = Class.forName("theskidster." + App.DOMAIN + ".level." + args.get(0));
                    
                    if(!c.getSimpleName().equals("Level") && Level.class.isAssignableFrom(c)) {
                        Constructor con = c.getConstructor(Level.class.getClasses());
                        Game.setLevel((Level) con.newInstance(new Object[0]));
                    } else {
                        setOutput("ERROR: Invalid argument. Must be a subclass of Level.", Color.RED);
                    }
                } catch(ClassNotFoundException | IllegalAccessException | IllegalArgumentException | 
                        InstantiationException | NoSuchMethodException | SecurityException | 
                        InvocationTargetException ex) {
                    setOutput(errorInvalidArg(args.get(0), "<Level>"), Color.RED);
                }
            }
        }
    }
}