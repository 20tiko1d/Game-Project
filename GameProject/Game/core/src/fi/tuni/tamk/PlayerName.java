package fi.tuni.tamk;

import com.badlogic.gdx.Game;
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
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PlayerName extends ScreenAdapter {

    private Main main;

    private Stage stage;
    private OrthographicCamera camera;

    private final int screenWidth = Gdx.graphics.getWidth();
    private final int screenHeight = Gdx.graphics.getHeight();

    private TextField textField;

    private boolean firstTime;

    private Sound buttonPressSound;

    public PlayerName(Main main, boolean firstTime) {
        this.main = main;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.viewPortWidth, Main.viewPortHeight);
        this.firstTime = firstTime;
        //buttonPressSound = Sounds.buttonPressSound;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        Skin mySkin = Textures.mySkin;

        Label boxBackground = new Label("", mySkin, "tutorialTest2");
        boxBackground.setSize(screenWidth / 2f, screenHeight / 2f);
        boxBackground.setPosition(screenWidth / 2f - boxBackground.getWidth() / 2f,
                screenHeight / 3f);

        Label label = new Label("", mySkin);
        label.setSize(screenWidth / 3f, screenHeight / 7f);
        label.setPosition(screenWidth / 2f - label.getWidth() / 2f, boxBackground.getY() +
                boxBackground.getHeight() - label.getHeight());

        textField = new TextField(GameConfiguration.open("name"), mySkin);
        textField.setSize(label.getWidth(), label.getHeight());
        textField.setPosition(label.getX(), label.getY() - textField.getHeight());

        TextButton buttonSave = new TextButton("save",mySkin,"pixel72");
        buttonSave.setSize(label.getWidth() / 2f,label.getHeight());
        buttonSave.setPosition(screenWidth / 2f, textField.getY() - buttonSave.getHeight());
        buttonSave.setColor(Color.GREEN);
        buttonSave.getLabel().setFontScale(GameConfiguration.fitText(buttonSave, -1, -1));
        buttonSave.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //buttonPressSound.play();
                if(checkName()) {
                    dispose();
                    if(firstTime) {
                        main.setScreen(new MenuScreen(main));
                    } else {
                        main.setScreen(new HighScores(main));
                    }
                } else {
                    writeError();
                }
            }
        });

        stage.addActor(boxBackground);
        stage.addActor(label);

        if(firstTime) {
            buttonSave.setPosition(screenWidth / 2f - buttonSave.getWidth() / 2f, buttonSave.getY());
            textField.setText("");
        } else {
            TextButton buttonCancel = new TextButton("Cancel", mySkin,"pixel72");
            buttonCancel.setSize(buttonSave.getWidth(), buttonSave.getHeight());
            buttonCancel.setPosition(label.getX(), textField.getY() - buttonCancel.getHeight());
            buttonCancel.setColor(Color.GREEN);
            buttonCancel.getLabel().setFontScale(GameConfiguration.fitText(buttonCancel, -1, -1));
            buttonCancel.addListener(new InputListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    //buttonPressSound.play();
                    dispose();
                    main.setScreen(new HighScores(main));
                }
            });
            stage.addActor(buttonCancel);
        }


        stage.addActor(textField);
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

    public boolean checkName() {
        if(textField.getText().length() >= 3) {
            GameConfiguration.save("name", textField.getText());
            return true;
        } else {
            return false;
        }
    }

    public void writeError() {
        Gdx.app.log("", "error");
    }
}

