package com.me.mygdxgame;

public class TargetCircleOnSelf extends MissileTarget{
	
	int radius;
	
	public TargetCircleOnSelf(int radius)
	{
		super(TargetingStrategy.CircleOnSelf);
		this.radius = radius;
	}
}
