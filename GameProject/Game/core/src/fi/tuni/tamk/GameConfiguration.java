package fi.tuni.tamk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public final class GameConfiguration {

    public static int gameLevel;

    private static final int SCORE = 100;

    private static final int OBJECT_SCORE = 20;

    public static int lvl1Size = 10;
    public static int lvl1PathLength = 15;
    public static int lvl1ObjectPairs = 2;

    public static int lvl2Size = 15;
    public static int lvl2PathLength = 23;
    public static int lvl2ObjectPairs = 4;

    public static int lvl3Size = 20;
    public static int lvl3PathLength = 31;
    public static int lvl3ObjectPairs = 6;

    public static final float PLAYER_SPEED = 4;

    public static final float RELATIVE_TILE_HEIGHT = 2 / 3f;
    public static final float WALL_HEIGHT = 2.1f;

    public static final float OBJECT_HEIGHT = 2;

    public static float joystickXOffset = Gdx.graphics.getWidth() / 32f;
    public static float joystickX = Gdx.graphics.getWidth() * 3 / 4f + joystickXOffset;
    public static float joystickY = Gdx.graphics.getWidth() / 32f;
    public static float joystickLength = Gdx.graphics.getWidth() * 3 / 16f;
    public static float joystickCenterX = joystickX + joystickLength / 2f;
    public static float joystickRightX = joystickX + joystickLength;

    private GameConfiguration() {}

    public static int getStartScore() {
        return SCORE * gameLevel;
    }

    public static int getObjectScore() {
        return OBJECT_SCORE * gameLevel;
    }

    public static GameScreen createGame(Main main) {
        World world = new World(new Vector2(0,0), true);

        GameScreen gameScreen= new GameScreen(main, world);
        MapGenerator generator= new MapGenerator(gameScreen);
        int size = lvl1Size;
        int pathLength = lvl1PathLength;
        int objectPairs = lvl1ObjectPairs;
        if(gameLevel == 2) {
            size = lvl2Size;
            pathLength = lvl2PathLength;
            objectPairs = lvl2ObjectPairs;
        }
        else if(gameLevel == 3) {
            size = lvl3Size;
            pathLength = lvl3PathLength;
            objectPairs = lvl3ObjectPairs;
        }
        generator.createMap(size, pathLength, world, objectPairs);
        return gameScreen;
    }
}
