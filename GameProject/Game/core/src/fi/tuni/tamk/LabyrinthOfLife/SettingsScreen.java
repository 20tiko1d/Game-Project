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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * The class contains the screen and all the features of the Settings.
 */
public class SettingsScreen extends ScreenAdapter {

    private final Main main;
    private PauseScreen pauseScreen;
    private GameScreen gameScreen;

    private OrthographicCamera camera;
    private Stage stage;

    private final float screenWidth = Gdx.graphics.getWidth();
    private final float screenHeight = Gdx.graphics.getHeight();

    private float leftX;
    private float rightX;

    private Image boostImage;
    private Image joystickImage;

    private Sound buttonPressSound;

    private Label musicVolumeLabel;

    public SettingsScreen(Main main) {
        this.main = main;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.viewPortWidth, Main.viewPortHeight);
    }

    public SettingsScreen(Main main, PauseScreen pauseScreen, GameScreen gameScreen) {
        this.main = main;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.viewPortWidth, Main.viewPortHeight);
        this.pauseScreen = pauseScreen;
        this.gameScreen = gameScreen;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        buttonPressSound = Sounds.buttonPressSound;
        Skin mySkin = Textures.mySkin;

        TextButton buttonMenu = new TextButton(GameConfiguration.getText("menu"),mySkin,"pixel72");
        buttonMenu.setSize(screenWidth / 10f,screenWidth / 10f);
        buttonMenu.setPosition(0,screenHeight - buttonMenu.getHeight());
        buttonMenu.getLabel().setFontScale(GameConfiguration.fitText(buttonMenu, -1, -1));
        buttonMenu.setColor(Color.YELLOW);
        buttonMenu.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(pauseScreen != null) {
                    pauseScreen.dispose();
                    gameScreen.dispose();
                }
                dispose();
                main.setScreen(new MenuScreen(main));
            }
        });

        Image controlsBackground = new Image(Textures.controlsBackground);
        controlsBackground.setSize(screenWidth / 3, screenWidth / 6);
        controlsBackground.setPosition(screenWidth / 20, screenWidth / 20);

        TextButton buttonInvert = new TextButton(GameConfiguration.getText("switchButton"),mySkin,"pixel72");
        buttonInvert.setSize(screenWidth / 6f,screenWidth / 12f);
        buttonInvert.setPosition(controlsBackground.getX() + controlsBackground.getWidth() / 2 -
                buttonInvert.getWidth() / 2,controlsBackground.getY() + controlsBackground.getHeight() + screenHeight / 20);
        buttonInvert.setColor(103 / 255f, 40 / 255f, 229 / 255f, 1);
        buttonInvert.getLabel().setFontScale(GameConfiguration.fitText(buttonInvert, -1, -1));
        buttonInvert.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                GameConfiguration.invert();
                setControlLocations();
            }
        });


        float controlSize = controlsBackground.getWidth() / 8;

        leftX = controlsBackground.getX() + controlsBackground.getWidth() / 16;
        rightX = controlsBackground.getX() + controlsBackground.getWidth() * 13 / 16;

        boostImage = new Image(Textures.boostTexture);
        boostImage.setSize(controlSize, controlSize);
        boostImage.setY(controlsBackground.getY() + controlsBackground.getWidth() / 16);

        joystickImage = new Image(Textures.joystickTexture);
        joystickImage.setSize(controlSize, controlSize);
        joystickImage.setY(boostImage.getY());

        setControlLocations();

        if(pauseScreen != null) {
            TextButton returnToGame = new TextButton(GameConfiguration.getText("back"),mySkin,"pixel72");
            returnToGame.setSize(screenWidth / 10f,screenWidth / 10f);
            returnToGame.setPosition(screenWidth - returnToGame.getWidth(),
                    screenHeight - returnToGame.getHeight());
            returnToGame.getLabel().setFontScale(GameConfiguration.fitText(returnToGame, -1, -1));
            returnToGame.setColor(Color.RED);
            returnToGame.addListener(new InputListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    buttonPressSound.play();
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    dispose();
                    main.setScreen(pauseScreen);
                }
            });
            stage.addActor(returnToGame);
        }

        musicVolumeLabel = new Label("", mySkin, "pixel50");
        musicVolumeLabel.setSize(screenWidth / 2, screenWidth / 10);
        musicVolumeLabel.setPosition(screenWidth / 2f, screenHeight / 2f);
        musicVolumeLabel.setAlignment(Align.center);
        musicVolumeLabel.setColor(Color.BLACK);
        musicVolumeLabel.setFontScale(0.5f);
        try {
            changeMusicVolume(Integer.parseInt(GameConfiguration.open("musicVolume")));
        } catch (Exception e) {}


        final Slider musicVolumeSlider = new Slider(0, 1, 0.1f, false, mySkin);
        musicVolumeSlider.setSize(musicVolumeLabel.getWidth() * 3 / 4, screenWidth / 20);
        musicVolumeSlider.setPosition(musicVolumeLabel.getX() + musicVolumeLabel.getWidth() / 2 -
                musicVolumeSlider.getWidth() / 2, musicVolumeLabel.getY() -
                musicVolumeSlider.getHeight());
        musicVolumeSlider.getStyle().knob.setMinWidth(screenWidth / 20);
        musicVolumeSlider.getStyle().knob.setMinHeight(screenWidth / 20);
        musicVolumeSlider.getStyle().background.setMinHeight(screenWidth / 30);
        try {
            musicVolumeSlider.setValue(Integer.parseInt(GameConfiguration.open("musicVolume")) / 10f);
        } catch (Exception e) {}

        musicVolumeSlider.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                int volume = (int) (musicVolumeSlider.getValue() * 10);
                String volumeString = "" + volume;
                GameConfiguration.save("musicVolume", volumeString);
                changeMusicVolume(volume);
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                int volume = (int) (musicVolumeSlider.getValue() * 10);
                String volumeString = "" + volume;
                GameConfiguration.save("musicVolume", volumeString);
                changeMusicVolume(volume);
            }
        });

        stage.addActor(buttonMenu);
        stage.addActor(buttonInvert);
        stage.addActor(controlsBackground);
        stage.addActor(boostImage);
        stage.addActor(joystickImage);
        stage.addActor(musicVolumeLabel);
        stage.addActor(musicVolumeSlider);

        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void render(float deltaTime) {
        main.batch.setProjectionMatrix(camera.combined);
        Gdx.gl.glClearColor(56 / 255f, 142 / 255f, 142 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
        stage.act();
    }

    @Override
    public void dispose() {
        stage.dispose();
        camera = null;
    }

    public void setControlLocations() {
        if(GameConfiguration.open("controls").equals("default")) {
            boostImage.setX(leftX);
            joystickImage.setX(rightX);
        } else {
            boostImage.setX(rightX);
            joystickImage.setX(leftX);
        }
    }

    public void changeMusicVolume(int volume) {
        String text = GameConfiguration.getText("musicVolume").toUpperCase();
        String volumeText = ":  " + volume;
        musicVolumeLabel.setText(text + volumeText);
    }
}
