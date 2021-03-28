package fi.tuni.tamk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;

public final class GameConfiguration {

    // Current game level
    public static int gameLevel;

    // Score settings
    private static final int SCORE = 100;
    private static final int OBJECT_SCORE = 20;

    // Easy level settings
    public static int lvl1Size = 10;
    public static int lvl1PathLength = 15;
    public static int lvl1ObjectPairs = 2;

    // Normal level settings
    public static int lvl2Size = 15;
    public static int lvl2PathLength = 23;
    public static int lvl2ObjectPairs = 4;

    // Hard level settings
    public static int lvl3Size = 20;
    public static int lvl3PathLength = 31;
    public static int lvl3ObjectPairs = 6;

    public static boolean tutorialOn = false;

    public static final float PLAYER_SPEED = 200;

    // Wall height settings
    public static final float RELATIVE_TILE_HEIGHT = 2 / 3f;
    public static final float WALL_HEIGHT = 2.1f;

    // Object height settings
    public static final float OBJECT_HEIGHT = 2;

    // Joystick settings
    public static float joystickXOffset = Gdx.graphics.getWidth() / 32f;
    public static float joystickX = Gdx.graphics.getWidth() * 3 / 4f + joystickXOffset;
    public static float joystickY = Gdx.graphics.getWidth() / 32f;
    public static float joystickLength = Gdx.graphics.getWidth() * 3 / 16f;

    // Saving stuff
    public static String noValue = "No value stored";
    public static String creditsString = "credits";

    // Personal info
    public static int credits;

    private GameConfiguration() {}

    public static int getStartScore() {
        return SCORE * gameLevel;
    }

    public static int getObjectScore() {
        return OBJECT_SCORE * gameLevel;
    }

    /**
     * Creates the game based on the selected game level.
     *
     * @param main Main object to pass along.
     * @return Game screen.
     */
    public static GameScreen createGame(Main main) {
        World world = new World(new Vector2(0,0), true);

        GameScreen gameScreen= new GameScreen(main, world);
        MapGenerator generator= new MapGenerator(gameScreen);
        if(tutorialOn) {
            generator.createTutorialMap(world);
            tutorialOn = false;
        } else {
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
        }
        return gameScreen;
    }

    public static String getText(String key) {
        I18NBundle myBundle;
        if(!open("language").equals(noValue)) {
            if(open("language").equals("fi_FI")) {
                Locale locale = new Locale("fi_FI");
                myBundle = I18NBundle.createBundle(Gdx.files.internal("MyBundle_fi_FI"), locale);
            } else {
                myBundle = I18NBundle.createBundle(Gdx.files.internal("MyBundle"));
            }
        } else {
            Locale locale = Locale.getDefault();
            myBundle = I18NBundle.createBundle(Gdx.files.internal("MyBundle"), locale);
        }
        return myBundle.get(key);
    }

    public static void save(String key, String value) {
        Preferences prefs = Gdx.app.getPreferences("MyPreferences");

        prefs.putString(key, value);
        prefs.flush();
    }

    public static String open(String key) {
        Preferences prefs = Gdx.app.getPreferences("MyPreferences");

        String value = prefs.getString(key, noValue);

        return value;
    }

    public static void checkFirstTime() {
        if(open(creditsString).equals(noValue)) {
            save(creditsString, "0");
        }
        credits = Integer.parseInt(open(creditsString));
    }
}
