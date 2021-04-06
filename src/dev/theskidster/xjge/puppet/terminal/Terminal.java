package dev.theskidster.xjge.puppet.terminal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.joml.Vector3i;
import static org.lwjgl.glfw.GLFW.*;
import dev.theskidster.xjge.main.App;
import dev.theskidster.xjge.main.Game;
import dev.theskidster.xjge.ui.Component;
import dev.theskidster.xjge.ui.RectangleBatch;
import dev.theskidster.xjge.ui.Text;
import dev.theskidster.xjge.util.Color;
import dev.theskidster.xjge.util.ServiceLocator;
import dev.theskidster.xjge.util.Timer;

/**
 * @author J Hoffman
 * Created: Jan 28, 2020
 */

/**
 * Provides direct access to the game engine through a command line which can be extended to support more commands as needed by the implementation. The terminal 
 * is particularly useful for performing debugging at runtime, or managing the application during demos. By default the terminal will always be opened in 
 * viewport 0 and will override access to the keyboard input device while it is open. If debugging is permitted, F1 can be used to open the command terminal.
 */
public class Terminal extends Component implements PropertyChangeListener {
    
    private int xIndex;
    private int yIndex;
    private int shiftElements = -1;
    
    private boolean shiftHeld;
    private boolean cursorIdle;
    private boolean cursorBlink;
    private boolean suggest;
    private boolean executed = true;
    
    private Vector3i carrotPos = new Vector3i(0, 0, 0);
    private Vector3i cursorPos = new Vector3i(8, -1, 0);
    private Vector3i cmdPos    = new Vector3i(8, 0, -1);
    
    private String suggestion          = "";
    private String prevTyped           = "";
    private Text[] text                = new Text[9];
    private StringBuilder typed        = new StringBuilder();
    private List<String> cmdHistory    = new ArrayList<>();
    private TerminalOutput[] cmdOutput = new TerminalOutput[5];
    
    public RectangleBatch rectBatch;
    private Timer timer = new Timer(1, 20, this);
    
    Map<String, TerminalCommand> commands = new TreeMap<>();
    private Map<Integer, Key> keyChars    = new HashMap<>();
    private Map<Integer, Integer> charPos = new HashMap<>();
    
    /**
     * Represents a key on a keyboard.
     */
    private class Key {
        private char c;
        private char C;
        
        public Key(char c, char C) {
            this.c = c;
            this.C = C;
        }
        
        public char getChar(boolean shiftHeld) { return (!shiftHeld) ? c : C; }
    }
    
    /**
     * Creates a new command terminal which can be used to interact directly with the game engine.
     */
    public Terminal() {
        super(new Vector3i(), 0, 0);
        
        setSplitPosition();
        
        for(int i = 0; i < text.length; i++) {
            text[i] = new Text();
        }
        
        //ENGINE
        commands.put("beep",                 new CmdBeep());
        commands.put("cls",                  new CmdCLS());
        commands.put("help",                 new CmdHelp());
        commands.put("setAudioDevice",       new CmdSetAudioDevice());
        commands.put("setDisplayDevice",     new CmdSetDisplayDevice());
        commands.put("setFullscreen",        new CmdSetFullscreen());
        commands.put("setMusicMasterVolume", new CmdSetMusicMasterVolume());
        commands.put("setSoundMasterVolume", new CmdSetSoundMasterVolume());
        commands.put("setSplitType",         new CmdSetSplitType());
        commands.put("setVSync",             new CmdSetVSync());
        commands.put("setVideoMode",         new CmdSetVideoMode());
        commands.put("showInputInfo",        new CmdShowInputInfo());
        commands.put("showLightSources",     new CmdShowLightSources());
        commands.put("showRuntimeInfo",      new CmdShowRuntimeInfo());
        commands.put("showSystemInfo",       new CmdShowSystemInfo());
        commands.put("terminate",            new CmdTerminate());
        
        //GAMEPLAY
        commands.put("setLevel", new CmdSetLevel());
        //TODO add more gameplay commands here.
        
        keyChars.put(GLFW_KEY_SPACE,      new Key(' ', ' '));
        keyChars.put(GLFW_KEY_APOSTROPHE, new Key('\'', '\"'));
        keyChars.put(GLFW_KEY_COMMA,      new Key(',', '<'));
        keyChars.put(GLFW_KEY_MINUS,      new Key('-', '_'));
        keyChars.put(GLFW_KEY_PERIOD,     new Key('.', '>'));
        keyChars.put(GLFW_KEY_SLASH,      new Key('/', '?'));
        keyChars.put(GLFW_KEY_0, new Key('0', ')'));
        keyChars.put(GLFW_KEY_1, new Key('1', '!'));
        keyChars.put(GLFW_KEY_2, new Key('2', '@'));
        keyChars.put(GLFW_KEY_3, new Key('3', '#'));
        keyChars.put(GLFW_KEY_4, new Key('4', '$'));
        keyChars.put(GLFW_KEY_5, new Key('5', '%'));
        keyChars.put(GLFW_KEY_6, new Key('6', '^'));
        keyChars.put(GLFW_KEY_7, new Key('7', '&'));
        keyChars.put(GLFW_KEY_8, new Key('8', '*'));
        keyChars.put(GLFW_KEY_9, new Key('9', '('));
        keyChars.put(GLFW_KEY_SEMICOLON, new Key(';', ':'));
        keyChars.put(GLFW_KEY_EQUAL,     new Key('=', '+'));
        keyChars.put(GLFW_KEY_A, new Key('a', 'A'));
        keyChars.put(GLFW_KEY_B, new Key('b', 'B'));
        keyChars.put(GLFW_KEY_C, new Key('c', 'C'));
        keyChars.put(GLFW_KEY_D, new Key('d', 'D'));
        keyChars.put(GLFW_KEY_E, new Key('e', 'E'));
        keyChars.put(GLFW_KEY_F, new Key('f', 'F'));
        keyChars.put(GLFW_KEY_G, new Key('g', 'G'));
        keyChars.put(GLFW_KEY_H, new Key('h', 'H'));
        keyChars.put(GLFW_KEY_I, new Key('i', 'I'));
        keyChars.put(GLFW_KEY_J, new Key('j', 'J'));
        keyChars.put(GLFW_KEY_K, new Key('k', 'K'));
        keyChars.put(GLFW_KEY_L, new Key('l', 'L'));
        keyChars.put(GLFW_KEY_M, new Key('m', 'M'));
        keyChars.put(GLFW_KEY_N, new Key('n', 'N'));
        keyChars.put(GLFW_KEY_O, new Key('o', 'O'));
        keyChars.put(GLFW_KEY_P, new Key('p', 'P'));
        keyChars.put(GLFW_KEY_Q, new Key('q', 'Q'));
        keyChars.put(GLFW_KEY_R, new Key('r', 'R'));
        keyChars.put(GLFW_KEY_S, new Key('s', 'S'));
        keyChars.put(GLFW_KEY_T, new Key('t', 'T'));
        keyChars.put(GLFW_KEY_U, new Key('u', 'U'));
        keyChars.put(GLFW_KEY_V, new Key('v', 'V'));
        keyChars.put(GLFW_KEY_W, new Key('w', 'W'));
        keyChars.put(GLFW_KEY_X, new Key('x', 'X'));
        keyChars.put(GLFW_KEY_Y, new Key('y', 'Y'));
        keyChars.put(GLFW_KEY_Z, new Key('z', 'Z'));
        keyChars.put(GLFW_KEY_LEFT_BRACKET,  new Key('[', '{'));
        keyChars.put(GLFW_KEY_BACKSLASH,     new Key('\\', '|'));
        keyChars.put(GLFW_KEY_RIGHT_BRACKET, new Key(']', '}'));
        keyChars.put(GLFW_KEY_GRAVE_ACCENT,  new Key('`', '~'));
    }

    @Override
    public void update() {
        timer.update();
        if(Game.tick(20) && cursorIdle) cursorBlink = !cursorBlink;
        
        if(!prevTyped.equals(typed.toString())) {
            suggest = commands.keySet().stream().anyMatch(name -> name.regionMatches(0, typed.toString(), 0, typed.length())) && typed.length() > 0;
            
            if(suggest) {
                suggestion = commands.keySet().stream()
                        .filter(name -> name.regionMatches(0, typed.toString(), 0, typed.length()))
                        .findFirst()
                        .get();
            }
        }
        
        prevTyped = typed.toString();
    }

    @Override
    public void render() {
        rectBatch.batchStart();
            rectBatch.drawRectangle(0, 0, App.getResolution().x, 16, Color.BLACK);
        rectBatch.batchEnd();
        
        for(int i = 0; i <= shiftElements; i++) {
            text[i].drawOutput(cmdOutput, cmdOutput[i], i, executed);   
        }
        
        executed = false;
        
        text[5].draw(">", carrotPos, Color.WHITE);
        if(suggest) text[6].draw(suggestion, cmdPos, Color.GRAY);
        if(cursorBlink) text[7].draw("_", cursorPos, Color.WHITE);
        if(validate()) text[8].drawCommand(typed.toString(), cmdPos);
        else           text[8].draw(typed.toString(), cmdPos, Color.WHITE);
    }

    @Override
    public void setSplitPosition() {
        width = switch(App.getSplitType()) {
            case NO_SPLIT, HORIZONTAL -> App.getResolution().x;
            default -> App.getResolution().x / 2;
        };
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch(evt.getPropertyName()) {
            //Used for cursor timer
            case "finished" -> cursorIdle = (Boolean) evt.getNewValue();
        }
    }
    
    /**
     * Handles input captured from the applications {@link dev.theskidster.xjge.main.Window Window}.
     * 
     * @param key    the key pressed by the user
     * @param action the action of the press. One of {@link org.lwjgl.glfw.GLFW#GLFW_PRESS GLFW_PRESS}, {@link org.lwjgl.glfw.GLFW#GLFW_RELEASE GLFW_RELEASE}, or 
     *               {@link org.lwjgl.glfw.GLFW#GLFW_REPEAT GLFW_REPEAT}.
     */
    public void processInput(int key, int action) {
        if(action == GLFW_PRESS || action == GLFW_REPEAT) {
            cursorIdle  = false;
            cursorBlink = true;
            timer.restart();
            
            keyChars.forEach((k, c) -> {
                if(key == k) insertChar(c.getChar(shiftHeld));
            });
            
            switch(key) {
                case GLFW_KEY_BACKSPACE -> {
                    if(xIndex > 0) {
                        xIndex--;
                        typed.deleteCharAt(xIndex);
                        cursorPos.x = charPos.get(xIndex);
                        scrollX();
                    }
                }
                    
                case GLFW_KEY_RIGHT -> {
                    xIndex++;
                    
                    if(xIndex < typed.length()) {
                        cursorPos.x = charPos.get(xIndex);
                        scrollX();
                    } else {
                        xIndex = typed.length();
                        if(typed.length() > 0) {
                            cursorPos.x = charPos.get(xIndex - 1) + 8;
                            scrollX();
                        } else {
                            cursorPos.x = 8;
                        }
                    }
                }
                    
                case GLFW_KEY_LEFT -> {
                    xIndex--;
                    
                    if(xIndex > 0) {
                        cursorPos.x = charPos.get(xIndex);
                        scrollX();
                    } else {
                        xIndex      = 0;
                        cursorPos.x = 8;
                    }
                }
                    
                case GLFW_KEY_DOWN -> {
                    if(cmdHistory.size() > 0) {
                        yIndex = (yIndex >= cmdHistory.size() - 1) ? cmdHistory.size() - 1 : yIndex + 1;
                        charPos.clear();
                        if(cmdHistory.get(yIndex) != null) autoComplete(cmdHistory.get(yIndex));
                    }
                }
                    
                case GLFW_KEY_UP -> {
                    if(cmdHistory.size() > 0) {
                        yIndex = (yIndex == 0) ? 0 : yIndex - 1;
                        charPos.clear();
                        if(cmdHistory.get(yIndex) != null) autoComplete(cmdHistory.get(yIndex));
                    }
                }
                    
                case GLFW_KEY_ENTER -> {
                    execute(typed.toString());
                    typed.delete(0, typed.length());
                    xIndex      = 0;
                    yIndex      = cmdHistory.size();
                    carrotPos.x = 0;
                    cursorPos.x = 8;
                    charPos.clear();
                }
                    
                case GLFW_KEY_TAB -> {
                    if(suggest) autoComplete(suggestion);
                }
            }
        } else {
            timer.start();
        }
        
        switch(key) {
            case GLFW_KEY_LEFT_SHIFT, GLFW_KEY_RIGHT_SHIFT -> {
                shiftHeld = action == GLFW_PRESS;
            }
        }
    }
    
    /**
     * Adds a character into the typed string of the terminal.
     * 
     * @param c the character to insert
     */
    private void insertChar(char c) {
        typed.insert(xIndex, c);
        charPos.put(xIndex, (xIndex * 8) + 8);
        cursorPos.x = charPos.get(xIndex) + 8;
        
        xIndex++;
        
        if(xIndex != typed.length()) {
            for(int i = xIndex; i < typed.length(); i++) {
                charPos.put(i, (i * 8) + 8);
            }
        }
        
        scrollX();
    }
    
    /**
     * Determines if the string in the command line matches an existing command.
     * 
     * @return true if the command is recognized by the terminal
     */
    private boolean validate() {
        if((typed.toString().length() > suggestion.length())) {
            return typed.toString().regionMatches(0, suggestion, 0, suggestion.length()) &&
                   typed.toString().charAt(suggestion.length()) == ' ';
        } else {
            return typed.toString().regionMatches(0, suggestion, 0, suggestion.length());
        }
    }
    
    /**
     * Completes typing the suggestion provided by the terminal.
     * 
     * @param s the suggestion to complete
     */
    private void autoComplete(String s) {
        typed.delete(0, typed.length());
        xIndex = 0;
        
        for(Character c : s.toCharArray()) {    
            insertChar(c);
        }
    }
    
    /**
     * Offsets the visible portion of the string typed in the terminal by the position of the cursor. Used to navigate large strings that extend beyond the 
     * screens width.
     */
    private void scrollX() {
        if(typed.length() > 0) {
            int xOffset = 0;
            
            if(charPos.get(charPos.size() - 1) + 8 > width - 16) {
                xOffset = (width - 8) - (charPos.get(charPos.size() - 1) + 8);
                
                if(cursorPos.x - 8 <= Math.abs(xOffset)) {
                    int x = (xIndex < 1) ? 0 : 8;
                    
                    xOffset -= ((cursorPos.x - (8 + x)) - Math.abs(xOffset));
                }
            }
            
            cursorPos.x += xOffset;
            carrotPos.x = xOffset;
            cmdPos.x    = xOffset + 8;
        } else {
            charPos.clear();
        }
    }
    
    /**
     * Executes a terminal command.
     * 
     * @param command the command parsed from the typed string
     */
    private void execute(String command) {
        String name = "";
        List<String> args = new ArrayList<>();
        
        cmdHistory.add(command);
        if(cmdHistory.size() == 33) cmdHistory.remove(0);
        
        if(suggestion.length() > 0 && command.regionMatches(0, suggestion, 0, suggestion.length())) {
            name = command.substring(0, suggestion.length());
        }
        
        if(command.contains(" ")) {
            String s1 = command.substring(command.indexOf(" "), command.length()).replaceAll(" ", "");
            String s2 = "";
            
            for(int i = 0; i < s1.length(); i++) {
                char c = s1.charAt(i);
                
                if(c != ',') s2 += c;
                
                if(c == ',' || i == s1.length() - 1) {
                    args.add(s2);
                    s2 = "";
                }
            }
        }
        
        TerminalOutput output;
        
        if(commands.containsKey(name)) {
            commands.get(name).execute(args);
            output = commands.get(name).getOutput();
        } else {
            output = new TerminalOutput("ERROR: Command not recognized. Check syntax or use help.\n", Color.RED);
        }
        
        if(output != null) {
            shiftElements = (shiftElements == 4) ? 4 : shiftElements + 1;
            
            for(int i = shiftElements; i > -1; i--) {
                if(i > 0) {
                    if(cmdOutput[i - 1] != null) {
                        cmdOutput[i] = cmdOutput[i - 1];
                    }
                } else {
                    cmdOutput[i] = new TerminalOutput(text[0].wrap(output.text, this.width), output.color);
                }
            }
        }
        
        executed = true;
    }
    
     private class CmdBeep extends TerminalCommand {
        public CmdBeep() {
            super("Plays a sound. Useful for testing audio devices.", 
                    
                  "Simply type beep to use. This command contains no addtional " +
                  "arguments.", 
                  
                  "beep");
        }

        @Override
        public void execute(List<String> args) {
            ServiceLocator.getAudio().playSound("beep", null, false);
        }
        
    }
    
    private class CmdCLS extends TerminalCommand {
        public CmdCLS() {
            super("Clears the terminal output.",

                  "Simply type cls to use. This command contains no additional " + 
                  "arguments.",

                  "cls");
        }

        @Override
        public void execute(List<String> args) {
            shiftElements = -1;
        }
    }
    
}