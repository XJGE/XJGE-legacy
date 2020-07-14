package dev.theskidster.xjge.graphics;

import dev.theskidster.xjge.main.App;
import dev.theskidster.xjge.main.Game;
import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;
import org.lwjgl.assimp.AIAnimation;

/**
 * @author J Hoffman
 * Created: Apr 29, 2020
 */

/**
 * Skeletal animations utilize a series of interconnected bones (collectively known as an "Armature") to offset the vertex positions of a models mesh. More 
 * generally, the individual bones of the models armature are arranged into one or more {@link KeyFrame KeyFrames} which are then played in sequence to create the 
 * illusion of movement.
 * <br><br>
 * Because the structure of the armature is hierarchal, moving a single {@link Bone} will in turn effect each of its children equally. That is, if we move the 
 * femur bone of some bipedal model for instance, the shin, foot, and toes will follow- however the hip bone (parent to the femur) will remain stationary.
 * <br><br>
 * The current implementation of skeletal animations as of version 1.2.0 of this engine does NOT make use of interpolation. This may be subject to change in the 
 * future.
 */
class SkeletalAnimation {
    
    private float animTime  = 0;
    private float frameTime = 0;
    final float duration;
    
    final String name;
    
    private List<KeyFrame> frames;
    private List<Matrix4f> finalTransforms;
    
    /**
     * Constructs a new skeletal animation using the animation data and keyframes provided.
     * 
     * @param aiAnimation the raw animation data as provided by Assimp
     * @param frames      a collection of every {@link KeyFrame KeyFrame} object that will be used by this animation
     */
    SkeletalAnimation(AIAnimation aiAnimation, List<KeyFrame> frames) {
        duration    = (float) aiAnimation.mDuration();
        name        = aiAnimation.mName().dataString();
        this.frames = frames;
        
        finalTransforms = new ArrayList<>();
        for(int i = 0; i < App.MAX_BONES; i++) finalTransforms.add(new Matrix4f());
    }
    
    List<Matrix4f> getFinalTransforms() {
        return finalTransforms;
    }
    
    /**
     * Increments the animations sequence forward.
     *
     * @param prevFrame the previous {@link KeyFrame} in this animations sequence, used to calculate interpolated bone positions
     * @param nextFrame the next {@link KeyFrame} in this animations sequence, used to calculate interpolated bone positions
     * @param speed     a non-negative number between 1 and 0. A value of zero will pause the animation at its current {@link KeyFrame}.
     * @param loop      if true, animations will loop indefinitely. Supplying false will cease animation playback after their durations are reached.
     */
    void step(KeyFrame prevFrame, KeyFrame nextFrame, float speed, boolean loop) {
        if(speed > 0) {
            frameTime += (speed + Game.getDelta());
            animTime  += (speed + Game.getDelta());
        }
        
        if(frameTime > 1) frameTime %= 1;
        
        if(animTime > duration) {
            if(loop) {
                animTime %= duration;
            } else {
                frameTime = 0;
                animTime  = duration;
            }
        }
        
        calcFinalTransforms(prevFrame, nextFrame);
    }
    
    /**
     * Calculates a new "intermediate frame" between the previous and next {@linkplain KeyFrame keyframes} in sequence.
     * <br><br>
     * More specifically, by linearly interpolating between the values of the two keyframes bone transformations, a new set of transformations will be produced 
     * dynamically, enabling animation playback speed to be altered freely with smooth results.
     *
     * @param prevFrame the previous {@link KeyFrame} in this animations sequence, used to calculate interpolated bone positions
     * @param nextFrame the next {@link KeyFrame} in this animations sequence, used to calculate interpolated bone positions
     */
    private void calcFinalTransforms(KeyFrame prevFrame, KeyFrame nextFrame) {
        prevFrame = frames.get(0);
        nextFrame = frames.get(0);
        
        for(int f = 0; f < frames.size(); f++) {
            nextFrame = frames.get(f);
            
            if(f > animTime) {
                if(f == frames.size() - 1) nextFrame = frames.get(0);
                else break;
            }
            
            prevFrame = frames.get(f);
        }
        
        for(int i = 0; i < App.MAX_BONES; i++) {
            prevFrame.boneTransforms[i].lerp(nextFrame.boneTransforms[i], frameTime, finalTransforms.get(i));
        }
    }
    
}