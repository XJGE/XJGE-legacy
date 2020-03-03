/**
 * Contains classes useful for building user interfaces.
 * 
 * <p>
 * The engines UI system uses several {@link Component} objects to comprise an interface. These components are local to the viewports in which they're attached. 
 * Additionally, some components may use {@link theskidster.xjge.puppets Puppet} objects to handle input (forms, menus, etc.) whereas others are used more passively to 
 * provide information to the user (health bar, gold, etc).
 * </p>
 * <p>
 * In addition to the abstract component class, this package also provides several utility classes including {@link Text} which enables text to be drawn to the screen 
 * with custom bitmap fonts. {@link RectangleBatch} for batch drawing single color rectangles (useful for backgrounds). And {@link Menu} to create custom menus 
 * controlled through an input device.
 * </p>
 */
package dev.theskidster.xjge.ui;