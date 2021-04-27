package fi.tuni.tiko1d;

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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Themes extends ScreenAdapter {

    private Main main;

    private OrthographicCamera camera;
    private Stage stage;

    private final float screenWidth = Gdx.graphics.getWidth();
    private final float screenHeight = Gdx.graphics.getHeight();

    private TextButton buttonSand;
    private TextButton buttonBush;

    private String buttonBushString;
    private String buttonSandString;

    private Sound buttonPressSound;

    private float themeMarkSandY;
    private float themeMarkBushY;
    private Image checkMarkTheme;

    private float playerMarkGhostY;
    private float playerMarkBlueDudeY;
    private float playerMarkPigY;
    private Image checkMarkPlayer;


    public Themes(Main main) {
        this.main = main;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.viewPortWidth, Main.viewPortHeight);
        buttonPressSound = Sounds.buttonPressSound;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        buttonSandString = GameConfiguration.getText("sand");
        buttonBushString = GameConfiguration.getText("bush");

        Skin mySkin = Textures.mySkin;

        TextButton buttonMenu = new TextButton(GameConfiguration.getText("menu"),mySkin,"pixel72");
        buttonMenu.setSize(screenWidth / 10f,screenWidth / 10f);
        buttonMenu.setPosition(0,screenHeight - screenWidth / 10f);
        buttonMenu.setColor(Color.YELLOW);
        buttonMenu.getLabel().setFontScale(GameConfiguration.fitText(buttonMenu, -1, -1, 2));
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
        buttonLevels.setPosition(screenWidth - buttonLevels.getWidth(),
                screenHeight - screenWidth / 10f);
        buttonLevels.setColor(0 / 255f, 255 / 255f, 195 / 255f, 1);
        buttonLevels.getLabel().setFontScale(GameConfiguration.fitText(buttonLevels, -1, -1, 2));
        buttonLevels.addListener(new InputListener(){
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

        Label themeLabel = new Label("", mySkin, "tutorialTest2");
        themeLabel.setSize(screenWidth / 3f, screenHeight * 3 / 4f);
        themeLabel.setPosition(screenWidth / 6f, 0);

        Drawable sandThemeDrawable = new TextureRegionDrawable(Textures.themeSand);
        ImageButton buttonThemeSand = new ImageButton(sandThemeDrawable);
        buttonThemeSand.setSize(themeLabel.getWidth() / 3f, themeLabel.getWidth() / 3f);
        buttonThemeSand.setPosition(themeLabel.getX() + themeLabel.getWidth() / 2f - buttonThemeSand.getWidth(),
                themeLabel.getY() + themeLabel.getHeight() * 8 / 9 - buttonThemeSand.getHeight());
        buttonThemeSand.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                GameConfiguration.save("theme", "sand");
                GameConfiguration.theme = "sand";
                checkMarkTheme.setY(themeMarkSandY);
            }
        });

        Drawable bushThemeDrawable = new TextureRegionDrawable(Textures.themeBush);
        ImageButton buttonThemeBush = new ImageButton(bushThemeDrawable);
        buttonThemeBush.setSize(buttonThemeSand.getWidth(), buttonThemeSand.getHeight());
        buttonThemeBush.setPosition(buttonThemeSand.getX(),
                themeLabel.getY() + themeLabel.getHeight() / 9);
        buttonThemeBush.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                GameConfiguration.save("theme", "bush");
                GameConfiguration.theme = "bush";
                checkMarkTheme.setY(themeMarkBushY);
            }
        });

        Label playerLabel = new Label("", mySkin, "tutorialTest2");
        playerLabel.setSize(screenWidth / 3f, screenHeight * 3 / 4f);
        playerLabel.setPosition(screenWidth / 2f, 0);

        Drawable ghostDrawable = new TextureRegionDrawable(Textures.playerGhost);
        ImageButton buttonGhost = new ImageButton(ghostDrawable);
        buttonGhost.setSize(playerLabel.getHeight() / 4f, playerLabel.getHeight() / 4f);
        buttonGhost.setPosition(playerLabel.getX() + playerLabel.getWidth() / 2f - buttonGhost.getWidth(),
                playerLabel.getY() + playerLabel.getHeight() * 15 / 16 - buttonGhost.getHeight());
        buttonGhost.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                GameConfiguration.save("player", "ghost");
                checkMarkPlayer.setY(playerMarkGhostY);
            }
        });

        Drawable blueDudeDrawable = new TextureRegionDrawable(Textures.playerBlueDude);
        ImageButton buttonBlueDude = new ImageButton(blueDudeDrawable);
        buttonBlueDude.setSize(buttonGhost.getWidth(), buttonGhost.getHeight());
        buttonBlueDude.setPosition(buttonGhost.getX(),
                playerLabel.getY() + playerLabel.getHeight() / 2 - buttonBlueDude.getHeight() / 2);
        buttonBlueDude.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                GameConfiguration.save("player", "blueDude");
                checkMarkPlayer.setY(playerMarkBlueDudeY);
            }
        });

        Drawable pigDrawable = new TextureRegionDrawable(Textures.playerPig);
        ImageButton buttonPig = new ImageButton(pigDrawable);
        buttonPig.setSize(buttonGhost.getWidth(), buttonGhost.getHeight());
        buttonPig.setPosition(buttonGhost.getX(),
                playerLabel.getY() + playerLabel.getHeight() / 16);
        buttonPig.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonPressSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                GameConfiguration.save("player", "pig");
                checkMarkPlayer.setY(playerMarkPigY);
            }
        });

        float checkMarkSize = screenWidth / 20f;
        themeMarkBushY = buttonThemeBush.getY() +
                buttonThemeBush.getHeight() / 2f - checkMarkSize / 2f;
        themeMarkSandY = buttonThemeSand.getY() +
                buttonThemeSand.getHeight() / 2f - checkMarkSize / 2f;

        float themeMarkY = themeMarkBushY;
        if(GameConfiguration.theme.equals("sand")) {
            themeMarkY = themeMarkSandY;
        }
        checkMarkTheme = new Image(Textures.checkMark);
        checkMarkTheme.setSize(checkMarkSize, checkMarkSize);
        checkMarkTheme.setPosition(buttonThemeBush.getX() + buttonThemeBush.getWidth() * 1.5f - checkMarkTheme.getWidth() / 2f, themeMarkY);


        float playerCheckMarkSize = screenWidth / 30f;
        playerMarkGhostY = buttonGhost.getY() + buttonGhost.getHeight() / 2f - playerCheckMarkSize / 2f;
        playerMarkBlueDudeY = buttonBlueDude.getY() + buttonBlueDude.getHeight() / 2f - playerCheckMarkSize / 2f;
        playerMarkPigY = buttonPig.getY() + buttonPig.getHeight() / 2f - playerCheckMarkSize / 2f;

        float playerMarkY = playerMarkGhostY;
        if(GameConfiguration.open("player").equals("blueDude")) {
            playerMarkY = playerMarkBlueDudeY;
        }
        else if(GameConfiguration.open("player").equals("pig")) {
            playerMarkY = playerMarkPigY;
        }

        checkMarkPlayer = new Image(Textures.checkMark);
        checkMarkPlayer.setSize(playerCheckMarkSize, playerCheckMarkSize);
        checkMarkPlayer.setPosition(buttonGhost.getX() + buttonGhost.getWidth() * 1.5f - checkMarkPlayer.getWidth() / 2f, playerMarkY);




        stage.addActor(buttonMenu);
        stage.addActor(buttonLevels);
        stage.addActor(themeLabel);
        stage.addActor(playerLabel);
        stage.addActor(buttonThemeSand);
        stage.addActor(buttonThemeBush);
        stage.addActor(checkMarkTheme);
        stage.addActor(buttonGhost);
        stage.addActor(buttonBlueDude);
        stage.addActor(buttonPig);
        stage.addActor(checkMarkPlayer);
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

    public String getMarkedText(String str) {
        return "X   " + str + "    ";
    }
}
