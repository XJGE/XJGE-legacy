package dev.theskidster.xjge.entities;

import dev.theskidster.xjge.graphics.LightSource;
import dev.theskidster.xjge.graphics.Model;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: Apr 27, 2020
 */

public class EntityTeapot extends Entity {

    private Model model;
    
    public EntityTeapot(Vector3f position) {
        super(position);
        
        model = new Model("mod_teapot.fbx");
    }

    @Override
    public void update() {
        model.delocalizeNormal();
        model.translation(position);
        model.rotateY(90);
        model.rotateZ(-135);
    }

    @Override
    public void render(Vector3f camPos, Vector3f camDir, Vector3f camUp, LightSource[] lights, int numLights) {
        model.render("default", lights, numLights);
    }

    @Override
    protected void destroy() {
        model.destroy();
    }
    
}