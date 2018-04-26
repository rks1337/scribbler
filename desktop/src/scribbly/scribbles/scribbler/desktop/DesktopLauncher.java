package scribbly.scribbles.scribbler.desktop;

import scribbly.scribbles.scribbler.core.Boot;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.addIcon("scrib_32.png", Files.FileType.Internal);
		config.title = "Physics Scribbler";
		config.vSyncEnabled = true;

		//ideal resolutions
		//1280 720
		//1280 800
		//1366 768

		//1920 1080 
		//1920 1200
		//2048 1536

		//2560 1440
		//2560 1600
		//2560 1700 

		//2960 1440

		config.width = 1280;
		config.height = 720;

		//		System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
		//		config.width = LwjglApplicationConfiguration.getDesktopDisplayMode().width;
		//		config.height = LwjglApplicationConfiguration.getDesktopDisplayMode().height;
		//		config.fullscreen = false;

		new LwjglApplication(new Boot(), config);
	}
}    