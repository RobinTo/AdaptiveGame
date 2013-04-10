package com.me.mygdxgame;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class AssetManager
{
	BitmapFont font;
	
	HashMap<String, Sound> sounds = new HashMap<String, Sound>();
	
	TextureAtlas mapTilesAtlas; // Map tiles
	TextureAtlas enemiesAtlas; // Enemies
	TextureAtlas miscAtlas; // Various small stuff like bullets, health bar,
							// sell and upgrade buttons
	TextureAtlas towersAtlas; // Towers
	
	public void initialize()
	{
		font = new BitmapFont(Gdx.files.internal("Fonts/KatyBerry.fnt"), Gdx.files.internal("Fonts/KatyBerry_0.tga"), false);

		mapTilesAtlas = new TextureAtlas(Gdx.files.internal("Images/mapTiles.atlas"));
		enemiesAtlas = new TextureAtlas(Gdx.files.internal("Images/enemies.atlas"));
		miscAtlas = new TextureAtlas(Gdx.files.internal("Images/misc.atlas"));
		towersAtlas = new TextureAtlas(Gdx.files.internal("Images/towers.atlas"));
	}
		
}
