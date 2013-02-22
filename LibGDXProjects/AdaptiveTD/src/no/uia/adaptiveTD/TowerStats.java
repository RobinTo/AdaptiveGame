package no.uia.adaptiveTD;

public class TowerStats {
	String type, towerTexture, missileTexture;
	float reloadTime;
	int damage, goldCost, upgradeCost, maxLevel, range;

	public TowerStats(String type, String towerTexture, String missileTexture,
			float ReloadTime, int damage, int goldCost, int upgradeCost,
			int maxLevel, int range) {
		this.type = type;
		this.towerTexture = towerTexture;
		this.missileTexture = missileTexture;
		this.reloadTime = ReloadTime;
		this.damage = damage;
		this.goldCost = goldCost;
		this.upgradeCost = upgradeCost;
		this.maxLevel = maxLevel;
		this.range = range * GameConstants.tileSize;
	}

	//Getters and setters
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public String getTowerTexture() {
		return towerTexture;
	}

	public void setTowerTexture(String towerTexture) {
		this.towerTexture = towerTexture;
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

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public int getGoldCost() {
		return goldCost;
	}

	public void setGoldCost(int goldCost) {
		this.goldCost = goldCost;
	}

	public int getUpgradeCost() {
		return upgradeCost;
	}

	public void setUpgradeCost(int upgradeCost) {
		this.upgradeCost = upgradeCost;
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