package fi.tuni.tamk;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Main extends ApplicationAdapter {

	private static final int VIEW_PORT_WIDTH = 10;
	private static final int VIEW_PORT_HEIGHT = 10;

	private OrthographicCamera camera;

	SpriteBatch batch;
	MapGenerator generator;
	private StaticObjects walls;
	private Rectangle rect;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, VIEW_PORT_WIDTH, VIEW_PORT_HEIGHT);

		generator = new MapGenerator();
		rect = new Rectangle(0, 0, VIEW_PORT_WIDTH, VIEW_PORT_HEIGHT);
		walls = new StaticObjects(generator.createMap(10, 11), rect);
	}

	@Override
	public void render () {
		batch.setProjectionMatrix(camera.combined);

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		walls.setRect(rect);

		batch.begin();
		try {
			walls.render(batch);
		} catch(Exception e) {}

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
