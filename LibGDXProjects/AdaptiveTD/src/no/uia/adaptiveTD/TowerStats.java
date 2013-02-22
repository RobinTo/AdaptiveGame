package no.uia.adaptiveTD;

public class TowerStats {
	String type, towerTexture1, towerTexture2, towerTexture3, missileTexture;
	float reloadTime;
	int damage1, damage2, damage3, buildCost, upgradeCost1, upgradeCost2, maxLevel, range;

	public TowerStats(String type, String towerTexture1,String towerTexture2,String towerTexture3, String missileTexture,
			float ReloadTime, int damage1, int damage2, int damage3, int buildCost, int upgradeCost1, int upgradeCost2,
			int maxLevel, int range) {
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
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTowerTexture(int currentLevel) {
		switch (currentLevel)
		{
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
		switch (currentLevel)
		{
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




/*


    DamageOverTime damageOverTime;
    public DamageOverTime DamageOverTime
    {
        get { return damageOverTime; }
        set { damageOverTime = value; }
    }
    Slow slow;
    public Slow Slow
    {
        get { return slow; }
        set { slow = value; }
    }
    AreaOfEffect areaOfEffect;

    public AreaOfEffect AreaOfEffect
    {
        get { return areaOfEffect; }
        set { areaOfEffect = value; }
    }
public struct AreaOfEffect
{
    float radius;
    public float Radius
    {
        get { return radius; }
        set { radius = value; }
    }
    public AreaOfEffect(float radius)
    {
        this.radius = radius;
    }
}

public struct DamageOverTime
{
    int damagePerTick;

    public int DamagePerTick
    {
        get { return damagePerTick; }
        set { damagePerTick = value; }
    }
    int ticks;

    public int Ticks
    {
        get { return ticks; }
        set { ticks = value; }
    }
    float duration;

    public float Duration
    {
        get { return duration; }
        set { duration = value; }
    }
    float durationSinceLastTick;

    float remainingDuration;
    public float RemainingDuration
    {
        get { return remainingDuration; }
        set { remainingDuration = value; }
    }

    public float DurationSinceLastTick
    {
        get { return durationSinceLastTick; }
        set { durationSinceLastTick = value; }
    }

    public DamageOverTime(bool parameter)
    {
        this.damagePerTick = 0;
        this.ticks = 2;
        this.duration = 0;
        this.remainingDuration = 0;
        this.durationSinceLastTick = duration / ticks;
    }
    public DamageOverTime(int damagePerTick, int ticks, float duration)
    {
        this.damagePerTick = damagePerTick;
        this.ticks = ticks;
        this.duration = duration;
        this.remainingDuration = duration;
        this.durationSinceLastTick = duration / ticks;
    }
}

public struct Slow
{
    int percentage;

    public int Percentage
    {
        get { return percentage; }
        set { percentage = value; }
    }
    float duration;

    public float Duration
    {
        get { return duration; }
        set { duration = value; }
    }

    public Slow(bool parameter)
    {
        this.percentage = 0;
        this.duration = 0;
    }
    public Slow(int percentage, float duration)
    {
        this.percentage = percentage;
        this.duration = duration;
    }
}

}

*/