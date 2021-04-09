package fi.tuni.tamk;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * The class is responsible for running the actual game play.
 */
public class GameScreen extends ScreenAdapter {


    private Main main;
    private GameScreen gameScreen;

    private float viewPortWidth;
    private float viewPortHeight;

    private float relativeWidth = Gdx.graphics.getWidth() / Main.viewPortWidth;
    private float relativeHeight = Gdx.graphics.getHeight() / Main.viewPortHeight;
    private float relativeTileHeight;

    private float screenWidth;
    private float screenHeight;

    private static final boolean DEBUG_PHYSICS = false;

    private OrthographicCamera camera;
    private Box2DDebugRenderer debugRenderer;

    private SpriteBatch batch;

    private Stage stage;
    private SpriteBatch player;
    private InputMultiplexer inputMultiplexer;

    // Exit
    private Body exitBody;
    private Texture exitOpenTexture = Textures.getExitOpenTexture();
    private boolean exitOpen = false;
    private int[][] exitLocations;
    private Rectangle exitRectangle;

    // Pairs
    private Array<String> array;
    private int[][] randomPairs;
    private float objectHeight;

    // Textures
    private Texture[][] map;
    private Texture playerTexture;
    private Texture objectTexture;
    private Image pairLabelBackground;

    private Skin mySkin;

    // Map rendering
    private Rectangle playerRect;
    private int playerLocX;
    private int playerLocY;
    private boolean created = false;
    private float mapY;
    private float tileWidth;
    private float tileHeight;
    private float wallHeight;

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
    private Touchpad touchpad;

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
    private Button buttonBoost;

    // Objects
    private Label pairLabel;
    private boolean pairClose = false;
    private int pairCount = 0;
    private int objectIndex;
    private boolean objectActivated = false;
    private boolean first;
    private boolean currentFirst;
    private int closeIndex;
    private int currentIndex = -2;
    private Button buttonValidate;
    private Button buttonSwitch;
    private Button buttonActivate;
    private String objectsFoundString;

    // Score
    private float score = 0;
    private int objectScore;
    private Label scoreLabel;
    private Label scoreChangeLabel;
    private Label objectLabel;
    private Label activatedLabel;
    private float scoreAddTime = 0;
    private int scoreAdd;
    private String scoreString;

    // Fps counter
    private int fpsCounter = 0;
    private float second = 1;

    // Tutorial
    private boolean tutorialOn;
    private boolean pauseGame = false;
    private int tutorialPhase;
    private Image tutorialTextBackground;
    private Button buttonTutorial;
    private Label tutorialLabel;
    private Image objectiveBackground;
    private Label objectiveText;


    public GameScreen(Main main, World world) {
        Gdx.app.log("", "mitä??");
        this.main = main;
        this.world = world;
        this.gameScreen = this;
        scoreString = GameConfiguration.getText("score");
        objectsFoundString = GameConfiguration.getText("pairsFound");
        score = GameConfiguration.getStartScore();
        objectScore = GameConfiguration.getObjectScore();
        playerSpeed = GameConfiguration.PLAYER_SPEED;
        viewPortWidth = Main.viewPortWidth;
        viewPortHeight = Main.viewPortHeight;
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        relativeTileHeight = GameConfiguration.RELATIVE_TILE_HEIGHT;
        tileWidth = Main.oneWidth;
        tileHeight = tileWidth * GameConfiguration.RELATIVE_TILE_HEIGHT;
        wallHeight = GameConfiguration.WALL_HEIGHT;
        objectHeight = tileHeight * GameConfiguration.OBJECT_HEIGHT;
        stage = new Stage(new ScreenViewport());
        tutorialOn = GameConfiguration.tutorialOn;
        mySkin = new Skin(Gdx.files.internal("skin/testi/testi6.json"));

        Image roundImage = new Image(Textures.getBackgroundTexture());
        roundImage.setBounds(screenWidth / 4f,
                0,
                screenWidth / 2f, screenHeight);

        Image side1Image = new Image(Textures.getSideTexture());
        side1Image.setBounds(0, 0, roundImage.getX(), screenHeight);

        Image side2Image = new Image(Textures.getSideTexture());
        side2Image.setBounds(roundImage.getX() + roundImage.getWidth(), 0,
                screenWidth - roundImage.getX() - roundImage.getWidth(),
                screenHeight);

        pairLabel = new Label("", mySkin);
        pairLabel.setBounds(screenWidth * 1.1f / 4f, screenHeight * 4f / 5f,
                screenWidth / 2.2f, screenHeight / 5f);
        pairLabel.setWrap(true);
        pairLabel.setAlignment(Align.center);

        pairLabelBackground = new Image(Textures.getPairLabelBackground());
        pairLabelBackground.setBounds(screenWidth / 4f, pairLabel.getY(),
                screenWidth / 2f, pairLabel.getHeight());

        scoreLabel = new Label(scoreString + ": " + score,
                mySkin);
        scoreLabel.setSize(side1Image.getWidth() * 4 / 10f, screenHeight / 10f);
        scoreLabel.setPosition(screenWidth / 100f, screenHeight - scoreLabel.getHeight());

        scoreChangeLabel = new Label("", mySkin, "default");
        scoreChangeLabel.setBounds(scoreLabel.getX() + scoreLabel.getWidth(), scoreLabel.getY(),
                scoreLabel.getWidth(), scoreLabel.getHeight());
        objectLabel = new Label("", mySkin);
        objectLabel.setBounds(scoreLabel.getX(), scoreLabel.getY() - scoreLabel.getHeight(),
                scoreLabel.getWidth(), scoreLabel.getHeight());
        activatedLabel = new Label(GameConfiguration.getText("activated"), mySkin, "big");
        activatedLabel.setBounds(pairLabel.getX(), 0, pairLabel.getWidth(), pairLabel.getHeight());
        activatedLabel.setColor(Color.GREEN);
        activatedLabel.setAlignment(Align.center);

        stage.addActor(roundImage);
        stage.addActor(side2Image);
        stage.addActor(side1Image);
        stage.addActor(scoreLabel);
        stage.addActor(scoreChangeLabel);
        stage.addActor(objectLabel);
        stage.addActor(pairLabelBackground);
        stage.addActor(pairLabel);
        stage.addActor(activatedLabel);

        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, viewPortWidth, viewPortHeight);

        debugRenderer = new Box2DDebugRenderer();

        player = new SpriteBatch();

        inputMultiplexer = new InputMultiplexer();

        getTextures();
    }

    @Override
    public void show() {
        if(!created) {
            array = FileReader.getPairElements();
            mapY = map.length * tileHeight;



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


            Button buttonPause = new TextButton(GameConfiguration.getText("pause"),mySkin,"default");
            buttonPause.setSize(screenWidth / 10f,screenWidth / 10f);
            buttonPause.setPosition(screenWidth * 9 / 10f,screenHeight - screenWidth / 10f);
            buttonPause.addListener(new InputListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    main.setScreen(new PauseScreen(main, gameScreen));
                }
            });

            buttonActivate = new TextButton(GameConfiguration.getText("activate"), mySkin, "default");
            buttonActivate.setSize(screenWidth / 2f, screenWidth / 10f);
            buttonActivate.setPosition(screenWidth / 4f, 0);
            buttonActivate.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    currentIndex = closeIndex;
                    currentFirst = first;
                    objectActivated = true;
                    if(tutorialOn && tutorialPhase == 3) {
                        pauseGame = false;
                    }
                }
            });
            buttonActivate.setVisible(false);
            buttonActivate.setDisabled(true);

            buttonValidate = new TextButton(GameConfiguration.getText("validate"),mySkin,"default");
            buttonValidate.setSize(screenWidth / 4f,screenWidth / 10f);
            buttonValidate.setPosition(screenWidth / 4f,0);
            buttonValidate.addListener(new InputListener() {
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
                                currentIndex = -2;
                                if(pairCount == 0) {
                                    openExit();
                                }
                                pairCount++;
                                score += objectScore;
                                scoreAdd = objectScore;
                                scoreAddTime = 2;
                                if(pairCount >= randomPairs.length - 1) {
                                    score += objectScore;
                                    scoreAdd += objectScore;
                                }
                                objectActivated = false;
                            }
                        }
                    } else {
                        if(currentIndex != -1 && currentIndex != closeIndex) {
                            score -= objectScore / 2f;
                            if(score < 0) {
                                score = 0;
                            }
                            scoreAdd = -objectScore / 2;
                            scoreAddTime = 2;
                        }

                        currentIndex = closeIndex;
                        currentFirst = first;
                    }
                    activateObject();
                    if(tutorialOn) {
                        tutorialAct();
                    }
                }
            });
            buttonValidate.setVisible(false);
            buttonValidate.setDisabled(true);

            buttonSwitch = new TextButton(GameConfiguration.getText("switchButton"),mySkin,"default");
            buttonSwitch.setSize(screenWidth / 4f,screenWidth / 10f);
            buttonSwitch.setPosition(screenWidth / 2f,0);
            buttonSwitch.addListener(new InputListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    currentIndex = closeIndex;
                    currentFirst = first;
                    activateObject();
                    if(tutorialOn && tutorialPhase == 8) {
                        pauseGame = false;
                        tutorialAct();
                    }
                }
            });
            buttonSwitch.setVisible(false);
            buttonSwitch.setDisabled(true);

            buttonBoost = new TextButton(GameConfiguration.getText("boostButton"),mySkin,"default");
            buttonBoost.setSize(screenHeight / 4f,screenHeight / 4f);
            buttonBoost.setPosition(screenWidth / 8f - buttonBoost.getWidth() / 2f,
                    screenHeight / 4f - buttonBoost.getHeight() / 2f);
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

            Drawable touchBackground;
            Drawable touchKnob;

            float size = screenHeight / 5f;

            Skin touchPadSkin = new Skin();
            touchPadSkin.add("touchBackground", Textures.getJoystickBack());
            touchPadSkin.add("touchKnob", Textures.getJoystickKnob());
            Touchpad.TouchpadStyle touchPadStyle = new Touchpad.TouchpadStyle();
            touchBackground = touchPadSkin.getDrawable("touchBackground");
            touchKnob = touchPadSkin.getDrawable("touchKnob");
            touchKnob.setMinHeight(size);
            touchKnob.setMinWidth(size);
            touchPadStyle.background = touchBackground;
            touchPadStyle.knob = touchKnob;
            touchpad = new Touchpad(0.75f, touchPadStyle);
            touchpad.setBounds(GameConfiguration.joystickX, GameConfiguration.joystickY,
                    GameConfiguration.joystickLength, GameConfiguration.joystickLength);
            touchpad.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {

                    float deltaY = ((Touchpad) actor).getKnobY();
                    float deltaX = ((Touchpad) actor).getKnobX();
                    velY = ((deltaY - GameConfiguration.joystickLength / 2f) / (GameConfiguration.joystickLength / 4f));
                    velX = ((deltaX - GameConfiguration.joystickLength / 2f) / (GameConfiguration.joystickLength / 4f));
                }
            });

            stage.addActor(buttonPause);
            stage.addActor(buttonActivate);
            stage.addActor(buttonValidate);
            stage.addActor(buttonSwitch);
            stage.addActor(buttonBoost);
            stage.addActor(touchpad);

            if(tutorialOn) {
                configTutorial();
            }
            created = true;
        }
        Gdx.input.setInputProcessor(inputMultiplexer);
    }


    @Override
    public void render(float deltaTime) {
        batch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if(tutorialOn) {
            checkTutorial();
        }

        if(created) {
            if(!pauseGame) {
                move(Gdx.graphics.getDeltaTime());
            }
            if(boost && !ifMaxZoom && !pairClose ) {
                handleBoost(Gdx.graphics.getDeltaTime());
            }
        }
        if(DEBUG_PHYSICS && created) {
            debugRenderer.render(world, camera.combined);
        }
        if(!pairClose) {
            pairLabel.setVisible(false);
            pairLabelBackground.setVisible(false);
            buttonValidate.setVisible(false);
            //buttonValidate.setDisabled(true);
            buttonSwitch.setDisabled(true);
            buttonSwitch.setVisible(false);
            buttonActivate.setVisible(false);
            buttonActivate.setDisabled(true);
            activatedLabel.setVisible(false);
        }
        if(!pauseGame) {
            updateScore(deltaTime);
        }

        objectLabel.setText(objectsFoundString + ": " + pairCount + "/" + (randomPairs.length - 1));

        if(exitOpen) {
            if(playerRect.overlaps(exitRectangle)) {
                dispose();
                if(GameConfiguration.tutorialOn) {
                    main.setScreen(new AfterTutorialScreen(main));
                } else {
                    main.setScreen(new AfterGameScreen(main, (int) score));
                }
            }
        }

        batch.begin();

        if(created) {
            drawMap(batch);
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
        created = false;
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

    public void doPhysicsStep(float deltaTime)  {
        fpsCounter++;
        float frameTime = deltaTime;

        if(deltaTime > 1 / 4f) {
            frameTime = 1 / 4f;
        }
        accumulator += frameTime;

        // Fps counter
        while(accumulator >= TIME_STEP && !pauseGame) {
            world.step(TIME_STEP, 8, 3);
            accumulator -= TIME_STEP;
            second -= TIME_STEP;
            if(second <= 0) {
                //Gdx.app.log("", "fps: " + fpsCounter);

                fpsCounter = 0;
                second = 1;
            }
        }
        //Gdx.app.log("", "Y: " + playerLocY + ", X: " + playerLocX);
        //Gdx.app.log("", ": " + tutorialPhase + "score: " + score);

        // Moves the camera
        camera.setToOrtho(false, viewPortWidth * minZoom / zoomRatio,
                    viewPortHeight * minZoom / zoomRatio);
        camera.position.x = Math.round(playerBody.getPosition().x * relativeWidth) / relativeWidth;
        camera.position.y = Math.round(playerBody.getPosition().y / (tileWidth / tileHeight) * relativeHeight) / relativeHeight;

        if(exitOpen) {
            playerRect.x = playerBody.getPosition().x - tileWidth;
            playerRect.y = playerBody.getPosition().y - tileWidth;
        }
        playerLocX = (int) ((playerBody.getPosition().x + tileWidth / 2) / tileWidth);
        playerLocY = (int) ((mapY * (tileWidth / tileHeight) - playerBody.getPosition().y + tileWidth / 2) / tileWidth);
        camera.update();
    }

    public void move(float time) {
        playerBody.setLinearVelocity(velX * playerSpeed * velMultiplier * time,
                                     velY * playerSpeed * velMultiplier * time);
    }

    /**
     * Method draws the labyrinth and all objects on there.
     *
     * It also handles that right amount of the map is being rendered.
     *
     * @param batch: Used to render objects.
     */
    public void drawMap(SpriteBatch batch) {
        int currentMany = Main.howMany;
        if(boost) {
            currentMany = (int) (Main.howMany / zoomRatio);
        }

        int minIndexX = playerLocX - currentMany / 2 - 3;
        int minIndexY = playerLocY - currentMany / 2 - 5;
        int maxIndexX = playerLocX + currentMany / 2 + 3;
        int maxIndexY = playerLocY + currentMany / 2 + 7;

        float objectX = 0;
        float objectY = 0;
        boolean playerDrawn = false;
        boolean isObject = false;

        for(int row = minIndexY; row < maxIndexY; row++) {
            for(int column = minIndexX; column < maxIndexX; column++) {
                Texture mapTexture = map[row][column];
                float locY = mapY - row * tileHeight;
                float currentTileHeight = tileHeight;
                if((float) mapTexture.getHeight() / mapTexture.getWidth() > relativeTileHeight) {
                    currentTileHeight = tileHeight * (1 + wallHeight);
                }
                batch.draw(mapTexture, column * tileWidth,
                        locY, tileWidth, currentTileHeight);

                if(row == playerLocY && column == playerLocX) {
                    float playerY = playerBody.getPosition().y / (tileWidth / tileHeight) + tileHeight / 2;
                    if(isObject) {
                        if(playerY > objectY) {
                            drawPlayer(batch, playerY);
                            drawObject(batch, objectX, objectY);
                        } else {
                            drawObject(batch, objectX, objectY);
                            drawPlayer(batch, playerY);
                        }
                        isObject = false;
                    } else {
                        drawPlayer(batch, playerY);
                    }
                    playerDrawn = true;
                    objectIndex = itemCollision();
                }
                for(int i = 0; i < randomPairs.length; i++) {
                    if (row == randomPairs[i][1] && column == randomPairs[i][2] ||
                        row == randomPairs[i][3] && column == randomPairs[i][4]) {
                        if(objectIndex == i && !playerDrawn) {
                            isObject = true;
                            objectX = column * tileWidth;
                            objectY = locY;
                        } else {
                            drawObject(batch, column * tileWidth, locY);
                        }
                    }
                }
            }
        }
    }

    public void drawPlayer(SpriteBatch batch, float playerY) {
        batch.draw(playerTexture, playerBody.getPosition().x - tileWidth / 2, playerY,
                tileWidth, tileWidth * ((float) playerTexture.getHeight() / playerTexture.getWidth()));
    }

    public void drawObject(SpriteBatch batch, float locX, float locY) {
        batch.draw(objectTexture, locX, locY, tileWidth, objectHeight);
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
     */
    public int itemCollision() {
        boolean stillClose = false;
        int index = -1;
        for(int i = 0; i < randomPairs.length; i++) {
            for(int j=0; j<2; j++) {
                int a = 1 + j * 2;
                int b = 2 + j * 2;
                if(Math.sqrt(Math.pow(playerBody.getPosition().y -
                        (tileWidth * map.length - randomPairs[i][a] * tileWidth - tileWidth / 2), 2)
                        + Math.pow(playerBody.getPosition().x -
                        (randomPairs[i][b] * tileWidth + tileWidth / 2), 2)) <= 1
                        && randomPairs[i][0] != -1) {

                    createPairLabel(randomPairs[i][0]);
                    closeIndex = randomPairs[i][0];
                    index = i;
                    if(j == 0) {
                        first = true;
                    } else {
                        first = false;
                    }
                    stillClose = true;
                }
            }
        }
        if(!stillClose) {
            pairClose = false;
        } else {
            pairClose = true;
            pairLabel.setVisible(true);
            pairLabelBackground.setVisible(true);
            if(first == currentFirst && currentIndex == closeIndex) {
                activateObject();
            } else {
                if(objectActivated && !tutorialOn) {
                    buttonValidate.setVisible(true);
                    buttonSwitch.setDisabled(false);
                    buttonSwitch.setVisible(true);
                } else {
                    if(!tutorialOn) {
                        buttonActivate.setVisible(true);
                        buttonActivate.setDisabled(false);
                    }
                }
            }
        }

        return index;
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

    public void updateScore(float deltaTime) {
        if(score - deltaTime >= 0 && !pairClose) {
            score -= deltaTime;
        }
        if(scoreAddTime > 0) {
            Color changeColor = Color.RED;
            char indicator = ' ';
            if(scoreAdd > 0) {
                indicator = '+';
                changeColor = Color.GREEN;
            }
            scoreChangeLabel.setText(indicator + " " + scoreAdd);
            scoreChangeLabel.setColor(changeColor);
            scoreAddTime -= deltaTime;
        } else {
            scoreChangeLabel.setText("");
        }
        scoreLabel.setText(scoreString + ": " + (int) score);
    }

    public void activateObject() {
        buttonActivate.setVisible(false);
        buttonActivate.setDisabled(true);
        buttonSwitch.setVisible(false);
        buttonSwitch.setDisabled(true);
        buttonValidate.setVisible(false);
        buttonValidate.setDisabled(true);
        activatedLabel.setVisible(true);
    }

    public void configTutorial() {
        Gdx.app.log("j", "tutorial??");
        tutorialPhase = 1;
        buttonBoost.setVisible(false);
        buttonBoost.setDisabled(true);
        buttonActivate.setVisible(false);
        buttonActivate.setDisabled(true);
        touchpad.setVisible(false);

        tutorialTextBackground = new Image(new Texture("textures/random/tutorialTextBackground.png"));
        tutorialTextBackground.setSize(screenWidth / 2f, screenHeight / 2f);
        tutorialTextBackground.setPosition(screenWidth / 2f - tutorialTextBackground.getWidth() / 2f,
                screenHeight * 4 / 5 - tutorialTextBackground.getHeight());

        buttonTutorial = new TextButton("Ok", mySkin, "default");
        buttonTutorial.setSize(tutorialTextBackground.getWidth() / 2, tutorialTextBackground.getHeight() / 4 );
        buttonTutorial.setPosition(screenWidth / 2f - buttonTutorial.getWidth() / 2f,
                tutorialTextBackground.getY());
        buttonTutorial.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                buttonTutorial.setVisible(false);
                buttonTutorial.setDisabled(true);
                tutorialTextBackground.setVisible(false);
                tutorialLabel.setVisible(false);
                objectiveBackground.setVisible(true);
                objectiveText.setVisible(true);
                tutorialAct();
            }
        });

        tutorialLabel = new Label("", mySkin, "default");
        tutorialLabel.setSize(tutorialTextBackground.getWidth() * 8 / 10, tutorialTextBackground.getHeight());
        tutorialLabel.setPosition(tutorialTextBackground.getX() + tutorialTextBackground.getWidth() / 10,
                tutorialTextBackground.getY() + buttonTutorial.getHeight());
        tutorialLabel.setWrap(true);

        objectiveBackground = new Image(new Texture("textures/random/tutorialTextBackground.png"));
        objectiveBackground.setSize(screenWidth / 4, screenHeight / 4);
        objectiveBackground.setPosition(0, screenHeight / 2);
        objectiveBackground.setVisible(false);

        objectiveText = new Label("", mySkin, "default");
        objectiveText.setSize(objectiveBackground.getWidth() * 8 / 10, objectiveBackground.getHeight());
        objectiveText.setPosition(objectiveBackground.getX() + objectiveText.getWidth() / 10f, objectiveBackground.getY());
        objectiveText.setWrap(true);
        objectiveText.setVisible(false);

        stage.addActor(tutorialTextBackground);
        stage.addActor(tutorialLabel);
        stage.addActor(buttonTutorial);
        stage.addActor(objectiveBackground);
        stage.addActor(objectiveText);
        }

    public void checkTutorial() {
        switch (tutorialPhase) {
            case 1:
                objectiveText.setText(GameConfiguration.getText("objective1"));
                tutorialLabel.setText(GameConfiguration.getText("tutorial1"));
                showTutorial();
                break;
            case 2:
                if(pairClose) {
                    objectiveText.setText(GameConfiguration.getText("objective2"));
                    tutorialLabel.setText(GameConfiguration.getText("tutorial2"));
                    showTutorial();
                }
                break;
            case 3:
                if(objectActivated) {
                    objectiveText.setText(GameConfiguration.getText("objective3"));
                    tutorialAct();
                }
                break;
            case 4:
                if(playerLocY <= 70) {
                    tutorialLabel.setText(GameConfiguration.getText("tutorial3"));
                    showTutorial();
                }
                break;
            case 5:
                if(playerLocY < 70 && pairClose) {
                    tutorialLabel.setText(GameConfiguration.getText("tutorial4"));
                    showTutorial();
                }
                break;
            case 6:
                if(playerLocY < 57 && playerLocX >= 45) {
                    tutorialLabel.setText(GameConfiguration.getText("tutorial5"));
                    showTutorial();
                }
                break;
            case 7:
                if(playerLocX > 50 && pairClose) {
                    tutorialLabel.setText(GameConfiguration.getText("tutorial6"));
                    showTutorial();
                }
                break;
            case 9:
                if(playerLocX < 50) {
                    tutorialLabel.setText(GameConfiguration.getText("tutorial7"));
                    showTutorial();
                }
                break;
            case 10:
                if(playerLocX < 50 && pairClose) {
                    tutorialLabel.setText(GameConfiguration.getText("tutorial8"));
                    showTutorial();
                }
                break;
            default:
        }
    }

    public void showTutorial() {
        pauseGame = true;
        tutorialTextBackground.setVisible(true);
        tutorialLabel.setVisible(true);
        buttonTutorial.setVisible(true);
        buttonTutorial.setDisabled(false);
        objectiveText.setVisible(false);
        objectiveBackground.setVisible(false);
    }

    public void tutorialAct() {
        switch (tutorialPhase) {
            case 1:
                pauseGame = false;
                touchpad.setVisible(true);
                break;
            case 2:
                buttonActivate.setVisible(true);
                buttonActivate.setDisabled(false);
                break;
            case 3:
                break;
            case 7:
                objectiveText.setText(GameConfiguration.getText("objective4"));
                buttonSwitch.setVisible(true);
                buttonValidate.setVisible(true);
                buttonValidate.setTouchable(Touchable.disabled);
                break;
            case 8:
                objectiveText.setText(GameConfiguration.getText("objective5"));
                break;
            case 9:
                buttonBoost.setVisible(true);
                pauseGame = false;
                break;
            case 10:
                buttonValidate.setTouchable(Touchable.enabled);
                objectiveText.setText(GameConfiguration.getText("objective6"));
                buttonValidate.setVisible(true);
                buttonSwitch.setVisible(true);
                buttonSwitch.setTouchable(Touchable.disabled);
                break;
            case 11:
                buttonSwitch.setTouchable(Touchable.enabled);
                objectiveText.setText(GameConfiguration.getText("objective7"));
                pauseGame = false;
                break;
            default:
                pauseGame = false;
        }
        tutorialPhase++;
        Gdx.app.log("", "phase: " + tutorialPhase);
    }
}
