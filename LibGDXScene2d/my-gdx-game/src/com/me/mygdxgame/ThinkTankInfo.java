package com.me.mygdxgame;

public class ThinkTankInfo
{
	double lastMetric = 0, currentMetric = 0, challengerMetric = 0;
	float maxJumpDistance = 0.2f;
	double gameLengthMultiplier = 1.0;
	double playerLevel = 0;
	
	double totalHappinessDiggersOn = 0;
	double totalHappinessDiggersOff = 0;
	double totalHappinessSuperMobsOn = 0;
	double totalHappinessSuperMobsOff = 0;
	double totalHappinessEarthquakeOn = 0;
	double totalHappinessEarthquakeOff = 0;

	int totalGames = 0;
	
	public void initialize()
	{
		lastMetric = 0;
		currentMetric = 0;
		challengerMetric = 0;
		maxJumpDistance = 0.2f;
		gameLengthMultiplier = 1.0;
		playerLevel = 0;
		totalHappinessDiggersOff = 0;
		totalHappinessDiggersOn = 0;
		totalHappinessEarthquakeOff = 0;
		totalHappinessEarthquakeOn = 0;
		totalHappinessSuperMobsOff = 0;
		totalHappinessSuperMobsOn = 0;
		totalGames = 0;
	}
}
