package dev.theskidster.xjge.graphics;

import dev.theskidster.xjge.main.App;
import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;

/**
 * @author J Hoffman
 * Created: Apr 29, 2020
 * Rewrite: Jul 16, 2020
 */

/**
 * Data structure which represents a single frame of a {@link SkeletalAnimation}. More specifically, a keyframe contains the individual transformations of each 
 * {@link Bone} in the models armature. Key frames can be conceptualized as a sort of "snapshot" of the armatures pose at some point in time which can be used in 
 * sequence with other keyframes to create the illusion of movement.
 */
class KeyFrame {
    
    private List<Matrix4f> transforms;
    
    /**
     * Constructs an array of {@link Bone} transformations that will be used to define the pose of a models armature at a certain point in time.
     */
    public KeyFrame() {
        transforms = new ArrayList<>();
        for(int b = 0; b < App.MAX_BONES; b++) transforms.add(new Matrix4f());
    }
    
    Matrix4f getTransform(int index)  { return transforms.get(index); }
    List<Matrix4f> getTransformData() { return transforms; }
    
    /**
     * Sets the transformation matrix of a bone within the collection at the index specified.
     * 
     * @param index     the location in the collection to store this transformation at
     * @param transform the value of the bone transformation
     */
    void setTransform(int index, Matrix4f transform) {
        transforms.get(index).set(transform);
    }
    
}