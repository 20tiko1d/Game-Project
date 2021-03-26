package fi.tuni.tamk;

import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;

public final class Textures {


    private Textures() {}

    public static ArrayList<Texture> getFloor1Textures() {
        ArrayList<Texture> textures = new ArrayList<Texture>();
        textures.add(new Texture("floors/grassDark.png"));
        return textures;
    }

    public static ArrayList<Texture> getFloor2Textures() {
        ArrayList<Texture> textures = new ArrayList<Texture>();
        textures.add(new Texture("floors/Rockfloor2.png"));
        return textures;
    }

    public static ArrayList<Texture> getWallTextures() {
        ArrayList<Texture> textures = new ArrayList<Texture>();
        textures.add(new Texture("walls/bushWall.png"));
        return textures;
    }

    public static Texture getPlayerTexture() {
        return new Texture("player0.66.png");
    }

    public static Texture getBackgroundTexture() {
        return new Texture("circle3.png");
    }

    public static Texture getObjectTexture() {
        return new Texture("pairs1.png");
    }

    public static Texture getExitCloseTexture() {
        return new Texture("walls/wallExitClose0.66.png");
    }

    public static Texture getExitOpenTexture() {
        return new Texture("walls/wallExitOpen0.66.png");
    }

    public static Texture getStartTexture() {
        return new Texture("walls/wallStart0.66.png");
    }

    public static Texture getSideTexture() { return new Texture("sides.png");}

    public static Texture getPairLabelBackground() { return new Texture("pairLabelBackground.png");}

    public static Texture getJoystickBack() { return new Texture("joystickBack2.png");}

    public static Texture getJoystickKnob() { return new Texture("joystickKnob2.png");}
}
