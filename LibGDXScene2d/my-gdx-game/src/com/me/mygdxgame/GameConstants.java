package com.me.mygdxgame;

public class GameConstants {

	public static final int tileSize = 64;
	public static final int screenHeight = 768;
	public static final int screenWidth = 1280;
	
	
	public static float morphY(float y)
	{
		return(screenHeight - 64 - y);
	}
}
