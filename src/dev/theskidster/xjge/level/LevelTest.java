package dev.theskidster.xjge.level;

import dev.theskidster.xjge.entities.Entity2DAnimTest;
import dev.theskidster.xjge.entities.Entity3DAnimTest;
import dev.theskidster.xjge.entities.EntityTeapot;
import dev.theskidster.xjge.entities.EntityTest;
import org.joml.Vector3f;
import dev.theskidster.xjge.main.App;
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
        
        entityList.add(new EntityTest(new Vector3f(0, 10, -150)));
        entityList.add(new EntityTeapot(new Vector3f(40, -10, -100)));
        entityList.add(new Entity3DAnimTest(new Vector3f(-40, -10, -100)));
        entityList.add(new Entity2DAnimTest(new Vector3f(0, -30, -100)));
    }

    @Override
    public void update() {
        entityList.forEach(e -> e.update());
        updateLightSources();
        
        resolveRemoveRequest();
        
        //ServiceLocator.getAudio().checkIntroFinished();
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