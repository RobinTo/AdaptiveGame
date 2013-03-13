package com.me.mygdxgame;

public class TargetCircle extends MissileTarget{

	int radius;
	int x1, y1;
	
	public TargetCircle(int targetX, int targetY, int radius)
	{
		super(TargetingStrategy.Circle);
		this.radius = radius;
		this.x1 = targetX;
		this.y1 = targetY;
	}
}
