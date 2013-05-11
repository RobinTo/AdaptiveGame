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
		sounds.put("earthquake", Gdx.audio.newSound(Gdx.files.internal("sounds/earthquake.mp3")));
		sounds.put("diggerEnemy", Gdx.audio.newSound(Gdx.files.internal("sounds/diggerEnemy.mp3")));
		sounds.put("superEnemy", Gdx.audio.newSound(Gdx.files.internal("sounds/superEnemy.mp3")));
		sounds.put("MonsterSpeedIncreased",  Gdx.audio.newSound(Gdx.files.internal("sounds/monsterSpeedIncreased.mp3")));
		sounds.put("MonsterSpeedDecreased",  Gdx.audio.newSound(Gdx.files.internal("sounds/monsterSpeedDecreased.mp3")));
	}
	public void loadMusic()
	{
		allMusic.put(6, Gdx.audio.newMusic(Gdx.files.internal("sounds/earthquake.mp3")));
		allMusic.put(1, Gdx.audio.newMusic(Gdx.files.internal("music/slow.mp3")));
		allMusic.put(2, Gdx.audio.newMusic(Gdx.files.internal("music/mid.mp3")));
		allMusic.put(3, Gdx.audio.newMusic(Gdx.files.internal("music/fast.mp3")));
		allMusic.put(4, Gdx.audio.newMusic(Gdx.files.internal("music/faster.mp3")));
		allMusic.put(5, Gdx.audio.newMusic(Gdx.files.internal("music/crazy.mp3")));
		totalTracks = allMusic.size();
		currentTrack = 2;
		currentSong = allMusic.get(currentTrack);
		currentSong.setVolume(0.5f);
	}
	public void checkMusic()
	{
		if (!currentSong.isPlaying())
		{
			//Repeat
			currentSong.setVolume(0.5f);
			currentSong.play();
			/* Playlist
			currentTrack = (currentTrack == totalTracks) ? (1) : (currentTrack + 1);
			currentSong.dispose();
			currentSong = allMusic.get(currentTrack);
			currentSong.play();
			*/
		}
	}
	public void playSong(int number)
	{
		currentSong.stop();
		currentSong = allMusic.get(number);
		currentSong.setVolume(0.5f);
		currentSong.play();
	}
}
