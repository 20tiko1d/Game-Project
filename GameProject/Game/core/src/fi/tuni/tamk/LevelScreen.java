package fi.tuni.tamk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class LevelScreen extends ScreenAdapter {

    private Main main;

    private Stage stage;

    private Rectangle rect;
    private int marginal;

    private Texture [][] map;
    private int [][] randomPairs;
    Array<String> array = FileReader.getPairElements();

    public LevelScreen(Main main){
        this.main = main;
        calculateAreas();
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        Skin mySkin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

        Button buttonEasy = new TextButton("Easy",mySkin,"default");
        buttonEasy.setSize(rect.width,rect.height);
        buttonEasy.setPosition(rect.x,rect.y);
        buttonEasy.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                createGame(10, 15, 2);
            }
        });
        stage.addActor(buttonEasy);

        Button buttonMedium = new TextButton("Medium",mySkin,"default");
        buttonMedium.setSize(rect.width,rect.height);
        if(Main.isPortrait) {
            buttonMedium.setPosition(rect.x,rect.y - 2 * marginal - rect.height);
        } else {
            buttonMedium.setPosition(rect.x + 2 * marginal + rect.width, rect.y);
        }

        buttonMedium.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                createGame(15, 23, 4);
            }
        });
        stage.addActor(buttonMedium);

        Button buttonHard = new TextButton("Hard",mySkin,"default");
        buttonHard.setSize(rect.width,rect.height);
        if(Main.isPortrait) {
            buttonHard.setPosition(rect.x,rect.y - (2 * marginal + rect.height) * 2);
        } else {
            buttonHard.setPosition(rect.x + (2 * marginal + rect.width) * 2, rect.y);
        }
        buttonHard.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                createGame(20, 31, 6);
            }
        });
        stage.addActor(buttonHard);

        Button buttonMenu = new TextButton("Menu",mySkin,"default");
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
        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        stage.draw();
        stage.act();
    }

    @Override
    public void dispose () {
        stage.dispose();
        rect = null;
    }

    public void createGame(int size, int pathLength, int numOfPairs) {
        World world = new World(new Vector2(0,0), true);
        Body playerBody = world.createBody(getDefinitionOfBody());
        playerBody.createFixture(getFixtureDefinition());

        GameScreen gameScreen= new GameScreen(main, world, playerBody, this);
        MapGenerator generator= new MapGenerator(main, gameScreen);


        map = generator.createMap(size, pathLength, world, Main.oneWidth, Main.viewPortWidth, Main.viewPortHeight, array.size, numOfPairs, this);
        dispose();
        main.setScreen(gameScreen);
    }

    public BodyDef getDefinitionOfBody() {
        BodyDef myBodyDef = new BodyDef();
        myBodyDef.type = BodyDef.BodyType.DynamicBody;
        myBodyDef.position.set(Main.viewPortWidth / 2, Main.viewPortHeight / 2 - 2);

        return myBodyDef;
    }

    public FixtureDef getFixtureDefinition() {
        FixtureDef playerFixtureDef = new FixtureDef();
        playerFixtureDef.density = 1;
        playerFixtureDef.restitution = 0;
        playerFixtureDef.friction = 0.5f;
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(Main.oneWidth / 2);
        playerFixtureDef.shape = circleShape;
        return playerFixtureDef;
    }

    public Texture[][] getMap() {
        return map;
    }

    public void calculateAreas() {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        rect = new Rectangle();
        rect.x = width / 5f;
        rect.y = height / 5f;
        rect.width = rect.x * 3;
        rect.height = rect.y * 3;
        marginal = 5;
        float singleSize;
        if(!Main.isPortrait) {
            singleSize = rect.height;
            if(rect.width / 3 < singleSize) {
                singleSize = rect.width / 3;
                rect.x = rect.x + marginal;
                rect.y = height / 2f - (singleSize - 2 * marginal) / 2;
            } else {
                rect.x = width / 2f - 2 * marginal - 1.5f * (singleSize - 2 * marginal);
                rect.y = rect.y + marginal;
            }
        } else {
            singleSize = rect.width;
            if(rect.height / 3 < singleSize) {
                singleSize = rect.height / 3;
                rect.x = width / 2f - (singleSize - 2 * marginal) / 2;
            } else {
                rect.x = rect.x + marginal;
            }
            rect.y = height / 2f + singleSize / 2 + marginal;
        }
        rect.height = singleSize - 2 * marginal;
        rect.width = rect.height;
    }

    public void setRandomPairs(int [][] randomPairs) {
        this.randomPairs = randomPairs;
    }

    public int[][] getRandomPairs() {
        return randomPairs;
    }

    public Array<String> getArray() {
        return array;
    }
}
