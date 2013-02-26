package com.me.mygdxgame;

public class EnemyStats {
	String type, enemyTexture;
	int health, speed, goldYield;

	public EnemyStats(String type, String enemyTexture, int health, float speed, int goldYield) {
		this.type = type;
		this.enemyTexture = enemyTexture;
		this.health = health;
		this.speed = (int)(speed * GameConstants.tileSize);
		this.goldYield = goldYield;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getGoldYield() {
		return goldYield;
	}

	public void setGoldYield(int goldYield) {
		this.goldYield = goldYield;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEnemyTexture() {
		return enemyTexture;
	}

	public void setEnemyTexture(String enemyTexture) {
		this.enemyTexture = enemyTexture;
	}


}
