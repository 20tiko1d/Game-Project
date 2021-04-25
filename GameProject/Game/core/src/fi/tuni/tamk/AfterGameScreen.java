package fi.tuni.tamk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class AfterGameScreen extends ScreenAdapter {

    private Main main;

    private Stage stage;
    private OrthographicCamera camera;

    private float score;

    private Sound buttonPressSound;
    private Music levelCompletedMusic;

    public AfterGameScreen(Main main, float score) {
        this.main = main;
        this.score = score;
        //buttonPressSound = Sounds.buttonPressSound;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.viewPortWidth, Main.viewPortHeight);

        GameConfiguration.sendHighScores((int) (score * 100));
        //GameConfiguration.getHighScores(GameConfiguration.gameLevel);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        levelCompletedMusic = Sounds.levelCompletedMusic;
        levelCompletedMusic.play();

        Skin mySkin = Textures.mySkin;

        TextButton buttonMenu = new TextButton(GameConfiguration.getText("menu"), mySkin,"pixel72");
        buttonMenu.setSize(Gdx.graphics.getWidth() / 10f,Gdx.graphics.getWidth() / 10f);
        buttonMenu.setPosition(0,Gdx.graphics.getHeight() - Gdx.graphics.getWidth() / 10f);
        buttonMenu.setColor(Color.YELLOW);
        buttonMenu.getLabel().setFontScale(GameConfiguration.fitText(buttonMenu, - 1, -1));
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

        TextButton buttonPlayAgain = new TextButton(GameConfiguration.getText("playAgain"), mySkin,"pixel72");
        buttonPlayAgain.setSize(Gdx.graphics.getWidth() / 5f,Gdx.graphics.getWidth() / 8f);
        buttonPlayAgain.setPosition(Gdx.graphics.getWidth() / 2f + 30,Gdx.graphics.getHeight() / 3f);
        buttonPlayAgain.setColor(Color.GREEN);
        buttonPlayAgain.getLabel().setFontScale(GameConfiguration.fitText(buttonPlayAgain, - 1,-1));
        buttonPlayAgain.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //buttonPressSound.play();
                GameScreen gameScreen = GameConfiguration.createGame(main);
                dispose();
                main.setScreen(gameScreen);
            }
        });

        TextButton buttonLevelScreen = new TextButton(GameConfiguration.getText("levels"), mySkin,"pixel72");
        buttonLevelScreen.setSize(Gdx.graphics.getWidth() / 5f,Gdx.graphics.getWidth() / 8f);
        buttonLevelScreen.setPosition(Gdx.graphics.getWidth() / 2f - buttonLevelScreen.getWidth() - 30,Gdx.graphics.getHeight() / 3f);
        buttonLevelScreen.getLabel().setFontScale(GameConfiguration.fitText(buttonLevelScreen, - 1, -1));
        buttonLevelScreen.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //buttonPressSound.play();
                dispose();
                main.setScreen(new LevelScreen(main));
            }
        });

        TextButton storeButton = new TextButton(GameConfiguration.getText("themes"),mySkin,"pixel72");
        storeButton.setSize(Gdx.graphics.getWidth() / 10f,Gdx.graphics.getWidth() / 10f);
        storeButton.setPosition(Gdx.graphics.getWidth() - storeButton.getWidth(),
                Gdx.graphics.getHeight() - Gdx.graphics.getWidth() / 10f);
        storeButton.setColor(56 / 255f, 114 / 255f, 207 / 255f, 1);
        storeButton.getLabel().setFontScale(GameConfiguration.fitText(storeButton, -1, -1));
        storeButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //buttonPressSound.play();
                dispose();
                main.setScreen(new Themes(main));
            }
        });

        Label scoreLabel = new Label( GameConfiguration.getText("score") + ": " + MathUtils.round(score * 100) / 100f, mySkin, "pixel72");
        float scoreLabelWidth = Gdx.graphics.getWidth() / 2f;
        float scoreLabelHeight = Gdx.graphics.getHeight() / 4f;


        scoreLabel.setBounds(scoreLabelWidth / 2, scoreLabelHeight * 3,
                scoreLabelWidth, scoreLabelHeight);
        scoreLabel.setFontScale(2);
        scoreLabel.setColor(Color.BLACK);

        stage.addActor(buttonMenu);
        stage.addActor(buttonPlayAgain);
        stage.addActor(buttonLevelScreen);
        stage.addActor(storeButton);
        stage.addActor(scoreLabel);

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
        levelCompletedMusic.stop();
        //levelCompletedMusic = null;
        //buttonPressSound.dispose();
    }

    /*
    public void addCredits() {
        float newCredits = score / 50 + 1;
        int oldCredits = GameConfiguration.credits;
        float sum = newCredits + oldCredits;
        GameConfiguration.credits = sum;
        String newCreditString = "" + sum;
        GameConfiguration.save(GameConfiguration.creditsString, newCreditString);
        Gdx.app.log("", newCreditString);
    }*/
}
