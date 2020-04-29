package dev.theskidster.xjge.graphics;

import dev.theskidster.xjge.main.Game;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2i;

/**
 * @author J Hoffman
 * Created: Apr 29, 2020
 */

/**
 * Represents a 2D sprite animation that iterates over a series of sub-images from a {@link SpriteSheet} in sequence to create the illusion of movement.
 */
public class SpriteAnimation {
    
    public int currFrame;
    public int speed;
    
    public List<Vector2i> frames = new ArrayList<>();
    
    /**
     * Creates a new single-frame animation- a non-animation if you will.
     * 
     * @param frame the frame to display indefinitely until the animation is changed
     */
    public SpriteAnimation(Vector2i frame) {
        frames.add(frame);
        speed = 1;
    }
    
    /**
     * Creates a new 2D animation using the frames provided.
     * 
     * @param frames the frames that comprise the animation in sequence
     * @param speed  the speed of the animation in game ticks
     */
    public SpriteAnimation(List<Vector2i> frames, int speed) {
        this.frames = frames;
        this.speed  = speed;
    }
    
    /**
     * Steps the animation forward changing the texture coordinates of the {@link SpriteSheet} and subsequently the sub-image rendered.
     * 
     * @param sprite the sprite sheet used by this animation
     */
    public void updateAnimation(SpriteSheet sprite) {
        if(currFrame < frames.size()) {
            if(Game.tick(speed)) {
                sprite.texCoords.set(
                        sprite.imgOffsets.get(frames.get(currFrame)).x,
                        sprite.imgOffsets.get(frames.get(currFrame)).y);
                currFrame++;
            }
        } else {
            currFrame = 0;
        }
    }
    
}