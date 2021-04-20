package fi.tuni.tamk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PlayerName extends ScreenAdapter {

    private Main main;

    private Stage stage;
    private OrthographicCamera camera;

    private final int screenWidth = Gdx.graphics.getWidth();
    private final int screenHeight = Gdx.graphics.getHeight();

    public PlayerName(Main main) {
        this.main = main;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.viewPortWidth, Main.viewPortHeight);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        Skin mySkin = Textures.mySkin;

        TextButton buttonSave = new TextButton("Score",mySkin,"pixel72");
        buttonSave.setSize(screenWidth / 10f,screenWidth / 10f);
        buttonSave.setPosition(0,screenHeight - screenWidth / 10f);
        buttonSave.setColor(Color.YELLOW);
        buttonSave.getLabel().setFontScale(GameConfiguration.fitText(buttonSave, -1, -1));
        buttonSave.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                try {
                    GameConfiguration.getHighScores();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        stage.addActor(buttonSave);
        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void render(float deltaTime) {
        main.batch.setProjectionMatrix(camera.combined);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(100 / 255f, 100 / 255f, 100 / 255f, 1);

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
        buttonHighScores.setText(GameConfiguration.getText("personalButton"));
    }
}

