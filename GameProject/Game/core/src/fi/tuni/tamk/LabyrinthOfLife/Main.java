package fi.tuni.tamk.LabyrinthOfLife;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

/**
 * The main class.
 *
 * @author Artur Haavisto
 */
public class Main extends Game {

	public static float viewPortWidth;
	public static float viewPortHeight;
	public static int howMany = 20;
	public static float oneWidth = 10f / howMany / 2;
	public static boolean isPortrait = false;

	public Sounds sounds;
	public Textures textures;

	@Override
	public void create () {
		this.sounds = new Sounds();
		this.textures = new Textures();
		checkDevice();
		GameConfiguration.checkFirstTime();
		if(GameConfiguration.open("name").equals(GameConfiguration.noValue)) {
			setScreen(new PlayerNameScreen(this, true));
		} else {
			setScreen(new MenuScreen(this));
		}
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {}

	/**
	 * checks the device display portions.
	 */
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
