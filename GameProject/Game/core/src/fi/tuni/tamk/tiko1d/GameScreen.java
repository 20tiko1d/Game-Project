package fi.tuni.tamk.tiko1d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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
    private Texture exitClosedTexture;
    private Texture exitOpenTexture;
    private boolean exitOpen = false;
    private int[] exitLocations;
    private Rectangle exitRectangle;
    private boolean exitTop;
    private boolean exitLeft;

    // Start locations
    private int[][] startLocations;

    // Pairs
    private Array<String> array;
    private int[][] randomPairs;
    private float objectHeight;

    // Textures
    private Texture[][] map;
    private Texture[] playerTextures;
    private Texture playerTexture;
    private Texture objectTexture;
    private Image pairLabelBackground;
    private TextureRegion background;

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
    private TextButton buttonBoost;

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
    private TextButton buttonValidate;
    private TextButton buttonSwitch;
    private TextButton buttonActivate;
    private String objectsFoundString;
    private float[] objectBounciness;
    private boolean[] objectDirections;
    private float bounciness = 0.1f;
    private Texture shadow;

    // Score
    private float score;
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
    private TextButton buttonTutorial;
    private Label tutorialLabel;
    private Image objectiveBackground;
    private Label objectiveText;

    // Sounds
    private Music backgroundMusic;
    private Sound buttonPressSound;
    private Sound activationSound;
    private Sound switchSound;
    private Sound connectingSound;
    private Sound wrongValidationSound;
    private Sound walkSound;
    private Sound sprintSound;

    private boolean walkSoundOn = false;
    private boolean sprintSoundOn = false;

    public GameScreen(Main main, World world) {
        this.main = main;
        this.world = world;
        this.gameScreen = this;
        scoreString = GameConfiguration.getText("score").toUpperCase();
        objectsFoundString = GameConfiguration.getText("pairsFound").toUpperCase();
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
        mySkin = Textures.mySkin;
        shadow = Textures.shadow;
        buttonPressSound = Sounds.buttonPressSound;
        activationSound = Sounds.activationSound;
        switchSound = Sounds.switchSound;
        connectingSound = Sounds.connectingSound;
        wrongValidationSound = Sounds.wrongValidationSound;
        //walkSound = Sounds.walkSound;
        //sprintSound = Sounds.sprintSound;

        calculateCircle();

        Image roundImage = new Image(Textures.getBackgroundTexture());
        roundImage.setBounds(screenWidth / 4f,
                0,
                screenWidth / 2f, screenHeight);

        Image backgroundImage = new Image(background);
        backgroundImage.setBounds(0, 0, screenWidth, screenHeight);

        Image side1Image = new Image(Textures.getSideTexture());
        side1Image.setBounds(0, 0, roundImage.getX(), screenHeight);

        Image side2Image = new Image(Textures.getSideTexture());
        side2Image.setBounds(roundImage.getX() + roundImage.getWidth(), 0,
                screenWidth - roundImage.getX() - roundImage.getWidth(),
                screenHeight);

        pairLabel = new Label("", mySkin, "objectLabel");
        pairLabel.setBounds(screenWidth * 1.1f / 4f, screenHeight * 4f / 5f,
                screenWidth / 2.2f, screenHeight / 5f);
        pairLabel.setWrap(true);
        pairLabel.setAlignment(Align.center);
        pairLabel.setScale(1.3f);
        pairLabel.setColor(Color.WHITE);

        pairLabelBackground = new Image(Textures.getPairLabelBackground());
        pairLabelBackground.setBounds(screenWidth / 4f, pairLabel.getY(),
                screenWidth / 2f, pairLabel.getHeight());

        scoreLabel = new Label(scoreString + ": " + score, mySkin, "pixel48");
        scoreLabel.setSize(side1Image.getWidth() * 4 / 10f, screenHeight / 10f);
        scoreLabel.setPosition(screenWidth / 100f, screenHeight - scoreLabel.getHeight());
        scoreLabel.setFontScale(0.7f);
        scoreLabel.setColor(Color.BLACK);

        scoreChangeLabel = new Label("", mySkin, "pixel48");
        scoreChangeLabel.setBounds(scoreLabel.getX() + scoreLabel.getWidth() * 1.5f, scoreLabel.getY(),
                scoreLabel.getWidth(), scoreLabel.getHeight());
        scoreChangeLabel.setFontScale(0.7f);
        objectLabel = new Label("", mySkin, "pixel50");
        objectLabel.setBounds(scoreLabel.getX(), scoreLabel.getY() - scoreLabel.getHeight(),
                scoreLabel.getWidth(), scoreLabel.getHeight());
        objectLabel.setFontScale(0.4f);
        objectLabel.setColor(Color.BLACK);
        activatedLabel = new Label(GameConfiguration.getText("activated").toUpperCase(), mySkin, "pixel50");
        activatedLabel.setBounds(pairLabel.getX(), 0, pairLabel.getWidth(), pairLabel.getHeight() / 2);
        activatedLabel.setColor(Color.GREEN);
        activatedLabel.setScale(3f);
        activatedLabel.setAlignment(Align.center);

        //stage.addActor(roundImage);
        //stage.addActor(side2Image);
        //stage.addActor(side1Image);
        stage.addActor(backgroundImage);
        stage.addActor(scoreLabel);
        stage.addActor(scoreChangeLabel);
        stage.addActor(objectLabel);
        //stage.addActor(pairLabelBackground);
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
            backgroundMusic = Sounds.backgroundMusic;
            backgroundMusic.setLooping(true);
            try {
                backgroundMusic.setVolume(Integer.parseInt(GameConfiguration.open("musicVolume")) / 100f);
            } catch (Exception e) {}

            backgroundMusic.play();
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
                    if(keycode == Input.Keys.SPACE) {
                        ifZoomIn = false;
                        ifMaxZoom = false;
                        velMultiplier = 1;
                        isSpace = false;
                    }
                    return true;
                }
            }));

            final Drawable drawable = new TextureRegionDrawable(new TextureRegion(Textures.pauseButtonTexture));
            final Button buttonPause = new Button(drawable);
            buttonPause.setSize(screenWidth / 10f,screenWidth / 10f);
            buttonPause.setPosition(screenWidth * 9 / 10f,screenHeight - screenWidth / 10f);
            buttonPause.addListener(new InputListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    buttonPressSound.play();
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    backgroundMusic.pause();
                    main.setScreen(new PauseScreen(main, gameScreen));
                }
            });

            buttonActivate = new TextButton(GameConfiguration.getText("activate"), mySkin, "pixel72");
            buttonActivate.setSize(screenWidth / 2f, screenWidth / 10f);
            buttonActivate.setPosition(screenWidth / 4f, 0);
            buttonActivate.setColor(Color.GREEN);
            buttonActivate.getLabel().setFontScale(GameConfiguration.fitText(buttonActivate, -1, -1, 2));
            buttonActivate.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    activationSound.play();
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

            buttonValidate = new TextButton(GameConfiguration.getText("validate"),mySkin,"pixel72");
            buttonValidate.setSize(screenWidth / 4f,screenWidth / 10f);
            buttonValidate.setPosition(screenWidth / 4f,0);
            buttonValidate.setColor(Color.GREEN);
            buttonValidate.getLabel().setFontScale(GameConfiguration.fitText(buttonValidate, -1, -1, 2));
            buttonValidate.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if(currentIndex == closeIndex && currentFirst != first) {
                        connectingSound.play();
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
                        wrongValidationSound.play();
                        if(currentIndex != -1 && currentIndex != closeIndex) {
                            score -= objectScore / 2f;
                            if(score < 0) {
                                score = 0;
                            }
                            scoreAdd = -objectScore / 2;
                            scoreAddTime = 2;
                        }

                        //currentIndex = closeIndex;
                        //currentFirst = first;
                    }
                    activateObject();
                    if(tutorialOn) {
                        tutorialAct();
                    }
                }
            });
            buttonValidate.setVisible(false);
            buttonValidate.setDisabled(true);

            buttonSwitch = new TextButton(GameConfiguration.getText("switchButton"),mySkin,"pixel72");
            buttonSwitch.setSize(screenWidth / 4f,screenWidth / 10f);
            buttonSwitch.setPosition(screenWidth / 2f,0);
            buttonSwitch.setColor(Color.YELLOW);
            buttonSwitch.getLabel().setFontScale(GameConfiguration.fitText(buttonSwitch, -1, -1, 2));
            buttonSwitch.addListener(new InputListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    switchSound.play();
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

            buttonBoost = new TextButton(GameConfiguration.getText("boostButton"),mySkin,"boost");
            buttonBoost.setSize(screenWidth * 3 / 18,screenWidth * 3 / 18);
            buttonBoost.setPosition(GameConfiguration.getControlsX(1, buttonBoost.getWidth()),
                    screenHeight / 4f - buttonBoost.getHeight() / 2f);
            buttonBoost.setColor(1, 0, 0, 1);
            buttonBoost.getLabel().setFontScale(GameConfiguration.fitText(buttonBoost, 48, -1, 2));
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

            float size = screenWidth / 10f;

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
            touchpad.setBounds(GameConfiguration.getControlsX(2,
                    GameConfiguration.joystickLength), GameConfiguration.joystickY,
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
        } else {
            backgroundMusic.play();
            buttonBoost.setX(GameConfiguration.getControlsX(1, buttonBoost.getWidth()));
            touchpad.setX(GameConfiguration.getControlsX(2, touchpad.getWidth()));
            try {
                backgroundMusic.setVolume(Integer.parseInt(GameConfiguration.open("musicVolume")) / 100f);
            } catch (Exception e) {}
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
                    main.setScreen(new AfterGameScreen(main, score));
                }
            }
        }

        batch.begin();
        if(created) {
            drawMap(batch);
            handleObjectBouncing(deltaTime);
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
        playerTextures = null;
        batch.dispose();
        array = null;
        randomPairs = null;
        objectTexture.dispose();
        backgroundMusic.stop();
        backgroundMusic = null;
        //buttonPressSound.dispose();
        //activationSound.dispose();
        //switchSound.dispose();
        //connectingSound.dispose();
        //wrongValidationSound.dispose();
        //walkSound.dispose();
        //sprintSound.dispose();
    }

    public void getTextures() {
        playerTextures = Textures.getPlayerTexture(GameConfiguration.open("player"));
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

        int nearObjectIndex = 0;

        int exitCounter = 0;

        boolean drawExit = false;

        for(int row = minIndexY; row < maxIndexY; row++) {
            for(int column = minIndexX; column < maxIndexX; column++) {
                Texture mapTexture = map[row][column];
                float locY = mapY - row * tileHeight;
                float currentTileHeight = tileHeight;
                if((float) mapTexture.getHeight() / mapTexture.getWidth() > relativeTileHeight) {
                    currentTileHeight = tileHeight * (1 + wallHeight);
                }
                for(int j = 0; j < 3; j++) {
                    if(row == startLocations[j][0] && column == startLocations[j][1]) {
                        batch.draw(map[row - 1][column], column * tileWidth,
                                locY, tileWidth, tileHeight);
                    }
                }
                if(drawExit) {
                    Texture exitTexture = exitClosedTexture;
                    if(exitOpen) {
                        exitTexture = exitOpenTexture;
                    }
                    drawTexture(batch, mapTexture, column * tileWidth, locY, tileWidth,
                            tileHeight, false);
                    drawTexture(batch, exitTexture, (column - 2) * tileWidth, locY,
                            tileWidth * 3, tileHeight * 6, false);
                    drawExit = false;
                }
                else if(exitCounter > 0) {
                    drawTexture(batch, mapTexture, column * tileWidth, locY, tileWidth,
                            tileHeight, false);
                    drawExit = true;
                    exitCounter = 0;
                }
                else if(row == exitLocations[0] && column == exitLocations[1]) {
                    Texture exitTexture = exitClosedTexture;
                    if(exitOpen) {
                        exitTexture = exitOpenTexture;
                    }
                    if(exitTop) {
                        drawTexture(batch, mapTexture, column * tileWidth, locY, tileWidth,
                                tileHeight, false);
                        drawTexture(batch, exitTexture, (column - 2) * tileWidth,
                                locY, tileWidth * 3,
                                tileHeight * 4, false);
                    }
                    else if(exitLeft) {
                        drawTexture(batch, exitTexture, (column - 2) * tileWidth, locY,
                                tileWidth * 3, tileHeight * 6, true);
                    }
                    else {
                        exitCounter++;
                        drawTexture(batch, mapTexture, column * tileWidth, locY, tileWidth,
                                tileHeight, false);
                    }
                }
                else {
                    batch.draw(mapTexture, column * tileWidth, locY, tileWidth, currentTileHeight);
                }

                for(int i = 0; i < randomPairs.length; i++) {
                    if (row == randomPairs[i][1] && column == randomPairs[i][2] ||
                            row == randomPairs[i][3] && column == randomPairs[i][4]) {
                        int index = i * 2;
                        if(row == randomPairs[i][3]) {
                            index++;
                        }
                        if(objectIndex == i && !playerDrawn) {
                            nearObjectIndex = index;
                            isObject = true;
                            objectX = column * tileWidth;
                            objectY = locY;
                        } else {
                            drawObject(batch, column * tileWidth, locY, index);
                        }
                    }
                }

                if(row == playerLocY && column == playerLocX) {
                    float playerY = playerBody.getPosition().y / (tileWidth / tileHeight) + tileHeight / 2;
                    if(isObject) {
                        if(playerY > objectY + tileHeight / 4) {
                            drawPlayer(batch, playerY);
                            drawObject(batch, objectX, objectY, nearObjectIndex);
                        } else {
                            drawObject(batch, objectX, objectY, nearObjectIndex);
                            drawPlayer(batch, playerY);
                        }
                        isObject = false;
                    } else {
                        drawPlayer(batch, playerY);
                    }
                    playerDrawn = true;
                    objectIndex = itemCollision();
                }

            }
        }
    }

    public void drawPlayer(SpriteBatch batch, float playerY) {
        float playerVelX = playerBody.getLinearVelocity().x;
        float playerVelY = playerBody.getLinearVelocity().y;
        if(!(playerTexture != null && playerVelX == 0 && playerVelY == 0)) {
            boolean playerFront = true;
            boolean playerRight = true;
            boolean yIsBigger = true;
            if(playerVelY > 0) {
                playerFront = false;
            }
            if(playerVelX < 0) {
                playerRight = false;
            }
            if(Math.abs(playerVelX) > Math.abs(playerVelY)) {
                yIsBigger = false;
            }
            if(!yIsBigger) {
                if(playerRight) {
                    playerTexture = playerTextures[3];
                } else {
                    playerTexture = playerTextures[1];
                }
            } else {
                if(playerFront) {
                    playerTexture = playerTextures[0];
                } else {
                    playerTexture = playerTextures[2];
                }
            }
        }

        batch.draw(playerTexture, playerBody.getPosition().x - tileWidth / 2, playerY,
                tileWidth, tileWidth * ((float) playerTexture.getHeight() / playerTexture.getWidth()));
    }

    public void drawObject(SpriteBatch batch, float locX, float locY, int objectIndex) {
        drawShadow(batch, locX, locY, objectIndex);
        batch.draw(objectTexture, locX, locY + Math.round(objectBounciness[objectIndex] * relativeHeight) / relativeHeight + tileHeight / 2, tileWidth, objectHeight);
    }

    public void drawTexture(SpriteBatch batch, Texture texture, float locX, float locY, float width, float height, boolean flip) {
        batch.draw(texture, flip ? locX + width : locX, locY, flip ? - width : width, height);
    }

    public void drawShadow(SpriteBatch batch, float locX, float locY, int objectIndex) {
        float width = tileWidth * 9 / 10 - Math.round(objectBounciness[objectIndex] * relativeHeight) / relativeHeight / 2;
        float height = tileHeight / 2 - Math.round(objectBounciness[objectIndex] * relativeHeight) / relativeHeight / 2;
        float shadowLocX = locX + (tileWidth - width) / 2;
        float shadowLocY = locY + (tileHeight - height) / 2;
        batch.draw(shadow, shadowLocX, shadowLocY, width, height);
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
                        (randomPairs[i][b] * tileWidth + tileWidth / 2), 2)) <= tileWidth * 2
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

        // set random bounciness value
        objectBounciness = new float[randomPairs.length * 2];
        objectDirections = new boolean[randomPairs.length * 2];
        for(int i = 0; i < objectBounciness.length; i++) {
            objectBounciness[i] = MathUtils.random(0, 100) / 1000f;
            objectDirections[i] = true;
        }
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

    public void setExitLocations(int[] exitLocations) {
        this.exitLocations = exitLocations;
    }

    public void setStartLocations(int[][] startLocations) {
        this.startLocations = startLocations;
    }

    public void setExitTop(boolean exitTop, boolean exitLeft) {
        this.exitTop = exitTop;
        this.exitLeft = exitLeft;
        exitOpenTexture = Textures.getExitOpenTexture(exitTop);
        exitClosedTexture = Textures.getExitCloseTexture(exitTop);
        Gdx.app.log("", "Exit on top: " + exitTop + ", Left: " + exitLeft);
    }

    public void openExit() {
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
            scoreChangeLabel.setText(indicator + "" + scoreAdd);
            scoreChangeLabel.setColor(changeColor);
            scoreAddTime -= deltaTime;
        } else {
            scoreChangeLabel.setText("");
        }
        scoreLabel.setText(scoreString + ": " + MathUtils.round(score * 100) / 100f);
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

        buttonTutorial = new TextButton("Ok", mySkin, "pixel72");
        buttonTutorial.setSize(tutorialTextBackground.getWidth() / 2, tutorialTextBackground.getHeight() / 4 );
        buttonTutorial.setPosition(screenWidth / 2f - buttonTutorial.getWidth() / 2f,
                tutorialTextBackground.getY());
        buttonTutorial.setColor(Color.GREEN);
        buttonTutorial.getLabel().setScale(GameConfiguration.fitText(buttonTutorial, -1, -1, 2));
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

        tutorialLabel = new Label("", mySkin, "tutorialTest2");
        tutorialLabel.setSize(tutorialTextBackground.getWidth() * 8 / 10, tutorialTextBackground.getHeight());
        tutorialLabel.setPosition(tutorialTextBackground.getX() + tutorialTextBackground.getWidth() / 10,
                tutorialTextBackground.getY());
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

        //stage.addActor(tutorialTextBackground);
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
    }

    public void handleObjectBouncing(float deltaTime) {
        for(int i = 0; i < objectBounciness.length; i++) {
            float bounce = bounciness;
            if(!objectDirections[i]) {
                bounce = bounce * -1;
            }
            objectBounciness[i] += deltaTime * bounce;
            if(objectBounciness[i] > 0.1f) {
                objectDirections[i] = !objectDirections[i];
                objectBounciness[i] = 0.2f - objectBounciness[i];
            }
            else if(objectBounciness[i] < 0) {
                objectDirections[i] = !objectDirections[i];
                objectBounciness[i] = objectBounciness[i] * -1;
            }
        }
    }

    public void calculateCircle() {
        float radius = Gdx.graphics.getWidth() / 2f;
        if(radius > Gdx.graphics.getHeight() * 9 / 10f) {
            radius = Gdx.graphics.getHeight() * 9 / 10f;
        }
        Texture wholeBackground = Textures.background;
        int width = (int) (wholeBackground.getWidth() * 3 / 10 * Gdx.graphics.getWidth() / radius);
        int height = (int) (wholeBackground.getHeight() * 3 / 10 * Gdx.graphics.getHeight() / radius);
        int x = wholeBackground.getWidth() / 2 - width / 2;
        int y = wholeBackground.getHeight() / 2 - height / 2;
        background = new TextureRegion(wholeBackground, x, y, width, height);
    }

    public void handleMovingSounds() {
        if(boost) {
            if(walkSoundOn) {
                walkSound.stop();
            }
            if(!sprintSoundOn) {
                sprintSound.setLooping(100000, true);
                sprintSound.play();
            }
        } else {
            if(sprintSoundOn) {
                sprintSound.stop();
            }
            if(!walkSoundOn) {
                walkSound.setLooping(100000, true);
                walkSound.play();
            }
        }
    }
}
