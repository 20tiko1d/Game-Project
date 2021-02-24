package fi.tuni.tamk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * The class is responsible for running the actual game play.
 */
public class GameScreen extends ScreenAdapter {


    private Main main;

    private static final boolean DEBUG_PHYSICS = false;

    private OrthographicCamera camera;
    private Box2DDebugRenderer debugRenderer;

    private SpriteBatch batch;

    private Stage stage;
    private SpriteBatch player;

    // Pairs
    private Array<String> array;
    private int[][] randomPairs;

    // Textures
    private Texture[][] map;
    private Texture playerTexture;
    private Texture backgroundImg;
    private Texture objectTexture;

    private Skin mySkin;

    // Map rendering
    private int playerLocX;
    private int playerLocY;
    private boolean created = false;
    private float mapY;
    private float portraitCorrection;

    // Controls
    private boolean boost = false;
    private boolean isUp = false;
    private boolean isDown = false;
    private boolean isLeft = false;
    private boolean isRight = false;
    private float velX;
    private float velY;
    private float velMultiplier = 1;

    private boolean first;
    private boolean currentFirst;
    private int closeIndex;
    private int currentIndex = -1;
    private Button buttonTake;
    private Button buttonSwitch;

    // Box2d
    private World world;
    private Body playerBody;
    private double accumulator;
    private final float TIME_STEP = 1 / 60f;

    // ZoomOut
    private float zoomRatio = 1;
    private float zoomOut = 0.5f;
    private boolean zoomInProgress = false;
    private boolean ifZoomOut = true;
    private float zoomTimeLength = 10;
    private float zoomTimer;
    private float transitionTimer;

    private Label pairLabel;
    private boolean pairClose = false;


    public GameScreen(Main main, World world) {
        this.main = main;
        this.world = world;
        stage = new Stage(new ScreenViewport());
        mySkin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));
        pairLabel = new Label("", mySkin);
        pairLabel.setBounds(Gdx.graphics.getWidth() / 4f, Gdx.graphics.getHeight() * 4.3f / 5f,
                Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 5f);
        stage.addActor(pairLabel);
        pairLabel.setFontScale(4);
        pairLabel.setColor(Color.BLACK);
        stage.addActor(pairLabel);
        batch = new SpriteBatch();

        if(Main.isPortrait) {
            portraitCorrection = Main.viewPortHeight / Main.viewPortWidth;
        }

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.viewPortWidth, Main.viewPortHeight);

        debugRenderer = new Box2DDebugRenderer();

        player = new SpriteBatch();

        getTextures();
    }

    @Override
    public void show() {
        array = FileReader.getPairElements();
        mapY = map.length * Main.oneWidth / 2;
        created = true;
        InputMultiplexer inputMultiplexer = new InputMultiplexer();

        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(new InputMultiplexer( new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if(keycode == Input.Keys.UP && !isUp) {
                    velY++;
                    isUp = true;
                }
                if(keycode == Input.Keys.DOWN && !isDown) {
                    velY--;
                    isDown = true;
                }
                if(keycode == Input.Keys.LEFT && !isLeft) {
                    velX--;
                    isLeft = true;
                }
                if(keycode == Input.Keys.RIGHT && !isRight) {
                    velX++;
                    isRight = true;
                }
                if(keycode == Input.Keys.SHIFT_LEFT && !boost) {
                    boost = true;
                }
                return true;
            }

            @Override
            public boolean keyUp(int keycode) {
                if(keycode == Input.Keys.UP) {
                    velY--;
                    isUp = false;
                }
                if(keycode == Input.Keys.DOWN) {
                    velY++;
                    isDown = false;
                }
                if(keycode == Input.Keys.LEFT) {
                    velX++;
                    isLeft = false;
                }
                if(keycode == Input.Keys.RIGHT) {
                    velX--;
                    isRight = false;
                }
                if(keycode == Input.Keys.SPACE && !zoomInProgress) {
                    Gdx.app.log("", "jotain");
                    transitionTimer = 1;
                    zoomInProgress = true;
                }
                if(keycode == Input.Keys.SHIFT_LEFT) {
                    boost = false;
                }
                return true;
            }
        }));


        Button buttonExit = new TextButton("Exit",mySkin,"default");
        buttonExit.setSize(Gdx.graphics.getWidth() / 10f,Gdx.graphics.getWidth() / 10f);
        buttonExit.setPosition(Gdx.graphics.getWidth() * 9 / 10f,Gdx.graphics.getHeight() - Gdx.graphics.getWidth() / 10f);
        buttonExit.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                created = false;
                dispose();
                main.setScreen(new LevelScreen(main));
            }
        });
        stage.addActor(buttonExit);
        buttonTake = new TextButton("Take",mySkin,"default");
        buttonTake.setSize(Gdx.graphics.getWidth() / 4f,Gdx.graphics.getWidth() / 10f);
        buttonTake.setPosition(Gdx.graphics.getWidth() / 4f,Gdx.graphics.getHeight() - Gdx.graphics.getWidth() / 7f);
        buttonTake.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(currentIndex == closeIndex && currentFirst != first) {
                    for(int i = 0; i < randomPairs.length; i++) {
                        if(randomPairs[i][0] == closeIndex) {
                            randomPairs[i][1] = 0;
                            randomPairs[i][2] = 0;
                            randomPairs[i][3] = 0;
                            randomPairs[i][4] = 0;
                            currentIndex = -1;
                        }
                    }
                } else {
                    currentIndex = closeIndex;
                    currentFirst = first;
                }
            }
        });
        stage.addActor(buttonTake);

        buttonSwitch = new TextButton("Switch",mySkin,"default");
        buttonSwitch.setSize(Gdx.graphics.getWidth() / 4f,Gdx.graphics.getWidth() / 10f);
        buttonSwitch.setPosition(Gdx.graphics.getWidth() / 2f,Gdx.graphics.getHeight() - Gdx.graphics.getWidth() / 7f);
        buttonSwitch.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                currentIndex = closeIndex;
                currentFirst = first;
            }
        });
        stage.addActor(buttonSwitch);

        Button buttonBoost = new TextButton("Boost",mySkin,"default");
        buttonBoost.setSize(Gdx.graphics.getHeight() / 5f,Gdx.graphics.getHeight() / 5f);
        buttonBoost.setPosition(Gdx.graphics.getWidth() / 15f,Gdx.graphics.getWidth() / 15f);
        buttonBoost.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(!boost) {
                    boost = true;
                }
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                boost = false;
            }
        });
        //stage.addActor(buttonBoost);

        Drawable touchBackground;
        Drawable touchKnob;

        float size = Gdx.graphics.getHeight() / 5f;

        Skin touchPadSkin = new Skin();
        touchPadSkin.add("touchBackground", new Texture("joystickBack.png"));
        touchPadSkin.add("touchKnob", new Texture("joystickKnob.png"));
        Touchpad.TouchpadStyle touchPadStyle = new Touchpad.TouchpadStyle();
        touchBackground = touchPadSkin.getDrawable("touchBackground");
        touchKnob = touchPadSkin.getDrawable("touchKnob");
        touchKnob.setMinHeight(size);
        touchKnob.setMinWidth(size);
        touchPadStyle.background = touchBackground;
        touchPadStyle.knob = touchKnob;
        Touchpad touchpad = new Touchpad(0.75f, touchPadStyle);
        touchpad.setBounds(Gdx.graphics.getWidth() - Gdx.graphics.getHeight() / 2.5f, 0,
                Gdx.graphics.getHeight() / 2.5f, Gdx.graphics.getHeight() / 2.5f);
        touchpad.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                float deltaY = ((Touchpad) actor).getKnobY();
                float deltaX = ((Touchpad) actor).getKnobX();
                velY = (deltaY - Gdx.graphics.getHeight() / 5f) / (Gdx.graphics.getHeight() / 10f);
                velX = (deltaX - Gdx.graphics.getHeight() / 5f) / (Gdx.graphics.getHeight() / 10f);
            }
        });
        stage.addActor(touchpad);

        Gdx.input.setInputProcessor(inputMultiplexer);
    }


    @Override
    public void render(float deltaTime) {
        batch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(created) {
            move(Gdx.graphics.getDeltaTime());
            if(zoomInProgress) {
                zoomOut(Gdx.graphics.getDeltaTime());
            }
        }
        if(DEBUG_PHYSICS && created) {
            debugRenderer.render(world, camera.combined);
        }
        if(!pairClose) {
            pairLabel.setText(null);
            buttonTake.setVisible(false);
            buttonTake.setDisabled(true);
            buttonSwitch.setDisabled(true);
            buttonSwitch.setVisible(false);
        }

        batch.begin();

        if(created) {
            drawMap(batch);
            batch.draw(backgroundImg, playerBody.getPosition().x - 7.5f, playerBody.getPosition().y / 2 - 7.5f + portraitCorrection, 15, 15);
        }
        batch.end();
        if(created) {
            doPhysicsStep(Gdx.graphics.getDeltaTime());
        }
        stage.draw();
        stage.act();
    }

    @Override
    public void dispose () {
        world.dispose();
        playerBody = null;
        map = null;
        stage.dispose();
        player.dispose();
        debugRenderer.dispose();
        playerTexture.dispose();
        backgroundImg.dispose();
        batch.dispose();
        array = null;
        randomPairs = null;
        objectTexture.dispose();
    }

    public void getTextures() {
        backgroundImg = Textures.getBackgroundTexture();
        playerTexture = Textures.getPlayerTexture();
        objectTexture = Textures.getObjectTexture();
    }

    public void doPhysicsStep(float deltaTime) {
        float frameTime = deltaTime;

        if(deltaTime > 1 / 4f) {
            frameTime = 1 / 4f;
        }
        accumulator += frameTime;

        while(accumulator >= TIME_STEP) {
            world.step(TIME_STEP, 8, 3);
            accumulator -= TIME_STEP;
        }
        camera.position.x = Math.round(playerBody.getPosition().x * 100) / 100f;
        camera.position.y = Math.round(playerBody.getPosition().y * 50) / 100f;
        playerLocX = (int) ((playerBody.getPosition().x + Main.oneWidth / 2) / Main.oneWidth);
        playerLocY = (int) ((mapY * 2 - playerBody.getPosition().y + Main.oneWidth / 2) / Main.oneWidth);
        camera.update();
    }

    public void move(float time) {
        playerBody.setLinearVelocity(velX * 4 * velMultiplier, velY * 4 * velMultiplier);
    }

    /**
     * Method draws the labyrinth and all objects on there.
     *
     * It also handles that right amount of the map is being rendered.
     *
     * @param batch: Used to render objects.
     */
    public void drawMap(SpriteBatch batch) {
        float currentOneWidth = Main.oneWidth;
        int currentMany = Main.howMany;
        if(zoomInProgress) {
            currentMany = (int) (Main.howMany * (zoomRatio + 0.2));
        }

        int minIndexX = playerLocX - currentMany / 2;
        int minIndexY = playerLocY - currentMany;
        int maxIndexX = minIndexX + currentMany;
        int maxIndexY = minIndexY + currentMany * 2 + 5;

        for(int row = minIndexY; row < maxIndexY; row++) {
            for(int column = minIndexX; column < maxIndexX; column++) {
                Texture mapTexture = map[row][column];
                float locY = mapY - row * currentOneWidth / 2;
                batch.draw(mapTexture, column * currentOneWidth,
                        locY, currentOneWidth, currentOneWidth * ((float) mapTexture.getHeight() / mapTexture.getWidth()));

                if(row == playerLocY && column == playerLocX) {
                    batch.draw(playerTexture, playerBody.getPosition().x - currentOneWidth / 2,
                            playerBody.getPosition().y / 2 + currentOneWidth / 4, currentOneWidth,
                            currentOneWidth * ((float) playerTexture.getHeight() / playerTexture.getWidth()));
                    itemCollision(row, column);
                }
                for(int i = 0; i < randomPairs.length; i++) {
                    if (row == randomPairs[i][1] && column == randomPairs[i][2] ||
                        row == randomPairs[i][3] && column == randomPairs[i][4]) {
                        batch.draw(objectTexture, column * currentOneWidth,
                                locY + currentOneWidth / 2,
                                currentOneWidth, currentOneWidth * ((float) objectTexture.getHeight() / objectTexture.getWidth()));
                    }
                }
            }
        }
    }

    /**
     * Method handles the zoom-feature transitions.
     *
     * @param deltaTime: Used to count time.
     */
    public void zoomOut(float deltaTime) {
        if(zoomTimer <= 0) {
            if(transitionTimer >= 0) {
                float frameTime = deltaTime;
                while(transitionTimer >= 0 && frameTime >= 1 / 60f) {
                    changeRatio();
                    frameTime -= 1 / 60f;
                    transitionTimer -= 1 / 100f;
                }
                if(transitionTimer <= 0 && ifZoomOut && !boost) {
                    zoomTimer = zoomTimeLength;
                    ifZoomOut = false;
                    return;
                }
                if(transitionTimer <= 0 && !ifZoomOut && !boost) {
                    ifZoomOut = true;
                    zoomInProgress = false;
                    zoomRatio = 1;
                }
            } else {
                if(!boost) {
                    transitionTimer = 1;
                }

            }
        } else {
            zoomTimer -= deltaTime;
        }
    }

    public void changeRatio() {
        if(ifZoomOut) {
            zoomRatio += zoomOut / 100f;
        } else {
            zoomRatio -= zoomOut / 100f;
        }
    }

    /**
     * Method is responsible for the player and pair-object encounters.
     *
     * @param row: Row that the player is currently on.
     * @param column: Column that the player is currently on.
     */
    public void itemCollision(int row, int column) {
        boolean stillClose = false;
        for(int i = 0; i < randomPairs.length; i++) {
            if(Math.abs(row - randomPairs[i][1]) + Math.abs(column - randomPairs[i][2]) <= 2 && randomPairs[i][0] != -1) {
                createPairLabel(randomPairs[i][0]);
                closeIndex = randomPairs[i][0];
                first = true;
                stillClose = true;
            }
            if(Math.abs(row - randomPairs[i][3]) + Math.abs(column - randomPairs[i][4]) <= 2 && randomPairs[i][0] != -1) {
                createPairLabel(randomPairs[i][0]);
                closeIndex = randomPairs[i][0];
                first = false;
                stillClose = true;

            }
        }
        if(!stillClose) {
            pairClose = false;
        } else {
            buttonTake.setVisible(true);
            buttonTake.setDisabled(false);
            buttonSwitch.setDisabled(false);
            buttonSwitch.setVisible(true);
            pairClose = true;
            pairLabel.setVisible(true);
        }
    }

    public void createPairLabel(int index) {
        pairLabel.setText(array.get(index));
    }

    public void handleBoost(float deltaTime) {
        transitionTimer = 1;
        zoomInProgress = false;
    }

    public void setRandomPairs(int [][] randomPairs) {
        this.randomPairs = randomPairs;
    }

    public void setMap(Texture [][] map) {
        this.map = map;
    }

    public void setPlayerBody(Body playerBody) {
        this.playerBody = playerBody;
    }

    public void setPlayerLoc(int locX, int locY) {
        this.playerLocX = locX;
        this.playerLocY = locY;
    }
}
