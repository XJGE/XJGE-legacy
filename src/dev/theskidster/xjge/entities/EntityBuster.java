package dev.theskidster.xjge.entities;

import dev.theskidster.xjge.graphics.LightSource;
import dev.theskidster.xjge.graphics.Model;
import dev.theskidster.xjge.util.Camera;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: Jul 26, 2020
 */

public class EntityBuster extends Entity {

    public final Model model;
    
    public EntityBuster(Vector3f position) {
        super(position);
        
        model = new Model("mod_buster.fbx");
        model.setAnimation("TPose", 0);
    }

    @Override
    public void update() {
        model.delocalizeNormal();
        model.translation(position);
        model.scale(0.04f);
        
        model.updateAnimation();
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