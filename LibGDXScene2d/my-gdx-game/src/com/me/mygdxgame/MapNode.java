package com.me.mygdxgame;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class MapNode
{
	float g;
	float h;
	float f;

	int x, y;
	
	MapNode parentNode;
	
	public MapNode(int x, int y, float g, float h)
	{
		this.x = x;
		this.y = y;
		this.g = g;
		this.h = h;
		this.f = g+h;
	}
	
	public void updateG(int g)
	{
		this.g = g;
		this.f = g+h;
	}
}
