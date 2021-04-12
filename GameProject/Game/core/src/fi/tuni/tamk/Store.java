package fi.tuni.tamk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import javax.swing.JRadioButton;

public class Store extends ScreenAdapter {

    private Main main;

    private OrthographicCamera camera;
    private Stage stage;

    private Label creditLabel;

    private String creditsString;

    private TextButton buttonSand;
    private TextButton buttonBush;

    private String buttonBushString;
    private String buttonSandString;

    public Store(Main main) {
        this.main = main;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.viewPortWidth, Main.viewPortHeight);
        creditsString = GameConfiguration.getText("credits");
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        buttonSandString = GameConfiguration.getText("sand");
        buttonBushString = GameConfiguration.getText("bush");

        Skin mySkin = Textures.mySkin;

        Button buttonMenu = new TextButton(GameConfiguration.getText("menu"),mySkin,"default");
        buttonMenu.setSize(Gdx.graphics.getWidth() / 10f,Gdx.graphics.getWidth() / 10f);
        buttonMenu.setPosition(0,Gdx.graphics.getHeight() - Gdx.graphics.getWidth() / 10f);
        buttonMenu.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dispose();
                main.setScreen(new MenuScreen(main));
            }
        });
        stage.addActor(buttonMenu);

        Button buttonLevels = new TextButton(GameConfiguration.getText("levels"),mySkin,"default");
        buttonLevels.setSize(Gdx.graphics.getWidth() / 10f,Gdx.graphics.getWidth() / 10f);
        buttonLevels.setPosition(Gdx.graphics.getWidth() - buttonLevels.getWidth(),
                Gdx.graphics.getHeight() - Gdx.graphics.getWidth() / 10f);
        buttonLevels.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dispose();
                main.setScreen(new LevelScreen(main));
            }
        });


        creditLabel = new Label( creditsString + ": " + GameConfiguration.credits, mySkin, "default");
        creditLabel.setBounds(Gdx.graphics.getWidth() / 2f - Gdx.graphics.getWidth() / 10f, Gdx.graphics.getHeight() * 4 / 5f,
                Gdx.graphics.getWidth() / 5f, Gdx.graphics.getHeight() / 10f);


        float themeButtonWidth = Gdx.graphics.getWidth() / 5f;
        float themeButtonHeight = Gdx.graphics.getHeight() / 10f;
        float themeButtonX = Gdx.graphics.getWidth() / 2f - themeButtonWidth / 2f;
        float themeButtonY = Gdx.graphics.getHeight() / 2f;
        float themeButtonGapY = themeButtonHeight / 10f;

        String sandString = buttonSandString;
        if(GameConfiguration.theme.equals("sand")) {
            sandString = getMarkedText(buttonSandString);
        }
        buttonSand = new TextButton(sandString,mySkin,"default");
        buttonSand.setSize(themeButtonWidth, themeButtonHeight);
        buttonSand.setPosition(themeButtonX, themeButtonY);
        buttonSand.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                GameConfiguration.save("theme", "sand");
                GameConfiguration.theme = "sand";
                buttonSand.setText(getMarkedText(buttonSandString));
                buttonBush.setText(buttonBushString);
            }
        });

        String bushString = buttonBushString;
        if(GameConfiguration.theme.equals("bush")) {
            bushString = getMarkedText(buttonBushString);
        }
        buttonBush = new TextButton(bushString,mySkin,"default");
        buttonBush.setSize(themeButtonWidth, themeButtonHeight);
        buttonBush.setPosition(themeButtonX, themeButtonY - themeButtonGapY - themeButtonHeight);
        buttonBush.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                GameConfiguration.save("theme", "bush");
                GameConfiguration.theme = "bush";
                buttonSand.setText(buttonSandString);
                buttonBush.setText(getMarkedText(buttonBushString));
            }
        });

        stage.addActor(buttonBush);
        stage.addActor(buttonSand);
        stage.addActor(buttonLevels);
        stage.addActor(creditLabel);
        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void render(float deltaTime) {
        main.batch.setProjectionMatrix(camera.combined);
        Gdx.gl.glClearColor(0, 0, 1, 1);
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
        return "X    " + str + "      ";
    }
}
