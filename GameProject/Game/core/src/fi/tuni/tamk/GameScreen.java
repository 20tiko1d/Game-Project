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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
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

    // jotain
    private String wallUrl = "walls/wall3.png";

    private Main main;
    private LevelScreen levelScreen;

    //private static final float VIEW_PORT_WIDTH = 12;
    //private static final float VIEW_PORT_HEIGHT = 14.4f;
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
    private Texture imgWall;
    private Texture pairTexture;

    private Skin mySkin;

    // Map rendering
    private boolean created = false;
    private float mapX;
    private float mapY;
    private int startX;
    private int startY;
    private float mapXStart;
    private float mapYStart;
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


    public GameScreen(Main main, World world, Body playerBody, LevelScreen levelScreen) {
        this.main = main;
        this.world = world;
        this.playerBody = playerBody;
        this.levelScreen = levelScreen;
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
        mapX = (Main.viewPortWidth / 2) - (startX * Main.oneWidth) - (Main.oneWidth / 2);
        mapY = (Main.viewPortHeight / 2) + (startY * Main.oneWidth) / 2 + (Main.oneWidth / 4) - 1.8f * Main.oneWidth;
        mapXStart = mapX;
        mapYStart = mapY;
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
            batch.draw(backgroundImg, Main.viewPortWidth / 2 - 7.5f, Main.viewPortHeight / 2 - 7.5f + portraitCorrection, 15, 15);
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
        imgWall.dispose();
        batch.dispose();
        array = null;
        randomPairs = null;
        pairTexture.dispose();
    }

    public void setStart(int x, int y) {
        startX = x;
        startY = y;
    }

    public void setExit(int x, int y) {

    }


    public void getTextures() {
        backgroundImg = new Texture("circle2.png");
        imgWall = new Texture(wallUrl);
        playerTexture = new Texture("player2.png");
        pairTexture = new Texture("pairs1.png");
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
        mapX = mapXStart - (playerBody.getPosition().x - Main.viewPortWidth / 2);
        mapY = mapYStart - (playerBody.getPosition().y - (Main.viewPortHeight / 2)) / 2;
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
            currentOneWidth = Main.oneWidth / zoomRatio;
            currentMany = (int) (Main.howMany * (zoomRatio + 0.2));
        }

        int minIndexX = (int) ((Main.viewPortWidth / 2 - mapX) / zoomRatio / currentOneWidth) - currentMany / 2;
        int minIndexY = (int) ((((mapY - (Main.viewPortHeight / 2)) / zoomRatio - currentOneWidth / 4) / (currentOneWidth / 2)) - currentMany);
        int maxIndexX = minIndexX + currentMany;
        int maxIndexY = minIndexY + currentMany * 2 + 5;

        float x = Main.viewPortWidth / 2 - (Main.viewPortWidth / 2 - mapX) / zoomRatio + (minIndexX + 1) * currentOneWidth;
        float y = Main.viewPortHeight / 2 - currentOneWidth / 4 + (mapY - (Main.viewPortHeight / 2)) / zoomRatio - (minIndexY + 1) * (currentOneWidth / 2);

        int playerX = (int) (minIndexX + (Main.viewPortWidth / 2 + currentOneWidth / 2 - x) / currentOneWidth);
        int playerY = (int) (minIndexY + (y - (Main.viewPortHeight / 2)) / (currentOneWidth / 2) + 2.5);

        for(int row = minIndexY; row < maxIndexY; row++) {
            for(int column = minIndexX; column < maxIndexX; column++) {
                float oneHeight = currentOneWidth / 2;
                if (map[row][column].getHeight() > 40) {
                    oneHeight = currentOneWidth * 2.5f;
                };
                Texture mapTexture = map[row][column];
                batch.draw(mapTexture, x + (column - minIndexX) * currentOneWidth,
                        y - (row - minIndexY) * currentOneWidth / 2 + portraitCorrection,
                        currentOneWidth, currentOneWidth * ((float) mapTexture.getHeight() / mapTexture.getWidth()));

                if(row == playerY && column == playerX) {
                    batch.draw(playerTexture, Main.viewPortWidth / 2 - currentOneWidth / 2,
                            Main.viewPortHeight / 2 - currentOneWidth / 1.5f + portraitCorrection, currentOneWidth,
                            currentOneWidth * ((float) playerTexture.getHeight() / playerTexture.getWidth()));
                    itemCollision(row, column);
                }
                for(int i = 0; i < randomPairs.length; i++) {
                    if (row == randomPairs[i][1] && column == randomPairs[i][2] ||
                        row == randomPairs[i][3] && column == randomPairs[i][4]) {
                        batch.draw(pairTexture, x + (column - minIndexX) * currentOneWidth,
                                y - (row - minIndexY) * currentOneWidth / 2 + portraitCorrection + oneHeight,
                                currentOneWidth, currentOneWidth * ((float) pairTexture.getHeight() / pairTexture.getWidth()));
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
}
