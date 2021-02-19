package fi.tuni.tamk;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Player {
    private Texture texture;

    private Rectangle rect;


    public Player(Texture texture, Rectangle rect) {
        this.texture = texture;
        this.rect = rect;
    }
}
