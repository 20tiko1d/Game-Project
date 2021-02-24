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

/**
 * The class contains and controls the Main menu screen.
 */
public class MenuScreen extends ScreenAdapter {

    private Main main;

    private Stage stage;
    private OrthographicCamera camera;

    public MenuScreen(Main main) {
        this.main = main;
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

        Skin mySkin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

        Button buttonPlay = new TextButton("PLAY",mySkin,"default");
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
                dispose();
                main.setScreen(new LevelScreen(main));
            }
        });
        stage.addActor(buttonPlay);

        Button buttonSettings = new TextButton("Settings",mySkin,"default");
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
                // settings.
            }
        });
        stage.addActor(buttonSettings);

        Button buttonPersonal = new TextButton("Profile",mySkin,"default");
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
                // Profile.
            }
        });
        stage.addActor(buttonPersonal);
        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void render(float deltaTime) {
        main.batch.setProjectionMatrix(camera.combined);
        Gdx.gl.glClearColor(0, 1, 0, 1);
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
