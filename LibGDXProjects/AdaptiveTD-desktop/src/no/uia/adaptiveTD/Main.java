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
		cfg.width = GameConstants.screenWidth;
		cfg.height = GameConstants.screenHeight;
		Settings settings = new Settings();
		settings.maxWidth = 512;
		settings.maxHeight = 512;
		
		// For now just make a different path here for each computer, and comment out wrong ones when compiling.
		// String path = "D:\\github\\AdaptiveGame\\LibGDXProjects\\AdaptiveTD-android\\assets\\Images";
		// String path = "C:\\Users\\Robin\\github\\AdaptiveGame\\LibGDXProjects\\AdaptiveTD-android\\assets\\Images";
		String path = "D:\\Github\\AdaptiveGame\\LibGDXProjects\\AdaptiveTD-android\\assets\\Images";	
        TexturePacker2.process(settings, path + "\\Enemies", path, "enemies");
        TexturePacker2.process(settings, path + "\\MiscSmall", path, "misc");
        TexturePacker2.process(settings, path + "\\Towers", path, "towers");
        TexturePacker2.process(settings, path + "\\MapTiles", path, "mapTiles");
		
		new LwjglApplication(new AdaptiveTD(), cfg);
	}
}
