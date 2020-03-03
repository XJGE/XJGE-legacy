package dev.theskidster.xjge.level;

import org.joml.Vector3f;
import dev.theskidster.xjge.entities.EntityTest;
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
        
        entityList.add(new EntityTest(new Vector3f(0, 0, -50)));
    }

    @Override
    public void update() {
        entityList.forEach(e -> e.update());
        resolveRemoveRequest();
        
        //ServiceLocator.getAudio().checkIntroFinished();
    }

    @Override
    public void render() {
        entityList.forEach(e -> e.render());
    }

    @Override
    public void exit() {}
    
}