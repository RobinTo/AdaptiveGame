package com.me.mygdxgame;

public class ThinkTankInfo
{
	double lastMetric = 0, currentMetric = 0, challengerMetric = 0;
	float maxJumpDistance = 0.4f;
	final float maxJumpDistanceConst = 0.4f;
	double gameLengthMultiplier = 1.0;
	double playerLevel = 0;

	float nudgeChance;
	float nudgeChanceInGame = 0.2f;

	int lastDifficulty = 0;

	float superEnemyChance = 0; 
	int startGold = 0;
	
	float diggerChance;

	int speedLevel;

	int successiveGameCounter = 0;
	
	public void initialize()
	{
		nudgeChance = 0;
		currentMetric = 0;
		challengerMetric = 0;
		maxJumpDistance = 0.4f;
		gameLengthMultiplier = 1.0;
		playerLevel = 0;
		startGold = 100;
	}
}
