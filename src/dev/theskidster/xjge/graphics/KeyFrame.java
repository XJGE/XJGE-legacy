package dev.theskidster.xjge.graphics;

import dev.theskidster.xjge.main.App;
import java.util.Arrays;
import org.joml.Matrix4f;

/**
 * @author J Hoffman
 * Created: Apr 29, 2020
 */

/**
 * Data structure which represents a single frame of a {@link SkeletalAnimation}. More specifically, a keyframe contains the individual transformations of each 
 * {@link Bone} in the models armature. Key frames can be conceptualized as a sort of "snapshot" of the armatures pose at some point in time which can be used in 
 * sequence with other keyframes to create the illusion of movement.
 */
class KeyFrame {
    
    final Matrix4f[] boneTransforms;
    final Matrix4f IDENTITY = new Matrix4f();
    
    /**
     * Constructs an array of {@link Bone} transformations that will be used to define the pose of a models armature at a certain point in time.
     */
    KeyFrame() {
        boneTransforms = new Matrix4f[App.MAX_BONES];
        Arrays.fill(boneTransforms, IDENTITY);
    }
    
}