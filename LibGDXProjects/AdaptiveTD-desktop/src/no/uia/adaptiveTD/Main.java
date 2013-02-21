package no.uia.adaptiveTD;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "AdaptiveTD";
		cfg.useGL20 = false;
		cfg.width = 480;
		cfg.height = 320;
		Settings settings = new Settings();
		settings.maxWidth = 512;
		settings.maxHeight = 512;
        TexturePacker2.process(settings, "assets\\images", "assets\\Images", "game");
		
		new LwjglApplication(new AdaptiveTD(), cfg);
	}
}
