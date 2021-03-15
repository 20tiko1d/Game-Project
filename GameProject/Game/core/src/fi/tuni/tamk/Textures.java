package fi.tuni.tamk;

import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;

public final class Textures {


    private Textures() {}

    public static ArrayList<Texture> getFloor1Textures() {
        ArrayList<Texture> textures = new ArrayList<Texture>();
        textures.add(new Texture("floors/Rockfloor2.png"));
        textures.add(new Texture("floors/Rockfloor.png"));
        return textures;
    }

    public static ArrayList<Texture> getFloor2Textures() {
        ArrayList<Texture> textures = new ArrayList<Texture>();
        textures.add(new Texture("floors/floor2.png"));
        return textures;
    }

    public static ArrayList<Texture> getWallTextures() {
        ArrayList<Texture> textures = new ArrayList<Texture>();
        textures.add(new Texture("walls/wall3.0.66.png"));
        return textures;
    }

    public static Texture getPlayerTexture() {
        return new Texture("player0.66.png");
    }

    public static Texture getBackgroundTexture() {
        return new Texture("circle3.png");
    }

    public static Texture getObjectTexture() {
        return new Texture("object.png");
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
}
