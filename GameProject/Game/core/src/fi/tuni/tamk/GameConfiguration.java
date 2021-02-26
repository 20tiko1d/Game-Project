package fi.tuni.tamk;

public final class GameConfiguration {

    public static int gameLevel;

    private static final int SCORE = 100;

    private static final int OBJECT_SCORE = 20;


    private GameConfiguration() {}

    public static int getStartScore() {
        return SCORE * gameLevel;
    }

    public static int getObjectScore() {
        return OBJECT_SCORE * gameLevel;
    }

}
