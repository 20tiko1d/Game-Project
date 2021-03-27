package fi.tuni.tamk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.logging.Level;

import javax.xml.soap.Text;

public class AfterGameScreen extends ScreenAdapter {

    private Main main;

    private Stage stage;
    private OrthographicCamera camera;

    private int score;

    public AfterGameScreen(Main main, int score) {
        this.main = main;
        this.score = score;
        addCredits();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.viewPortWidth, Main.viewPortHeight);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        Skin mySkin = new Skin(Gdx.files.internal("skin/testi/testi3.json"));

        Button buttonMenu = new TextButton(GameConfiguration.getText("menu"), mySkin,"default");
        buttonMenu.setSize(Gdx.graphics.getWidth() / 10f,Gdx.graphics.getWidth() / 10f);
        buttonMenu.setPosition(0,Gdx.graphics.getHeight() - Gdx.graphics.getWidth() / 10f);
        buttonMenu.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dispose();
                main.setScreen(new MenuScreen(main));
            }
        });
        stage.addActor(buttonMenu);

        Button buttonPlayAgain = new TextButton(GameConfiguration.getText("playAgain"), mySkin,"default");
        buttonPlayAgain.setSize(Gdx.graphics.getWidth() / 5f,Gdx.graphics.getWidth() / 8f);
        buttonPlayAgain.setPosition(Gdx.graphics.getWidth() / 2f + 30,Gdx.graphics.getHeight() / 3f);
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
        stage.addActor(buttonPlayAgain);

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
        stage.addActor(buttonLevelScreen);

        Label scoreLabel = new Label( GameConfiguration.getText("score") + ": " + score, mySkin, "default");
        float scoreLabelWidth = Gdx.graphics.getWidth() / 2f;
        float scoreLabelHeight = Gdx.graphics.getHeight() / 4f;


        scoreLabel.setBounds(scoreLabelWidth / 2, scoreLabelHeight * 3,
                scoreLabelWidth, scoreLabelHeight);
        scoreLabel.setFontScale(3);
        scoreLabel.setColor(Color.BLACK);
        stage.addActor(scoreLabel);

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

    public void addCredits() {
        int newCredits = score / 50 + 1;
        int oldCredits = GameConfiguration.credits;
        int sum = newCredits + oldCredits;
        GameConfiguration.credits = sum;
        String newCreditString = "" + sum;
        GameConfiguration.save(GameConfiguration.creditsString, newCreditString);
        Gdx.app.log("", newCreditString);
    }
}
