package theskidster.xjge.puppet.terminal;

import java.util.List;
import theskidster.xjge.main.App;
import theskidster.xjge.util.Color;
import theskidster.xjge.util.ScreenSplitType;

/**
 * @author J Hoffman
 * Created: Jan 28, 2020
 */

class CmdSetSplitType extends TerminalCommand {
    
    public CmdSetSplitType() {
        super("Changes what kind of split is used for splitscreen play. Otherwise hidden " +
              "viewports will use either the default camera or whichever was most " + 
              "recently set.", 

              "Parameter must be one of: no_split, vertical, horizontal, triple, or quadruple.",

              "setSplitType (no_split|vertical|horizontal|triple|quadruple)");
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
                switch(args.get(0)) {
                    case "no_split":   App.setSplitType(ScreenSplitType.NO_SPLIT);   break;
                    case "vertical":   App.setSplitType(ScreenSplitType.VERTICAL);   break;
                    case "horizontal": App.setSplitType(ScreenSplitType.HORIZONTAL); break;
                    case "triple":     App.setSplitType(ScreenSplitType.TRIPLE);     break;
                    case "quadruple":  App.setSplitType(ScreenSplitType.QUADRUPLE);  break;

                    default:
                        setOutput(errorInvalidArg(args.get(0), "(no_split), (vertical), (horizontal), (triple), or (quadruple)"), Color.RED);
                        break;
                }
            }
        }
    }
    
}