package dev.theskidster.xjge.entities;

import dev.theskidster.xjge.graphics.Cell;
import dev.theskidster.xjge.graphics.Graphics;
import dev.theskidster.xjge.graphics.LightSource;
import dev.theskidster.xjge.graphics.SpriteAnimation;
import dev.theskidster.xjge.graphics.SpriteSheet;
import dev.theskidster.xjge.graphics.Texture;
import dev.theskidster.xjge.shader.core.ShaderCore;
import dev.theskidster.xjge.util.Camera;
import dev.theskidster.xjge.main.ErrorUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector2i;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Apr 29, 2020
 */

public class EntitySprite extends Entity {

    private Graphics g;
    private Texture texture;
    private SpriteSheet sprite;
    
    private Map<String, SpriteAnimation> animations = new HashMap<>();
    
    public EntitySprite(Vector3f position) {
        super(position);
        
        Cell cell = new Cell(20, 20);
        
        g       = new Graphics();
        texture = new Texture("spr_engineicons.png");
        sprite  = new SpriteSheet(texture, cell);
        
        glBindTexture(GL_TEXTURE_2D, texture.handle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glBindTexture(GL_TEXTURE_2D, 0);
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(20);
            g.indices  = stack.mallocInt(6);
            
            //(vec3 position), (vec2 tex coords)
            g.vertices.put(0)         .put(cell.height).put(0)  .put(0)              .put(0);
            g.vertices.put(cell.width).put(cell.height).put(0)  .put(sprite.imgWidth).put(0);
            g.vertices.put(cell.width).put(0)          .put(0)  .put(sprite.imgWidth).put(sprite.imgHeight);
            g.vertices.put(0)         .put(0)          .put(0)  .put(0)              .put(sprite.imgHeight);
            
            g.indices.put(0).put(1).put(2);
            g.indices.put(2).put(3).put(0);
            
            g.vertices.flip();
            g.indices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (5 * Float.BYTES), 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, (5 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        
        var frames = new ArrayList<Vector2i>();
            frames.add(new Vector2i(0, 0));
            frames.add(new Vector2i(1, 0));
            frames.add(new Vector2i(2, 0));
            frames.add(new Vector2i(1, 0));
            
        animations.put("load", new SpriteAnimation(frames, 20));
    }

    @Override
    public void update() {
        g.modelMatrix.translation(position);
        
        animations.get("load").updateAnimation(sprite);
    }

    @Override
    public void render(Camera camera, LightSource[] lights, int numLights) {
        glEnable(GL_DEPTH_TEST);
        
        ShaderCore.use("default");
        
        glBindTexture(GL_TEXTURE_2D, texture.handle);
        glBindVertexArray(g.vao);
        
        ShaderCore.setInt("uType", 7);
        ShaderCore.setMat4("uModel", false, g.modelMatrix);
        ShaderCore.setVec2("uTexCoords", sprite.texCoords);
                
        glDrawElements(GL_TRIANGLES, g.indices.limit(), GL_UNSIGNED_INT, 0);
        glDisable(GL_DEPTH_TEST);
        
        ErrorUtil.checkGLError();
    }

    @Override
    protected void destroy() {
        g.freeBuffers();
        texture.freeTexture();
    }
    
}