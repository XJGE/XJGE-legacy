package dev.theskidster.xjge.level;

import dev.theskidster.xjge.entities.EntityTeapot;
import dev.theskidster.xjge.entities.EntityTest;
import dev.theskidster.xjge.graphics.Light;
import dev.theskidster.xjge.main.App;
import dev.theskidster.xjge.util.Color;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: Apr 27, 2020
 */

public class LevelTest2 extends Level {

    @Override
    public void init() {
        App.setClearColor(Color.ORANGE);
        entityList.add(new EntityTeapot(new Vector3f()));
        entityList.add(new EntityTeapot(new Vector3f(20)));
        entityList.add(new EntityTest(new Vector3f(-30, 0, -50)));
        
        setWorldLight(Light.BLOOD);
    }

    @Override
    public void update() {
        entityList.forEach(e -> e.update());
        updateLightSources();
        
        resolveRemoveRequest();
    }

    @Override
    public void render(Vector3f camPos, Vector3f camDir, Vector3f camUp) {
        entityList.forEach(e -> e.render(camPos, camDir, camUp, getLightSources(), getNumLights()));
        renderLightSources(camPos, camDir, camUp);
    }

    @Override
    public void exit() {
        freeEntities();
        freeLightSources();
    }
    
}