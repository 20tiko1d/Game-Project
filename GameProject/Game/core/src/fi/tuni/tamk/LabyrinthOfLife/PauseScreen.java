package fi.tuni.tamk.LabyrinthOfLife;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Screen for pausing the game.
 */
public class PauseScreen extends ScreenAdapter {
    private final Main main;
    private final GameScreen gameScreen;
    private final PauseScreen pauseScreen;

    private final float screenWidth;
    private final float screenHeight;

    private Stage stage;

    private boolean tutorial = false;

    private final Sound buttonPressSound;
    private final Textures textures;

    /**
     * Pause screen constructor.
     *
     * @param main: Game class object.
     * @param gameScreen: Game screen class object.
     */
    public PauseScreen(Main main, GameScreen gameScreen) {
        this.main = main;
        this.textures = main.textures;
        this.gameScreen = gameScreen;
        this.pauseScreen = this;
        if(GameConfiguration.tutorialOn) {
            tutorial = true;
            GameConfiguration.tutorialOn = false;
        }
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        buttonPressSound = main.sounds.buttonPressSound;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        Skin mySkin = textures.mySkin;

        float width = Gdx.graphics.getWidth() / 3f;
        float height = Gdx.graphics.getHeight() / 6f;
        float locX = Gdx.graphics.getWidth() / 2f - width / 2f;
        float startY = Gdx.graphics.getHeight() * 4 / 6f;
        float buttonsGap = height / 6f;

        Label pauseLabel = new Label(GameConfiguration.getText("gamePaused").toUpperCase(), mySkin, "pixel100");
        pauseLabel.setBounds(locX, screenHeight - height * 1.5f, width, height);
        pauseLabel.setAlignment(Align.center);
        pauseLabel.setColor(Color.BLACK);

        final Drawable drawable = new TextureRegionDrawable(new TextureRegion(textures.playButtonTexture));
        final Button buttonReturnToGame = new Button(drawable);
        buttonReturnToGame.setSize(screenWidth / 10f,screenWidth / 10f);
        buttonReturnToGame.setPosition(screenWidth * 9 / 10f,screenHeight - screenWidth / 10f);
        buttonReturnToGame.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dispose();
                if(tutorial) {
                    GameConfiguration.tutorialOn = true;
                }
                main.setScreen(gameScreen);
            }
        });

        TextButton buttonSettings = new TextButton(GameConfiguration.getText("settingsButton"),mySkin,"pixel48");
        buttonSettings.setSize(width,height);
        buttonSettings.setPosition(locX, startY - height - buttonsGap);
        buttonSettings.setColor(1, 208 / 255f, 0, 1);
        buttonSettings.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                main.setScreen(new SettingsScreen(main, textures, pauseScreen, gameScreen));
            }
        });

        TextButton buttonLevels = new TextButton(GameConfiguration.getText("levels"),mySkin,"pixel48");
        buttonLevels.setSize(width,height);
        buttonLevels.setPosition(locX, startY - 2 * height - 2 * buttonsGap);
        buttonLevels.setColor(0 / 255f, 255 / 255f, 195 / 255f, 1);
        buttonLevels.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(!GameConfiguration.firstTime) {
                    gameScreen.dispose();
                    dispose();
                    main.setScreen(new LevelScreen(main));
                }
            }
        });

        TextButton buttonPlayAgain = new TextButton(GameConfiguration.getText("restart"),mySkin,"pixel48");
        buttonPlayAgain.setSize(width,height);
        buttonPlayAgain.setPosition(locX, startY - 3 * height - 3 * buttonsGap);
        buttonPlayAgain.setColor(Color.GREEN);
        buttonPlayAgain.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(tutorial) {
                    GameConfiguration.tutorialOn = true;
                }
                gameScreen.dispose();
                GameScreen gameScreen = GameConfiguration.createGame(main);
                main.setScreen(gameScreen);
            }
        });

        TextButton buttonMenu = new TextButton(GameConfiguration.getText("menu"),mySkin,"pixel48");
        buttonMenu.setSize(Gdx.graphics.getWidth() / 10f,Gdx.graphics.getWidth() / 10f);
        buttonMenu.setPosition(0,Gdx.graphics.getHeight() - Gdx.graphics.getWidth() / 10f);
        buttonMenu.setColor(Color.YELLOW);
        buttonMenu.getLabel().setFontScale(GameConfiguration.fitText(buttonMenu, -1, -1));
        buttonMenu.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                gameScreen.dispose();
                dispose();
                main.setScreen(new MenuScreen(main));
            }
        });

        stage.addActor(pauseLabel);
        stage.addActor(buttonLevels);
        stage.addActor(buttonMenu);
        stage.addActor(buttonPlayAgain);
        stage.addActor(buttonReturnToGame);
        stage.addActor(buttonSettings);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClearColor(56 / 255f, 142 / 255f, 142 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
        stage.act();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
