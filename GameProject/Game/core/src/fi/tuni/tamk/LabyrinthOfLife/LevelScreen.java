package fi.tuni.tamk.LabyrinthOfLife;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Screen contains difficulty choosing options.
 *
 * @author Artur Haavisto
 */
public class LevelScreen extends ScreenAdapter {

    private final Main main;

    private Stage stage;
    private final SpriteBatch batch;

    private Texture backgroundImage;
    private float backgroundHeight;

    private final int screenWidth;
    private final int screenHeight;

    private Rectangle rect;
    private int marginal;

    private final Sound buttonPressSound;

    private final Textures textures;

    /**
     * Level screen constructor.
     *
     * @param main: Game class object.
     */
    public LevelScreen(Main main){
        this.main = main;
        this.textures = main.textures;
        calculateAreas();
        batch = new SpriteBatch();
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        buttonPressSound = main.sounds.buttonPressSound;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        backgroundImage = textures.getMenuBackground();
        backgroundHeight = (float) screenWidth * backgroundImage.getHeight() / backgroundImage.getWidth();

        Skin mySkin = textures.mySkin;

        TextButton buttonEasy = new TextButton(GameConfiguration.getText("easyLevel"), mySkin,"pixel72");
        buttonEasy.setSize(rect.width,rect.height);
        buttonEasy.setPosition(rect.x,rect.y);
        buttonEasy.setColor(Color.GREEN);
        buttonEasy.getLabel().setFontScale(GameConfiguration.fitText(buttonEasy, -1, -1));
        buttonEasy.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
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
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
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
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                GameConfiguration.gameLevel = 3;
                createGame();
            }
        });

        TextButton buttonTutorial = new TextButton(GameConfiguration.getText("tutorial"),mySkin,"pixel72");
        buttonTutorial.setSize(screenWidth / 10f,screenWidth / 10f);
        buttonTutorial.setPosition(0, 0);
        buttonTutorial.setColor(Color.GRAY);
        buttonTutorial.getLabel().setFontScale(GameConfiguration.fitText(buttonTutorial, -1, -1));
        buttonTutorial.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
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
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dispose();
                main.setScreen(new MenuScreen(main));
            }
        });

        TextButton buttonThemes = new TextButton(GameConfiguration.getText("themes"),mySkin,"pixel72");
        buttonThemes.setSize(screenWidth / 5f,screenWidth / 10f);
        buttonThemes.setPosition(screenWidth / 2f - buttonThemes.getWidth() / 2f,
                screenHeight - buttonThemes.getHeight());
        buttonThemes.setColor(56 / 255f, 114 / 255f, 207 / 255f, 1);
        buttonThemes.getLabel().setFontScale(GameConfiguration.fitText(buttonThemes, -1, -1));
        buttonThemes.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dispose();
                main.setScreen(new ThemesScreen(main));
            }
        });

        TextButton buttonHighScores = new TextButton(GameConfiguration.getText("highScoresWrap"),mySkin,"pixel72");
        buttonHighScores.setSize(screenWidth / 10f,screenWidth / 10f);
        buttonHighScores.setPosition(screenWidth -buttonHighScores.getWidth(),
                0);
        buttonHighScores.setColor(180 / 255f, 90 / 255f, 230 / 255f, 1);
        buttonHighScores.getLabel().setFontScale(GameConfiguration.fitText(buttonHighScores, 40, -1));
        buttonHighScores.getLabel().setWrap(true);
        buttonHighScores.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dispose();
                main.setScreen(new HighScoresScreen(main));
            }
        });

        stage.addActor(buttonEasy);
        stage.addActor(buttonMedium);
        stage.addActor(buttonHard);
        stage.addActor(buttonTutorial);
        stage.addActor(buttonMenu);
        stage.addActor(buttonThemes);
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
