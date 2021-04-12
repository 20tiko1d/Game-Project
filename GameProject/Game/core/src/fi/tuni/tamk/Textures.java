package fi.tuni.tamk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
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

    public static Texture[] getPlayerTexture() {
        Texture [] playerTextures = new Texture[4];
        // Front
        playerTextures[0] = new Texture("textures/player/Ghost/Kawaiighost-front.png");
        // Left
        playerTextures[1] = new Texture("textures/player/Ghost/Kawaiighost-left.png");
        // Back
        playerTextures[2] = new Texture("textures/player/Ghost/Kawaiighost-back.png");
        // Right
        playerTextures[3] = new Texture("textures/player/Ghost/Kawaiighost-right.png");
        return playerTextures;
    }

    public static Texture getBackgroundTexture() {
        return new Texture("textures/random/Sumu.png");
    }

    public static Texture getObjectTexture() {
        return new Texture("textures/object/mushroom.png");
    }

    public static Texture getExitCloseTexture() {
        return new Texture("textures/walls/random/wallExitClose0.66.png");
    }

    public static Texture getExitOpenTexture() {
        return new Texture("textures/walls/random/wallExitOpen0.66.png");
    }

    public static Texture getStartTexture() {
        return new Texture("textures/walls/random/wallStart0.66.png");
    }

    public static Texture getSideTexture() { return new Texture("textures/random/sides.png");}

    public static Texture getPairLabelBackground() { return new Texture("textures/random/pairLabelBackground.png");}

    public static Texture getJoystickBack() { return new Texture("textures/joystick/random/joystickBack.png");}

    public static Texture getJoystickKnob() { return new Texture("textures/joystick/random/joystickKnob.png");}

    public static Texture getTutorialTextBackground() {
        return new Texture("textures/random/tutorialTextBackground.png");
    }

    public static Texture getMenuBackground() {
        return new Texture("textures/random/menuBackground3.png");
    }
}
