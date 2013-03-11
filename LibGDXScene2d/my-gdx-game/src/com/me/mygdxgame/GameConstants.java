package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.files.FileHandle;

public class GameConstants {

	public static final int tileSize = 64;
	public static final int screenHeight = 768;
	public static final int screenWidth = 1280;
	public static final int startGold = 100;
	public static final int startLives = 10;
	
	public static float morphY(float y)
	{
		return(screenHeight - 64 - y);
	}
	
	public static List<String> readRawTextFile(FileHandle handle)
    {
		List<String> fileContent = new ArrayList<String>();
		
		String content = handle.readString();
		int last = 0;
		for(int i = 0; i<content.length(); i++)
		{
			if(content.charAt(i) == '\n')
			{
				String addString = content.substring(last, i);
				addString = addString.replace("\n", "");
	        	addString = addString.replace("\r", "");
				fileContent.add(addString);
				last = i;
			}
		}
		return fileContent;
    }
}
