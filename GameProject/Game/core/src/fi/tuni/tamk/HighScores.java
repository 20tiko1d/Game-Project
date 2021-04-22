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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * The class contains all of the player's personal achievements and the profile.
 */
public class HighScores extends ScreenAdapter {

    private Main main;

    private final int screenWidth = Gdx.graphics.getWidth();
    private final int screenHeight = Gdx.graphics.getHeight();

    private OrthographicCamera camera;
    private Stage stage;

    private Sound buttonPressSound;

    public HighScores(Main main) {
        this.main = main;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.viewPortWidth, Main.viewPortHeight);
        //buttonPressSound = Sounds.buttonPressSound;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        Skin mySkin = Textures.mySkin;

        TextButton buttonMenu = new TextButton(GameConfiguration.getText("menu"),mySkin,"pixel72");
        buttonMenu.setSize(screenWidth / 10f,screenWidth / 10f);
        buttonMenu.setPosition(0,Gdx.graphics.getHeight() - screenWidth / 10f);
        buttonMenu.setColor(Color.YELLOW);
        buttonMenu.getLabel().setFontScale(GameConfiguration.fitText(buttonMenu, -1, -1));
        buttonMenu.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //buttonPressSound.play();
                dispose();
                main.setScreen(new MenuScreen(main));
            }
        });

        TextButton buttonLevels = new TextButton(GameConfiguration.getText("levels"),mySkin,"pixel72");
        buttonLevels.setSize(screenWidth / 10f,screenWidth / 10f);
        buttonLevels.setPosition(screenWidth - buttonLevels.getWidth(), 0);
        buttonLevels.setColor(0 / 255f, 255 / 255f, 195 / 255f, 1);
        buttonLevels.getLabel().setFontScale(GameConfiguration.fitText(buttonLevels, -1, -1));
        buttonLevels.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //buttonPressSound.play();
                if(!GameConfiguration.firstTime) {
                    dispose();
                    main.setScreen(new LevelScreen(main));
                }
            }
        });

        Label nameLabel = new Label("    " + GameConfiguration.open("name"), mySkin, "objectLabel");
        nameLabel.setSize(screenWidth / 2f, screenHeight / 7f);
        nameLabel.setPosition(screenWidth / 2f - nameLabel.getWidth() / 2f, screenHeight - nameLabel.getHeight());

        TextButton buttonRename = new TextButton("Edit",mySkin,"pixel72");
        buttonRename.setSize(nameLabel.getHeight(),nameLabel.getHeight());
        buttonRename.setPosition(nameLabel.getX() + nameLabel.getWidth() - buttonRename.getWidth(),
                nameLabel.getY());
        buttonRename.setColor(0 / 255f, 255 / 255f, 195 / 255f, 1);
        buttonRename.getLabel().setFontScale(GameConfiguration.fitText(buttonRename, -1, -1));
        buttonRename.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //buttonPressSound.play();
                dispose();
                main.setScreen(new PlayerName(main, false));
            }
        });

        stage.addActor(buttonMenu);
        stage.addActor(buttonLevels);
        stage.addActor(nameLabel);
        stage.addActor(buttonRename);
        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void render(float deltaTime) {
        main.batch.setProjectionMatrix(camera.combined);
        Gdx.gl.glClearColor(0, 100 / 255f, 0, 1);
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
