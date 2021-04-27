package fi.tuni.tamk.tiko1d;

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
	public static float oneWidth = 10f / howMany / 2;
	public static boolean isPortrait = false;

	SpriteBatch batch;

	@Override
	public void create () {
		checkDevice();
		GameConfiguration.checkFirstTime();
		batch = new SpriteBatch();
		if(GameConfiguration.open("name").equals(GameConfiguration.noValue)) {
			setScreen(new PlayerName(this, true));
		} else {
			setScreen(new MenuScreen(this));
		}
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
	}

	public void checkDevice() {
		float radius = Gdx.graphics.getWidth() / 2f;
		if(radius > Gdx.graphics.getHeight() * 9 / 10f) {
			radius = Gdx.graphics.getHeight() * 9 / 10f;
		}
		float multiplier = Gdx.graphics.getWidth() / 2f / radius * 0.9f;

		viewPortWidth = 10 * multiplier;
		viewPortHeight = (float) Gdx.graphics.getHeight() / Gdx.graphics.getWidth() * 10 * multiplier;
	}
}
