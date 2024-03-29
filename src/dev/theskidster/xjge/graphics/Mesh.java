package dev.theskidster.xjge.graphics;

import dev.theskidster.xjge.main.App;
import dev.theskidster.xjge.main.Logger;
import dev.theskidster.xjge.main.ErrorUtil;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.joml.Matrix4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIBone;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.AIVertexWeight;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;

/**
 * @author J Hoffman
 * Created: Apr 27, 2020
 */

/**
 * Represents a 3D polygonal collection of vertices, edges, and faces that will define the shape of a {@link Model}.
 */
class Mesh {
    
    final int vao   = glGenVertexArrays();
    private int vbo = glGenBuffers();
    final int ibo   = glGenBuffers();
    
    int matIndex;
    
    IntBuffer indices;
    Matrix4f modelMatrix = new Matrix4f();
    
    /**
     * Creates a mesh object that will be used by the engine to render a {@link Model}.
     * 
     * @param aiMesh the mesh object provided by the Assimp library with which vertex data will be parsed
     */
    Mesh(AIMesh aiMesh, List<Bone> bones) {
        glBindVertexArray(vao);
        
        matIndex = aiMesh.mMaterialIndex(); //Used to specify which texture to use for this mesh
        
        parsePositionData(aiMesh);
        parseTexCoordData(aiMesh);
        parseNormalData(aiMesh);
        parseBoneData(aiMesh, bones);
        parseFaceData(aiMesh);
        
        glEnableVertexAttribArray(0); //position
        glEnableVertexAttribArray(1); //texture coordinates
        glEnableVertexAttribArray(3); //normal
        glEnableVertexAttribArray(7); //boneIDs
        glEnableVertexAttribArray(8); //weights
    }
    
    /**
     * Extracts the vertex positions of the mesh object and provides them to the graphics pipeline.
     * 
     * @param aiMesh the mesh object provided by the Assimp library with which vertex data will be parsed
     */
    private void parsePositionData(AIMesh aiMesh) {
        FloatBuffer positionBuf    = MemoryUtil.memAllocFloat(aiMesh.mNumVertices() * 3);
        AIVector3D.Buffer aiVecBuf = aiMesh.mVertices();
        
        while(positionBuf.hasRemaining()) {
            AIVector3D aiVec = aiVecBuf.get();
            
            positionBuf.put(aiVec.x())
                       .put(aiVec.y())
                       .put(aiVec.z());
        }
        
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, positionBuf.flip(), GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        
        MemoryUtil.memFree(positionBuf);
        
        ErrorUtil.checkGLError();
    }
    
    /**
     * Extracts the texture coordinates of the mesh object and provides them to the graphics pipeline. If a model contains no textures, the values will be 
     * initialized to zero by default.
     * 
     * @param aiMesh the mesh object provided by the Assimp library with which vertex data will be parsed
     */
    private void parseTexCoordData(AIMesh aiMesh) {
        FloatBuffer texCoordBuf    = MemoryUtil.memAllocFloat(aiMesh.mNumVertices() * 2);
        AIVector3D.Buffer aiVecBuf = aiMesh.mTextureCoords(0);
        
        if(aiVecBuf != null) {
            for(int i = 0; i < aiVecBuf.remaining(); i++) {
                AIVector3D aiVec = aiVecBuf.get(i);
                                
                texCoordBuf.put(aiVec.x())
                           .put(aiVec.y());
            }
        }
        
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, texCoordBuf.flip(), GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        
        MemoryUtil.memFree(texCoordBuf);
        
        ErrorUtil.checkGLError();
    }
    
    /**
     * Extracts the vertex normals of the mesh object and provides them to the graphics pipeline. If a model doesn't refactor light, the values will be 
     * initialized to zero by default.
     * 
     * @param aiMesh the mesh object provided by the Assimp library with which vertex data will be parsed
     */
    private void parseNormalData(AIMesh aiMesh) {
        FloatBuffer normalBuf      = MemoryUtil.memAllocFloat(aiMesh.mNumVertices() * 3);
        AIVector3D.Buffer aiVecBuf = aiMesh.mNormals();
        
        if(aiVecBuf != null) {
            for(int i = 0; i < aiVecBuf.remaining(); i++) {
                AIVector3D aiVec = aiVecBuf.get(i);
                
                normalBuf.put(aiVec.x())
                         .put(aiVec.y())
                         .put(aiVec.z());
            }
        }
        
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, normalBuf.flip(), GL_STATIC_DRAW);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0);
        
        MemoryUtil.memFree(normalBuf);
        
        ErrorUtil.checkGLError();
    }
    
    /**
     * Extracts vertex data necessary for use during skeletal animation. If no animation data is present, this step is skipped.
     * 
     * @param aiMesh the mesh object provided by the Assimp library with which vertex data will be parsed
     */
    private void parseBoneData(AIMesh aiMesh, List<Bone> bones) {
        PointerBuffer boneBuf = aiMesh.mBones();
        
        if(boneBuf != null) {
            Map<Integer, List<VertexWeight>> weights = new TreeMap<>();
            
            for(int b = 0; b < aiMesh.mNumBones(); b++) {
                AIBone aiBone = AIBone.create(boneBuf.get(b));
                Bone bone     = new Bone(bones.size(), aiBone.mName().dataString(), aiBone.mOffsetMatrix());
                
                bones.add(bone);
                
                for(int w = 0; w < aiBone.mNumWeights(); w++) {
                    AIVertexWeight aiWeight = aiBone.mWeights().get(w);
                    VertexWeight weight     = new VertexWeight(bone.id, aiWeight.mVertexId(), aiWeight.mWeight());
                    
                    List<VertexWeight> vwList = weights.get(weight.vertexID);
                    
                    if(vwList == null) {
                        vwList = new ArrayList<>();
                        weights.put(weight.vertexID, vwList);
                    }
                    
                    vwList.add(weight);
                }
            }
            
            IntBuffer boneIDBuf   = MemoryUtil.memAllocInt(Integer.BYTES * aiMesh.mNumVertices());
            FloatBuffer weightBuf = MemoryUtil.memAllocFloat(Float.BYTES * aiMesh.mNumVertices());
            
            for(int i = 0; i < aiMesh.mNumVertices(); i++) {
                List<VertexWeight> vwList = weights.get(i);
                int listSize = (vwList != null) ? vwList.size() : 0;
                
                for(int k = 0; k < App.MAX_WEIGHTS; k++) {
                    if(k < listSize) {
                        if(vwList != null) {
                            VertexWeight weight = vwList.get(k);
                            
                            boneIDBuf.put(weight.boneID);
                            weightBuf.put(weight.weight);
                        } else {
                            Logger.logSevere("Unable to find any vertex weight data.", null);
                        }
                    } else {
                        boneIDBuf.put(0);
                        weightBuf.put(0.0f);
                    }
                }
            }
            
            vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, boneIDBuf.flip(), GL_STATIC_DRAW);
            glVertexAttribPointer(7, 4, GL_FLOAT, false, 0, 0);
            MemoryUtil.memFree(boneIDBuf);
            
            vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, weightBuf.flip(), GL_STATIC_DRAW);
            glVertexAttribPointer(8, 4, GL_FLOAT, false, 0, 0);
            MemoryUtil.memFree(weightBuf);
            
            ErrorUtil.checkGLError();   
        }
    }
    
    /**
     * Uses the number of faces in the mesh to generate indices that can be used by the graphics pipeline to optimize rendering.
     * 
     * @param aiMesh the mesh object provided by the Assimp library with which vertex data will be parsed
     */
    private void parseFaceData(AIMesh aiMesh) {
        indices = MemoryUtil.memAllocInt(aiMesh.mNumFaces() * 3);
        AIFace.Buffer aiFaceBuf = aiMesh.mFaces();
        
        for(int i = 0; i < aiMesh.mNumFaces(); i++) {
            AIFace aiFace = aiFaceBuf.get(i);
            indices.put(aiFace.mIndices());
        }
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.flip(), GL_STATIC_DRAW);
        
        ErrorUtil.checkGLError();
    }
    
    /**
     * Frees the OpenGL buffer objects associated with this mesh.
     */
    void freeBuffers() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ibo);
    }
    
}