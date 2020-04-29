package dev.theskidster.xjge.graphics;

import java.util.List;
import org.lwjgl.assimp.AIAnimation;

/**
 * @author J Hoffman
 * Created: Apr 29, 2020
 */

/**
 * Represents a 3D animation that uses a series of interconnected {@link Bone bones} to move a models {@link Mesh}.
 */
class SkeletalAnimation {
    
    int currFrame;
    double duration;
    String name;
    List<KeyFrame> frames;
    
    /**
     * Creates a new representation of a single 3D animation.
     * 
     * @param aiAnimation the raw animation data as provided by Assimp
     * @param frames      the {@link KeyFrame keyframes} used by this animation
     */
    SkeletalAnimation(AIAnimation aiAnimation, List<KeyFrame> frames) {
        currFrame   = 0;
        duration    = aiAnimation.mDuration();
        name        = aiAnimation.mName().dataString();
        this.frames = frames;
    }
    
    /**
     * Finds the current {@link KeyFrame} of this animation.
     * 
     * @return the frame currently being displayed
     */
    KeyFrame getCurrFrame() {
        return frames.get(currFrame);
    }
    
    /**
     * Gets the next {@link KeyFrame} in sequence.
     * 
     * @return the keyframe following the current one
     */
    KeyFrame getNextFrame() {
        step();
        return frames.get(currFrame);
    }
    
    /**
     * Steps the animation forward by one {@link KeyFrame}.
     */
    void step() {
        int nextFrame = currFrame + 1;
        currFrame     = (nextFrame > frames.size() - 1) ? 0 : nextFrame;
    }
    
}