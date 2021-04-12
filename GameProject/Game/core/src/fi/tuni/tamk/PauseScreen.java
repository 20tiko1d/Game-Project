package fi.tuni.tamk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PauseScreen extends ScreenAdapter {
    private Main main;
    private GameScreen gameScreen;
    private PauseScreen pauseScreen;

    private float screenWidth;
    private float screenHeight;

    private Stage stage;
    private OrthographicCamera camera;

    private boolean tutorial = false;

    public PauseScreen(Main main, GameScreen gameScreen) {
        this.main = main;
        this.gameScreen = gameScreen;
        this.pauseScreen = this;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.viewPortWidth, Main.viewPortHeight);
        if(GameConfiguration.tutorialOn) {
            tutorial = true;
            GameConfiguration.tutorialOn = false;
        }
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        float multiplier = 1;
        if(Main.isPortrait) {
            multiplier = 1.4f;
        }

        Skin mySkin = Textures.mySkin;

        float width = Gdx.graphics.getWidth() / 3f;
        float height = Gdx.graphics.getHeight() / (6f * multiplier);
        float locX = Gdx.graphics.getWidth() / 2f - width / 2f;
        float startY = Gdx.graphics.getHeight() * 4 / 6f;
        float buttonsGap = height / 6f;

        Label pauseLabel = new Label(GameConfiguration.getText("gamePaused"), mySkin);
        pauseLabel.setBounds(locX, startY + height + buttonsGap, width, height);
        pauseLabel.setFontScale(3);

        final Drawable drawable = new TextureRegionDrawable(new TextureRegion(Textures.pauseButtonTexture));
        final Button buttonReturnToGame = new Button(drawable);
        buttonReturnToGame.setSize(screenWidth / 10f,screenWidth / 10f);
        buttonReturnToGame.setPosition(screenWidth * 9 / 10f,screenHeight - screenWidth / 10f);
        buttonReturnToGame.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dispose();
                if(tutorial) {
                    GameConfiguration.tutorialOn = true;
                }
                main.setScreen(gameScreen);
            }
        });

        TextButton buttonSettings = new TextButton(GameConfiguration.getText("settingsButton"),mySkin,"default");
        buttonSettings.setSize(width,height);
        buttonSettings.setPosition(locX, startY - height - buttonsGap);
        buttonSettings.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                main.setScreen(new SettingsScreen(main, pauseScreen, gameScreen));
            }
        });

        TextButton buttonLevels = new TextButton(GameConfiguration.getText("levels"),mySkin,"default");
        buttonLevels.setSize(width,height);
        buttonLevels.setPosition(locX, startY - 2 * height - 2 * buttonsGap);
        buttonLevels.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(!GameConfiguration.firstTime) {
                    gameScreen.dispose();
                    dispose();
                    main.setScreen(new LevelScreen(main));
                }
            }
        });

        TextButton buttonPlayAgain = new TextButton(GameConfiguration.getText("restart"),mySkin,"default");
        buttonPlayAgain.setSize(width,height);
        buttonPlayAgain.setPosition(locX, startY - 3 * height - 3 * buttonsGap);
        buttonPlayAgain.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(tutorial) {
                    GameConfiguration.tutorialOn = true;
                }
                gameScreen.dispose();
                GameScreen gameScreen = GameConfiguration.createGame(main);
                main.setScreen(gameScreen);
            }
        });

        Button buttonMenu = new TextButton(GameConfiguration.getText("menu"),mySkin,"default");
        buttonMenu.setSize(Gdx.graphics.getWidth() / 10f,Gdx.graphics.getWidth() / 10f);
        buttonMenu.setPosition(0,Gdx.graphics.getHeight() - Gdx.graphics.getWidth() / 10f);
        buttonMenu.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                gameScreen.dispose();
                dispose();
                main.setScreen(new MenuScreen(main));
            }
        });

        stage.addActor(pauseLabel);
        stage.addActor(buttonLevels);
        stage.addActor(buttonMenu);
        stage.addActor(buttonPlayAgain);
        stage.addActor(buttonReturnToGame);
        stage.addActor(buttonSettings);

        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void render(float deltaTime) {
        main.batch.setProjectionMatrix(camera.combined);
        Gdx.gl.glClearColor(0, 255, 234, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
        stage.act();
    }

    @Override
    public void dispose() {
        stage.dispose();
        camera = null;
    }

}
