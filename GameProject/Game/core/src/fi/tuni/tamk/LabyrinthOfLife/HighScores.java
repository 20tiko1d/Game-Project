package fi.tuni.tamk.LabyrinthOfLife;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;

/**
 * The class contains all of the player's personal achievements and the profile.
 */
public class HighScores extends ScreenAdapter {

    private final Main main;

    private final int screenWidth = Gdx.graphics.getWidth();
    private final int screenHeight = Gdx.graphics.getHeight();

    private OrthographicCamera camera;
    private Stage stage;

    private Skin mySkin;

    private float scoreWidth;
    private float scoreHeight;
    private float scoreY;

    private float startX;
    private float listWidth;

    private final Sound buttonPressSound;

    public HighScores(Main main) {
        this.main = main;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.viewPortWidth, Main.viewPortHeight);
        buttonPressSound = Sounds.buttonPressSound;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        mySkin = Textures.mySkin;

        TextButton buttonMenu = new TextButton(GameConfiguration.getText("menu"),mySkin,"pixel72");
        buttonMenu.setSize(screenWidth / 10f,screenWidth / 10f);
        buttonMenu.setPosition(0,Gdx.graphics.getHeight() - screenWidth / 10f);
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
                dispose();
                main.setScreen(new MenuScreen(main));
            }
        });

        TextButton buttonLevels = new TextButton(GameConfiguration.getText("levels"),mySkin,"pixel72");
        buttonLevels.setSize(screenWidth / 10f,screenWidth / 10f);
        buttonLevels.setPosition(screenWidth - buttonLevels.getWidth(), screenHeight - buttonLevels.getHeight());
        buttonLevels.setColor(0 / 255f, 255 / 255f, 195 / 255f, 1);
        buttonLevels.getLabel().setFontScale(GameConfiguration.fitText(buttonLevels, -1, -1));
        buttonLevels.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(!GameConfiguration.firstTime) {
                    dispose();
                    main.setScreen(new LevelScreen(main));
                }
            }
        });

        Label nameLabel = new Label("    " + GameConfiguration.open("name"), mySkin, "tutorialTest");
        nameLabel.setSize(screenWidth / 2f, screenHeight / 7f);
        nameLabel.setPosition(screenWidth / 2f - nameLabel.getWidth() / 2f, screenHeight - nameLabel.getHeight());

        TextButton buttonRename = new TextButton("Edit",mySkin,"pixel72");
        buttonRename.setSize(nameLabel.getHeight(),nameLabel.getHeight());
        buttonRename.setPosition(nameLabel.getX() + nameLabel.getWidth() - buttonRename.getWidth(),
                nameLabel.getY());
        buttonRename.setColor(Color.GREEN);
        buttonRename.getLabel().setFontScale(GameConfiguration.fitText(buttonRename, -1, -1));
        buttonRename.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dispose();
                main.setScreen(new PlayerName(main, false));
            }
        });

        Label backgroundLabel = new Label("", mySkin, "tutorialTest2");
        backgroundLabel.setSize(screenWidth * 8 / 10f, screenHeight * 8 / 10f);
        backgroundLabel.setPosition(screenWidth / 10f, 0);

        Label easyLabel = new Label(GameConfiguration.getText("easyLevel"), mySkin, "pixel72");
        easyLabel.setSize(backgroundLabel.getWidth() / 3, backgroundLabel.getHeight() * 2 / 10);
        easyLabel.setPosition(backgroundLabel.getX(), backgroundLabel.getY() + backgroundLabel.getHeight() - easyLabel.getHeight());
        easyLabel.setAlignment(Align.center);
        easyLabel.setColor(Color.BLACK);

        Label mediumLabel = new Label(GameConfiguration.getText("mediumLevel"), mySkin, "pixel72");
        mediumLabel.setSize(easyLabel.getWidth(), easyLabel.getHeight());
        mediumLabel.setPosition(easyLabel.getX() + easyLabel.getWidth(), easyLabel.getY());
        mediumLabel.setAlignment(Align.center);
        mediumLabel.setColor(Color.BLACK);

        Label hardLabel = new Label(GameConfiguration.getText("hardLevel"), mySkin, "pixel72");
        hardLabel.setSize(easyLabel.getWidth(), easyLabel.getHeight());
        hardLabel.setPosition(mediumLabel.getX() + mediumLabel.getWidth(), easyLabel.getY());
        hardLabel.setAlignment(Align.center);
        hardLabel.setColor(Color.BLACK);


        Label easyBackgroundLabel = new Label("", mySkin, "tutorialTest2");
        easyBackgroundLabel.setSize(backgroundLabel.getWidth() / 3, backgroundLabel.getHeight() * 8 / 10f);
        easyBackgroundLabel.setPosition(backgroundLabel.getX(), 0);
        easyBackgroundLabel.setColor(Color.WHITE);

        Label mediumBackgroundLabel = new Label("", mySkin, "tutorialTest2");
        mediumBackgroundLabel.setSize(easyBackgroundLabel.getWidth(), easyBackgroundLabel.getHeight());
        mediumBackgroundLabel.setPosition(easyBackgroundLabel.getX() + easyBackgroundLabel.getWidth(), 0);

        Label hardBackgroundLabel = new Label("", mySkin, "tutorialTest2");
        hardBackgroundLabel.setSize(easyBackgroundLabel.getWidth(), easyBackgroundLabel.getHeight());
        hardBackgroundLabel.setPosition(mediumBackgroundLabel.getX() + mediumBackgroundLabel.getWidth(), 0);

        stage.addActor(buttonMenu);
        stage.addActor(buttonLevels);
        stage.addActor(nameLabel);
        stage.addActor(buttonRename);
        stage.addActor(backgroundLabel);
        stage.addActor(easyLabel);
        stage.addActor(mediumLabel);
        stage.addActor(hardLabel);
        stage.addActor(easyBackgroundLabel);
        stage.addActor(mediumBackgroundLabel);
        stage.addActor(hardBackgroundLabel);

        Gdx.input.setInputProcessor(stage);

        scoreWidth = easyBackgroundLabel.getWidth() * 9 / 10f;
        scoreHeight = easyBackgroundLabel.getHeight() * 9 / 10f;
        scoreY = easyBackgroundLabel.getY() + easyBackgroundLabel.getHeight() * 9 / 10f;
        startX = easyBackgroundLabel.getX();
        listWidth = easyBackgroundLabel.getWidth();
        createHighScoreLabels();
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

    public void createHighScoreLabels() {
        GameConfiguration.getHighScores(1);
        ArrayList<HighScoreEntry> highScoresEasy = GameConfiguration.highScores;
        GameConfiguration.getHighScores(2);
        ArrayList<HighScoreEntry> highScoresMedium = GameConfiguration.highScores;
        GameConfiguration.getHighScores(3);
        ArrayList<HighScoreEntry> highScoresHard = GameConfiguration.highScores;
        GameConfiguration.highScores = null;

        if(highScoresEasy != null) {
            createScoreLabel(startX + listWidth / 20f, getTopHighScores(highScoresEasy));
            createScoreLabel(startX + listWidth * 21 / 20f, getTopHighScores(highScoresMedium));
            createScoreLabel(startX + listWidth * 41 / 20f, getTopHighScores(highScoresHard));
        } else {
            Image errorBackground = new Image(new Texture("textures/random/tutorialTextBackground.png"));
            errorBackground.setSize(screenWidth / 2f, screenHeight / 4f);
            errorBackground.setPosition(screenWidth / 2f - errorBackground.getWidth() / 2f,
                    screenHeight / 2f - errorBackground.getHeight() / 2f);
            stage.addActor(errorBackground);
            Label errorLabel = new Label("    " + GameConfiguration.getText("fetchError"), mySkin, "default");
            errorLabel.setSize(errorBackground.getWidth(), errorBackground.getHeight());
            errorLabel.setPosition(errorBackground.getX(), errorBackground.getY());
            stage.addActor(errorLabel);
        }
    }

    public ArrayList<HighScoreEntry> getTopHighScores(ArrayList<HighScoreEntry> highScores) {
        ArrayList<HighScoreEntry> topScores = new ArrayList<>();

        for(int i = 0; i < 10; i++) {
            int biggestValue = -1;
            int index = 0;
            for(int j = 0; j < highScores.size(); j++) {
                if(highScores.get(j).getScore() > biggestValue) {
                    biggestValue = highScores.get(j).getScore();
                    index = j;
                }
            }
            if(biggestValue > 0) {
                topScores.add(highScores.get(index));
                highScores.remove(index);
            }
        }
        return topScores;
    }

    public void createScoreLabel(float x, ArrayList<HighScoreEntry> highScores) {
        int size = highScores.size();
        for(int i = 0; i < 10; i++) {
            Label label = new Label("", mySkin, "default");
            label.setSize(scoreWidth, scoreHeight / 10f);
            label.setPosition(x, scoreY - scoreHeight /20f - i * label.getHeight());
            label.setColor(Color.BLACK);
            String name = "";
            String score = "";
            String beforeNumber = "   ";
            if(i == 9) {
                beforeNumber = " ";
            }
            if(size > i) {
                name = highScores.get(i).getName() + ": ";
                score = "" + highScores.get(i).getScore() / 100f;
            }
            label.setText(beforeNumber + (i + 1) + ". " + name + score);
            if(i < 3) {
                float y = scoreY - scoreHeight / 20f - i * label.getHeight();
                String textureName = "gold";
                if(i == 1) {
                    textureName = "silver";
                }
                else if(i == 2) {
                    textureName = "bronze";
                }
                Image backgroundImage = new Image(Textures.getMedalTexture(textureName));
                backgroundImage.setBounds(x, y, scoreWidth, scoreHeight / 10f);
                stage.addActor(backgroundImage);
            }

            stage.addActor(label);
        }
    }
}
