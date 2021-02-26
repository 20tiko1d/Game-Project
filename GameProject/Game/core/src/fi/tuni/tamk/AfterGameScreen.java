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

import javax.xml.soap.Text;

public class AfterGameScreen extends ScreenAdapter {

    private Main main;

    private Stage stage;
    private OrthographicCamera camera;

    private int score;

    public AfterGameScreen(Main main, int score) {
        this.main = main;
        this.score = score;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.viewPortWidth, Main.viewPortHeight);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        Skin mySkin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

        Button buttonMenu = new TextButton("Menu",mySkin,"default");
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

        Label scoreLabel = new Label("Score: " + score, mySkin, "big");
        float scoreLabelWidth = Gdx.graphics.getWidth() / 2f;
        float scoreLabelHeight = Gdx.graphics.getHeight() / 4f;


        scoreLabel.setBounds(scoreLabelWidth / 2, scoreLabelHeight * 3,
                scoreLabelWidth, scoreLabelHeight);
        scoreLabel.setFontScale(3);
        scoreLabel.setColor(Color.BLACK);
        stage.addActor(scoreLabel);

        TextArea scoreArea = new TextArea("Score: ", mySkin);
        scoreArea.setBounds(scoreLabelWidth / 2, scoreLabelHeight * 3,
                scoreLabelWidth, scoreLabelHeight);
        //stage.addActor(scoreArea);

        //Text scoreText = new Text("Score: " + score, mySkin);

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
