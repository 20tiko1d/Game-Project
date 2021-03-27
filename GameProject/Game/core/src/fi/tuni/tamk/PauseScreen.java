package fi.tuni.tamk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PauseScreen extends ScreenAdapter {
    private Main main;
    private GameScreen gameScreen;
    private PauseScreen pauseScreen;

    private Stage stage;
    private OrthographicCamera camera;

    public PauseScreen(Main main, GameScreen gameScreen) {
        this.main = main;
        this.gameScreen = gameScreen;
        this.pauseScreen = this;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.viewPortWidth, Main.viewPortHeight);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        float multiplier = 1;
        if(Main.isPortrait) {
            multiplier = 1.4f;
        }

        Skin mySkin = new Skin(Gdx.files.internal("skin/testi/testi3.json"));

        TextButton buttonReturnToGame = new TextButton(GameConfiguration.getText("continueGame"),mySkin,"default");
        buttonReturnToGame.setSize(Gdx.graphics.getWidth() / 4f,Gdx.graphics.getHeight() / (8f * multiplier));
        buttonReturnToGame.setPosition(Gdx.graphics.getWidth() / 2f - buttonReturnToGame.getWidth() / 2,
                Gdx.graphics.getHeight() / 2f);
        buttonReturnToGame.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dispose();
                main.setScreen(gameScreen);
            }
        });

        TextButton buttonSettings = new TextButton(GameConfiguration.getText("settingsButton"),mySkin,"default");
        buttonSettings.setSize(Gdx.graphics.getWidth() / 4f,Gdx.graphics.getHeight() / (8f * multiplier));
        buttonSettings.setPosition(Gdx.graphics.getWidth() / 2f - buttonSettings.getWidth() / 2f,
                buttonReturnToGame.getY() - buttonSettings.getHeight() - Gdx.graphics.getHeight() / 20f);
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

        TextButton buttonPlayAgain = new TextButton(GameConfiguration.getText("restart"),mySkin,"default");
        buttonPlayAgain.setSize(Gdx.graphics.getWidth() / 4f,Gdx.graphics.getHeight() / (8f * multiplier));
        buttonPlayAgain.setPosition(Gdx.graphics.getWidth() / 2f - buttonPlayAgain.getWidth() / 2f,
                buttonSettings.getY() - buttonPlayAgain.getHeight() - Gdx.graphics.getHeight() / 20f);
        buttonPlayAgain.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
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
