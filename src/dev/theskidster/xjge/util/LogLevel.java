package dev.theskidster.xjge.util;

/**
 * @author J Hoffman
 * Created: Jan 14, 2020
 */

/**
 * Used to control the precedence of logging output.
 */
public enum LogLevel {
    /**
     * This will output a message to the logger console. Useful for debugging, indicates no issues.
     */
    INFO,
    
    /**
     * The engine may have entered an invalid state but hasn't crashed, expect undefined behavior.
     */
    WARNING,
    
    /**
     * The application has encountered a fatal error that will require it to cease execution.
     */
    SEVERE
}