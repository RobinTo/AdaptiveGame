package com.me.mygdxgame;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class MapTile extends ExtendedActor{

	public MapTile(Sprite sprite, float x, float y)
	{
		super(sprite);
		this.setPosition(x, y);
		
	}
}
