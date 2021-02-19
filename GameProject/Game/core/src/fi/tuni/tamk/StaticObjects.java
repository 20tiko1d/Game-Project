package fi.tuni.tamk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class StaticObjects {
    private int [][] walls;
    private Texture[][] wallsImg;
    private Rectangle rect;
    private Rectangle singleRect;

    private int labyrinthSize;
    private int relativeLength;

    public StaticObjects(int [][] walls, Rectangle rect) {
        this.walls = walls;
        this.rect = rect;
        singleRect = new Rectangle();
        labyrinthSize = walls.length;
        relativeLength = labyrinthSize - 2;
        setTextures();
    }

    public void setTextures() {
        Texture img1 = new Texture("walls/wall_bottom.png");
        Texture img2 = new Texture("walls/wall_left.png");
        Texture img3 = new Texture("walls/wall_top.png");
        Texture img4 = new Texture("walls/wall_right.png");
        Texture img5 = new Texture("walls/wall_bottom_left.png");
        Texture img6 = new Texture("walls/wall_bottom_top.png");
        Texture img7 = new Texture("walls/wall_bottom_right.png");
        Texture img8 = new Texture("walls/wall_bottom_left_top.png");
        Texture img9 = new Texture("walls/wall_bottom_left_right.png");
        Texture img10 = new Texture("walls/wall_top_left.png");
        Texture img11 = new Texture("walls/wall_top_right.png");
        Texture img12 = new Texture("walls/wall_top_left_right.png");
        Texture img13 = new Texture("walls/wall_left_right.png");
        Texture img14 = new Texture("walls/wall_bottom_right_top.png");
        Texture img15 = new Texture("walls/wall_empty.png");

        wallsImg = new Texture[walls.length][walls.length];

        for(int row = 0; row < walls.length; row++) {
            for(int column = 0; column < walls[row].length; column++) {
                Texture img;
                switch (walls[row][column]) {
                    case 1:
                        img = img1;
                        break;
                    case 2:
                        img = img2;
                        break;
                    case 3:
                        img = img3;
                        break;
                    case 4:
                        img = img4;
                        break;
                    case 5:
                        img = img5;
                        break;
                    case 6:
                        img = img6;
                        break;
                    case 7:
                        img = img7;
                        break;
                    case 8:
                        img = img8;
                        break;
                    case 9:
                        img = img9;
                        break;
                    case 10:
                        img = img10;
                        break;
                    case 11:
                        img = img11;
                        break;
                    case 12:
                        img = img12;
                        break;
                    case 13:
                        img = img13;
                        break;
                    case 14:
                        img = img14;
                        break;
                    default:
                        img = img15;

                }
                wallsImg[row][column] = img;
            }
        }
    }

    public void render(SpriteBatch batch) {
        for(int row = 0; row < wallsImg.length; row++) {
            for(int column = 0; column < wallsImg[row].length; column++) {
                batch.draw(wallsImg[row][column], singleRect.x + column * relativeLength * singleRect.width / labyrinthSize,
                        singleRect.y - row * relativeLength * singleRect.height / labyrinthSize, singleRect.width,
                        singleRect.height);
            }
        }
    }

    public void setRect(Rectangle rect) {
        this.rect = rect;
        singleRect.x = rect.x;
        singleRect.y = rect.y + rect.height * (walls.length - 1) / walls.length;
        singleRect.width = (rect.width * labyrinthSize) / (walls.length * relativeLength);
        singleRect.height = (rect.height * labyrinthSize) / (walls.length * relativeLength);
    }
}
