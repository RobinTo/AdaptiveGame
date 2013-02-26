package com.me.mygdxgame;

public class TowerStats {
	String type, towerTexture1, towerTexture2, towerTexture3, missileTexture;
	float reloadTime;
	int damage1, damage2, damage3, buildCost, upgradeCost1, upgradeCost2,
			maxLevel, range, slowPercentage1, slowPercentage2, slowPercentage3,
			slowDuration1, slowDuration2, slowDuration3;

	public TowerStats(String type, String towerTexture1, String towerTexture2,
			String towerTexture3, String missileTexture, float ReloadTime,
			int damage1, int damage2, int damage3, int buildCost,
			int upgradeCost1, int upgradeCost2, int maxLevel, int range,
			int slowPercentage1, int slowPercentage2, int slowPercentage3,
			int slowDuration1, int slowDuration2, int slowDuration3) {
		this.type = type;
		this.towerTexture1 = towerTexture1;
		this.towerTexture2 = towerTexture2;
		this.towerTexture3 = towerTexture3;
		this.missileTexture = missileTexture;
		this.reloadTime = ReloadTime;
		this.damage1 = damage1;
		this.damage2 = damage2;
		this.damage3 = damage3;
		this.buildCost = buildCost;
		this.upgradeCost1 = upgradeCost1;
		this.upgradeCost2 = upgradeCost2;
		this.maxLevel = maxLevel;
		this.range = range * GameConstants.tileSize;
		this.slowPercentage1 = slowPercentage1;
		this.slowPercentage2 = slowPercentage2;
		this.slowPercentage3 = slowPercentage3;
		this.slowDuration1 = slowDuration1;
		this.slowDuration2 = slowDuration2;
		this.slowDuration3 = slowDuration3;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTowerTexture(int currentLevel) {
		switch (currentLevel) {
		case 2:
			return towerTexture1;
		case 3:
			return towerTexture2;
		default:
			return towerTexture3;
		}
	}

	public void setTowerTexture1(String towerTexture1) {
		this.towerTexture1 = towerTexture1;
	}

	public void setTowerTexture2(String towerTexture2) {
		this.towerTexture2 = towerTexture2;
	}

	public void setTowerTexture3(String towerTexture3) {
		this.towerTexture3 = towerTexture3;
	}

	public String getMissileTexture() {
		return missileTexture;
	}

	public void setMissileTexture(String missileTexture) {
		this.missileTexture = missileTexture;
	}

	public float getReloadTime() {
		return reloadTime;
	}

	public void setReloadTime(float reloadTime) {
		this.reloadTime = reloadTime;
	}

	public int getDamage(int currentLevel) {
		switch (currentLevel) {
		case 2:
			return damage2;
		case 3:
			return damage3;
		default:
			return damage1;

		}
	}

	public void setDamage1(int damage1) {
		this.damage1 = damage1;
	}

	public void setDamage2(int damage2) {
		this.damage2 = damage2;
	}

	public void setDamage3(int damage3) {
		this.damage3 = damage3;
	}

	public int getSlowPercentage(int currentLevel){
		switch (currentLevel) {
		case 2:
			return slowPercentage2;
		case 3:
			return slowPercentage3;
		default:
			return slowPercentage1;
		}
	}
	
	public int getSlowDuration(int currentLevel){
		switch (currentLevel) {
		case 2:
			return slowDuration2;
		case 3:
			return slowDuration3;
		default:
			return slowDuration1;
		}
	}
	
	public int getBuildCost() {
		return buildCost;
	}

	public void setBuildCost(int buildCost) {
		this.buildCost = buildCost;
	}

	public int getUpgradeCost1() {
		return upgradeCost1;
	}

	public void setUpgradeCost1(int upgradeCost1) {
		this.upgradeCost1 = upgradeCost1;
	}

	public int getUpgradeCost2() {
		return upgradeCost2;
	}

	public void setUpgradeCost2(int upgradeCost2) {
		this.upgradeCost2 = upgradeCost2;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

}

