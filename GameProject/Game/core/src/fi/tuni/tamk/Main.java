package fi.tuni.tamk;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
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
	private StaticObjects walls;
	private Rectangle rect;

	private Stage stage;
	private Button button;

	private boolean created = false;
	private boolean notFinished = true;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, VIEW_PORT_WIDTH, VIEW_PORT_HEIGHT);

		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);

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
				if(notFinished) {
					notFinished = false;
					if(created) {
						walls = null;
						generator = null;
						rect = null;
					}
					generator = new MapGenerator();
					rect = new Rectangle(1, 1, VIEW_PORT_WIDTH - 2, VIEW_PORT_WIDTH - 2);
					walls = new StaticObjects(generator.createMap(15, 21), rect);
					created = true;
					notFinished = true;
				}
			}
		});
		stage.addActor(button);
	}

	@Override
	public void render () {
		batch.setProjectionMatrix(camera.combined);

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if(created) {
			walls.setRect(rect);
		}
		stage.draw();
		batch.begin();
		try {
			walls.render(batch);
		} catch(Exception e) {}

		stage.act();

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
