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
        textures.add(new Texture("walls/Brickwall2.png"));
        return textures;
    }

    public static Texture getPlayerTexture() {
        return new Texture("player2.png");
    }

    public static Texture getBackgroundTexture() {
        return new Texture("circle2.png");
    }

    public static Texture getObjectTexture() {
        return new Texture("pairs1.png");
    }

}
