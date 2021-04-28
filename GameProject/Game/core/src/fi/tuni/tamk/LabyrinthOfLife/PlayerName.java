package fi.tuni.tamk.LabyrinthOfLife;

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

    private final Main main;

    private Stage stage;
    private OrthographicCamera camera;

    private final int screenWidth = Gdx.graphics.getWidth();
    private final int screenHeight = Gdx.graphics.getHeight();

    private TextField textField;
    private Label errorLabel;

    private final boolean firstTime;

    private final Sound buttonPressSound;

    public PlayerName(Main main, boolean firstTime) {
        this.main = main;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.viewPortWidth, Main.viewPortHeight);
        this.firstTime = firstTime;
        buttonPressSound = Sounds.buttonPressSound;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        Skin mySkin = Textures.mySkin;

        Label boxBackground = new Label("", mySkin, "tutorialTest2");
        boxBackground.setSize(screenWidth / 2f, screenHeight / 2f);
        boxBackground.setPosition(screenWidth / 2f - boxBackground.getWidth() / 2f,
                screenHeight / 3f);

        Label label = new Label("", mySkin, "default");
        label.setSize(screenWidth / 3f, screenHeight / 7f);
        label.setPosition(screenWidth / 2f - label.getWidth() / 2f, boxBackground.getY() +
                boxBackground.getHeight() - label.getHeight());
        label.setText(GameConfiguration.getText("giveName"));
        label.setColor(Color.BLACK);

        textField = new TextField(GameConfiguration.open("name"), mySkin, "textFieldPixel");
        textField.setSize(label.getWidth(), label.getHeight());
        textField.setPosition(label.getX(), label.getY() - textField.getHeight());

        errorLabel = new Label("", mySkin, "default");
        errorLabel.setSize(label.getWidth(), label.getHeight() / 2);
        errorLabel.setPosition(label.getX(), textField.getY() - errorLabel.getHeight());
        errorLabel.setColor(Color.RED);

        TextButton buttonSave = new TextButton("save",mySkin,"pixel72");
        buttonSave.setSize(label.getWidth() / 2f,label.getHeight());
        buttonSave.setPosition(boxBackground.getX() + boxBackground.getWidth() -
                buttonSave.getWidth(), boxBackground.getY());
        buttonSave.setColor(Color.GREEN);
        buttonSave.getLabel().setFontScale(GameConfiguration.fitText(buttonSave, -1, -1));
        buttonSave.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(checkName()) {
                    dispose();
                    if(firstTime) {
                        main.setScreen(new MenuScreen(main));
                    } else {
                        main.setScreen(new HighScores(main));
                    }
                }
            }
        });

        if(firstTime) {
            buttonSave.setPosition(screenWidth / 2f - buttonSave.getWidth() / 2f, buttonSave.getY());
            textField.setText("");
        } else {
            TextButton buttonCancel = new TextButton("Cancel", mySkin,"pixel72");
            buttonCancel.setSize(buttonSave.getWidth(), buttonSave.getHeight());
            buttonCancel.setPosition(boxBackground.getX(), boxBackground.getY());
            buttonCancel.setColor(Color.RED);
            buttonCancel.getLabel().setFontScale(GameConfiguration.fitText(buttonCancel, -1, -1));
            buttonCancel.addListener(new InputListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    buttonPressSound.play();
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    dispose();
                    main.setScreen(new HighScores(main));
                }
            });
            stage.addActor(buttonCancel);
        }

        stage.addActor(boxBackground);
        stage.addActor(label);
        stage.addActor(textField);
        stage.addActor(errorLabel);
        stage.addActor(buttonSave);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float deltaTime) {
        main.batch.setProjectionMatrix(camera.combined);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(56 / 255f, 142 / 255f, 142 / 255f, 1);

        stage.draw();
        stage.act();
    }

    @Override
    public void dispose() {
        stage.dispose();
        camera = null;
    }

    public boolean checkName() {
        boolean allRight = true;
        String name = textField.getText();
        if(textField.getText().length() < 3 || textField.getText().length() > 16) {
            allRight = false;
            errorLabel.setText(GameConfiguration.getText("nameLengthError"));
        }

        for(char chr : name.toCharArray()) {
            if(Character.isWhitespace(chr)) {
                allRight = false;
                errorLabel.setText(GameConfiguration.getText("spacesError"));
            }
        }

        if(allRight) {
            GameConfiguration.save("name", name);
            return true;
        } else {
            return false;
        }
    }
}

