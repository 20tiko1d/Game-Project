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

	private  float col_width = 3;
	private float row_height = 1;

	private OrthographicCamera camera;

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

	private float mapX;
	private float mapY;

	private Texture imgWall;

	private Texture [][] map;
	private int howManyX = 25;
	//private int howManyY = 40;

	private float velX;
	private float velY;

	private boolean isUp = false;
	private boolean isDown = false;
	private boolean isLeft = false;
	private boolean isRight = false;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, VIEW_PORT_WIDTH, VIEW_PORT_HEIGHT);

		generator = new MapGenerator(this);

		stage = new Stage(new ScreenViewport());
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(stage);

		imgWall = new Texture("walls/wall3.png");

		createButton();
		inputMultiplexer.addProcessor( new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				if(keycode == Input.Keys.UP && !isUp) {
					velY--;
					isUp = true;
				}
				if(keycode == Input.Keys.DOWN && !isDown) {
					velY++;
					isDown = true;
				}
				if(keycode == Input.Keys.LEFT && !isLeft) {
					velX++;
					isLeft = true;
				}
				if(keycode == Input.Keys.RIGHT && !isRight) {
					velX--;
					isRight = true;
				}
				return true;
			}

			@Override
			public boolean keyUp(int keycode) {
				if(keycode == Input.Keys.UP) {
					velY++;
					isUp = false;
				}
				if(keycode == Input.Keys.DOWN) {
					velY--;
					isDown = false;
				}
				if(keycode == Input.Keys.LEFT) {
					velX--;
					isLeft = false;
				}
				if(keycode == Input.Keys.RIGHT) {
					velX++;
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

		move(Gdx.graphics.getDeltaTime());

		stage.draw();
		batch.begin();

		stage.act();
		if(created) {
			drawMap(batch);
		}
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}

	public void move(float time) {
		if(velX != 0) {
			mapX += velX * 4 * time;
		}
		if(velY != 0) {
			mapY += velY * 2 * time;
		}
	}

	public void createButton() {
		Skin mySkin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

		button = new TextButton("Create Game",mySkin,"small");
		button.setSize(150,50);
		button.setPosition(425,1000);
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
		map = generator.createMap(15, 25);
		oneWidth = (VIEW_PORT_WIDTH - VIEW_PORT_WIDTH * 2 / 10f) / howManyX;
		mapX = VIEW_PORT_WIDTH / 2 - startX * oneWidth - oneWidth / 2;
		mapY = VIEW_PORT_HEIGHT / 2 + startY * oneWidth / 2 + oneWidth / 4;
		created = true;
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
		int minIndexX = (int) ((VIEW_PORT_WIDTH / 2 - mapX) / oneWidth - 1) - howManyX / 2;
		int minIndexY = (int) ((mapY - VIEW_PORT_HEIGHT / 2) / (oneWidth / 2) - 1) - howManyX;
		int maxIndexX = minIndexX + howManyX;
		int maxIndexY = minIndexY + howManyX * 2;



		float x = mapX + (minIndexX + 1) * oneWidth;
		float y = mapY - (minIndexY + 1) * (oneWidth / 2) - 2;
		Gdx.app.log("", "x: " + minIndexX);
		Gdx.app.log("", "y: " + minIndexY);



		for(int row = minIndexY; row < maxIndexY; row++) {
			for(int column = minIndexX; column < maxIndexX; column++) {
				float oneHeight = oneWidth / 2;
				if (map[row][column] == imgWall) {
					oneHeight = oneWidth * 2.5f;
				}
				batch.draw(map[row][column], x + (column - minIndexX) * oneWidth,
						y - (row - minIndexY) * oneWidth / 2, oneWidth, oneHeight );
			}
		}
	}
}
