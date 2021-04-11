package fi.tuni.tamk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
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

    public MenuScreen(Main main) {
        this.main = main;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.viewPortWidth, Main.viewPortHeight);
        batch = new SpriteBatch();
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        float multiplier = 1;
        if(Main.isPortrait) {
            multiplier = 1.4f;
        }
        Texture flagTexture = Textures.engFlag;
        if(GameConfiguration.open("language").equals("en_GB")) {
            flagTexture = Textures.finFlag;
        }
        final Drawable drawable = new TextureRegionDrawable(new TextureRegion(flagTexture));
        final Button flagButton = new Button(drawable);
        flagButton.setSize(Gdx.graphics.getWidth() / 8f, Gdx.graphics.getWidth() / 16f);
        flagButton.setPosition(Gdx.graphics.getWidth() - flagButton.getWidth() - 50,
                Gdx.graphics.getHeight() - flagButton.getHeight() - 50);
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
        stage.addActor(flagButton);

        backgroundImage = Textures.getMenuBackground();

        Skin mySkin = new Skin(Gdx.files.internal("skin/testi/testi6.json"));

        buttonPlay = new TextButton(GameConfiguration.getText("playButton"),mySkin,"defaultBig");
        buttonPlay.setSize(Gdx.graphics.getWidth() * multiplier / 3f,(Gdx.graphics.getHeight() / 7f) / multiplier);
        buttonPlay.setPosition(Gdx.graphics.getWidth() / 2f - buttonPlay.getWidth() / 2,
                Gdx.graphics.getHeight() / 2f);
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
        stage.addActor(buttonPlay);

        buttonSettings = new TextButton(GameConfiguration.getText("settingsButton"),mySkin,"default");
        buttonSettings.setSize(Gdx.graphics.getWidth() / 4f,Gdx.graphics.getHeight() / (8f * multiplier));
        buttonSettings.setPosition(Gdx.graphics.getWidth() / 2f - buttonSettings.getWidth() / 2f,
                buttonPlay.getY() - buttonSettings.getHeight() - Gdx.graphics.getHeight() / 20f);
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
        stage.addActor(buttonSettings);

        buttonPersonal = new TextButton(GameConfiguration.getText("personalButton"),mySkin,"default");
        buttonPersonal.setSize(Gdx.graphics.getWidth() / 4f,Gdx.graphics.getHeight() / (8f * multiplier));
        buttonPersonal.setPosition(Gdx.graphics.getWidth() / 2f - buttonPersonal.getWidth() / 2f,
                buttonSettings.getY() - buttonPersonal.getHeight() - Gdx.graphics.getHeight() / 20f);
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
        stage.addActor(buttonPersonal);
        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void render(float deltaTime) {
        main.batch.setProjectionMatrix(camera.combined);
        //Gdx.gl.glClearColor(0, 255, 234, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(backgroundImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
