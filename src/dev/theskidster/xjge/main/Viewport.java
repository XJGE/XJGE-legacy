package dev.theskidster.xjge.main;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.joml.Vector2i;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;
import dev.theskidster.xjge.util.Camera;
import dev.theskidster.xjge.graphics.Graphics;
import dev.theskidster.xjge.puppet.freecam.Freecam;
import dev.theskidster.xjge.shader.core.ShaderCore;
import dev.theskidster.xjge.ui.Component;
import dev.theskidster.xjge.util.ErrorUtil;

/**
 * @author J Hoffman
 * Created: Jan 15, 2020
 */

/**
 * Represents a rectangular region through which the perspective of a scene and its rendered objects may be viewed.
 */
class Viewport {
    
    final int id;
    final int texHandle;
    int width;
    int height;
    
    boolean active;
    
    private Graphics g = new Graphics();
    Vector2i botLeft   = new Vector2i();
    Vector2i topRight  = new Vector2i();
    Camera prevCamera  = new Freecam();
    Camera currCamera  = new Freecam();
    
    Map<String, Component> ui = new LinkedHashMap<>();
    
    /**
     * Creates a new viewport object.
     * 
     * @param id the unique number used to identify the viewport in other parts of the engine. Corresponds with 
     *           {@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_1 GLFW_JOYSTICK} values.
     */
    Viewport(int id) {
        this.id = id;
        
        width     = App.getResolution().x;
        height    = App.getResolution().y;
        texHandle = glGenTextures();
        
        createTextureAttachment();
        
        active = (id == 0);
    }
    
    /**
     * Used in {@link App#resetViewports()} to transfer the state of viewport into this new instance.
     * 
     * @param viewport the previous viewport who's state we want to capture
     */
    Viewport(Viewport viewport) {
        id         = viewport.id;
        texHandle  = viewport.texHandle;
        width      = viewport.width;
        height     = viewport.height;
        active     = viewport.active;
        botLeft    = viewport.botLeft;
        topRight   = viewport.topRight;
        g          = viewport.g;
        prevCamera = viewport.prevCamera;
        currCamera = viewport.currCamera;
        ui         = viewport.ui;
    }
    
    /**
     * Creates a new OpenGL texture object to be attached to the Framebuffer.
     */
    private void createTextureAttachment() {
        glBindTexture(GL_TEXTURE_2D, texHandle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glBindTexture(GL_TEXTURE_2D, 0);
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(20);
            g.indices  = stack.mallocInt(6);
            
            //(vec3 position), (vec2 texCoords)
            g.vertices.put(0)    .put(height) .put(0)  .put(1).put(1);
            g.vertices.put(width).put(height) .put(0)  .put(0).put(1);
            g.vertices.put(width).put(0)      .put(0)  .put(0).put(0);
            g.vertices.put(0)    .put(0)      .put(0)  .put(1).put(0);
            
            g.indices.put(0).put(1).put(2);
            g.indices.put(3).put(2).put(0);
            
            g.vertices.flip();
            g.indices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (5 * Float.BYTES), 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, (5 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
    }
    
    /**
     * Renders a scene from the perspective of this viewport. Viewport rendering is done in three phases:
     * 
     * <ol>
     * <li>The perspective of the camera object used by this viewport is rendered.</li>
     * <li>The viewports UI components will be drawn in order of their z-positions.</li> 
     * <li>The texture attachment associated with this viewport by the Framebuffer will be updated to reflect the changes made by the previous two steps.</li>
     * </ol>
     * 
     * @param phase the rendering phase to complete. One of "camera", "ui", or "texture".
     * @see App#renderViewports(Level, Matrix4f) 
     */
    void render(String phase) {
        ShaderCore.use("default");
        
        switch(phase) {
            case "camera" -> currCamera.render();
                
            case "ui" -> {
                currCamera.setType("ortho", width, height);
                ui.forEach((name, component) -> component.render());
                resetCamera();
            }
                
            case "texture" -> {
                glBindTexture(GL_TEXTURE_2D, texHandle);
                glBindVertexArray(g.vao);
                
                ShaderCore.setInt("uType", 0);
                
                glDrawElements(GL_TRIANGLES, g.indices.limit(), GL_UNSIGNED_INT, 0);
                ErrorUtil.checkGLError();
            }
        }
    }
    
    /**
     * Convenience method used to revert the viewports camera projection matrix back to whatever type (orthogonal or perspective) it was using before.
     */
    void resetCamera() {
        currCamera.setType(currCamera.type, width, height);
    }
    
    /**
     * Sets the resolution and position of the viewport.
     * 
     * @param width  the width of the viewport in pixels
     * @param height the height of the viewport in pixels
     * @param x1     the x coordinate of the viewports bottom left corner
     * @param y1     the y coordinate of the viewports bottom left corner
     * @param x2     the x coordinate of the viewports top right corner
     * @param y2     the y coordinate of the viewports top right corner
     * @see App#setSplitType(ScreenSplitType) 
     */
    void setBounds(int width, int height, int x1, int y1, int x2, int y2) {
        this.width  = width;
        this.height = height;
        botLeft.set(x1, y1);
        topRight.set(x2, y2);
        
        createTextureAttachment();
        
        ui.forEach((name, component) -> component.setSplitPosition());
    }
    
    /**
     * Adds a new UI component to this viewport. UI components will be rendered in the order of their z-positions with lower numbers denoting a higher priority. 
     * For example, a component with a z-position of 0 will be rendered in front of a component with a z-position of 1. Accessed exclusively through 
     * {@link App#addUIComponent(int, String, Component)}.
     * 
     * @param name      the name that will be used to identify and remove the component later
     * @param component the component object to use
     * @see dev.theskidster.xjge.ui
     */
    void addUIComponent(String name, Component component) {
        ui.put(name, component);
        
        List<Map.Entry<String, Component>> compList = new LinkedList<>(ui.entrySet());
        
        Collections.sort(compList, (var o1, var o2) -> {
            return (o2.getValue()).compareTo(o1.getValue());
        });
        
        var temp = new LinkedHashMap<String, Component>();
        compList.forEach(comp2 -> temp.put(comp2.getKey(), comp2.getValue()));
        
        ui.clear();
        ui.putAll(temp);
    }
    
    /**
     * Removes a UI component from this viewport. Accessed exclusively through {@link App#removeUIComponent(int, String)}.
     * 
     * @param name 
     */
    void removeUIComponent(String name) {
        ui.remove(name);
    }
    
}