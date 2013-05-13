package com.me.mygdxgame;

public class ThinkTankInfo
{
	double lastMetric = 0, currentMetric = 0, challengerMetric = 0;
	float maxJumpDistance = 0.2f;
	double gameLengthMultiplier = 1.0;
	double playerLevel = 0;

	float nudgeChanceInGame = 0.2f;

	int lastDifficulty = 0;
	
	int totalGames = 0;
	
	public void initialize()
	{
		lastMetric = 0;
		currentMetric = 0;
		challengerMetric = 0;
		maxJumpDistance = 0.4f;
		gameLengthMultiplier = 1.0;
		playerLevel = 0;
		totalGames = 0;
	}
}
