package com.me.mygdxgame;

public class TargetLine extends MissileTarget{

	int x1, x2, y1, y2;
	public TargetLine(int x1, int y1, int x2, int y2)
	{
		super(TargetingStrategy.Line);
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
}
