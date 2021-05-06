package fi.tuni.tamk.LabyrinthOfLife;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.ArrayList;

/**
 * Class for textures.
 *
 * @author Artur Haavisto
 */
public final class Textures {

    // Skin
    public Skin mySkin = new Skin(Gdx.files.internal("skin/gameUI.json"));

    // Flag textures
    public Texture finFlag = new Texture("textures/flags/fin_flag.png");
    public Texture engFlag = new Texture("textures/flags/gbr_flag.png");

    // Menu screen button background (40px width, 30px Height)
    public Texture menuButtonBackground = new Texture("textures/backgrounds/menuButtonBackground.png");

    // Pause button
    public Texture pauseButtonTexture = new Texture("textures/random/pauseButton.png");

    // Play button in pause screen
    public Texture playButtonTexture = new Texture("textures/random/playButton.png");

    // Object shadow
    public Texture shadow = new Texture("textures/random/shadow.png");

    // Game background "mist"
    public Texture background = new Texture("textures/backgrounds/fogBackground.png");

    // Tutorial guidance background
    public Texture tutorialTextBackground = new Texture("textures/backgrounds/tutorialTextBackground.png");

    // Theme buttons
    public Texture themeBush = new Texture("textures/themeScreenTextures/themeBush.png");
    public Texture themeSand = new Texture("textures/themeScreenTextures/themeSand.png");
    public Texture checkMark = new Texture("textures/themeScreenTextures/checkMark.png");
    public Texture playerGhost = new Texture("textures/themeScreenTextures/ghostTheme.png");
    public Texture playerBlueDude = new Texture("textures/themeScreenTextures/blueDudeTheme.png");
    public Texture playerPig = new Texture("textures/themeScreenTextures/pigTheme.png");

    // Controls images in settings
    public Texture controlsBackground = new Texture("textures/controlsSetting/backgroundWhite.png");
    public Texture boostTexture = new Texture("textures/controlsSetting/boostSettings.png");
    public Texture joystickTexture = new Texture("textures/controlsSetting/joystickSettings.png");

    /**
     * Get floor textures which are inside of the labyrinth.
     *
     * Returned textures depend on the chosen theme.
     *
     * @return ArrayList of floor textures.
     */
    public ArrayList<Texture> getInsideFloorTextures() {
        ArrayList<Texture> textures = new ArrayList<>();
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

    /**
     * Get floor textures which are outside of the labyrinth.
     *
     * Returned textures depend on the chosen theme.
     *
     * @return ArrayList of floor textures.
     */
    public ArrayList<Texture> getOutsideFloorTextures() {
        ArrayList<Texture> textures = new ArrayList<>();

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

    /**
     * Get wall textures.
     *
     * Returned textures depend on the chosen theme.
     *
     * @return ArrayList of wall textures.
     */
    public ArrayList<Texture> getWallTextures() {
        ArrayList<Texture> textures = new ArrayList<>();
      
        if(GameConfiguration.theme.equals("bush")) {
            textures.add(new Texture("textures/walls/Bush/bushWall.png"));
        }
        else {
            textures.add(new Texture("textures/walls/Sand/Old-brickwall.png"));
        }

        return textures;
    }

    /**
     * Get player textures.
     *
     * Returned textures depend on the chosen character. The array contains textures for all 4
     * directions.
     *
     * @param name: Name of the character.
     * @return Array of the character textures.
     */
    public Texture[] getPlayerTexture(String name) {
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

    /**
     * Get in game object textures.
     *
     * @return ArrayList of all possible in game object textures.
     */
    public Texture getObjectTexture() {
        ArrayList<Texture> textures = new ArrayList<>();

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
        textures.add(new Texture("textures/object/piggyBank.png"));
        textures.add(new Texture("textures/object/snowGlobe.png"));
        textures.add(new Texture("textures/object/sushi.png"));
        textures.add(new Texture("textures/object/taco.png"));
        textures.add(new Texture("textures/object/tree.png"));
        textures.add(new Texture("textures/object/watermelon.png"));

        int random = MathUtils.random(0, textures.size() - 1);
        return textures.get(random);
    }

    /**
     * Get closed exit texture.
     *
     * @param onTop: Tells if the exit is on top of the labyrinth.
     * @return Closed exit texture.
     */
    public Texture getExitCloseTexture(boolean onTop) {
        if(onTop) {
            return new Texture("textures/walls/exit/wallExitTop_closed.png");
        } else {
            return new Texture("textures/walls/exit/wallExitSide_closed.png");
        }
    }

    /**
     * Get open exit texture.
     *
     * @param onTop: Tells if the exit is on top of the labyrinth.
     * @return Open exit texture.
     */
    public Texture getExitOpenTexture(boolean onTop) {
        if(onTop) {
            return new Texture("textures/walls/exit/wallExitTop_open.png");
        }
        return new Texture("textures/walls/exit/wallExitSide_open.png");
    }

    /**
     * Get start texture.
     *
     * @return Start texture.
     */
    public Texture getStartTexture() {
        return new Texture("textures/walls/start/wallStart.png");
    }

    /**
     * Gets texture for the in game object sentence backgrounds.
     *
     * @return Object background.
     */
    public Texture getObjectLabelBackground() { return new Texture("textures/backgrounds/pairLabelBackground.png");}

    /**
     * Gets the joystick background texture.
     *
     * @return Joystick background texture.
     */
    public Texture getJoystickBack() { return new Texture("textures/joystick/joystickBack.png");}

    /**
     * Gets the joystick knob texture.
     *
     * @return Joystick knob texture.
     */
    public Texture getJoystickKnob() { return new Texture("textures/joystick/joystickKnob.png");}

    /**
     * Gets menu background image.
     *
     * @return Menu background texture.
     */
    public Texture getMenuBackground() {
        return new Texture("textures/backgrounds/menuBackgroundImage.png");
    }

    /**
     * Gets top 3 high scores backgrounds.
     *
     * The background depends on the given name.
     *
     * @param name: Name of the medal color.
     * @return High scores medal color texture.
     */
    public Texture getMedalTexture(String name) {
        return new Texture("textures/medalColors/" + name + ".png");
    }
}
