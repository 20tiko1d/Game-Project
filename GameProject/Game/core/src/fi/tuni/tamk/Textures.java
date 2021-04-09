package fi.tuni.tamk;

import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;

public final class Textures {


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

    public static Texture getPlayerTexture() {
        return new Texture("textures/player/random/Ukkeli.png");
    }

    public static Texture getBackgroundTexture() {
        return new Texture("textures/random/Sumu.png");
    }

    public static Texture getObjectTexture() {
        return new Texture("textures/object/mushroom1.png");
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

    public static Texture getJoystickBack() { return new Texture("textures/joystick/random/joystickBack2.png");}

    public static Texture getJoystickKnob() { return new Texture("textures/joystick/random/joystickKnob2.png");}

    public static Texture getTutorialTextBackground() {
        return new Texture("textures/random/tutorialTextBackground.png");
    }
}
