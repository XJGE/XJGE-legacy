package dev.theskidster.xjge.entities;

import dev.theskidster.xjge.graphics.LightSource;
import dev.theskidster.xjge.graphics.Model;
import dev.theskidster.xjge.util.Camera;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: Jun 23, 2020
 */

public class EntityBody extends Entity {

    //@todo this is a temp class! will likely be replaced by more robust solution
    
    private float angle = 0;
    public Model model;
    
    public EntityBody(Vector3f position) {
        super(position);
        
        model = new Model("mod_mario.fbx");
        model.setAnimation("Armature|TPose");
    }

    @Override
    public void update() {
        model.delocalizeNormal();
        model.translation(position);
        model.scale(0.01f);
        
        model.updateAnimation();
        
        //model.rotateY(angle++);
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