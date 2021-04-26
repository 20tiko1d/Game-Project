package fi.tuni.tamk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.ArrayList;

public final class Textures {

    // Skin
    public static Skin mySkin = new Skin(Gdx.files.internal("skin/gameUI.json"));

    // Flag textures
    public static Texture finFlag = new Texture("textures/random/fin_flag.png");
    public static Texture engFlag = new Texture("textures/random/gbr_flag.png");

    // Menu screen button background (40px width, 30px Height)
    public static Texture menuButtonBackground = new Texture("textures/random/menuButtonBackground2.png");

    // Pause button
    public static Texture pauseButtonTexture = new Texture("textures/random/pauseButton.png");

    // Play button in pause screen
    public static Texture playButtonTexture = new Texture("textures/random/playButton.png");

    // Object shadow
    public static Texture shadow = new Texture("textures/random/shadow2.png");

    // Game background "mist"
    public static Texture background = new Texture("textures/random/fog8.png");

    // Theme buttons
    public static Texture themeBush = new Texture("textures/random/themeBush.png");
    public static Texture themeSand = new Texture("textures/random/themeSand.png");
    public static Texture checkMark = new Texture("textures/random/checkMark.png");
    public static Texture playerGhost = new Texture("textures/random/ghostTheme.png");
    public static Texture playerBlueDude = new Texture("textures/random/blueDudeTheme.png");
    public static Texture playerPig = new Texture("textures/random/pigTheme.png");

    // Controls images in settings
    public static Texture whiteBackground = new Texture("textures/random/backgroundWhite.png");
    public static Texture boostTexture = new Texture("textures/random/boostSettings.png");
    public static Texture joystickTexture = new Texture("textures/random/joystickSettings.png");


    private Textures() {}

    public static ArrayList<Texture> getFloor1Textures() {
        ArrayList<Texture> textures = new ArrayList<Texture>();
        if(GameConfiguration.theme.equals("bush")) {
            textures.add(new Texture("textures/floorsInside/Bush/Rockfloor.png"));
        }
        else {
            textures.add(new Texture("textures/floorsInside/Sand/sandstonefloor1.png"));
            textures.add(new Texture("textures/floorsInside/Sand/sandstonefloor2.png"));
            textures.add(new Texture("textures/floorsInside/Sand/sandstonefloor3.png"));
        }
        return textures;
    }

    public static ArrayList<Texture> getFloor2Textures() {
        ArrayList<Texture> textures = new ArrayList<Texture>();

        if(GameConfiguration.theme.equals("bush")) {
            textures.add(new Texture("textures/floorsOutside/Bush/grass1.png"));
            textures.add(new Texture("textures/floorsOutside/Bush/grass2.png"));
            textures.add(new Texture("textures/floorsOutside/Bush/plant1.png"));
        }
        else {
            textures.add(new Texture("textures/floorsOutside/Sand/sandfloor3.png"));
        }
        return textures;
    }

    public static ArrayList<Texture> getWallTextures() {
        ArrayList<Texture> textures = new ArrayList<Texture>();
      
        if(GameConfiguration.theme.equals("bush")) {
            textures.add(new Texture("textures/walls/Bush/bushWall.png"));
        }
        else {
            textures.add(new Texture("textures/walls/Sand/Old-brickwall.png"));
        }

        return textures;
    }

    public static Texture[] getPlayerTexture(String name) {
        Texture [] playerTextures = new Texture[4];
        // Front
        playerTextures[0] = new Texture("textures/player/" + name + "/" + name + "Front.png");
        // Left
        playerTextures[1] = new Texture("textures/player/" + name + "/" + name + "Left.png");
        // Back
        playerTextures[2] = new Texture("textures/player/" + name + "/" + name + "Back.png");
        // Right
        playerTextures[3] = new Texture("textures/player/" + name + "/" + name + "Right.png");
        return playerTextures;
    }

    public static Texture getBackgroundTexture() {
        return new Texture("textures/random/Sumu.png");
    }

    public static Texture getObjectTexture() {
        ArrayList<Texture> textures = new ArrayList<Texture>();

        textures.add(new Texture("textures/object/alarmClock.png"));
        textures.add(new Texture("textures/object/apple.png"));
        textures.add(new Texture("textures/object/board.png"));
        textures.add(new Texture("textures/object/cactus.png"));
        textures.add(new Texture("textures/object/carrot.png"));
        textures.add(new Texture("textures/object/cherry.png"));
        textures.add(new Texture("textures/object/diamond.png"));
        textures.add(new Texture("textures/object/eggs.png"));
        textures.add(new Texture("textures/object/flower.png"));
        textures.add(new Texture("textures/object/mushroom.png"));
        textures.add(new Texture("textures/object/pencils.png"));
        textures.add(new Texture("textures/object/snowglobe.png"));
        textures.add(new Texture("textures/object/sushi.png"));
        textures.add(new Texture("textures/object/taco.png"));
        textures.add(new Texture("textures/object/tree.png"));
        textures.add(new Texture("textures/object/watermelon.png"));

        int random = MathUtils.random(0, textures.size() - 1);
        return textures.get(random);
    }

    public static Texture getExitCloseTexture(boolean onTop) {
        if(onTop) {
            return new Texture("textures/walls/random/wallExitTop_closed.png");
        } else {
            return new Texture("textures/walls/random/wallExitClose0.66.png");
        }
    }

    public static Texture getExitOpenTexture(boolean onTop) {
        if(onTop) {
            return new Texture("textures/walls/random/exitTestOpenUp.png");
        }
        return new Texture("textures/walls/random/wallExitOpen0.66.png");
    }

    public static Texture getStartTexture() {
        return new Texture("textures/walls/random/wallStart10.png");
    }

    public static Texture getSideTexture() { return new Texture("textures/random/sides.png");}

    public static Texture getPairLabelBackground() { return new Texture("textures/random/pairLabelBackground.png");}

    public static Texture getJoystickBack() { return new Texture("textures/joystick/random/joystickBack.png");}

    public static Texture getJoystickKnob() { return new Texture("textures/joystick/random/joystickKnob.png");}

    public static Texture getTutorialTextBackground() {
        return new Texture("textures/random/tutorialTextBackground.png");
    }

    public static Texture getMenuBackground() {
        return new Texture("textures/random/backgroundImage5.png");
    }
}
