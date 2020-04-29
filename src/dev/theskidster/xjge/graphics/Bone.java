package dev.theskidster.xjge.graphics;

import org.joml.Matrix4f;
import org.lwjgl.assimp.AIMatrix4x4;

/**
 * @author J Hoffman
 * Created: Apr 29, 2020
 */

/**
 * Simple data structure which represents a single bone of a greater skeleton used for {@link SkeletalAnimation skeletal animations}.
 */
class Bone {
    
    int id;
    String name;
    Matrix4f offset;
    
    /**
     * Creates a new bone object.
     * 
     * @param id     an index value used to identify the bone in the vertex shader
     * @param name   the unique name of this bone that will correspond to a {@link Node} object in the model files hierarchy
     * @param offset a matrix representing this bones position offset relative to the model origin
     */
    Bone(int id, String name, AIMatrix4x4 offset) {
        this.id     = id;
        this.name   = name;
        this.offset = Graphics.convertFromAssimp(offset);
    }
    
}