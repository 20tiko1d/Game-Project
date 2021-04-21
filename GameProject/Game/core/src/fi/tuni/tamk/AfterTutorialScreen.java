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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class AfterTutorialScreen extends ScreenAdapter {
    private Main main;

    private Stage stage;
    private OrthographicCamera camera;

    private Sound buttonPressSound;

    public AfterTutorialScreen(Main main) {
        this.main = main;
        GameConfiguration.tutorialOn = false;
        if(GameConfiguration.firstTime) {
            GameConfiguration.save("firstTime", "false");
            GameConfiguration.firstTime = false;
        }
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.viewPortWidth, Main.viewPortHeight);
        //buttonPressSound = Sounds.buttonPressSound;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        Skin mySkin = Textures.mySkin;

        TextButton buttonPlayAgain = new TextButton(GameConfiguration.getText("playTutorialAgain"), mySkin,"pixel48");
        buttonPlayAgain.setSize(Gdx.graphics.getWidth() / 5f,Gdx.graphics.getWidth() / 8f);
        buttonPlayAgain.setPosition(Gdx.graphics.getWidth() / 2f + 30,Gdx.graphics.getHeight() / 3f);
        buttonPlayAgain.setColor(Color.GREEN);
        buttonPlayAgain.getLabel().setWrap(true);
        buttonPlayAgain.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //buttonPressSound.play();
                GameConfiguration.tutorialOn = true;
                GameConfiguration.gameLevel = 1;
                GameScreen gameScreen = GameConfiguration.createGame(main);
                dispose();
                main.setScreen(gameScreen);
            }
        });

        TextButton buttonLevelScreen = new TextButton(GameConfiguration.getText("levels"), mySkin,"pixel72");
        buttonLevelScreen.setSize(Gdx.graphics.getWidth() / 5f,Gdx.graphics.getWidth() / 8f);
        buttonLevelScreen.setPosition(Gdx.graphics.getWidth() / 2f - buttonLevelScreen.getWidth() - 30,Gdx.graphics.getHeight() / 3f);
        buttonLevelScreen.setColor(0 / 255f, 255 / 255f, 195 / 255f, 1);
        buttonLevelScreen.getLabel().setFontScale(GameConfiguration.fitText(buttonLevelScreen, -1, -1));
        buttonLevelScreen.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //buttonPressSound.play();
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
