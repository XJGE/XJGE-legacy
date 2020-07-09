package dev.theskidster.xjge.level;

import dev.theskidster.xjge.entities.EntityBody;
import dev.theskidster.xjge.graphics.Light;
import dev.theskidster.xjge.graphics.Skybox;
import dev.theskidster.xjge.main.App;
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
    
    public static EntityBody mario      = new EntityBody(new Vector3f(0, -10, -40));
    //public static Entity3DAnimTest test = new Entity3DAnimTest(new Vector3f(50, 0, -40));
    
    @Override
    public void init() {
        App.setSplitType(ScreenSplitType.NO_SPLIT);
        App.setClearColor(Color.SOFT_BLUE);

        setSkybox(Skybox.NOON);
        setWorldLight(Light.NOON);
        
        //entityList.add(new EntityTest(new Vector3f(0, 10, -80)));
        //entityList.add(new EntityTeapot(new Vector3f(-40, 0, -60)));
        //entityList.add(test);
        //entityList.add(new EntityCube(new Vector3f()));
        entityList.add(mario);
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
    
    public static void setAnimation(String name) {
        //test.model.setAnimation(name);
        mario.model.setAnimation(name);
    }
    
}