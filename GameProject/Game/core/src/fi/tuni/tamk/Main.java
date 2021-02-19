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

	private  float col_width = 3;
	private float row_height = 1;

	private OrthographicCamera camera;
	private Box2DDebugRenderer debugRenderer;

	SpriteBatch batch;
	MapGenerator generator;
	private Rectangle rect;

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
			//batch.draw(playerTexture, VIEW_PORT_WIDTH / 2 - oneWidth / 2, VIEW_PORT_HEIGHT / 2 - 2 - oneWidth / 2, oneWidth, oneWidth);
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
		Gdx.app.log("", "X: " + playerBody.getPosition().x + ", Y: " + playerBody.getPosition().y);
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
		Gdx.app.log("", "mapX: " + mapX);
		Gdx.app.log("", "mapY: " + mapY);
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
		int minIndexX = (int) ((VIEW_PORT_WIDTH / 2 - mapX) / oneWidth) - howManyX / 2;
		int minIndexY = (int) ((mapY - (VIEW_PORT_HEIGHT / 2 - 2 - oneWidth / 4)) / (oneWidth / 2)) - howManyX;
		int maxIndexX = minIndexX + howManyX;
		int maxIndexY = minIndexY + howManyX * 2 + 5;
		Gdx.app.log("", "minIndexX: " + (((mapY - (VIEW_PORT_HEIGHT / 2 - 2)) / (oneWidth / 2) - 1) - howManyX));
		//Gdx.app.log("", "minIndexY: " + minIndexY);


		int playerX = (int) ((VIEW_PORT_WIDTH / 2 + oneWidth / 2 - mapX) / oneWidth);
		//int playerX = minIndexX + howManyX / 2;
		int playerY = (int) ((mapY - (VIEW_PORT_HEIGHT / 2 - 2 - oneWidth / 4)) / (oneWidth / 2) + 0.70);
		//int playerY = minIndexY + howManyX;
		//Gdx.app.log("", "playerIndexX: " + playerX);
		//Gdx.app.log("", "playerIndexY: " + playerY);


		float x = mapX + (minIndexX + 1) * oneWidth;
		float y = mapY - (minIndexY + 1) * (oneWidth / 2);

		for(int row = minIndexY; row < maxIndexY; row++) {
			for(int column = minIndexX; column < maxIndexX; column++) {
				float oneHeight = oneWidth / 2;
				if (map[row][column] == imgWall) {
					oneHeight = oneWidth * 2.5f;
				}

				batch.draw(map[row][column], x + (column - minIndexX) * oneWidth,
						y - (row - minIndexY) * oneWidth / 2, oneWidth, oneHeight );

				if(row == playerY && column == playerX) {
					batch.draw(playerTexture, VIEW_PORT_WIDTH / 2 - oneWidth / 2, VIEW_PORT_HEIGHT / 2 - 2 - oneWidth / 1.5f, oneWidth, oneWidth * 2);
				}

			}
		}
	}
}
