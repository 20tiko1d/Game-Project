package fi.tuni.tamk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
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
    private TextButton buttonPersonal;

    private Texture backgroundImage;
    private Texture buttonBackground;
    private float backgroundHeight;

    private final int screenWidth = Gdx.graphics.getWidth();
    private final int screenHeight = Gdx.graphics.getHeight();

    public MenuScreen(Main main) {
        this.main = main;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.viewPortWidth, Main.viewPortHeight);
        batch = new SpriteBatch();
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        backgroundImage = Textures.getMenuBackground();
        buttonBackground = Textures.menuButtonBackground;
        backgroundHeight = (float) screenWidth * backgroundImage.getHeight() / backgroundImage.getWidth();

        Skin mySkin = Textures.mySkin;

        Texture flagTexture = Textures.engFlag;
        if(GameConfiguration.open("language").equals("en_GB")) {
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
                if(GameConfiguration.open("language").equals("fi_FI")) {
                    flagButton.getStyle().up = new TextureRegionDrawable(new TextureRegion(Textures.finFlag));
                    GameConfiguration.save("language", "en_GB");
                } else {
                    flagButton.getStyle().up = new TextureRegionDrawable(new TextureRegion(Textures.engFlag));
                    GameConfiguration.save("language", "fi_FI");
                }
                updateLanguage();
            }
        });



        buttonPlay = new TextButton(GameConfiguration.getText("playButton"),mySkin,"pixel48");
        buttonPlay.setSize(screenWidth / 3f,screenHeight / 7f);
        buttonPlay.setPosition(screenWidth / 2f - buttonPlay.getWidth() / 2,
                screenHeight / 2f);
        buttonPlay.setColor(Color.GREEN);
        buttonPlay.getLabel().setFontScale(2);
        buttonPlay.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
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

        buttonSettings = new TextButton(GameConfiguration.getText("settingsButton"),mySkin,"pixel48");
        buttonSettings.setSize(screenWidth / 4f,screenHeight / 8f);
        buttonSettings.setPosition(screenWidth / 2f - buttonSettings.getWidth() / 2f,
                buttonPlay.getY() - buttonSettings.getHeight() - screenHeight / 20f);
        buttonSettings.setColor(buttonColors);
        buttonSettings.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dispose();
                main.setScreen(new SettingsScreen(main));
            }
        });

        buttonPersonal = new TextButton(GameConfiguration.getText("personalButton"),mySkin,"pixel48");
        buttonPersonal.setSize(screenWidth / 4f,screenHeight / 8f);
        buttonPersonal.setPosition(screenWidth / 2f - buttonPersonal.getWidth() / 2f,
                buttonSettings.getY() - buttonPersonal.getHeight() - screenHeight / 20f);
        buttonPersonal.setColor(buttonColors);
        buttonPersonal.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                // Player profile?
            }
        });

        stage.addActor(flagButton);
        stage.addActor(buttonPlay);
        stage.addActor(buttonSettings);
        stage.addActor(buttonPersonal);
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
    }


    public void updateLanguage() {
        buttonPlay.setText(GameConfiguration.getText("playButton"));
        buttonSettings.setText(GameConfiguration.getText("settingsButton"));
        buttonPersonal.setText(GameConfiguration.getText("personalButton"));
    }

}
