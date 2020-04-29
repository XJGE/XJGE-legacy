package dev.theskidster.xjge.entities;

import dev.theskidster.xjge.graphics.LightSource;
import dev.theskidster.xjge.graphics.Model;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: Apr 29, 2020
 */

public class Entity3DAnimTest extends Entity {

    private Model model;
    
    public Entity3DAnimTest(Vector3f position) {
        super(position);
        
        model = new Model("mod_test.fbx");
        model.setAnimation("Armature|Wiggle");
    }

    @Override
    public void update() {
        model.delocalizeNormal();
        model.translation(position);
        model.scale(0.15f);
        
        model.updateAnimation();
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