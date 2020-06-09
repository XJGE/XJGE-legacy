package dev.theskidster.xjge.entities;

import dev.theskidster.xjge.graphics.LightSource;
import dev.theskidster.xjge.graphics.Model;
import dev.theskidster.xjge.util.Camera;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: Apr 27, 2020
 */

public class EntityTeapot extends Entity {

    private Model model;
    private float angle;
    
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
    public void render(Camera camera, LightSource[] lights, int numLights) {
        model.render("default", lights, numLights);
    }

    @Override
    protected void destroy() {
        model.destroy();
    }
    
}