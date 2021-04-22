package fi.tuni.tamk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * The class contains the screen and all the features of the Settings.
 */
public class SettingsScreen extends ScreenAdapter {

    private Main main;
    private PauseScreen pauseScreen;
    private GameScreen gameScreen;

    private OrthographicCamera camera;
    private Stage stage;

    private Sound buttonPressSound;

    public SettingsScreen(Main main) {
        this.main = main;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.viewPortWidth, Main.viewPortHeight);
        //buttonPressSound = Sounds.buttonPressSound;
    }

    public SettingsScreen(Main main, PauseScreen pauseScreen, GameScreen gameScreen) {
        this.main = main;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.viewPortWidth, Main.viewPortHeight);
        this.pauseScreen = pauseScreen;
        this.gameScreen = gameScreen;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        Skin mySkin = Textures.mySkin;

        Button buttonMenu = new TextButton(GameConfiguration.getText("menu"),mySkin,"default");
        buttonMenu.setSize(Gdx.graphics.getWidth() / 10f,Gdx.graphics.getWidth() / 10f);
        buttonMenu.setPosition(0,Gdx.graphics.getHeight() - Gdx.graphics.getWidth() / 10f);
        buttonMenu.setColor(Color.YELLOW);
        buttonMenu.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //buttonPressSound.play();
                if(pauseScreen != null) {
                    pauseScreen.dispose();
                    gameScreen.dispose();
                }
                dispose();
                main.setScreen(new MenuScreen(main));
            }
        });

        if(pauseScreen != null) {
            Button returnToGame = new TextButton(GameConfiguration.getText("back"),mySkin,"default");
            returnToGame.setSize(Gdx.graphics.getWidth() / 10f,Gdx.graphics.getWidth() / 10f);
            returnToGame.setPosition(Gdx.graphics.getWidth() - returnToGame.getWidth(),
                    Gdx.graphics.getHeight() - Gdx.graphics.getWidth() / 10f);
            returnToGame.addListener(new InputListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    //buttonPressSound.play();
                    dispose();
                    main.setScreen(pauseScreen);
                }
            });
            stage.addActor(returnToGame);
        }

        stage.addActor(buttonMenu);

        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void render(float deltaTime) {
        main.batch.setProjectionMatrix(camera.combined);
        Gdx.gl.glClearColor(56 / 255f, 64 / 255f, 61 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
        stage.act();
    }

    @Override
    public void dispose() {
        stage.dispose();
        camera = null;
        //buttonPressSound.dispose();
    }
}
