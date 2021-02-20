package fi.tuni.tamk;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Main extends ApplicationAdapter {

	private static final float VIEW_PORT_WIDTH = 12;
	private static final float VIEW_PORT_HEIGHT = 14.4f;
	private static final boolean DEBUG_PHYSICS = false;

	private OrthographicCamera camera;
	private Box2DDebugRenderer debugRenderer;

	SpriteBatch batch;
	MapGenerator generator;

	private Stage stage;
	private Button button;

	private boolean created = false;
	private boolean notFinished = true;

	private int startX;
	private int startY;

	private float oneWidth;

	private float mapXStart;
	private float mapYStart;

	private float mapX;
	private float mapY;

	private Texture imgWall;

	private Texture [][] map;
	private int howManyX = 20;

	private float velX;
	private float velY;

	private boolean isUp = false;
	private boolean isDown = false;
	private boolean isLeft = false;
	private boolean isRight = false;

	private Texture backgroundImg;

	private World world;

	private SpriteBatch player;
	private Texture playerTexture;
	private Body playerBody;

	private double accumulator;
	private final float TIME_STEP = 1 / 60f;

	private float zoomRatio = 1;
	private float zoomOut = 0.5f;
	private boolean zoomInProgress = false;
	private boolean ifZoomOut = true;

	private float zoomTimeLength = 10;
	private float zoomTimer;
	private float transitionTimer;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, VIEW_PORT_WIDTH, VIEW_PORT_HEIGHT);

		generator = new MapGenerator(this);

		backgroundImg = new Texture("circle2.png");

		stage = new Stage(new ScreenViewport());
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(stage);

		oneWidth = (VIEW_PORT_WIDTH - VIEW_PORT_WIDTH / 10f) / howManyX;

		imgWall = new Texture("walls/wall3.png");

		createButton();
		inputMultiplexer.addProcessor( new InputAdapter() {
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
			/*
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				dragStartY = Gdx.input.getY();
				return true;
			}

			@Override
			public boolean 	touchDragged(int screenX, int screenY, int pointer) {
				checkMove(Gdx.input.getY());
				return true;
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				velY = 0;
				return true;
			}*/
		});
		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	@Override
	public void render () {
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

		stage.draw();
		batch.begin();

		stage.act();
		if(created) {
			drawMap(batch);
			batch.draw(backgroundImg, VIEW_PORT_WIDTH / 2 - 8, VIEW_PORT_HEIGHT / 2 - 2 - 8, 16, 16);
		}
		batch.end();
		if(created) {
			doPhysicsStep(Gdx.graphics.getDeltaTime());
		}

	}
	
	@Override
	public void dispose () {
		batch.dispose();
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
		mapX = mapXStart - (playerBody.getPosition().x - VIEW_PORT_WIDTH / 2);
		mapY = mapYStart - (playerBody.getPosition().y - (VIEW_PORT_HEIGHT / 2 - 2)) / 2;
	}

	public void move(float time) {
		playerBody.setLinearVelocity(velX * 8, velY * 8);
	}

	public void createButton() {
		Skin mySkin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

		button = new TextButton("Create Game",mySkin,"small");
		button.setSize(150,50);
		button.setPosition(425,1100);
		button.addListener(new InputListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				created = false;
				createGame();
			}
		});
		stage.addActor(button);
	}

	public void createGame() {
		map = null;
		try {
			world.dispose();
			player.dispose();
			playerBody = null;
			debugRenderer.dispose();
		} catch(Exception e) {}

		world = new World(new Vector2(0,0), true);
		debugRenderer = new Box2DDebugRenderer();
		map = generator.createMap(15, 23, world, oneWidth, VIEW_PORT_WIDTH, VIEW_PORT_HEIGHT);
		player = new SpriteBatch();
		playerTexture = new Texture("player2.png");
		playerBody = world.createBody(getDefinitionOfBody());
		playerBody.createFixture(getFixtureDefinition());
		mapX = VIEW_PORT_WIDTH / 2 - startX * oneWidth - oneWidth / 2;
		mapY = VIEW_PORT_HEIGHT / 2 - 2 + startY * oneWidth / 2 + oneWidth / 4;
		mapXStart = mapX;
		mapYStart = mapY;
		created = true;
	}

	public BodyDef getDefinitionOfBody() {
		BodyDef myBodyDef = new BodyDef();
		myBodyDef.type = BodyDef.BodyType.DynamicBody;
		myBodyDef.position.set(VIEW_PORT_WIDTH / 2, VIEW_PORT_HEIGHT / 2 - 2);
		return myBodyDef;
	}

	public FixtureDef getFixtureDefinition() {
		FixtureDef playerFixtureDef = new FixtureDef();
		playerFixtureDef.density = 1;
		playerFixtureDef.restitution = 0;
		playerFixtureDef.friction = 0.5f;
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(oneWidth / 2);
		playerFixtureDef.shape = circleShape;
		return playerFixtureDef;
	}

	public void setStart(int x, int y) {
		startX = x;
		startY = y;
	}

	public void setExit(int x, int y) {

	}

	public Texture getFloorTexture1() {
		return new Texture("floors/floor1.png");
	}

	public Texture getFloorTexture2() {
		return new Texture("floors/floor2.png");
	}

	public Texture getWallTexture() {
		return imgWall;
	}

	public void drawMap(SpriteBatch batch) {
		float currentOneWidth = oneWidth;
		int currentManyX = howManyX;
		if(zoomInProgress) {
			currentOneWidth = oneWidth / zoomRatio;
			currentManyX = (int) (howManyX * (zoomRatio + 0.2));
		}

		int minIndexX = (int) ((VIEW_PORT_WIDTH / 2 - mapX) / zoomRatio / currentOneWidth) - currentManyX / 2;
		int minIndexY = (int) ((((mapY - (VIEW_PORT_HEIGHT / 2 - 2)) / zoomRatio - currentOneWidth / 4) / (currentOneWidth / 2)) - currentManyX);
		int maxIndexX = minIndexX + currentManyX;
		int maxIndexY = minIndexY + currentManyX * 2 + 5;

		float x = VIEW_PORT_WIDTH / 2 - (VIEW_PORT_WIDTH / 2 - mapX) / zoomRatio + (minIndexX + 1) * currentOneWidth;
		float y = VIEW_PORT_HEIGHT / 2 - 2 - currentOneWidth / 4 + (mapY - (VIEW_PORT_HEIGHT / 2 - 2)) / zoomRatio - (minIndexY + 1) * (currentOneWidth / 2);

		/*
		int playerX = (int) ((VIEW_PORT_WIDTH / 2 + oneWidth / 2 - mapX) / zoomRatio / currentOneWidth);
		int playerY = (int) (((mapY - (VIEW_PORT_HEIGHT / 2 - 2) / zoomRatio - currentOneWidth / 4))
				/ (currentOneWidth / 2));*/
		int playerX = (int) (minIndexX + (VIEW_PORT_WIDTH / 2 + currentOneWidth / 2 - x) / currentOneWidth);
		int playerY = (int) (minIndexY + (y - (VIEW_PORT_HEIGHT / 2 - 2)) / (currentOneWidth / 2) + 2.5);

		Gdx.app.log("", "x: " + playerX);
		Gdx.app.log("", "y: " + playerY);
		for(int row = minIndexY; row < maxIndexY; row++) {
			for(int column = minIndexX; column < maxIndexX; column++) {
				//Gdx.app.log("", "tapahtuu");
				float oneHeight = currentOneWidth / 2;
				if (map[row][column] == imgWall) {
					oneHeight = currentOneWidth * 2.5f;
				}
				//Gdx.app.log("", "X: " + (x + (column - minIndexX) + currentOneWidth));
				batch.draw(map[row][column], x + (column - minIndexX) * currentOneWidth,
						y - (row - minIndexY) * currentOneWidth / 2, currentOneWidth, oneHeight );

				if(row == playerY && column == playerX) {
					batch.draw(playerTexture, VIEW_PORT_WIDTH / 2 - currentOneWidth / 2,
							VIEW_PORT_HEIGHT / 2 - 2 - currentOneWidth / 1.5f, currentOneWidth,
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
					return;
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
