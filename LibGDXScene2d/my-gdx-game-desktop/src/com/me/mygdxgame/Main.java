package com.me.mygdxgame;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;

public class Main
{
	public static void main(String[] args)
	{
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "my-gdx-game";
		cfg.useGL20 = false;

		cfg.width = GameConstants.screenWidth;
		cfg.height = GameConstants.screenHeight;

		Settings settings = new Settings();
		settings.maxWidth = 1024;
		settings.maxHeight = 1024;

		// This part regenerates texture atlases each time the desktop project is ran.
		// Remove both for no recompiling of images.
		//packForPublish(settings);
		packForCompiling(settings);

		new LwjglApplication(new MyGdxGame(), cfg);
	}

	private static void packForPublish(Settings settings)
	{
		// ----------------------------------------------
		// Use this to deploy, if we want people to be able to change the images. 
		String path = "Images";
		// Make copies in desktop project
		TexturePacker2.process(settings, path + "\\Enemies", "Images",
				"enemies");
		TexturePacker2
				.process(settings, path + "\\MiscSmall", "Images", "misc");
		TexturePacker2.process(settings, path + "\\Towers", "Images", "towers");
		TexturePacker2.process(settings, path + "\\MapTiles", "Images",
				"mapTiles");
		TexturePacker2.process(settings, path + "\\LargeImages", "Images",
				"largeImages");
		// ----------------------------------------------
	}

	private static void packForCompiling(Settings settings)
	{
		// Enter path to android asset folder on your computer, or remove this to compile if you want
		// to compile and no new images have been added.
		boolean packed = false;
		String path = "D:\\github\\AdaptiveGame\\LibGDXScene2d\\my-gdx-game-android\\assets\\Images";
		try
		{
			packFiles(path, settings);
			packed = true;
		}
		catch(Exception e)
		{System.out.println(e.getMessage());}
		if(!packed)
		{
			path = "C:\\Users\\Robin\\github\\AdaptiveGame\\LibGDXScene2d\\my-gdx-game-android\\assets\\Images";
			try
			{
				packFiles(path, settings);
			} catch (Exception e)
			{System.out.println(e.getMessage());
			}
		}
		
	}
	
	private static void packFiles(String path, Settings settings)
	{
		// Make copies in android project
		TexturePacker2
				.process(settings, path + "\\Enemies", "../my-gdx-game-android/assets/Images", "enemies"); // settings, source path, destination path, texture atlas file name
		TexturePacker2
				.process(settings, path + "\\MiscSmall", "../my-gdx-game-android/assets/Images", "misc");
		TexturePacker2
				.process(settings, path + "\\Towers", "../my-gdx-game-android/assets/Images", "towers");
		TexturePacker2
				.process(settings, path + "\\MapTiles", "../my-gdx-game-android/assets/Images", "mapTiles");
		TexturePacker2
			.process(settings, path + "\\LargeImages", "../my-gdx-game-android/assets/Images", "largeImages");

		// Make copies in desktop project
		TexturePacker2
				.process(settings, path + "\\Enemies", "../my-gdx-game-desktop/bin/Images", "enemies");
		TexturePacker2
				.process(settings, path + "\\MiscSmall", "../my-gdx-game-desktop/bin/Images", "misc");
		TexturePacker2
				.process(settings, path + "\\Towers", "../my-gdx-game-desktop/bin/Images", "towers");
		TexturePacker2
				.process(settings, path + "\\MapTiles", "../my-gdx-game-desktop/bin/Images", "mapTiles");
		TexturePacker2
			.process(settings, path + "\\LargeImages", "../my-gdx-game-desktop/bin/Images", "largeImages");
	}
}
