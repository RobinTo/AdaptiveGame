package no.uia.adaptiveTD;

public class EnemyStats {
	int health, speed, goldYield;
	String type, enemyTexture, yellowHealthBar, redHealthBar;

	public EnemyStats(String type, int health, int speed, int goldYield,
			String enemyTexture, String redHealthBar, String yellowHealthBar) {
		this.type = type;
		this.health = health;
		this.speed = speed;
		this.goldYield = goldYield;
		this.enemyTexture = enemyTexture;
		this.yellowHealthBar = yellowHealthBar;
		this.redHealthBar = redHealthBar;
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

	public String getYellowHealthBar() {
		return yellowHealthBar;
	}

	public void setYellowHealthBar(String yellowHealthBar) {
		this.yellowHealthBar = yellowHealthBar;
	}

	public String getRedHealthBar() {
		return redHealthBar;
	}

	public void setRedHealthBar(String redHealthBar) {
		this.redHealthBar = redHealthBar;
	}

}
