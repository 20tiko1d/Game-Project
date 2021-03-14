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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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

    private float viewPortWidth;
    private float viewPortHeight;

    private float relativeWidth = Gdx.graphics.getWidth() / Main.viewPortWidth;
    private float relativeHeight = Gdx.graphics.getHeight() / Main.viewPortHeight;

    private static final boolean DEBUG_PHYSICS = false;

    private OrthographicCamera camera;
    private Box2DDebugRenderer debugRenderer;

    private SpriteBatch batch;

    private Stage stage;
    private SpriteBatch player;

    // Exit
    private Body exitBody;
    private Texture exitOpenTexture = Textures.getExitOpenTexture();
    private boolean exitOpen = false;
    private int[][] exitLocations;
    private Rectangle exitRectangle;

    // Pairs
    private Array<String> array;
    private int[][] randomPairs;

    // Textures
    private Texture[][] map;
    private Texture playerTexture;
    private Texture objectTexture;

    private Skin mySkin;

    // Map rendering
    private Rectangle playerRect;
    private int playerLocX;
    private int playerLocY;
    private boolean created = false;
    private float mapY;
    private float portraitCorrection;
    private float centerX;
    private float centerY;

    // Controls
    private boolean boost = false;
    private boolean isUp = false;
    private boolean isDown = false;
    private boolean isLeft = false;
    private boolean isRight = false;
    private boolean isSpace = false;
    private float velX;
    private float velY;
    private float velMultiplier = 1;
    private float playerSpeed;

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
    private float zoomSpeed = 0.5f;
    private float minZoom = 1;
    private float maxZoom = 1.5f;
    private boolean ifMaxZoom = false;
    private boolean ifZoomIn = true;
    private float zoomTimeLength = 10;
    private float zoomTimer;

    // Object pairs
    private Label pairLabel;
    private boolean pairClose = false;
    private int pairCount = 0;

    private float score = 0;
    private int objectScore;
    private Label scoreLabel;

    private int fpsCounter = 0;
    private float second = 1;


    public GameScreen(Main main, World world) {
        this.main = main;
        this.world = world;
        score = GameConfiguration.getStartScore();
        objectScore = GameConfiguration.getObjectScore();
        playerSpeed = GameConfiguration.PLAYER_SPEED;
        viewPortWidth = Main.viewPortWidth;
        viewPortHeight = Main.viewPortHeight;
        centerX = main.getCenterX();
        centerY = main.getCenterY();
        stage = new Stage(new ScreenViewport());
        mySkin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

        Image image = new Image(Textures.getBackgroundTexture());
        image.setBounds(Gdx.graphics.getWidth() / 4f,
                0,
                Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight());

        stage.addActor(image);

        Image side1Image = new Image(Textures.getSideTexture());
        side1Image.setBounds(0, 0, image.getX(), Gdx.graphics.getHeight());
        stage.addActor(side1Image);

        Image side2Image = new Image(Textures.getSideTexture());
        side2Image.setBounds(image.getX() + image.getWidth(), 0,
                Gdx.graphics.getWidth() - image.getX() - image.getWidth(),
                Gdx.graphics.getHeight());
        stage.addActor(side2Image);

        pairLabel = new Label("", mySkin);
        pairLabel.setBounds(Gdx.graphics.getWidth() / 4f, Gdx.graphics.getHeight() * 4f / 5f,
                Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 5f);
        pairLabel.setFontScale(4);
        pairLabel.setColor(Color.BLACK);
        pairLabel.setWrap(true);
        stage.addActor(pairLabel);

        scoreLabel = new Label("Score: " + score, mySkin, "black");
        scoreLabel.setBounds(Gdx.graphics.getWidth() / 15f, Gdx.graphics.getHeight() * 4 / 5f,
                Gdx.graphics.getWidth() / 5f, Gdx.graphics.getHeight() / 5f);
        scoreLabel.setFontScale(4);
        stage.addActor(scoreLabel);

        batch = new SpriteBatch();

        if(Main.isPortrait) {
            portraitCorrection = viewPortHeight / viewPortWidth;
        }

        camera = new OrthographicCamera();
        camera.setToOrtho(false, viewPortWidth, viewPortHeight);

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
                if(keycode == Input.Keys.SPACE && !isSpace) {
                    ifZoomIn = true;
                    boost = true;
                    velMultiplier = 1.5f;
                    isSpace = true;
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
                if(keycode == Input.Keys.SPACE && !ifZoomIn) {
                    Gdx.app.log("", "jotain");
                    ifZoomIn = true;
                }
                if(keycode == Input.Keys.SHIFT_LEFT) {
                    boost = false;
                }
                if(keycode == Input.Keys.SPACE) {
                    ifZoomIn = false;
                    ifMaxZoom = false;
                    velMultiplier = 1;
                    isSpace = false;
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
        buttonTake.setPosition(Gdx.graphics.getWidth() / 4f,0);
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
                            if(pairCount == 0) {
                                openExit();
                            }
                            pairCount++;
                            score += objectScore;
                            if(pairCount >= randomPairs.length) {
                                score += objectScore;
                            }
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
        buttonSwitch.setPosition(Gdx.graphics.getWidth() / 2f,0);
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
        //buttonBoost.setColor(Color.RED);
        buttonBoost.setColor(1, 0, 0, 1);
        buttonBoost.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                ifZoomIn = true;
                boost = true;
                velMultiplier = 1.5f;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                ifZoomIn = false;
                ifMaxZoom = false;
                velMultiplier = 1;
            }
        });
        stage.addActor(buttonBoost);

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
            if(boost && !ifMaxZoom) {
                handleBoost(Gdx.graphics.getDeltaTime());
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

        if(score - deltaTime >= 0) {
            score -= deltaTime;
        }
        scoreLabel.setText("Score: " + (int) score);

        if(exitOpen) {
            if(playerRect.overlaps(exitRectangle)) {
                created = false;
                dispose();
                main.setScreen(new AfterGameScreen(main, (int) score));
            }
        }

        batch.begin();

        if(created) {
            drawMap(batch);
            //batch.draw(backgroundImg, playerBody.getPosition().x - 7.5f * minZoom / zoomRatio,
                    //playerBody.getPosition().y / 2 - 7.5f * minZoom / zoomRatio + portraitCorrection,
                  //  15 * minZoom / zoomRatio, 15 * minZoom / zoomRatio);
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
        batch.dispose();
        array = null;
        randomPairs = null;
        objectTexture.dispose();
    }

    public void getTextures() {
        playerTexture = Textures.getPlayerTexture();
        objectTexture = Textures.getObjectTexture();
    }

    public void doPhysicsStep(float deltaTime) {
        fpsCounter++;
        float frameTime = deltaTime;

        if(deltaTime > 1 / 4f) {
            frameTime = 1 / 4f;
        }
        accumulator += frameTime;

        while(accumulator >= TIME_STEP) {
            world.step(TIME_STEP, 8, 3);
            accumulator -= TIME_STEP;
            second -= TIME_STEP;
            if(second <= 0) {
                Gdx.app.log("", "fps: " + fpsCounter);
                fpsCounter = 0;
                second = 1;
            }
        }
        camera.setToOrtho(false, viewPortWidth * minZoom / zoomRatio,
                    viewPortHeight * minZoom / zoomRatio);
        camera.position.x = Math.round(playerBody.getPosition().x * relativeWidth) / relativeWidth;
        camera.position.y = Math.round(playerBody.getPosition().y / 2 * relativeHeight) / relativeHeight;

        if(exitOpen) {
            playerRect.x = playerBody.getPosition().x - Main.oneWidth;
            playerRect.y = playerBody.getPosition().y - Main.oneWidth;
        }
        playerLocX = (int) ((playerBody.getPosition().x + Main.oneWidth / 2) / Main.oneWidth);
        playerLocY = (int) ((mapY * 2 - playerBody.getPosition().y + Main.oneWidth / 2) / Main.oneWidth);
        camera.update();
    }

    public void move(float time) {
        playerBody.setLinearVelocity(velX * playerSpeed * velMultiplier, velY * playerSpeed * velMultiplier);
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
        if(boost) {
            currentMany = (int) (Main.howMany / zoomRatio);
        }

        int minIndexX = playerLocX - currentMany / 2 - 3;
        int minIndexY = playerLocY - currentMany;
        int maxIndexX = minIndexX + currentMany + 3;
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

    public void handleBoost(float deltaTime) {
        float frameTime = deltaTime;
        while(frameTime >= 1 / 60f) {
            changeRatio();
            frameTime -= 1 / 60f;
        }
    }

    public void changeRatio() {
        if(ifZoomIn) {
            zoomRatio += zoomSpeed * 2 / 100f;
            if(zoomRatio >= maxZoom) {
                zoomRatio = maxZoom;
                ifMaxZoom = true;
            }
        } else {
            zoomRatio -= zoomSpeed / 100f;
            if(zoomRatio <= minZoom) {
                zoomRatio = minZoom;
                boost = false;
            }
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

    public void setExitBody(Body exitBody) {
        this.exitBody = exitBody;
    }

    public void setExitLocations(int[][] exitLocations) {
        this.exitLocations = exitLocations;
    }

    public void openExit() {
        for(int i = 0; i < exitLocations.length; i++) {
            map[exitLocations[i][0]][exitLocations[i][1]] = exitOpenTexture;
        }
        world.destroyBody(exitBody);
        playerRect = new Rectangle();
        playerRect.x = playerBody.getPosition().x - Main.oneWidth / 2;
        playerRect.y = playerBody.getPosition().y - Main.oneWidth / 2;
        playerRect.width = Main.oneWidth;
        playerRect.height = Main.oneWidth;
        exitOpen = true;
    }

    public void setExitRectangle(Rectangle exitRectangle) {
        this.exitRectangle = exitRectangle;
    }
}
