package dev.theskidster.xjge.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

/**
 * @author J Hoffman
 * Created: Jan 15, 2020
 */

/**
 * Component object which can be used to relay information about state changes occurring in the implementing object to one or more observers located anywhere in 
 * the application. Objects which use observable should define the properties (often fields) observers should look for in their constructors with the properties 
 * collection provided.
 */
public class Observable {
    
    private PropertyChangeSupport observable;
    public Map<String, Object> properties = new HashMap<>();
    
    /**
     * Creates a new observable object that will look for state changes in the object provided and supply it to other parts of the application.
     * 
     * @param object the implementing object that will expose its state
     */
    public Observable(Object object) {
        observable = new PropertyChangeSupport(object);
    }
    
    /**
     * Exposes this objects state to another. Observer objects must implement {@link java.beans.PropertyChangeListener PropertyChangeListener}.
     * 
     * @param observer the object that is interested in the state changes of the one implementing the {@link Observable} component
     */
    public void addObserver(PropertyChangeListener observer) {
        observable.addPropertyChangeListener(observer);
    }
    
    /**
     * Revokes an observers access to view this objects state changes.
     * 
     * @param observer the observer to remove
     */
    public void removeObserver(PropertyChangeListener observer) {
        observable.removePropertyChangeListener(observer);
    }
    
    /**
     * Notifies all observers of state changes in this object.
     * 
     * @param name     the name of the property (field) we're observing 
     * @param property an object representing the value of the property changed
     */
    public void notifyObservers(String name, Object property) {
        observable.firePropertyChange(name, properties.get(name), property);
        properties.put(name, property);
    }
    
}