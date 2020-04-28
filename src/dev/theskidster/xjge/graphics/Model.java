package dev.theskidster.xjge.graphics;

import dev.theskidster.xjge.main.App;
import dev.theskidster.xjge.shader.core.ShaderCore;
import dev.theskidster.xjge.util.ErrorUtil;
import dev.theskidster.xjge.util.LogLevel;
import dev.theskidster.xjge.util.Logger;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Map;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * @author J Hoffman
 * Created: Apr 25, 2020
 */

/**
 * Represents a 3D model. Models may possess animations, multiple meshes, textures, or a combination of these things. Various limitations are imposed by the 
 * engine to ensure models are loaded with consistency. The Autodesk .fbx file format is the preferred format of this engine for its compact size, though other 
 * formats should work as well.
 */
public class Model {
    
    private AIScene aiScene;
    private Matrix3f normal  = new Matrix3f();
    private Vector3f noValue = new Vector3f();
    
    private Mesh[] meshes;
    private Texture[] textures;
    
    /**
     * Parses the file provided and generates a 3D model from the data it contains.
     * 
     * @param filename the name of the file to load. Expects the file extension to be included.
     */
    public Model(String filename) {
        loadModel(filename, 
                aiProcess_JoinIdenticalVertices | 
                aiProcess_Triangulate | 
                aiProcess_GenSmoothNormals | 
                aiProcess_LimitBoneWeights | 
                aiProcess_FixInfacingNormals);
    }
    
    /**
     * Overloaded version of {@link Model(String)} that permits the use of custom post processing arguments.
     * 
     * @param filename the name of the file to load. Expects the file extension to be included.
     * @param args     the Assimp arguments to use for post processing such as {@link org.lwjgl.assimp.Assimp#aiProcess_Triangulate aiProcess_Triangulate}, 
     *                 {@link org.lwjgl.assimp.Assimp#aiProcess_GenSmoothNormals aiProcess_GenSmoothNormals}, 
     *                 {@link org.lwjgl.assimp.Assimp#aiProcess_FixInfacingNormals aiProcess_FixInfacingNormals}, etc.
     */
    public Model(String filename, int args) {
        loadModel(filename, args); 
    }
    
    /**
     * Specifies various file open/read/close procedures and then constructs a new model instance using the data parsed from the file.
     * 
     * @param filename the name of the file to load. Expects the file extension to be included.
     * @param args     the Assimp arguments to use for post processing such as {@link org.lwjgl.assimp.Assimp#aiProcess_Triangulate aiProcess_Triangulate}, 
     *                 {@link org.lwjgl.assimp.Assimp#aiProcess_GenSmoothNormals aiProcess_GenSmoothNormals}, 
     *                 {@link org.lwjgl.assimp.Assimp#aiProcess_FixInfacingNormals aiProcess_FixInfacingNormals}, etc.
     */
    private void loadModel(String filename, int args) {
        String filepath = "/dev/theskidster/" + App.DOMAIN + "/assets/" + filename;
        
        try(InputStream file = Model.class.getResourceAsStream(filepath)) {
            byte[] data = file.readAllBytes();
            
            ByteBuffer modelBuf = MemoryUtil.memAlloc(data.length).put(data).flip();
            AIFileIO aiFileIO   = AIFileIO.create();
            AIFile aiFile       = AIFile.create();
            
            AIFileOpenProcI openProcedure = new AIFileOpenProc() {
                @Override
                public long invoke(long pFileIO, long fileName, long openMode) {
                    AIFileReadProcI readProcedure = new AIFileReadProc() {
                        @Override
                        public long invoke(long pFile, long pBuffer, long size, long count) {
                            long numBytes = Math.min(modelBuf.remaining(), size * count);
                            MemoryUtil.memCopy(MemoryUtil.memAddress(modelBuf) + modelBuf.position(), pBuffer, numBytes);
                            
                            return numBytes;
                        }
                    };
                    
                    AIFileSeekI seekProcedure = new AIFileSeek() {
                        @Override
                        public int invoke(long pFile, long offset, int origin) {
                            switch(origin) {
                                case Assimp.aiOrigin_CUR: modelBuf.position(modelBuf.position() + (int) offset); break;
                                case Assimp.aiOrigin_SET: modelBuf.position((int) offset);                       break;
                                case Assimp.aiOrigin_END: modelBuf.position(modelBuf.limit() + (int) offset);    break;
                            }
                            
                            return 0;
                        }
                    };
                    
                    AIFileTellProcI tellProcedure = new AIFileTellProc() {
                        @Override
                        public long invoke(long pFile) { return modelBuf.limit(); }
                    };
                    
                    aiFile.ReadProc(readProcedure);
                    aiFile.SeekProc(seekProcedure);
                    aiFile.FileSizeProc(tellProcedure);
                    
                    return aiFile.address();
                }
            };
            
            AIFileCloseProcI closeProcedure = new AIFileCloseProc() {
                @Override
                public void invoke(long pFileIO, long pFile) {}
            };
            
            aiFileIO.set(openProcedure, closeProcedure, NULL);
            
            aiScene = aiImportFileEx(filepath.substring(1), args, aiFileIO);
            
            if(aiScene != null) {
                MemoryUtil.memFree(modelBuf);
                
                parseMeshData(aiScene.mMeshes());
                parseTextureData(aiScene.mMaterials());
            } else {
                MemoryUtil.memFree(modelBuf);
                throw new IllegalStateException(aiGetErrorString());
            }
        } catch(Exception e) {
            Logger.setStackTrace(e);
            Logger.log(LogLevel.WARNING, "Failed to load model: \"" + filename + "\"");
        }
    }
    
    /**
     * Parses mesh data used by this model.
     * 
     * @param meshBuf    the buffer of model mesh data provided by Assimp
     * @throws Exception if the data parsed from the file is invalid
     */
    private void parseMeshData(PointerBuffer meshBuf) throws Exception {
        meshes = new Mesh[aiScene.mNumMeshes()];
        
        for(int i = 0; i < meshes.length; i++) {
            AIMesh aiMesh = AIMesh.create(meshBuf.get(i));
            meshes[i]     = new Mesh(aiMesh);
        }
    }
    
    /**
     * Parses the textures used by this model.
     * <br><br>
     * The engine imposes a maximum number of textures a single model may use at once though the {@link dev.theskidster.xjge.main.App#MAX_TEXTURES MAX_TEXTURES}
     * field. By default this is set to four but can be changed at the discretion of the implementation to better suit its needs.
     * <br><br>
     * Models are loaded directly from memory and as such, must embed their textures inside materials to load correctly. Additionally, textures must be located 
     * in the same directory as the model file itself.
     * 
     * @param materialBuf the buffer of model material data provided by Assimp
     * @throws Exception  if one or more textures could not be located. This will likely result in the engine using a placeholder texture instead.
     */
    private void parseTextureData(PointerBuffer materialBuf) throws Exception {
        if(aiScene.mNumMaterials() > App.MAX_TEXTURES) {
            textures = new Texture[App.MAX_TEXTURES];
            Logger.log(LogLevel.WARNING, 
                    "Invalid number of textures. Limit of " + App.MAX_TEXTURES + 
                    " permitted, found " + aiScene.mNumMaterials());
        } else {
            textures = new Texture[aiScene.mNumMaterials()];
        }
        
        /*
        The reason we use materials to load model textures here is because we want to 
        load models from memory within the .jar file. As such, you must embed your 
        textures in whatever 3D modeling program you're using prior to loading it into 
        the engine.
        */
        
        for(int i = 0; i < textures.length; i++) {
            AIMaterial aiMaterial = AIMaterial.create(materialBuf.get(i));
            
            AIString filename = AIString.calloc();
            Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, filename, (IntBuffer) null, null, null, null, null, null);
            
            textures[i] = new Texture(filename.dataString());
            
            glBindTexture(GL_TEXTURE_2D, textures[i].handle);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glBindTexture(GL_TEXTURE_2D, 0);
        }
    }
    
    /**
     * Couples the local space of the models mesh normals to that of the current scenes world space. Use this to fix the direction of the light source relative 
     * to the model whenever it's being illuminated incorrectly.
     */
    public void delocalizeNormal() {
        for(Mesh mesh : meshes) normal.set(mesh.modelMatrix.invert());
    }
    
    /**
     * Rotates the entire 3D model in relation to the worlds x-axis.
     * 
     * @param angle the angle with which the model will be rotated
     */
    public void rotateX(float angle) {
        for(Mesh mesh : meshes) mesh.modelMatrix.rotateX((float) Math.toRadians(angle));
    }
    
    /**
     * Rotates the entire 3D model in relation to the worlds y-axis.
     * 
     * @param angle the angle with which the model will be rotated
     */
    public void rotateY(float angle) {
        for(Mesh mesh : meshes) mesh.modelMatrix.rotateY((float) Math.toRadians(angle));
    }
    
    /**
     * Rotates the entire 3D model in relation to the worlds z-axis.
     * 
     * @param angle the angle with which the model will be rotated
     */
    public void rotateZ(float angle) {
        for(Mesh mesh : meshes) mesh.modelMatrix.rotateZ((float) Math.toRadians(angle));
    }
    
    /**
     * Scales the entire 3D model by the factor specified.
     * 
     * @param factor the factor with which the models size will be multiplied by
     */
    public void scale(int factor) {
        for(Mesh mesh : meshes) mesh.modelMatrix.scale(factor);
    }
    
    /**
     * translates the entire 3D model to the location specified.
     * 
     * @param position the position to set the model to
     */
    public void translation(Vector3f position) {
        for(Mesh mesh : meshes) mesh.modelMatrix.translation(position);
    }
    
    public void render(String shader, LightSource[] lights, int numLights) {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        
        ShaderCore.use(shader);
        
        for(int m = 0; m < meshes.length; m++) {
            if(m < textures.length) glBindTexture(GL_TEXTURE_2D, textures[m].handle);
            
            glBindVertexArray(meshes[m].vao);
            
            ShaderCore.setMat4("uModel", false, meshes[m].modelMatrix);
            ShaderCore.setMat3("uNormal", true, normal);
            ShaderCore.setInt("uNumLights", numLights);
            ShaderCore.setInt("uType", 5);
            
            for(int i = 0; i < App.MAX_LIGHTS; i++) {
                if(lights[i] != null) {
                    if(lights[i].enabled) {
                        ShaderCore.setFloat("uLights[" + i + "].brightness", lights[i].getBrightness());
                        ShaderCore.setFloat("uLights[" + i + "].contrast",   lights[i].getContrast());
                        ShaderCore.setVec3("uLights[" + i + "].position",    lights[i].getPosition());
                        ShaderCore.setVec3("uLights[" + i + "].ambient",     lights[i].getAmbient());
                        ShaderCore.setVec3("uLights[" + i + "].diffuse",     lights[i].getDiffuse());
                    } else {
                        ShaderCore.setFloat("uLights[" + i + "].brightness", 0);
                        ShaderCore.setFloat("uLights[" + i + "].contrast",   0);
                        ShaderCore.setVec3("uLights[" + i + "].position",    noValue);
                        ShaderCore.setVec3("uLights[" + i + "].ambient",     noValue);
                        ShaderCore.setVec3("uLights[" + i + "].diffuse",     noValue);
                    }
                }
            }
            
            glDrawElements(GL_TRIANGLES, meshes[m].indices.limit(), GL_UNSIGNED_INT, 0);
        }
        
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        
        ErrorUtil.checkGLError();
    }
    
    /**
     * Frees all resources allocated by this model.
     */
    public void destroy() {
        for(Mesh mesh : meshes) mesh.freeBuffers();
        for(Texture texture : textures) texture.freeTexture();
    }
    
}