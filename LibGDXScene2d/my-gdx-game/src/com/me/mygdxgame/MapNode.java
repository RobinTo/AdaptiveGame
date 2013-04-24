package com.me.mygdxgame;

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
