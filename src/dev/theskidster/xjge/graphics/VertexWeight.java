package dev.theskidster.xjge.graphics;

/**
 * @author J Hoffman
 * Created: Apr 29, 2020
 */

/**
 * Data structure that represents a vertex weight that will determine how much influence a {@link Bone} has on a {@link Mesh}. Typically multiple weights will 
 * influence a single vertex to an extent.
 */
class VertexWeight {
    
    int boneID;
    int vertexID;
    float weight;
    
    /**
     * Creates a new vertex weight.
     * 
     * @param boneID   an index value used to identify the bone in the vertex shader
     * @param vertexID the id of the vertex this weight will effect
     * @param weight   the total influence of this weight between 0 and 1
     */
    VertexWeight(int boneID, int vertexID, float weight) {
        this.boneID   = boneID;
        this.vertexID = vertexID;
        this.weight   = weight;
    }
    
}