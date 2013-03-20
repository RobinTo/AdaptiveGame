package com.me.mygdxgame;

public class TowerStats
{

	MissileEffect missileEffects;

	String type, towerTexture, upgradesTo, missileTexture, shootSound,
			impactSound;
	int sellPrice, upgradeCost, buildCost, range, radius;
	float reloadTime;
	boolean buildable;
	String description;

	public TowerStats(String type, String description, String upgradesTo, String towerTexture,
			String missileTexture, int sellPrice, int upgradeCost,
			int buildCost, MissileEffect missileEffects, float reloadTime,
			int range, int radius, boolean buildable, String shootSound,
			String impactSound)
	{
		this.type = type;
		this.description = description;
		this.towerTexture = towerTexture;
		this.missileEffects = missileEffects;
		this.sellPrice = sellPrice;
		this.upgradesTo = upgradesTo;
		this.upgradeCost = upgradeCost;
		this.buildCost = buildCost;
		this.missileTexture = missileTexture;
		this.reloadTime = reloadTime;
		this.range = range;
		this.radius = radius;
		this.buildable = buildable;
		this.shootSound = shootSound;
		this.impactSound = impactSound;
	}
}
