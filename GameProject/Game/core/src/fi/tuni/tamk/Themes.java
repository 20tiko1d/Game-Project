package fi.tuni.tamk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import javax.swing.JRadioButton;

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

    public Themes(Main main) {
        this.main = main;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.viewPortWidth, Main.viewPortHeight);
        //buttonPressSound = Sounds.buttonPressSound;
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
        buttonLevels.setPosition(screenWidth - buttonLevels.getWidth(),
                screenHeight - screenWidth / 10f);
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
                dispose();
                main.setScreen(new LevelScreen(main));
            }
        });

        Drawable sandThemeDrawable = new TextureRegionDrawable(Textures.themeSand);
        ImageButton buttonThemeSand = new ImageButton(sandThemeDrawable);
        buttonThemeSand.setSize(screenWidth / 10f, screenWidth / 10f);
        buttonThemeSand.setPosition(screenWidth / 4f - buttonThemeSand.getWidth() / 2f,
                screenHeight * 2 / 3 - buttonThemeSand.getHeight() / 2f);
        buttonThemeSand.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //buttonPressSound.play();
                GameConfiguration.save("theme", "sand");
                GameConfiguration.theme = "sand";
                checkMarkTheme.setY(themeMarkSandY);
            }
        });

        Drawable bushThemeDrawable = new TextureRegionDrawable(Textures.themeBush);
        ImageButton buttonThemeBush = new ImageButton(bushThemeDrawable);
        buttonThemeBush.setSize(screenWidth / 10f, screenWidth / 10f);
        buttonThemeBush.setPosition(screenWidth / 4f - buttonThemeSand.getWidth() / 2f,
                screenHeight / 3 - buttonThemeSand.getHeight() / 2f);
        buttonThemeBush.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //buttonPressSound.play();
                GameConfiguration.save("theme", "bush");
                GameConfiguration.theme = "bush";
                checkMarkTheme.setY(themeMarkBushY);
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

        stage.addActor(buttonMenu);
        stage.addActor(buttonLevels);
        stage.addActor(buttonThemeSand);
        stage.addActor(buttonThemeBush);
        stage.addActor(checkMarkTheme);
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
        //buttonPressSound.dispose();
    }

    public String getMarkedText(String str) {
        return "X   " + str + "    ";
    }
}
