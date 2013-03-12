package com.me.mygdxgame;

public class TargetSingle extends MissileTarget{
	Enemy targetEnemy;
	
	public TargetSingle(Enemy targetEnemy)
	{
		super(TargetingStrategy.Single);
		this.targetEnemy = targetEnemy;
	}
	
}
