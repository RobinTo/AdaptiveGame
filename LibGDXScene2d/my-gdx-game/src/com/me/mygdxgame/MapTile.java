package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class MapTile extends ExtendedActor{

	List<Direction> possibleDirections = new ArrayList<Direction>();
	
	public MapTile(Sprite sprite, float x, float y)
	{
		super(sprite);
		this.setPosition(x, y);
		
	}
}
