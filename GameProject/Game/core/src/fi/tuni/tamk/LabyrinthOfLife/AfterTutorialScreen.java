package fi.tuni.tamk.LabyrinthOfLife;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Screen for after completing the tutorial.
 *
 * @author Artur Haavisto
 */
public class AfterTutorialScreen extends ScreenAdapter {
    private final Main main;

    private Stage stage;

    private final float screenWidth;
    private final float screenHeight;

    private final Sound buttonPressSound;
    private Music levelCompletedMusic;

    private final Textures textures;

    /**
     * After the tutorial screen constructor.
     *
     * @param main: Game class object.
     */
    public AfterTutorialScreen(Main main) {
        this.main = main;
        this.textures = main.textures;
        GameConfiguration.tutorialOn = false;
        if(GameConfiguration.firstTime) {
            GameConfiguration.save("firstTime", "false");
            GameConfiguration.firstTime = false;
        }
        buttonPressSound = main.sounds.buttonPressSound;
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        levelCompletedMusic = main.sounds.levelCompletedMusic;
        levelCompletedMusic.play();

        Skin mySkin = textures.mySkin;

        TextButton buttonPlayAgain = new TextButton(GameConfiguration.getText("playTutorialAgain"), mySkin,"pixel48");
        buttonPlayAgain.setSize(screenWidth / 5f,screenWidth / 8f);
        buttonPlayAgain.setPosition(screenWidth / 2f + 30,screenHeight / 3f);
        buttonPlayAgain.setColor(Color.GREEN);
        buttonPlayAgain.getLabel().setWrap(true);
        buttonPlayAgain.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                GameConfiguration.tutorialOn = true;
                GameConfiguration.gameLevel = 1;
                GameScreen gameScreen = GameConfiguration.createGame(main);
                dispose();
                main.setScreen(gameScreen);
            }
        });

        TextButton buttonLevelScreen = new TextButton(GameConfiguration.getText("levels"), mySkin,"pixel72");
        buttonLevelScreen.setSize(screenWidth / 5f,screenWidth / 8f);
        buttonLevelScreen.setPosition(screenWidth / 2f - buttonLevelScreen.getWidth() - 30,screenHeight / 3f);
        buttonLevelScreen.setColor(0 / 255f, 255 / 255f, 195 / 255f, 1);
        buttonLevelScreen.getLabel().setFontScale(GameConfiguration.fitText(buttonLevelScreen, -1, -1));
        buttonLevelScreen.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dispose();
                main.setScreen(new LevelScreen(main));
            }
        });

        Label tutorialLabel = new Label(GameConfiguration.getText("tutorialFinished"), mySkin, "pixel100");
        tutorialLabel.setSize(screenWidth / 2f, screenWidth / 8);

        tutorialLabel.setPosition(screenWidth / 2 - tutorialLabel.getWidth() / 2,
                screenHeight * 3 / 4);
        tutorialLabel.setColor(Color.BLACK);
        tutorialLabel.setAlignment(Align.center);

        stage.addActor(buttonLevelScreen);
        stage.addActor(buttonPlayAgain);
        stage.addActor(tutorialLabel);

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
        levelCompletedMusic.stop();
        stage.dispose();
    }
}
