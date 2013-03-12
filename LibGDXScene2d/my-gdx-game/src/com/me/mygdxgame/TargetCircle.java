package com.me.mygdxgame;

public class TargetCircle extends MissileTarget{

	int radius;
	
	public TargetCircle(int radius)
	{
		super(TargetingStrategy.Circle);
		this.radius = radius;
	}
}
