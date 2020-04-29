package dev.theskidster.xjge.graphics;

import dev.theskidster.xjge.main.App;
import java.util.Arrays;
import org.joml.Matrix4f;

/**
 * @author J Hoffman
 * Created: Apr 29, 2020
 */

/**
 * Data structure that represents a single frame of a {@link SkeletalAnimation}. These can be though of as a single frame of a stop motion claymation model.
 */
class KeyFrame {
    
    final Matrix4f[] boneTransforms;
    
    /**
     * Creates a new KeyFrame object that will be used to represent a single pos of an animated models skeleton.
     */
    KeyFrame() {
        boneTransforms = new Matrix4f[App.MAX_BONES];
        Arrays.fill(boneTransforms, App.IDENTITY);
    }
    
}