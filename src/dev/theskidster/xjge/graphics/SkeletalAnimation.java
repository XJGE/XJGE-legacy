package dev.theskidster.xjge.graphics;

import java.util.List;
import org.lwjgl.assimp.AIAnimation;

/**
 * @author J Hoffman
 * Created: Apr 29, 2020
 */

/**
 * Skeletal animations utilize a series of interconnected bones (collectively known as an "Armature") to offset the vertex positions of a models mesh. More 
 * generally, the individual bones of the models armature are arranged into one or more {@link KeyFrame keyframes} which are then played in sequence to create the 
 * illusion of movement.
 * <br><br>
 * Because the structure of the armature is hierarchal, moving a single {@link Bone} will in turn effect each of its children equally. That is, if we move the 
 * femur bone of some bipedal model for instance, the shin, foot, and toes will follow- however the hip bone (parent to the femur) will remain stationary.
 * <br><br>
 * The current implementation of skeletal animations as of version 1.2.0 of this engine does NOT make use of interpolation. This may be subject to change in the 
 * future.
 */
class SkeletalAnimation {
    
    int currFrame;
    double duration;
    String name;
    List<KeyFrame> frames;
    
    /**
     * Constructs a new skeletal animation using the animation data and keyframes provided.
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
     * Finds the next {@link KeyFrame} in sequence.
     * 
     * @return the keyframe following the one currently being displayed
     */
    KeyFrame getNextFrame() {
        step();
        return frames.get(currFrame);
    }
    
    /**
     * Increments the sequence of the animation forward one {@link KeyFrame}.
     */
    void step() {
        int nextFrame = currFrame + 1;
        currFrame     = (nextFrame > frames.size() - 1) ? 0 : nextFrame;
    }
    
}