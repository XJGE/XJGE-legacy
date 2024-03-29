/**
 * Contains a single abstract {@link Entity} class which is used to represent dynamic objects within the game world. Subclasses of this type exhibit a loose 
 * structure influenced by the engines {@link dev.theskidster.xjge.main.Game#loop() Game.loop()}. This structure consists of an {@link Entity#update() update()} 
 * method for game logic, a {@link Entity#render(Vector3f, Vector3f, Vector3f, LightSource[], int) render()} method to organize calls to the graphics API, and a 
 * {@link Entity#destroy() destroy()} method to facilitate resource deallocation after an entity is no longer needed.
 * <p>
 * Its encouraged to keep the inheritance hierarchy of entity subclasses shallow and only include abstract subclasses of Entity in this package. All implementing 
 * classes should be contained in their own package adjacent to this one in a similar fashion to the puppet object implementations. Additionally, entity objects 
 * should always choose composition over inheritance by using utility classes like {@link dev.theskidster.xjge.graphics.Graphics Graphics}, 
 * {@link dev.theskidster.xjge.puppets.Puppet Puppet}, {@link dev.theskidster.xjge.util.Observable Observable}, {@link dev.theskidster.xjge.util.Timer Timer}, and 
 * whatever other components the implementation chooses to provide (like collision utilities).
 * </p>
 * @see dev.theskidster.xjge.puppets
 */
package dev.theskidster.xjge.entities;