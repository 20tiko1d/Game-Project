package fi.tuni.tamk.LabyrinthOfLife;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class AfterGameScreen extends ScreenAdapter {

    private final Main main;

    private Stage stage;
    private OrthographicCamera camera;

    private final float score;

    private final float screenWidth;
    private final float screenHeight;

    private final Sound buttonPressSound;
    private Music levelCompletedMusic;

    private Textures textures;

    public AfterGameScreen(Main main, Textures textures, float score) {
        this.main = main;
        this.textures = textures;
        this.score = score;
        buttonPressSound = Sounds.buttonPressSound;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.viewPortWidth, Main.viewPortHeight);

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        GameConfiguration.sendHighScores((int) (score * 100));
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        levelCompletedMusic = Sounds.levelCompletedMusic;
        levelCompletedMusic.play();

        Skin mySkin = textures.mySkin;

        TextButton buttonMenu = new TextButton(GameConfiguration.getText("menu"), mySkin,"pixel72");
        buttonMenu.setSize(screenWidth / 10f,screenWidth / 10f);
        buttonMenu.setPosition(0,screenHeight - screenWidth / 10f);
        buttonMenu.setColor(Color.YELLOW);
        buttonMenu.getLabel().setFontScale(GameConfiguration.fitText(buttonMenu, - 1, -1));
        buttonMenu.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dispose();
                main.setScreen(new MenuScreen(main, textures));
            }
        });

        TextButton buttonPlayAgain = new TextButton(GameConfiguration.getText("playAgain"), mySkin,"pixel72");
        buttonPlayAgain.setSize(screenWidth / 5f,screenWidth / 8f);
        buttonPlayAgain.setPosition(screenWidth / 2f + 30,screenHeight / 3f);
        buttonPlayAgain.setColor(Color.GREEN);
        buttonPlayAgain.getLabel().setFontScale(GameConfiguration.fitText(buttonPlayAgain, 40,-1));
        buttonPlayAgain.getLabel().setWrap(true);
        buttonPlayAgain.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                GameScreen gameScreen = GameConfiguration.createGame(main, textures);
                dispose();
                main.setScreen(gameScreen);
            }
        });

        TextButton buttonLevelScreen = new TextButton(GameConfiguration.getText("levels"), mySkin,"pixel72");
        buttonLevelScreen.setSize(screenWidth / 5f,screenWidth / 8f);
        buttonLevelScreen.setPosition(screenWidth / 2f - buttonLevelScreen.getWidth() - 30,screenHeight / 3f);
        buttonLevelScreen.getLabel().setFontScale(GameConfiguration.fitText(buttonLevelScreen, - 1, -1));
        buttonLevelScreen.setColor(0 / 255f, 255 / 255f, 195 / 255f, 1);
        buttonLevelScreen.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dispose();
                main.setScreen(new LevelScreen(main, textures));
            }
        });

        TextButton themesButton = new TextButton(GameConfiguration.getText("themes"),mySkin,"pixel72");
        themesButton.setSize(screenWidth / 10f,screenWidth / 10f);
        themesButton.setPosition(screenWidth - themesButton.getWidth(),
                screenHeight - screenWidth / 10f);
        themesButton.setColor(56 / 255f, 114 / 255f, 207 / 255f, 1);
        themesButton.getLabel().setFontScale(GameConfiguration.fitText(themesButton, -1, -1));
        themesButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dispose();
                main.setScreen(new Themes(main, textures));
            }
        });

        TextButton buttonHighScores = new TextButton(GameConfiguration.getText("highScoresWrap"),mySkin,"pixel72");
        buttonHighScores.setSize(screenWidth / 10f,screenWidth / 10f);
        buttonHighScores.setPosition(screenWidth -buttonHighScores.getWidth(),
                0);
        buttonHighScores.setColor(180 / 255f, 90 / 255f, 230 / 255f, 1);
        buttonHighScores.getLabel().setFontScale(GameConfiguration.fitText(buttonHighScores, 40, -1));
        buttonHighScores.getLabel().setWrap(true);
        buttonHighScores.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dispose();
                main.setScreen(new HighScores(main, textures));
            }
        });

        Label scoreLabel = new Label( GameConfiguration.getText("score") + ": " + ((int) (score * 100)) / 100f, mySkin, "pixel100");
        float scoreLabelWidth = screenWidth / 2f;
        float scoreLabelHeight = screenHeight / 4f;


        scoreLabel.setBounds(scoreLabelWidth / 2, scoreLabelHeight * 3,
                scoreLabelWidth, scoreLabelHeight);
        scoreLabel.setColor(Color.BLACK);
        scoreLabel.setAlignment(Align.center);

        stage.addActor(buttonMenu);
        stage.addActor(buttonPlayAgain);
        stage.addActor(buttonLevelScreen);
        stage.addActor(themesButton);
        stage.addActor(scoreLabel);
        stage.addActor(buttonHighScores);

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
        levelCompletedMusic.stop();
    }
}
