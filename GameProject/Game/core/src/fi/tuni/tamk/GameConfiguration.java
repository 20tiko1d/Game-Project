package fi.tuni.tamk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    public static float joystickXOffset = Gdx.graphics.getWidth() / 32f;
    public static float joystickLength = Gdx.graphics.getWidth() * 3 / 16f;
    public static float joystickX = Gdx.graphics.getWidth() * 3 / 4f + joystickXOffset;
    public static float joystickY = Gdx.graphics.getHeight() / 4f - joystickLength / 2;


    // Saving stuff
    public static String noValue = "No value stored";

    public static boolean firstTime;

    // High Score
    public static ArrayList<HighScoreEntry> highScores;

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
        if(open("firstTime").equals(noValue)) {
            save("theme", "sand");
            save("player", "ghost");
            save("controls", "default");
            firstTime = true;
        } else {
            firstTime = false;
        }

        theme = open("theme");
    }

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

    public static float fitText(TextButton button, int givenFontSize, int givenMaxSize) {
        float width = button.getWidth() * 0.8f;
        int length = button.getText().length();
        float fontSize = givenFontSize;
        if(givenFontSize < 0) {
            String styleName = button.getStyle().font.getData().name;
            String fontSizeString = "";
            for(int i = styleName.length() - 3; i < styleName.length() - 1; i++) {
                try {
                    fontSizeString = fontSizeString + Integer.parseInt(styleName.substring(i));
                } catch (Exception e) {}
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
    public void receiveHighScore(ArrayList<HighScoreEntry> highScores) {
        this.highScores = highScores;
    }

    @Override
    public void failedToRetrieveHighScores(String s) {
        Gdx.app.log("", "failedToRetrieveHighScores: " + s);
    }

    @Override
    public void receiveSendReply(int httpResponse) {
        Gdx.app.log("", "receiveSendReply: " + httpResponse);
    }

    @Override
    public void failedToSendHighScore(String s) {
        Gdx.app.log("", "failedToSendHighScore: " + s);
    }

    public static void getHighScores(int mapId) {
        try {
            HighScoreServer.readConfig("highscore.config");
            HighScoreServer.fetchHighScores(new GameConfiguration(), mapId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendHighScores(int score) {
        HighScoreEntry entry = new HighScoreEntry(open("name"), score, 1, gameLevel);
        try {
            HighScoreServer.readConfig("highscore.config");
            HighScoreServer.sendNewHighScore(entry, new GameConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static float getControlsX(int indicator, float width) {
        boolean isDefault = open("controls").equals("default");
        float screenWidth = Gdx.graphics.getWidth();
        if((indicator == 1 && isDefault) || (indicator == 2 && !isDefault)) {
            return screenWidth / 8 - width / 2;
        } else {
            return screenWidth * 7 / 8 - width / 2;
        }
    }

    public static void invert() {
        if(open("controls").equals("default")) {
            save("controls", "inverted");
        } else {
            save("controls", "default");
        }
    }
}
