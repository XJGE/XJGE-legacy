package dev.theskidster.xjge.graphics;

import dev.theskidster.xjge.main.App;
import dev.theskidster.xjge.shader.core.ShaderCore;
import dev.theskidster.xjge.util.Camera;
import dev.theskidster.xjge.util.Color;
import dev.theskidster.xjge.util.ErrorUtil;
import dev.theskidster.xjge.main.Logger;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
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
 * engine to ensure models are loaded with consistency. The Autodesk&trade; .fbx file format is the preferred format of this engine for its compact size, though 
 * other formats should work as well.
 */
public class Model {
    
    private int prevNumKeyFrames;
    
    private float speed = 1.5f;
    
    private boolean loop = true;
    
    private AIScene aiScene;
    private Vector3f color   = new Vector3f(1);
    private Vector3f noValue = new Vector3f();
    private Matrix3f normal  = new Matrix3f();
    private Matrix4f rootTransform;
    
    private Node rootNode;
    private SkeletalAnimation currAnimation;
    
    private Mesh[] meshes;
    private Texture[] textures;
    private Matrix4f[] tempTransforms = new Matrix4f[2];
    
    private List<Bone> bones = new ArrayList<>();
    
    private Map<String, SkeletalAnimation> animations;
    
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
                                case Assimp.aiOrigin_CUR -> modelBuf.position(modelBuf.position() + (int) offset);
                                case Assimp.aiOrigin_SET -> modelBuf.position((int) offset);
                                case Assimp.aiOrigin_END -> modelBuf.position(modelBuf.limit() + (int) offset);
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
            
            if(aiScene == null) {
                MemoryUtil.memFree(modelBuf);
                throw new IllegalStateException(aiGetErrorString());
            } else {
                MemoryUtil.memFree(modelBuf);
                
                AINode aiRoot = aiScene.mRootNode();
                rootTransform = Graphics.convertFromAssimp(aiRoot.mTransformation());
                rootNode      = parseFileHierarchy(aiRoot, null);
                
                parseMeshData(aiScene.mMeshes());
                parseTextureData(aiScene.mMaterials());
                parseAnimationData(aiScene.mAnimations());
            }
        } catch(Exception e) {
            Logger.logWarning("Failed to load model: \"" + filename + "\"", e);
        }
    }
    
    /**
     * Translates the structure of the model file into a hierarchy that can be used by the engine.
     * 
     * @param aiNode the Assimp data structure from which a new node object will be constructed
     * @param parent the parent of the unprocessed Assimp node or null if this is the root node
     * @return a new child node of the parent provided 
     */
    private Node parseFileHierarchy(AINode aiNode, Node parent) {
        String nodeName = aiNode.mName().dataString();
        Node node       = new Node(nodeName, parent);
        
        PointerBuffer childBuf = aiNode.mChildren();
        
        for(int i = 0; i < aiNode.mNumChildren(); i++) {
            AINode aiChild = AINode.create(childBuf.get(i));
            Node childNode = parseFileHierarchy(aiChild, node);
            
            node.children.add(childNode);
        }
        
        return node;
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
            meshes[i]     = new Mesh(aiMesh, bones);
        }
    }
    
    /**
     * Parses each texture that will be used by this model.
     * <br><br>
     * The engine imposes a number of significant restrictions regarding textures that should be considered during the model creation process. Specifically;
     * <ol>
     * <li>Models may not exceed the maximum number of allowed textures specified through the {@link App#MAX_TEXTURES MAX_TEXTURES} field. By default this 
     * number is four, but may be altered at the discretion of the implementation as needed.</li>
     * <br>
     * <li>Any {@link Mesh} object representing part of this model can not use more than one texture concurrently. That is, a single texture may be shared 
     * between multiple meshes, but a single mesh may not exhibit multiple textures.</li>
     * <br>
     * <li>Since models are loaded directly from memory, they must embed their textures inside of materials in order to load correctly.</li>
     * <br>
     * <li>Texture image files used by models must be located in the same directory as the model file itself.</li>
     * </ol>
     * 
     * @param materialBuf the buffer of models material data provided by Assimp
     * @throws Exception  if one or more textures could not be located. The engine will instead use a placeholder texture.
     */
    private void parseTextureData(PointerBuffer materialBuf) throws Exception {
        if(aiScene.mNumMaterials() > App.MAX_TEXTURES) {
            textures = new Texture[App.MAX_TEXTURES];
            Logger.logWarning(
                    "Invalid number of textures. Limit of " + App.MAX_TEXTURES + 
                    " permitted, found " + aiScene.mNumMaterials(), 
                    null);
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
            
            filename.free();
            
            glBindTexture(GL_TEXTURE_2D, textures[i].handle);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glBindTexture(GL_TEXTURE_2D, 0);
        }
    }
    
    /**
     * Parses data required by this model during skeletal animation.
     * 
     * @param animationBuf the buffer of the models animation data as provided by Assimp
     */
    private void parseAnimationData(PointerBuffer animationBuf) {
        animations = new HashMap<>();
        
        for(int i = 0; i < aiScene.mNumAnimations(); i++) {
            AIAnimation aiAnimation  = AIAnimation.create(animationBuf.get(i));
            PointerBuffer channelBuf = aiAnimation.mChannels();
            
            for(int c = 0; c < aiAnimation.mNumChannels(); c++) {
                AINodeAnim aiNodeAnim = AINodeAnim.create(channelBuf.get(c));
                String nodeName       = aiNodeAnim.mNodeName().dataString();
                Node node             = rootNode.getNodeByName(nodeName);
                
                genTransforms(aiNodeAnim, node);
            }
            
            List<KeyFrame> keyFrames = genKeyFrames();
            prevNumKeyFrames         += keyFrames.size();
            
            SkeletalAnimation animation = new SkeletalAnimation(aiAnimation, keyFrames);
            animations.put(animation.name, animation);
        }
    }
    
    /**
     * Generates the final transforms of each {@link Node} that will be used to move the bones of the model during a {@link SkeletalAnimation}.
     * 
     * @param aiNodeAnim the Assimp structure that will be parsed to calculate a nodes transformations
     * @param node       the node that will contain the calculated transformations
     */
    private void genTransforms(AINodeAnim aiNodeAnim, Node node) {
        AIVectorKey.Buffer aiPosKeyBuf   = aiNodeAnim.mPositionKeys();
        AIVectorKey.Buffer aiScaleKeyBuf = aiNodeAnim.mScalingKeys();
        AIQuatKey.Buffer aiRotKeyBuf     = aiNodeAnim.mRotationKeys();
        
        for(int i = 0; i < aiNodeAnim.mNumPositionKeys(); i++) {
            AIVectorKey aiVecKey = aiPosKeyBuf.get(i);
            AIVector3D aiVec     = aiVecKey.mValue();
            
            Matrix4f transform = new Matrix4f().translate(aiVec.x(), aiVec.y(), aiVec.z());
            
            AIQuatKey aiQuatKey    = aiRotKeyBuf.get(i);
            AIQuaternion aiQuat    = aiQuatKey.mValue();
            Quaternionf quaternion = new Quaternionf(aiQuat.x(), aiQuat.y(), aiQuat.z(), aiQuat.w());
            
            transform.rotate(quaternion);
            
            if(i < aiNodeAnim.mNumScalingKeys()) {
                aiVecKey = aiScaleKeyBuf.get(i);
                aiVec    = aiVecKey.mValue();
                
                transform.scale(aiVec.x(), aiVec.y(), aiVec.z());
            }
            
            node.transforms.add(transform);
        }
    }
    
    /**
     * generates every {@link KeyFrame} of a {@link SkeletalAnimation}.
     * 
     * @return the list of keyframes used by the animation.
     */
    private List<KeyFrame> genKeyFrames() {
        List<KeyFrame> frames = new ArrayList<>();
        
        /*
        We provide the prevNumKeyFrames field here to offset the starting frame
        of each animation since the keyframes specified by the model file are
        stored sequentially regardless of animation.
        */
        
        for(int i = prevNumKeyFrames; i < rootNode.getNumKeyFrames(); i++) {
            KeyFrame frame = new KeyFrame();
            frames.add(frame);
            
            for(int b = 0; b < bones.size(); b++) {
                Bone bone = bones.get(b);
                Node node = rootNode.getNodeByName(bone.name);

                Matrix4f boneTransform = Node.getParentTransform(node, i);

                boneTransform.mul(bone.offset);
                boneTransform = new Matrix4f(rootTransform).mul(boneTransform);

                frame.setTransform(b, boneTransform);
            }
        }
        
        return frames;
    }
    
    /**
     * Renders the 3D model. Should be called from within the implementing entities 
     * {@link dev.theskidster.xjge.entities.Entity#render(Camera, LightSource[], int) render()} method.
     * 
     * @param shader    the shader to use to render this model
     * @param lights    an array of light source objects inhabiting the current level
     * @param numLights the total number of lights in the level
     */
    public void render(String shader, LightSource[] lights, int numLights) {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        
        ShaderCore.use(shader);
        
        for(Mesh mesh : meshes) {
            glBindTexture(GL_TEXTURE_2D, textures[mesh.matIndex].handle);
            
            glBindVertexArray(mesh.vao);
            
            ShaderCore.setInt("uType", 5);
            ShaderCore.setMat4("uModel", false, mesh.modelMatrix);
            ShaderCore.setMat3("uNormal", true, normal);
            ShaderCore.setInt("uNumLights", numLights);
            ShaderCore.setVec3("uColor", color);
            
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
            
            if(currAnimation != null) {
                ShaderCore.setMat4("uBoneTransforms", false, currAnimation.getCurrFrame().getTransformData());
            }
            
            glDrawElements(GL_TRIANGLES, mesh.indices.limit(), GL_UNSIGNED_INT, 0);
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
    
    /**
     * Outputs a list of every animation this model has at its disposal to the console.
     */
    public void listAnimations() {
        animations.forEach((name, anim) -> Logger.logInfo(name));
    }
    
    /**
     * Sets the current that will be played by this model. A small transition animation will be generated if the value passed to the numFrames parameter
     * is greater than zero.
     * 
     * @param name      the name of the animation as it appears in the model file
     * @param numFrames the number of frames to transition between the current animation and the new one
     */
    public void setAnimation(String name, int numFrames) {
        if(!animations.containsKey(name)) {
            Logger.logWarning(
                    "Failed to set animation: \"" + name + "\". Model contains " + 
                    "no such animation.",
                    null);
            return;
        }
        
        if(currAnimation != null && numFrames > 1) {
            if(currAnimation.name.equals(name)) {
                Logger.logInfo("Animation: \"" + name +"\" is already playing.");
                return;
            }
            
            var frames = new ArrayList<KeyFrame>();
            
            for(int f = 1; f <= numFrames; f++) {
                KeyFrame frame = new KeyFrame();
                
                for(int b = 0; b < App.MAX_BONES; b++) {
                    animations.get(name).setFrameTime(currAnimation.getFrameTime());
                    animations.get(name).setSeekTime(currAnimation.getSeekTime());
                    
                    Matrix4f transforms = new Matrix4f();
                    
                    currAnimation.calcTransition(b).lerp(animations.get(name).calcTransition(b), f / ((float) numFrames), transforms);
                    
                    frame.setTransform(b, transforms);
                }
                
                frames.add(frame);
            }
            
            currAnimation = new SkeletalAnimation(currAnimation.name, name, frames);
        } else {
            currAnimation = animations.get(name);
        }
    }
    
    /**
     * Determines whether or not to loop this models current animation once its duration has been reached. Subsequent animations will inherit the value 
     * specified.
     * 
     * @param loop if true, animations will loop indefinitely. Supplying false will cease animation playback after their durations are reached.
     */
    public void setAnimationLooping(boolean loop) {
        this.loop = loop;
    }
    
    /**
     * Sets the playback speed of this models current animation. Subsequent animations will inherit the value specified.
     * 
     * @param speed a non-negative number between 1 and 0. A value of zero will pause the animation at its current {@link KeyFrame}.
     */
    public void setAnimationSpeed(float speed) {
        if(speed > 1)      speed = 1;
        else if(speed < 0) speed = 0;
        
        this.speed = speed * App.MAX_ANIM_SPEED;
    }
    
    /**
     * Sets the color of this model.
     * <br><br>
     * More specifically, values passed here will attenuate the final output of the fragment shader. As such, lighter colors will not "brighten" the surface
     * of the model like one might expect, but instead exhibit less influence entirely- passing {@link dev.theskidster.xjge.util.Color#WHITE Color.WHITE} for
     * example, will have no noticeable effect on the color of the model.
     * 
     * @param color the color this model will use
     */
    public void setColor(Color color) {
        this.color.set(color.r, color.g, color.b);
    }
    
    /**
     * Updates the current skeletal animation.
     */
    public void updateAnimation() {
        if(currAnimation.transition && currAnimation.getFinished()) {
            currAnimation = animations.get(currAnimation.nextAnim);
        }
        
        currAnimation.genCurrFrame(speed, loop);
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
    public void scale(float factor) {
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
    
}