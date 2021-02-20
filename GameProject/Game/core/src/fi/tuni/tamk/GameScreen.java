package fi.tuni.tamk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameScreen extends ScreenAdapter {

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

    // Textures
    private Texture[][] map;
    private Texture playerTexture;
    private Texture backgroundImg;
    private Texture imgWall;

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
    private boolean isUp = false;
    private boolean isDown = false;
    private boolean isLeft = false;
    private boolean isRight = false;
    private float velX;
    private float velY;

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


    public GameScreen(Main main, World world, Body playerBody, LevelScreen levelScreen) {
        this.main = main;
        this.world = world;
        this.playerBody = playerBody;
        this.levelScreen = levelScreen;

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
        map = levelScreen.getMap();
        mapX = (Main.viewPortWidth / 2) - (startX * Main.oneWidth) - (Main.oneWidth / 2);
        mapY = (Main.viewPortHeight / 2) + (startY * Main.oneWidth) / 2 + (Main.oneWidth / 4) - 1.8f * Main.oneWidth;
        mapXStart = mapX;
        mapYStart = mapY;
        created = true;
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        stage = new Stage(new ScreenViewport());
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
                return true;
            }
        }));
        Skin mySkin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

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
    }

    public void setStart(int x, int y) {
        startX = x;
        startY = y;
    }

    public void setExit(int x, int y) {

    }


    public void getTextures() {
        backgroundImg = new Texture("circle2.png");
        imgWall = new Texture("walls/wall3.png");
        playerTexture = new Texture("player2.png");
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
        playerBody.setLinearVelocity(velX * 4, velY * 4);
    }

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
                batch.draw(map[row][column], x + (column - minIndexX) * currentOneWidth,
                        y - (row - minIndexY) * currentOneWidth / 2 + portraitCorrection, currentOneWidth, oneHeight);

                if(row == playerY && column == playerX) {
                    batch.draw(playerTexture, Main.viewPortWidth / 2 - currentOneWidth / 2,
                                Main.viewPortHeight / 2 - currentOneWidth / 1.5f + portraitCorrection, currentOneWidth,
                                currentOneWidth * 2);
                }
            }
        }
    }

    public void zoomOut(float deltaTime) {
        if(zoomTimer <= 0) {
            if(transitionTimer >= 0) {
                float frameTime = deltaTime;
                while(transitionTimer >= 0 && frameTime >= 1 / 60f) {
                    changeRatio();
                    frameTime -= 1 / 60f;
                    transitionTimer -= 1 / 100f;
                }
                if(transitionTimer <= 0 && ifZoomOut) {
                    zoomTimer = zoomTimeLength;
                    ifZoomOut = false;
                    return;
                }
                if(transitionTimer <= 0 && !ifZoomOut) {
                    ifZoomOut = true;
                    zoomInProgress = false;
                    zoomRatio = 1;
                }
            } else {
                transitionTimer = 1;
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


}
