package fi.tuni.tamk.LabyrinthOfLife.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import fi.tuni.tamk.LabyrinthOfLife.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 2000;
		config.height = 1000;
		new LwjglApplication(new Main(), config);
	}
}
