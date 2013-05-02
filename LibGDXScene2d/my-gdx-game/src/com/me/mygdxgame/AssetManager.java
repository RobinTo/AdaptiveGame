package com.me.mygdxgame;

import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class AssetManager
{
	BitmapFont font;
	
	HashMap<String, Sound> sounds = new HashMap<String, Sound>();
	HashMap<Integer, Music> allMusic = new HashMap<Integer, Music>();
	Music currentSong;
	int currentTrack, totalTracks;
	
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
	public void loadSounds(HashMap<String,TowerStats> towerInfo)
	{
		Iterator<String> it = towerInfo.keySet().iterator();
		while (it.hasNext())
		{
			String s = it.next();
			if (!towerInfo.get(s).impactSound.equals(""))
				sounds.put(towerInfo.get(s).impactSound, Gdx.audio.newSound(Gdx.files.internal("sounds/" + towerInfo.get(s).impactSound)));
			if (!towerInfo.get(s).shootSound.equals(""))
				sounds.put(towerInfo.get(s).shootSound, Gdx.audio.newSound(Gdx.files.internal("sounds/" + towerInfo.get(s).shootSound)));
		}
	}
	public void loadMusic()
	{
		allMusic.put(1, Gdx.audio.newMusic(Gdx.files.internal("Music/Cops.mp3")));
		allMusic.put(2, Gdx.audio.newMusic(Gdx.files.internal("Music/Race.mp3")));
		allMusic.put(3, Gdx.audio.newMusic(Gdx.files.internal("Music/SimianAcres.mp3")));
		totalTracks = 3;
		currentTrack = 1;
		currentSong = allMusic.get(currentTrack);
		currentSong.setVolume(0.1f);
		currentSong.play();
	}
	public void checkMusic()
	{
		if (!currentSong.isPlaying())
		{
			currentTrack = (currentTrack == totalTracks) ? (1) : (currentTrack + 1);
			currentSong.dispose();
			currentSong = allMusic.get(currentTrack);
			currentSong.play();
		}
	}
}
