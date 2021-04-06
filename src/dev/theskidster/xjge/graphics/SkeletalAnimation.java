package dev.theskidster.xjge.graphics;

import dev.theskidster.xjge.main.App;
import dev.theskidster.xjge.main.Game;
import java.util.List;
import org.joml.Matrix4f;
import org.lwjgl.assimp.AIAnimation;

/**
 * @author J Hoffman
 * Created: Apr 29, 2020
 * Rewrite: Jul 16, 2020
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
    
    final float duration;
    private float frameTime;
    private float seekTime;
    
    final boolean transition;
    private boolean finished;
    
    final String name;
    final String nextAnim;
    private KeyFrame prevFrame;
    private final KeyFrame currFrame = new KeyFrame();
    private KeyFrame nextFrame;
    
    private final List<KeyFrame> frames;
    
    /**
     * Constructs a new skeletal animation using the animation data and keyframes provided.
     * 
     * @param aiAnimation the raw animation data as provided by Assimp
     * @param frames      a collection of every {@link KeyFrame KeyFrame} object that will be used by this animation
     */
    public SkeletalAnimation(AIAnimation aiAnimation, List<KeyFrame> frames) {
        duration    = (float) aiAnimation.mDuration();
        name        = parseName(aiAnimation.mName().dataString());
        this.frames = frames;
        transition  = false;
        nextAnim    = null;
    }
    
    /**
     * Overloaded variant of {@link SkeletalAnimation(AIAnimation, List<KeyFrame2>)}. Used to construct short transition animations.
     * 
     * @param currAnim the animation currently playing
     * @param nextAnim the animation to play following this one
     * @param frames   the {@linkplain KeyFrame keyframes} automatically generated by the {@link Model} class
     */
    public SkeletalAnimation(String currAnim, String nextAnim, List<KeyFrame> frames) {
        duration      = frames.size();
        name          = "Transition|" + currAnim + "->" + nextAnim;
        this.frames   = frames;
        transition    = true;
        this.nextAnim = nextAnim;
    }
    
    /**
     * Parses and formats a string from Assimp which represents this animations name.
     * 
     * @param aiName the name retrieved from the model file by Assimp
     * @return a human-readable string to reference this animation by in other parts of the engine
     */
    private String parseName(String aiName) {
        return (aiName.contains("Armature|")) ? aiName.substring(aiName.indexOf("|") + 1, aiName.length()) : aiName;
    }
    
    /**
     * Increments the animations sequence forward.
     * 
     * @param speed a non-negative number between 1 and 0. A value of zero will pause the animation at its current {@link KeyFrame}.
     * @param loop  if true, animations will loop indefinitely. Supplying false will cease animation playback after their durations are reached.
     */
    private void step(float speed, boolean loop) {
        if(speed > 0) {
            frameTime += (speed + Game.getDelta());
            seekTime  += (speed + Game.getDelta());
        }
        
        if(frameTime > 1) frameTime %= 1;
        
        if(transition) finished = (seekTime >= duration - 2);
        
        if(seekTime > duration) {
            if(loop) {
                seekTime %= duration;
            } else {
                frameTime = 0;
                seekTime  = duration;
            }
        }
    }
    
    /**
     * Finds the previous and next {@linkplain KeyFrame keyframes} nearest to the animations current timestamp.
     */
    private void findNearestFrames() {
        prevFrame = frames.get(0);
        nextFrame = frames.get(0);
        
        for(int f = 0; f < frames.size(); f++) {
            nextFrame = frames.get(f);
            
            if(f > seekTime) {
                if(f == frames.size() - 1) nextFrame = frames.get(0);
                else break;
            }
            
            prevFrame = frames.get(f);
        }
    }
    
    float getFrameTime()         { return frameTime; }
    float getSeekTime()          { return seekTime; }
    boolean getFinished()        { return finished; }
    KeyFrame getCurrFrame()      { return currFrame; }
    KeyFrame getFrame(int index) { return frames.get(index); }
    
    /**
     * Sets the time elapsed between the current {@link KeyFrame} and the next in sequence.
     * 
     * @param frameTime a non-negative number between 1 and 0 indicating the progression of the current {@link KeyFrame}
     */
    void setFrameTime(float frameTime) {
        if(frameTime > 1)      frameTime = 1;
        else if(frameTime < 0) frameTime = 0;
        
        this.frameTime = frameTime;
    }
    
    /**
     * Sets the current seek time of the animation. Similar in function to a slider on a video or movie that can be moved back and forth to change the current
     * time/frame/scene.
     * 
     * @param seekTime 
     */
    void setSeekTime(float seekTime) {
        this.seekTime = seekTime;
    }
    
    /**
     * Generates a new "intermediate frame" between the previous and next {@linkplain KeyFrame keyframes} in sequence.
     * <br><br>
     * More specifically, by linearly interpolating between the values of the two keyframes bone transformations, a new set of transformations will be produced 
     * dynamically, enabling animation playback speed to be altered freely with smooth results.
     * 
     * @param speed a non-negative number between 1 and 0. A value of zero will pause the animation at its current {@link KeyFrame}.
     * @param loop  if true, animations will loop indefinitely. Supplying false will cease animation playback after their durations are reached.
     */
    void genCurrFrame(float speed, boolean loop) {
        step(speed, loop);
        findNearestFrames();
        
        for(int b = 0; b < App.MAX_BONES; b++) {
            prevFrame.getTransform(b).lerp(nextFrame.getTransform(b), frameTime, currFrame.getTransform(b));
        }
    }
    
    /**
     * Calculates a new matrix object representing a bone transformation between two animations that will act as part of a larger {@link KeyFrame}.
     * 
     * @param index the location in the collection to store this transformation at
     * @return      a new matrix that will be used during a transition animation
     */
    Matrix4f calcTransition(int index) {
        findNearestFrames();
        Matrix4f result = new Matrix4f();
        prevFrame.getTransform(index).lerp(nextFrame.getTransform(index), frameTime, result);
        
        return result;
    }
    
}