package com.me.mygdxgame;

public class TowerStats {
	
	String type, towerTexture, upgradesTo, missileTexture;
	
	MissileEffect missileEffects;

	int sellPrice, upgradeCost, buildCost, range, radius;
	float reloadTime;
	
	public TowerStats(String type, String upgradesTo, String towerTexture, String missileTexture, int sellPrice, int upgradeCost, int buildCost, MissileEffect missileEffects, float reloadTime, int range, int radius) {
		this.type = type;
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
	}
}

