package dev.theskidster.xjge.main;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import static org.lwjgl.opengl.GL11.*;
import java.io.File;
import static org.lwjgl.glfw.GLFW.glfwGetVersionString;

/**
 * @author J Hoffman
 * Created: Jan 14, 2020
 * Rewrote: Apr 6, 2021
 */

/**
 * Keeps a chronological record of significant events occurring within the application.
 */
public final class Logger {
    
    private static PrintWriter writer;
    private final static StringBuilder builder = new StringBuilder();
    
    /**
     * Displays information pertaining to the system variables of the current operating system on which the engine is running.
     */
    static void logSystemInfo() {
        logInfo("--------------------------------------------------------------------------------");
        logInfo("OS NAME:\t\t" + System.getProperty("os.name"));
        logInfo("JAVA VERSION:\t" + System.getProperty("java.version"));
        logInfo("GLFW VERSION:\t" + glfwGetVersionString());
        logInfo("OPENGL VERSION:\t" + glGetString(GL_VERSION));
        logInfo("APP VERSION:\t" + App.ENGINE_VERSION);
        logInfo("--------------------------------------------------------------------------------");
        
        System.out.println();
        builder.append(System.lineSeparator());
    }
    
    /**
     * Writes low-priority informative output to the logger and console.
     * 
     * @param message the text to display
     */
    public static void logInfo(String message) {
        System.out.println("INFO: " + message);
                
        builder.append("INFO: ")
               .append(message)
               .append(System.lineSeparator());
    }
    
    /**
     * Writes a medium-priority message to the logger and console. Warning messages indicate that the engine may have entered an invalid state which could
     * result in undefined behavior.
     * 
     * @param message the text to display
     * @param e       an optional exception used to output a stack trace, if null is passed, no stack trace will be displayed
     */
    public static void logWarning(String message, Exception e) {
        String timestamp = new SimpleDateFormat("MM-dd-yyyy h:mma").format(new Date());
                
        System.out.println(System.lineSeparator() + timestamp);
        System.out.println("WARNING: " + message + System.lineSeparator());

        builder.append(System.lineSeparator())
               .append(timestamp)
               .append(System.lineSeparator())
               .append("WARNING: ")
               .append(message)
               .append(System.lineSeparator())
               .append(System.lineSeparator());

        if(e != null) {
            var stackTrace = e.getStackTrace();

            System.out.println(e.toString());
            builder.append(e.toString())
                   .append(System.lineSeparator());

            for(StackTraceElement element : stackTrace) {
                System.out.println("\t" + element.toString());

                builder.append("\t")
                       .append(element.toString())
                       .append(System.lineSeparator());
            }

            System.out.println();
            builder.append(System.lineSeparator());
        }
    }
    
    /**
     * Writes a high-priority message to the logger and console. Indicates that the application has encountered a fatal error that will require it to cease 
     * execution and generate a text file of the loggers contents.
     * 
     * @param message the text to display
     * @param e       an optional exception used to output a stack trace, if null is passed, the engine will generate a generic RuntimeException
     */
    public static void logSevere(String message, Exception e) {
        String date = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
        String time = new SimpleDateFormat("h:mma").format(new Date());
        
        String timestamp = date + " " + time;
                
        System.err.println(System.lineSeparator() + timestamp);
        System.err.println("ERROR: " + message + System.lineSeparator());

        builder.append(System.lineSeparator())
               .append(timestamp)
               .append(System.lineSeparator())
               .append("ERROR: ")
               .append(message)
               .append(System.lineSeparator())
               .append(System.lineSeparator());

        if(e == null) e = new RuntimeException();
        var stackTrace = e.getStackTrace();
        
        System.err.println(e.toString());
        builder.append(e.toString())
               .append(System.lineSeparator());

        for(StackTraceElement element : stackTrace) {
            System.err.println("\t" + element.toString());

            builder.append("\t")
                   .append(element.toString())
                   .append(System.lineSeparator());
        }

        System.err.println();
        builder.append(System.lineSeparator());
        
        File file     = new File("log " + date + ".txt");
        int duplicate = 0;

        while(file.exists()) {
            duplicate++;
            file = new File("log " + date + " (" + duplicate + ").txt");
        }

        try(FileWriter logFile = new FileWriter(file.getName())) {
            writer = new PrintWriter(logFile);
            writer.append(builder.toString());
            writer.close();
        } catch(Exception ex) {}

        System.exit(-1);
    }
    
}