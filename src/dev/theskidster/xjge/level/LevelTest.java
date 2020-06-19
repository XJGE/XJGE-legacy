package dev.theskidster.xjge.level;

import dev.theskidster.xjge.entities.Entity3DAnimTest;
import dev.theskidster.xjge.graphics.Light;
import dev.theskidster.xjge.graphics.Skybox;
import org.joml.Vector3f;
import dev.theskidster.xjge.main.App;
import dev.theskidster.xjge.util.Camera;
import dev.theskidster.xjge.util.Color;
import dev.theskidster.xjge.util.ScreenSplitType;

/**
 * @author J Hoffman
 * Created: Feb 13, 2020
 */

/**
 * Demonstrates the general structure of a game level. Intended for testing purposes only.
 */
public class LevelTest extends Level {
    
    @Override
    public void init() {
        App.setSplitType(ScreenSplitType.NO_SPLIT);
        App.setClearColor(Color.SOFT_BLUE);

        setSkybox(Skybox.NOON);
        setWorldLight(Light.NOON);
        
        //entityList.add(new EntityTest(new Vector3f(0, 10, -80)));
        //entityList.add(new EntityTeapot(new Vector3f(-40, 0, -60)));
        entityList.add(new Entity3DAnimTest(new Vector3f(0, 0, -40)));
    }

    @Override
    public void update() {
        entityList.forEach(e -> e.update());
        updateLightSources();
        
        resolveRemoveRequest();
        
        //ServiceLocator.getAudio().checkIntroFinished();
    }

    @Override
    public void render(Camera camera) {
        entityList.forEach(e -> e.render(camera, getLightSources(), getNumLights()));
    }

    @Override
    public void exit() {
        freeEntities();
        freeLightSources();
    }
    
}