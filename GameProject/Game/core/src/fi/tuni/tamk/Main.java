package fi.tuni.tamk;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * The main class which puts the game running.
 */
public class Main extends Game {

	public static float viewPortWidth;
	public static float viewPortHeight;
	public static int howMany = 20;
	public static float oneWidth = 10f / howMany;
	public static boolean isPortrait;

	SpriteBatch batch;

	@Override
	public void create () {
		checkDevice();
		batch = new SpriteBatch();
		setScreen(new MenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
	}

	public void checkDevice() {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		if(width > height) {
			isPortrait = false;
		} else {
			isPortrait = true;
		}
		if(isPortrait) {
			viewPortWidth = 10;
			float density = width / 10f;
			viewPortHeight = height / density;
		} else {
			viewPortHeight = 10;
			float density = height / 10f;
			viewPortWidth = width / density;
		}

	}
}
