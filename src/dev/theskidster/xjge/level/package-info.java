/**
 * This package supplies a single abstract class which can be subclassed and used to organize gameplay. Level objects contain the state of a game world and the entities 
 * which inhabit it. A good way to conceptualize a level is as a single floor of a skyscraper, with the 
 * {@link theskidster.xjge.main.Game#setLevel(Level) Game.setLevel()} method acting as an elevator between floors. Because the inheritance hierarchy of level subclasses 
 * is likely to be so shallow, they need not be included in their own packages like entities or puppet objects unless deemed absolutely necessary by the implementation 
 * for the sake of organization.
 */
package dev.theskidster.xjge.level;