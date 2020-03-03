package dev.theskidster.xjge.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import static org.lwjgl.glfw.GLFW.glfwGetVersionString;
import static org.lwjgl.openal.AL11.*;
import static org.lwjgl.opengl.GL11.*;
import dev.theskidster.xjge.main.App;

/**
 * @author J Hoffman
 * Created: Jan 14, 2020
 */

/**
 * Keeps a chronological record of significant events occurring within the engine.
 */
public final class Logger {
    
    private static boolean initialized;
    
    private static Exception ex;
    private static PrintWriter writer;
    
    /**
     * Creates a new plain text file that will record the output of the logger console. This file is overwritten every time the engine is ran.
     */
    public static void init() {
        if(!initialized) {
            try {
                FileWriter logFile = new FileWriter("log.txt");
                writer = new PrintWriter(logFile);
            } catch(IOException e) {
                e.printStackTrace();
            }
            
            initialized = true;
        } else {
            log(LogLevel.WARNING, "Initialization failed, logger is already initialized.");
        }
    }
    
    /**
     * Writes a message to the console.
     * 
     * @param level the level of severity
     * @param desc  the message that will display
     */
    public static void log(LogLevel level, String desc) {
        String message;
        String timestamp = new SimpleDateFormat("MM-dd-yyyy h:mma").format(new Date());
        
        switch(level) {
            case INFO:
                message = "INFO: " + desc;
                System.out.println(message);
                writer.println(message);
                break;
                
            case WARNING:
                message = "WARNING: " + desc;
                System.out.println(System.lineSeparator() + timestamp);
                System.out.println(message + System.lineSeparator());
                writer.println();
                writer.println(timestamp);
                writer.println(message);
                writer.println();
                if(ex != null) {
                    ex.printStackTrace(writer);
                    writer.println();
                    ex = null;
                }
                break;
                
            case SEVERE:
                message = "ERROR: " + desc;
                System.err.println(System.lineSeparator() + timestamp);
                System.err.println(message + System.lineSeparator());
                writer.println();
                writer.println(timestamp);
                writer.println(message);
                writer.println();
                if(ex != null) {
                    ex.printStackTrace(writer);
                } else {
                    setStackTrace(new RuntimeException());
                    ex.printStackTrace(writer);
                }
                writer.close();
                throw new RuntimeException();
        }
    }
    
    /**
     * Provides the logger console with a detailed report of an exception that the application may have encountered.
     * 
     * @param e the exception to log
     */
    public static void setStackTrace(Exception e) {
        ex = e;
    }
    
    /**
     * Writes the logger output to the log file created during {@link init()}.
     */
    public static void close() {
        writer.close();
    }
    
    /**
     * Displays information pertaining to the system variables of the current operating system in which the engine is running.
     */
    public static void printSysInfo() {
        log(LogLevel.INFO, "--------------------------------------------------------------------------------");
        log(LogLevel.INFO, "OS NAME:\t\t" + System.getProperty("os.name"));
        log(LogLevel.INFO, "JAVA VER:\t\t" + System.getProperty("java.version"));
        log(LogLevel.INFO, "GLFW VER:\t\t" + glfwGetVersionString());
        log(LogLevel.INFO, "OPENGL VER:\t" + glGetString(GL_VERSION));
        log(LogLevel.INFO, "OPENAL VER:\t" + alGetString(AL_VERSION));
        log(LogLevel.INFO, "MONITORS:\t\t" + App.getNumDisplayDevices() + " (" + App.getDisplayInfo() + ")");
        log(LogLevel.INFO, "SPEAKERS:\t\t" + App.getNumAudioDevices() + " (" + App.getAudioDeviceName().substring(15) + ")");
        log(LogLevel.INFO, "CONTROLLERS:\t" + (App.getNumInputDevices() - 1));
        log(LogLevel.INFO, "--------------------------------------------------------------------------------");
        
        System.out.println();
        writer.println();
    }
    
}