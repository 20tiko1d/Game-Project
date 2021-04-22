package fi.tuni.tamk;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * The class contains the level-choosing screen and also game generating functions.
 */
public class LevelScreen extends ScreenAdapter {

    private Main main;

    private Stage stage;

    private SpriteBatch batch;

    private Texture backgroundImage;
    private float backgroundHeight;

    private int screenWidth;
    private int screenHeight;

    private Rectangle rect;
    private int marginal;

    private Sound buttonPressSound;

    public LevelScreen(Main main){
        this.main = main;
        calculateAreas();
        batch = new SpriteBatch();
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        //buttonPressSound = Sounds.buttonPressSound;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        backgroundImage = Textures.getMenuBackground();
        backgroundHeight = (float) screenWidth * backgroundImage.getHeight() / backgroundImage.getWidth();

        Skin mySkin = Textures.mySkin;

        TextButton buttonEasy = new TextButton(GameConfiguration.getText("easyLevel"), mySkin,"pixel72");
        buttonEasy.setSize(rect.width,rect.height);
        buttonEasy.setPosition(rect.x,rect.y);
        buttonEasy.setColor(Color.GREEN);
        buttonEasy.getLabel().setFontScale(GameConfiguration.fitText(buttonEasy, -1, -1));
        buttonEasy.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //buttonPressSound.play();
                GameConfiguration.gameLevel = 1;
                createGame();
            }
        });

        TextButton buttonMedium = new TextButton(GameConfiguration.getText("mediumLevel"),mySkin,"pixel72");
        buttonMedium.setSize(rect.width,rect.height);
        if(Main.isPortrait) {
            buttonMedium.setPosition(rect.x,rect.y - 2 * marginal - rect.height);
        } else {
            buttonMedium.setPosition(rect.x + 2 * marginal + rect.width, rect.y);
        }
        buttonMedium.setColor(Color.ORANGE);
        buttonMedium.getLabel().setFontScale(GameConfiguration.fitText(buttonMedium, -1, -1));
        buttonMedium.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //buttonPressSound.play();
                GameConfiguration.gameLevel = 2;
                createGame();
            }
        });

        TextButton buttonHard = new TextButton(GameConfiguration.getText("hardLevel"),mySkin,"pixel72");
        buttonHard.setSize(rect.width,rect.height);
        if(Main.isPortrait) {
            buttonHard.setPosition(rect.x,rect.y - (2 * marginal + rect.height) * 2);
        } else {
            buttonHard.setPosition(rect.x + (2 * marginal + rect.width) * 2, rect.y);
        }
        buttonHard.setColor(Color.RED);
        buttonHard.getLabel().setFontScale(GameConfiguration.fitText(buttonHard, -1, -1));
        buttonHard.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //buttonPressSound.play();
                GameConfiguration.gameLevel = 3;
                createGame();
            }
        });

        TextButton buttonTutorial = new TextButton(GameConfiguration.getText("tutorial"),mySkin,"pixel72");
        buttonTutorial.setSize(screenWidth / 10f,screenWidth / 10f);
        buttonTutorial.setPosition(0, 0);
        buttonTutorial.setColor(Color.GRAY);
        buttonTutorial.getLabel().setFontScale(GameConfiguration.fitText(buttonTutorial, -1, -1));
        Gdx.app.log("", "" + buttonTutorial.getLabel().getFontScaleX());
        buttonTutorial.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //buttonPressSound.play();
                GameConfiguration.tutorialOn = true;
                GameConfiguration.gameLevel = 1;
                createGame();
            }
        });

        TextButton buttonMenu = new TextButton(GameConfiguration.getText("menu"),mySkin,"pixel72");
        buttonMenu.setSize(screenWidth / 10f,screenWidth / 10f);
        buttonMenu.setPosition(0,screenHeight - screenWidth / 10f);
        buttonMenu.setColor(Color.YELLOW);
        buttonMenu.getLabel().setFontScale(GameConfiguration.fitText(buttonMenu, -1, -1));
        buttonMenu.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //buttonPressSound.play();
                dispose();
                main.setScreen(new MenuScreen(main));
            }
        });

        TextButton buttonStore = new TextButton(GameConfiguration.getText("store"),mySkin,"pixel72");
        buttonStore.setSize(screenWidth / 5f,screenWidth / 15f);
        buttonStore.setPosition(screenWidth / 2f - buttonStore.getWidth() / 2f,
                screenHeight - buttonStore.getHeight());
        buttonStore.setColor(56 / 255f, 114 / 255f, 207 / 255f, 1);
        buttonStore.getLabel().setFontScale(GameConfiguration.fitText(buttonStore, -1, -1));
        buttonStore.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //buttonPressSound.play();
                dispose();
                main.setScreen(new Store(main));
            }
        });

        TextButton buttonHighScores = new TextButton(GameConfiguration.getText("highScores"),mySkin,"pixel72");
        buttonHighScores.setSize(screenWidth / 10f,screenWidth / 10f);
        buttonHighScores.setPosition(screenWidth -buttonHighScores.getWidth(),
                0);
        buttonHighScores.setColor(56 / 255f, 114 / 255f, 207 / 255f, 1);
        buttonHighScores.getLabel().setFontScale(GameConfiguration.fitText(buttonHighScores, -1, -1));
        buttonHighScores.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //buttonPressSound.play();
                dispose();
                main.setScreen(new HighScores(main));
            }
        });

        String themeString = GameConfiguration.getText("themeLabel") + ": " + GameConfiguration.theme.toUpperCase();
        Label themeLabel = new Label(themeString , mySkin, "default");
        themeLabel.setBounds(screenWidth * 5 / 7f, screenHeight * 9 / 10f,
                screenWidth / 5f, screenHeight / 10f);
        themeLabel.setColor(Color.BLACK);
        themeLabel.setFontScale(1.3f);

        stage.addActor(themeLabel);
        stage.addActor(buttonEasy);
        stage.addActor(buttonMedium);
        stage.addActor(buttonHard);
        stage.addActor(buttonTutorial);
        stage.addActor(buttonMenu);
        stage.addActor(buttonStore);
        stage.addActor(buttonHighScores);

        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(backgroundImage, 0, 0, screenWidth, backgroundHeight);
        batch.end();

        stage.draw();
        stage.act();
    }

    @Override
    public void dispose () {
        stage.dispose();
        rect = null;
    }

    /**
     * Method starts the creation of the game world.
     */
    public void createGame() {
        GameScreen gameScreen = GameConfiguration.createGame(main);
        dispose();
        main.setScreen(gameScreen);
    }

    /**
     * Method gives the locations and sizes of the level panels.
     */
    public void calculateAreas() {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        rect = new Rectangle();
        rect.x = width / 5f;
        rect.y = height / 5f;
        rect.width = rect.x * 3;
        rect.height = rect.y * 3;
        marginal = 5;
        float singleSize;
        if(!Main.isPortrait) {
            singleSize = rect.height;
            if(rect.width / 3 < singleSize) {
                singleSize = rect.width / 3;
                rect.x = rect.x + marginal;
                rect.y = height / 2f - (singleSize - 2 * marginal) / 2;
            } else {
                rect.x = width / 2f - 2 * marginal - 1.5f * (singleSize - 2 * marginal);
                rect.y = rect.y + marginal;
            }
        } else {
            singleSize = rect.width;
            if(rect.height / 3 < singleSize) {
                singleSize = rect.height / 3;
                rect.x = width / 2f - (singleSize - 2 * marginal) / 2;
            } else {
                rect.x = rect.x + marginal;
            }
            rect.y = height / 2f + singleSize / 2 + marginal;
        }
        rect.height = singleSize - 2 * marginal;
        rect.width = rect.height;
    }
}
