package fi.tuni.tamk;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class AfterTutorialScreen extends ScreenAdapter {
    private Main main;

    private Stage stage;
    private OrthographicCamera camera;

    public AfterTutorialScreen(Main main) {
        this.main = main;
        GameConfiguration.tutorialOn = false;
        if(GameConfiguration.firstTime) {
            GameConfiguration.save("firstTime", "false");
        }
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.viewPortWidth, Main.viewPortHeight);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        Skin mySkin = new Skin(Gdx.files.internal("skin/testi/testi3.json"));

        Button buttonPlayAgain = new TextButton(GameConfiguration.getText("playTutorialAgain"), mySkin,"default");
        buttonPlayAgain.setSize(Gdx.graphics.getWidth() / 5f,Gdx.graphics.getWidth() / 8f);
        buttonPlayAgain.setPosition(Gdx.graphics.getWidth() / 2f + 30,Gdx.graphics.getHeight() / 3f);
        buttonPlayAgain.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                GameConfiguration.tutorialOn = true;
                GameConfiguration.gameLevel = 1;
                GameScreen gameScreen = GameConfiguration.createGame(main);
                dispose();
                main.setScreen(gameScreen);
            }
        });

        Button buttonLevelScreen = new TextButton(GameConfiguration.getText("levels"), mySkin,"default");
        buttonLevelScreen.setSize(Gdx.graphics.getWidth() / 5f,Gdx.graphics.getWidth() / 8f);
        buttonLevelScreen.setPosition(Gdx.graphics.getWidth() / 2f - buttonLevelScreen.getWidth() - 30,Gdx.graphics.getHeight() / 3f);
        buttonLevelScreen.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                main.setScreen(new LevelScreen(main));
            }
        });

        Label tutorialLabel = new Label( GameConfiguration.getText("tutorialFinished"), mySkin, "default");
        float tutorialLabelWidth = Gdx.graphics.getWidth() / 2f;
        float tutorialLabelHeight = Gdx.graphics.getHeight() / 4f;

        tutorialLabel.setBounds(tutorialLabelWidth / 2, tutorialLabelHeight * 3,
                tutorialLabelWidth, tutorialLabelHeight);
        tutorialLabel.setFontScale(3);
        tutorialLabel.setColor(Color.BLACK);

        stage.addActor(buttonLevelScreen);
        stage.addActor(buttonPlayAgain);
        stage.addActor(tutorialLabel);

        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void render(float deltaTime) {
        main.batch.setProjectionMatrix(camera.combined);
        Gdx.gl.glClearColor(1, 1, 1, 1);
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
