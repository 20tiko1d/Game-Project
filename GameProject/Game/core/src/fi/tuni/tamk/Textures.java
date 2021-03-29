package fi.tuni.tamk;

import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;

public final class Textures {


    private Textures() {}

    public static ArrayList<Texture> getFloor1Textures() {
        ArrayList<Texture> textures = new ArrayList<Texture>();
        textures.add(new Texture("textures/floorsInside/Sand/sandstonefloor1.png"));
        textures.add(new Texture("textures/floorsInside/Sand/sandstonefloor2.png"));
        textures.add(new Texture("textures/floorsInside/Sand/sandstonefloor3.png"));
        return textures;
    }

    public static ArrayList<Texture> getFloor2Textures() {
        ArrayList<Texture> textures = new ArrayList<Texture>();
        textures.add(new Texture("textures/floorsInside/random/sandfloor3.png"));
        return textures;
    }

    public static ArrayList<Texture> getWallTextures() {
        ArrayList<Texture> textures = new ArrayList<Texture>();
        textures.add(new Texture("textures/walls/random/Old-brickwall.png"));
        return textures;
    }

    public static Texture getPlayerTexture() {
        return new Texture("textures/player/random/Ukkeli1.png");
    }

    public static Texture getBackgroundTexture() {
        return new Texture("textures/random/circle3.png");
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
}
