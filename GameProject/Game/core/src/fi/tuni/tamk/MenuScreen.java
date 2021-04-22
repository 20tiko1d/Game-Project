package fi.tuni.tamk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * The class contains and controls the Main menu screen.
 */
public class MenuScreen extends ScreenAdapter {

    private Main main;

    private Stage stage;
    private OrthographicCamera camera;
    private SpriteBatch batch;

    private TextButton buttonPlay;
    private TextButton buttonSettings;
    private TextButton buttonHighScores;

    private Texture backgroundImage;
    private Texture buttonBackground;
    private float backgroundHeight;

    private final int screenWidth = Gdx.graphics.getWidth();
    private final int screenHeight = Gdx.graphics.getHeight();

    private Sound buttonPressSound;

    public MenuScreen(Main main) {
        this.main = main;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.viewPortWidth, Main.viewPortHeight);
        batch = new SpriteBatch();
        //buttonPressSound = Sounds.buttonPressSound;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        backgroundImage = Textures.getMenuBackground();
        buttonBackground = Textures.menuButtonBackground;
        backgroundHeight = (float) screenWidth * backgroundImage.getHeight() / backgroundImage.getWidth();

        Skin mySkin = Textures.mySkin;

        Texture flagTexture = Textures.engFlag;
        if(GameConfiguration.getLanguage().equals("en_UK")) {
            flagTexture = Textures.finFlag;
        }
        final Drawable drawable = new TextureRegionDrawable(new TextureRegion(flagTexture));
        final Button flagButton = new Button(drawable);
        flagButton.setSize(screenWidth / 8f, screenWidth / 16f);
        flagButton.setPosition(screenWidth - flagButton.getWidth() - 50,
                screenHeight - flagButton.getHeight() - 50);
        flagButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(GameConfiguration.getLanguage().equals("fi_FI")) {
                    flagButton.getStyle().up = new TextureRegionDrawable(new TextureRegion(Textures.finFlag));
                    GameConfiguration.save("language", "en_UK");
                } else {
                    flagButton.getStyle().up = new TextureRegionDrawable(new TextureRegion(Textures.engFlag));
                    GameConfiguration.save("language", "fi_FI");
                }
                updateLanguage();
            }
        });

        TextButton buttonTest = new TextButton(GameConfiguration.getText("menu"),mySkin,"pixel72");
        buttonTest.setSize(screenWidth / 10f,screenWidth / 10f);
        buttonTest.setPosition(0,screenHeight - screenWidth / 10f);
        buttonTest.setColor(Color.YELLOW);
        buttonTest.getLabel().setFontScale(GameConfiguration.fitText(buttonTest, -1, -1));
        buttonTest.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dispose();
                main.setScreen(new AfterGameScreen(main, 1.1111f));
            }
        });

        buttonPlay = new TextButton(GameConfiguration.getText("playButton"),mySkin,"pixel72");
        buttonPlay.setSize(screenWidth / 3f,screenHeight / 7f);
        buttonPlay.setPosition(screenWidth / 2f - buttonPlay.getWidth() / 2,
                screenHeight / 2f);
        buttonPlay.setColor(Color.GREEN);
        buttonPlay.getLabel().setFontScale(GameConfiguration.fitText(buttonPlay, 72, 110));
        buttonPlay.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //buttonPressSound.play();
                if(GameConfiguration.firstTime) {
                    GameConfiguration.tutorialOn = true;
                    GameConfiguration.gameLevel = 1;
                    GameScreen gameScreen = GameConfiguration.createGame(main);
                    dispose();
                    main.setScreen(gameScreen);
                } else {
                    dispose();
                    main.setScreen(new LevelScreen(main));
                }
            }
        });

        Color buttonColors = new Color(1, 208 / 255f, 0, 1);

        buttonSettings = new TextButton(GameConfiguration.getText("settingsButton"),mySkin,"pixel72");
        buttonSettings.setSize(screenWidth / 4f,screenHeight / 8f);
        buttonSettings.setPosition(screenWidth / 2f - buttonSettings.getWidth() / 2f,
                buttonPlay.getY() - buttonSettings.getHeight() - screenHeight / 20f);
        buttonSettings.setColor(buttonColors);
        buttonSettings.getLabel().setFontScale(GameConfiguration.fitText(buttonSettings, -1, -1));
        buttonSettings.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //buttonPressSound.play();
                dispose();
                main.setScreen(new SettingsScreen(main));
            }
        });

        buttonHighScores = new TextButton(GameConfiguration.getText("highScores"),mySkin,"pixel72");
        buttonHighScores.setSize(screenWidth / 4f,screenHeight / 8f);
        buttonHighScores.setPosition(screenWidth / 2f - buttonHighScores.getWidth() / 2f,
                buttonSettings.getY() - buttonHighScores.getHeight() - screenHeight / 20f);
        buttonHighScores.setColor(buttonColors);
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

        stage.addActor(flagButton);
        stage.addActor(buttonPlay);
        stage.addActor(buttonSettings);
        stage.addActor(buttonHighScores);
        //stage.addActor(buttonTest);
        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void render(float deltaTime) {
        main.batch.setProjectionMatrix(camera.combined);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(backgroundImage, 0, 0, screenWidth, backgroundHeight);
        batch.draw(buttonBackground, screenWidth * 3 / 10f, screenHeight / 10f, screenWidth * 2 / 5f,
                screenWidth * 3 / 10f);
        batch.end();
        stage.draw();
        stage.act();
    }

    @Override
    public void dispose() {
        stage.dispose();
        camera = null;
        //buttonPressSound.dispose();
    }


    public void updateLanguage() {
        buttonPlay.setText(GameConfiguration.getText("playButton"));
        buttonSettings.setText(GameConfiguration.getText("settingsButton"));
        buttonHighScores.setText(GameConfiguration.getText("highScores"));
    }
}
