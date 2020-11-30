package dev.theskidster.xjge.level;

import dev.theskidster.xjge.entities.EntityBuster;
import dev.theskidster.xjge.graphics.Light;
import dev.theskidster.xjge.graphics.Skybox;
import dev.theskidster.xjge.main.App;
import dev.theskidster.xjge.main.Game;
import dev.theskidster.xjge.util.Camera;
import dev.theskidster.xjge.util.Color;
import dev.theskidster.xjge.util.ScreenSplitType;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: Feb 13, 2020
 */

/**
 * Demonstrates the general structure of a game level. Included for testing purposes.
 */
public class LevelTest extends Level {
    
    public static EntityBuster buster = new EntityBuster(new Vector3f(0, -10, -40));
    
    @Override
    public void init() {
        App.setSplitType(ScreenSplitType.NO_SPLIT);
        App.setClearColor(Color.SOFT_BLUE);
        
        setSkybox(Skybox.NOON);
        setWorldLight(Light.NOON);
        
        entityList.add(buster);
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