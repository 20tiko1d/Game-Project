package fi.tuni.tamk.LabyrinthOfLife;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Static class which contains Game settings and several tools.
 *
 * @author Artur Haavisto
 */
public final class GameConfiguration implements HighScoreListener {

    // Current game level
    public static int gameLevel;
    public static String theme;

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

    public static final float PLAYER_SPEED = 100;

    // Wall height settings
    public static final float RELATIVE_TILE_HEIGHT = 2 / 3f;
    public static final float WALL_HEIGHT = 2.1f;

    // Object height settings
    public static final float OBJECT_HEIGHT = 3;

    // Joystick settings
    public static float joystickLength = Gdx.graphics.getWidth() * 3 / 16f;
    public static float joystickY = Gdx.graphics.getHeight() / 4f - joystickLength / 2;


    // Saving stuff
    public static String noValue = "No value stored";

    public static boolean firstTime;

    // High Score
    public static ArrayList<HighScoreEntry> highScores;

    private GameConfiguration() {}

    /**
     * Gets starting score.
     *
     * @return Start score.
     */
    public static int getStartScore() {
        return SCORE * gameLevel;
    }

    /**
     * Gets object collecting score.
     *
     * @return Object collecting score.
     */
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
        MapGenerator generator= new MapGenerator(gameScreen, main);
        if(tutorialOn) {
            generator.createTutorialMap(world);
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

    /**
     * Gets a certain text depending on given key and used language.
     *
     * @param key: Name of the text key.
     * @return Text string.
     */
    public static String getText(String key) {
        Locale locale;
        if(open("language").equals("fi_FI")) {
            locale = new Locale("fi", "FI");
        }
        else if(open("language").equals("en_UK")) {
            locale = new Locale("en", "UK");
        } else {
            locale = Locale.getDefault();
        }
        I18NBundle myBundle = I18NBundle.createBundle(Gdx.files.internal("MyBundle"), locale);
        return myBundle.get(key);
    }

    /**
     * Saves data to storage.
     *
     * @param key: Name of the key.
     * @param value: Saved value.
     */
    public static void save(String key, String value) {
        Preferences prefs = Gdx.app.getPreferences("MyPreferences");
        prefs.putString(key, value);
        prefs.flush();
    }

    /**
     * Reads data from the storage.
     *
     * @param key: Name of the key.
     * @return Read data as a string.
     */
    public static String open(String key) {
        Preferences prefs = Gdx.app.getPreferences("MyPreferences");
        return prefs.getString(key, noValue);
    }

    /**
     * Checks if this is the first time player uses the app.
     */
    public static void checkFirstTime() {
        if(open("firstTime").equals(noValue)) {
            save("theme", "sand");
            save("player", "ghost");
            save("controls", "default");
            save("musicVolume", "5");
            firstTime = true;
        } else {
            firstTime = false;
        }
        theme = open("theme");
    }

    /**
     * Gets the language used.
     *
     * @return Language as a string.
     */
    public static String getLanguage() {
        String language;
        if(open("language").equals(noValue)) {
            if(new Locale("fi", "FI").equals(Locale.getDefault())) {
                language = "fi_FI";
            } else {
                language = "en_UK";
            }
        } else {
            language = open("language");
        }
        return language;
    }

    /**
     * Fits text to a button with given arguments.
     *
     * @param button: TextButton.
     * @param givenFontSize: Given font size.
     * @param givenMaxSize: Maximum font size.
     * @return Font scaling value.
     */
    public static float fitText(TextButton button, int givenFontSize, int givenMaxSize) {
        float width = button.getWidth() * 0.8f;
        int length = button.getText().length();
        float fontSize = givenFontSize;
        if(givenFontSize < 0) {
            String styleName = button.getStyle().font.getData().name;
            String fontSizeString = "";
            for(int i = styleName.length() - 3; i < styleName.length() - 1; i++) {
                String str = "";
                try {
                    str = str + Integer.parseInt(styleName.substring(i));
                } catch (Exception e) { }
                finally {
                    fontSizeString = fontSizeString + str;
                }
            }
            fontSize = Integer.parseInt(fontSizeString);
        }
        fontSize = fontSize * 0.7f;
        int maxSize = givenMaxSize;
        if(maxSize < 0) {
            maxSize = 48;
        }
        if(width >= maxSize * length) {
            return (float) maxSize / fontSize;
        } else {
            return width / length / fontSize;
        }
    }

    @Override
    public void receiveHighScore(ArrayList<HighScoreEntry> getHighScores) {
        highScores = getHighScores;
    }

    @Override
    public void failedToRetrieveHighScores(String s) {}

    @Override
    public void receiveSendReply(int httpResponse) {}

    @Override
    public void failedToSendHighScore(String s) {}

    /**
     * Gets the high scores depending on the given difficulty level.
     *
     * @param mapId: Difficulty id (1-3).
     */
    public static void getHighScores(int mapId) {
        HighScoreServer.readConfig("highscore.config");
        HighScoreServer.fetchHighScores(new GameConfiguration(), mapId);
    }

    /**
     * Sends high scores to the server.
     *
     * @param score: Score to send.
     */
    public static void sendHighScores(int score) {
        HighScoreEntry entry = new HighScoreEntry(open("name"), score, 1, gameLevel);
        HighScoreServer.readConfig("highscore.config");
        HighScoreServer.sendNewHighScore(entry, new GameConfiguration());
    }

    /**
     * Gets x location for the control component (boost-button or joystick).
     *
     * @param indicator: 1 if boost, 2 if joystick.
     * @param width: Width of the component.
     * @return X location of the component.
     */
    public static float getControlsX(int indicator, float width) {
        boolean isDefault = open("controls").equals("default");
        float screenWidth = Gdx.graphics.getWidth();
        if((indicator == 1 && isDefault) || (indicator == 2 && !isDefault)) {
            return screenWidth / 8 - width / 2;
        } else {
            return screenWidth * 7 / 8 - width / 2;
        }
    }

    /**
     * Inverts the components (boost-button and joystick).
     */
    public static void invert() {
        if(open("controls").equals("default")) {
            save("controls", "inverted");
        } else {
            save("controls", "default");
        }
    }
}
