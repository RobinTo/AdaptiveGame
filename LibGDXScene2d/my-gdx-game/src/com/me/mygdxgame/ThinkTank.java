package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class ThinkTank
{
	HashMap<String, TowerStats> defaultTowerInfo = new HashMap<String, TowerStats>();
	HashMap<String, EnemyStats> defaultEnemyInfo = new HashMap<String, EnemyStats>();
	HashMap<String, TowerStats> towerInfo = new HashMap<String, TowerStats>();
	HashMap<String, EnemyStats> enemyInfo = new HashMap<String, EnemyStats>();
	HashMap<Integer, HashMap<String, Float>> measurements = new HashMap<Integer, HashMap<String, Float>>();
	HashMap<String, Parameter> parameters = new HashMap<String, Parameter>();;
	HashMap<String, Parameter> oldParameters = new HashMap<String, Parameter>();
	HashMap<String, Relation> relations = new HashMap<String, Relation>();
	int timeBetweenMeasurements = 1;
	ThinkTankInfo thinkTankInfo;

	public ThinkTank()
	{
		thinkTankInfo = new ThinkTankInfo();
	}

	public void initializeParameters(FileHandle fileHandle)
	{
		if (fileHandle.exists())
		{
			// Fetch parameters from disk
			List<String> fileContent = GameConstants.readRawTextFile(fileHandle);

			for (int counter = 0; counter < fileContent.size(); counter++)
			{
				String line = fileContent.get(counter);
				String[] split = line.split(":");

				if (split[0].equals("gameLengthMultiplier"))
					thinkTankInfo.gameLengthMultiplier = Float.valueOf(split[1]);
				else if (split[0].equals("currentMetric"))
					thinkTankInfo.currentMetric = Float.valueOf(split[1]);
				else if (split[0].equals("maxJumpDistance"))
					thinkTankInfo.maxJumpDistance = Float.valueOf(split[1]);
				else
					parameters.put(split[0], new Parameter(split[0], Float.valueOf(split[1]), Float.valueOf(split[2]), Float.valueOf(split[3])));
			}
		}
		else
		{
			// Create new default parameters
			parameters.put("GlobalMonsterHP", new Parameter("GlobalMonsterHP", 1.0f));
			parameters.put("TEDotDamage", new Parameter("TEDotDamage", 1.0f));
			parameters.put("TEDotTicks", new Parameter("TEDotTicks", 1.0f));
			parameters.put("TESlowPercentage", new Parameter("TESlowPercentage", 1.0f, 0.1f, 1.4f));
			parameters.put("TESlowDuration", new Parameter("TESlowDuration", 1.0f));
			parameters.put("GlobalReloadTime", new Parameter("GlobalReloadTime", 1.0f));
			parameters.put("TEDamage", new Parameter("TEDamage", 1.0f, 0.01f, 10.0f));
			parameters.put("GlobalBuildCost", new Parameter("GlobalBuildCost", 1.0f));
			parameters.put("GlobalMonsterSpeed", new Parameter("GlobalMonsterSpeed", 1.0f, 0.1f, 10.0f));
			parameters.put("GlobalMonsterGoldYield", new Parameter("GlobalMonsterGoldYield", 1.0f));
			parameters.put("GlobalTowerRange", new Parameter("GlobalTowerRange", 1.0f, 0.1f, 10.0f));
			parameters.put("DiggerChance", new Parameter("DiggerChance", 0.2f, 0.0f, 1.0f));// Digger chance eats of the 0.5 set for Normal mob chance.parameters.put("SuperChance", new Parameter("SuperChance", 0.02f, 0.0f, 1.0f));// Set to 0 to disable super minions. Could add a seperate number for each type, if we desire.
			parameters.put("SuperChance", new Parameter("SuperChance", 0.2f, 0.0f, 1.0f));
			parameters.put("EarthquakeChance", new Parameter("EarthquakeChance", 0.2f, 0.0f, 1.0f)); // Earthquake enabled or not (Changing every 5th second)
			parameters.put("EarthquakeChanceInGame", new Parameter("EarthquakeChanceInGame", 0.2f, 0.1f, 0.9f)); // Earthquake chance for every second when earthquake is enabled.
		}

		thinkTankInfo.initialize();
		calculateSpeedLevel();

		setNewStats();
	}

	public void initializeRelations(FileHandle fileHandle)
	{
		if (fileHandle.exists())
		{
			// Fetch relations from disk
			List<String> fileContent = GameConstants.readRawTextFile(fileHandle);
			List<String> lines = new ArrayList<String>();
			for (int lineCounter = 0; lineCounter < fileContent.size(); lineCounter++)
			{
				String line = fileContent.get(lineCounter);
				if (line.equalsIgnoreCase("endRelation"))
				{
					Relation newRelation = new Relation(lines.get(0), parameters.get(lines.get(1)));
					for (int counter = 2; counter < lines.size(); counter++)
					{
						String[] split = lines.get(counter).split(":");
						newRelation.addRelatedParameter(parameters.get(split[0]), Float.valueOf(split[1]));
					}
					relations.put(newRelation.name, newRelation);
					lines.clear();
				}
				else
				{
					lines.add(line);
				}
			}
		}
		else
		{
			// Create new gameplay relations
			Relation tempRelation;

			tempRelation = new Relation("GlobalMonsterHP_TEDamage_GlobalMonsterSpeed", parameters.get("GlobalMonsterHP"));
			tempRelation.addRelatedParameter(parameters.get("TEDamage"), 0.5f);
			tempRelation.addRelatedParameter(parameters.get("GlobalMonsterSpeed"), -0.5f);
			relations.put("GlobalMonsterHP_TEDamage_GlobalMonsterSpeed", tempRelation);

			tempRelation = new Relation("GlobalMonsterHP_GlobalMonsterSpeed", parameters.get("GlobalMonsterHP"));
			tempRelation.addRelatedParameter(parameters.get("GlobalMonsterSpeed"), -1.0f);
			relations.put("GlobalMonsterHP_GlobalMonsterSpeed", tempRelation);

			tempRelation = new Relation("GlobalMonsterSpeed_GlobalTowerRange", parameters.get("GlobalMonsterSpeed"));
			tempRelation.addRelatedParameter(parameters.get("GlobalTowerRange"), 1.0f);
			relations.put("GlobalMonsterSpeed_GlobalTowerRange", tempRelation);

			tempRelation = new Relation("GlobalMonsterSpeed_GlobalReloadTime", parameters.get("GlobalMonsterSpeed"));
			tempRelation.addRelatedParameter(parameters.get("GlobalReloadTime"), -1.0f);
			relations.put("GlobalMonsterSpeed_GlobalReloadTime", tempRelation);

			tempRelation = new Relation("GlobalBuildCost_GlobalMonsterGoldYield", parameters.get("GlobalBuildCost"));
			tempRelation.addRelatedParameter(parameters.get("GlobalMonsterGoldYield"), 1.0f);
			relations.put("GlobalBuildCost_GlobalMonsterGoldYield", tempRelation);

			tempRelation = new Relation("GlobalReloadTime_TEDamage", parameters.get("GlobalReloadTime"));
			tempRelation.addRelatedParameter(parameters.get("TEDamage"), 1.0f);
			relations.put("GlobalReloadTime_TEDamage", tempRelation);

			tempRelation = new Relation("TESlowPercentage_TESlowDuration", parameters.get("TESlowPercentage"));
			tempRelation.addRelatedParameter(parameters.get("TESlowDuration"), -1.0f);
			relations.put("TESlowPercentage_TESlowDuration", tempRelation);

			tempRelation = new Relation("TEDotTicks_TEDotDamage", parameters.get("TEDotTicks"));
			tempRelation.addRelatedParameter(parameters.get("TEDotDamage"), -1.0f);
			relations.put("TEDotTicks_TEDotDamage", tempRelation);

			tempRelation = new Relation("DiggerChance", parameters.get("DiggerChance"));
			relations.put("DiggerChance", tempRelation);

			tempRelation = new Relation("SuperChance", parameters.get("SuperChance"));
			relations.put("SuperChance", tempRelation);

			tempRelation = new Relation("EarthquakeChanceInGame", parameters.get("EarthquakeChanceInGame"));
			relations.put("EarthquakeChanceInGame", tempRelation);

			tempRelation = new Relation("EarthquakeChance", parameters.get("EarthquakeChance"));
			relations.put("EarthquakeChance", tempRelation);

		}
	}

	public void calculateVariety(float totalTime, List<Tower> towers)
	{
		// Calculate tower variety
		if (Math.floor(totalTime) % timeBetweenMeasurements == 0 && !measurements.containsKey((int) Math.floor(totalTime)))
		{
			float variety = 0;
			List<String> types = new ArrayList<String>();
			for (Tower t : towers)
			{
				if (!types.contains(t.towerStats.type))
				{
					variety++;
					types.add(t.towerStats.type);
				}
			}
			variety = variety / towerInfo.size();
		}
	}

	public void calculateVariables(int happy, int difficult, int livesLeft)
	{
		// ---Calculate playerLevel---
		Iterator<Integer> it = measurements.keySet().iterator();

		double maxVariety = 0;
		while (it.hasNext())
		{
			int key = it.next();
			if (measurements.get(key).get("variety") > maxVariety)
				maxVariety += measurements.get(key).get("variety");
		}

		thinkTankInfo.playerLevel = ((double) livesLeft / (double) GameConstants.startLives + 0.2 * maxVariety);
		thinkTankInfo.playerLevel = (thinkTankInfo.playerLevel > 1.1) ? 1.1 : thinkTankInfo.playerLevel; // PlayerLevel not above 1.1
		thinkTankInfo.playerLevel = (thinkTankInfo.playerLevel < 0.1) ? 0.1 : thinkTankInfo.playerLevel; // PlayerLevel not below 0.1
		// ---End Calculate playerLevel---

		if (happy == 1)
			thinkTankInfo.maxJumpDistance = thinkTankInfo.maxJumpDistanceConst;
		else if (happy == 3)
		{
			thinkTankInfo.maxJumpDistance -= thinkTankInfo.maxJumpDistance / 5;
			thinkTankInfo.maxJumpDistance = thinkTankInfo.maxJumpDistance < 0.05f ? 0.05f : thinkTankInfo.maxJumpDistance;
		}

		Random rand = new Random();

		thinkTankInfo.challengerMetric = 0;
		// Rolls from 1 to 10 one time per happy. Perhaps make rolls happy*2 for more chance of new happy.
		for (int i = 0; i < happy; i++)
		{
			int metricRoll = rand.nextInt(10);
			thinkTankInfo.challengerMetric = metricRoll > thinkTankInfo.challengerMetric ? metricRoll : thinkTankInfo.challengerMetric;
		}

		// Multiply in how many games have been played. Game 5 counts more than metric from game 1.
		thinkTankInfo.challengerMetric *= thinkTankInfo.gameLengthMultiplier; // Removed difficult from this metric, as it is irrelevant.

		thinkTankInfo.lastDifficulty = difficult;
		thinkTankInfo.gameLengthMultiplier += 0.2;
		thinkTankInfo.lastMetric = thinkTankInfo.currentMetric;
		// Jump from variables if metric is higher than last one.
		if ((thinkTankInfo.challengerMetric >= thinkTankInfo.currentMetric || (happy == 3)))
		{
			// Jump between specified interval
			// So all variables are added a random value between
			// -maxJumpDistance and +maxJumpDistance
			oldParameters = this.deepCopyParameters(parameters);
			thinkTankInfo.currentMetric = thinkTankInfo.challengerMetric;
			jump(parameters);

		}
		else
		// Jump from oldVariables if metric is lower or equal to the last one.
		{
			// Jump between specified interval
			// So all variables are added a random value between
			// -maxJumpDistance and +maxJumpDistance

			// Keep hp, difficulty is irrelevant to happiness.
			float hpMultiplier = 0;
			hpMultiplier += parameters.get("GlobalMonsterHP").value;

			this.restoreCopiedParameters(parameters, oldParameters);

			parameters.get("GlobalMonsterHP").value = 0 + hpMultiplier;

			jump(parameters);
		}

		setNewStats();
	}

	public void writeParametersToDisk(FileHandle fileHandle)
	{
		fileHandle.writeString("", false);
		fileHandle.writeString("gameLengthMultiplier:" + thinkTankInfo.gameLengthMultiplier + ":\r\n", true);
		fileHandle.writeString("currentMetric:" + thinkTankInfo.currentMetric + ":\r\n", true);
		fileHandle.writeString("maxJumpDistance:" + thinkTankInfo.maxJumpDistance + ":\r\n", true);
		Iterator<String> parameterIterator = parameters.keySet().iterator();

		while (parameterIterator.hasNext())
		{
			String key = parameterIterator.next();
			Parameter parameter = parameters.get(key);
			fileHandle.writeString(parameter.name + ":" + parameter.value + ":" + parameter.minValue + ":" + parameter.maxValue + ":\r\n", true);
		}
	}

	public void writeRelationsToDisk(FileHandle fileHandle)
	{
		fileHandle.writeString("", false);
		Iterator<String> relationsIterator = relations.keySet().iterator();
		while (relationsIterator.hasNext())
		{
			Relation relation = relations.get(relationsIterator.next());
			fileHandle.writeString(relation.name + "\r\n", true);
			fileHandle.writeString(relation.variableParameter.name + "\r\n", true);
			Iterator<Parameter> relatedParametersIterator = relation.relatedParametersAndImpact.keySet().iterator();
			while (relatedParametersIterator.hasNext())
			{
				Parameter relatedParameter = relatedParametersIterator.next();
				fileHandle.writeString(relatedParameter.name + ":" + relation.relatedParametersAndImpact.get(relatedParameter) + ":\r\n", true);
			}
			fileHandle.writeString("endRelation" + "\r\n", true);
		}
	}

	public void clear()
	{
		measurements.clear();
	}

	public void clean(String parameterSavePath, String relationsSavePath)
	{
		Gdx.files.external(parameterSavePath).delete();
		this.initializeParameters(Gdx.files.external(parameterSavePath));
		Gdx.files.external(relationsSavePath).delete();
		this.initializeRelations(Gdx.files.external(relationsSavePath));
		oldParameters = this.deepCopyParameters(parameters);
		thinkTankInfo.successiveGameCounter = 0;
		thinkTankInfo.currentMetric = 0;
		thinkTankInfo.challengerMetric = 0;
	}

	private void setNewStats()
	{
		// Changing of stats, first by de-normalizing the parameters
		float diggerChance = parameters.get("DiggerChance").value;
		this.thinkTankInfo.diggerChance = diggerChance * diggerChance * diggerChance;
		float superChance = parameters.get("DiggerChance").value;
		this.thinkTankInfo.superEnemyChance = superChance * superChance * superChance;
		this.thinkTankInfo.nudgeChance = parameters.get("EarthquakeChance").value;
		this.thinkTankInfo.nudgeChanceInGame = parameters.get("EarthquakeChanceInGame").value;
		this.thinkTankInfo.startGold = (int) (100f * parameters.get("GlobalBuildCost").value);

		Iterator<String> enemyStatsIterator = enemyInfo.keySet().iterator();
		while (enemyStatsIterator.hasNext())
		{
			String key = enemyStatsIterator.next();
			enemyInfo.get(key).goldYield = (int) (defaultEnemyInfo.get(key).goldYield * parameters.get("GlobalMonsterGoldYield").value);
			enemyInfo.get(key).health = (int) (defaultEnemyInfo.get(key).health * parameters.get("GlobalMonsterHP").value);
			enemyInfo.get(key).speed = defaultEnemyInfo.get(key).speed * parameters.get("GlobalMonsterSpeed").value;

		}

		Iterator<String> towerStatsIterator = towerInfo.keySet().iterator();
		while (towerStatsIterator.hasNext())
		{
			String key = towerStatsIterator.next();
			towerInfo.get(key).buildCost = ((int) (defaultTowerInfo.get(key).buildCost * parameters.get("GlobalBuildCost").value) < 1) ? 1
					: (int) (defaultTowerInfo.get(key).buildCost * parameters.get("GlobalBuildCost").value); // Minimum 1
			towerInfo.get(key).range = (int) (defaultTowerInfo.get(key).range * parameters.get("GlobalTowerRange").value);
			towerInfo.get(key).reloadTime = defaultTowerInfo.get(key).reloadTime * parameters.get("GlobalReloadTime").value;
			towerInfo.get(key).sellPrice = ((int) (defaultTowerInfo.get(key).sellPrice * parameters.get("GlobalBuildCost").value) < 1) ? 1
					: (int) (defaultTowerInfo.get(key).sellPrice * parameters.get("GlobalBuildCost").value); // Minimum 1;// Linked parameter

			HashMap<String, FloatingBoolean> effects = towerInfo.get(key).missileEffects.writtenEffects;
			if (effects.containsKey("currentHealth"))
			{
				effects.put("currentHealth", new FloatingBoolean(effects.get("currentHealth").b, effects.get("currentHealth").f
						* parameters.get("TEDamage").value));
			}
			if (effects.containsKey("currentMoveSpeedMultiplier"))
			{
				float newMoveSpeedMultiplier = 1.0f - (1.0f - effects.get("currentMoveSpeedMultiplier").f) * parameters.get("TESlowPercentage").value;
				newMoveSpeedMultiplier = (newMoveSpeedMultiplier < 0) ? 0 : newMoveSpeedMultiplier;
				effects.put("currentMoveSpeedMultiplier", new FloatingBoolean(effects.get("currentMoveSpeedMultiplier").b, newMoveSpeedMultiplier));

			}
			if (effects.containsKey("currentSlowDuration"))
			{
				effects.put(
						"currentSlowDuration",
						new FloatingBoolean(effects.get("currentSlowDuration").b, effects.get("currentSlowDuration").f * parameters.get("TESlowDuration").value));
			}
			if (effects.containsKey("dotTicksLeft"))
			{
				effects.put("dotTicksLeft", new FloatingBoolean(effects.get("dotTicksLeft").b, effects.get("dotTicksLeft").f
						* parameters.get("TEDotTicks").value));
			}
			if (effects.containsKey("currentDotDamage"))
			{
				effects.put("currentDotDamage",
						new FloatingBoolean(effects.get("currentDotDamage").b, effects.get("currentDotDamage").f * parameters.get("TEDotDamage").value));
			}
		}

		towerStatsIterator = towerInfo.keySet().iterator();
		while (towerStatsIterator.hasNext())
		{
			String key = towerStatsIterator.next();
			towerInfo.get(key).upgradeCost = findUpgradeCost(towerInfo.get(key), true);
		}
	}

	private void jump(HashMap<String, Parameter> parameters)
	{
		Random random = new Random();
		// Change each parameter+
		Iterator<String> relationsIterator = relations.keySet().iterator();
		while (relationsIterator.hasNext())
		{
			Relation relation = relations.get(relationsIterator.next());
			float distance = (float) ((random.nextDouble() - 0.5) * 2 * thinkTankInfo.maxJumpDistance);
			relation.changeBalance(distance);
		}
		// Change difficulty
		float distance = (random.nextFloat() - 0.5f) * 2 * thinkTankInfo.maxJumpDistance;

		System.out.println("DIFF: " + thinkTankInfo.lastDifficulty);
		float moveAbs = Math.abs(distance); // Abs to know if + or -

		// Might want to not move certain chances by distance. Incrementing
		// Earthquake chance may never make it go back down.
		// Since earthquake chance can be changed even tho earthquakes are not
		// enabled, making the change unperceivable. So you might get
		// earthquakes enables with almost constant earthquakes.
		if (thinkTankInfo.lastDifficulty == 1)
		{
			Parameter parameterHP = parameters.get("GlobalMonsterHP");
			parameterHP.value += moveAbs;
			if (parameterHP.value > parameterHP.maxValue)
				parameterHP.value = parameterHP.maxValue;

			Parameter parameterSpeed = parameters.get("GlobalMonsterSpeed");
			parameterSpeed.value += moveAbs / 2;
			if (parameterSpeed.value > parameterSpeed.maxValue)
				parameterSpeed.value = parameterSpeed.maxValue;
			
			double chance = random.nextDouble();
			if (chance < 0.5)
			{
				double chanceTwo = random.nextDouble();
				if (chanceTwo < 0.33)
				{
					Parameter parameter = parameters.get("GlobalTowerRange");
					parameter.value = (parameter.value - moveAbs < parameter.minValue) ? parameter.minValue : parameter.value - moveAbs;
				}
				else if (chanceTwo < 0.66)
				{
					Parameter parameter = parameters.get("TEDamage");
					parameter.value = (parameter.value - moveAbs < parameter.minValue) ? parameter.minValue : parameter.value - moveAbs;
				}
				else
				{
					Parameter parameter = parameters.get("GlobalMonsterGoldYield");
					parameter.value = (parameter.value - moveAbs < parameter.minValue) ? parameter.minValue : parameter.value - moveAbs;
				}
			}
			else
			{
				double chanceTwo = random.nextDouble();
				if (chanceTwo < 0.33)
				{
					Parameter parameter = parameters.get("DiggerChance");
					parameter.value = (parameter.value + moveAbs > parameter.maxValue) ? parameter.maxValue : parameter.value + moveAbs;
				}
				else if (chanceTwo < 0.66)
				{
					Parameter parameter = parameters.get("SuperChance");
					parameter.value = (parameter.value + moveAbs > parameter.maxValue) ? parameter.maxValue : parameter.value + moveAbs;
				}
				else
				{
					Parameter parameter = parameters.get("EarthquakeChance");
					parameter.value = (parameter.value + moveAbs > parameter.maxValue) ? parameter.maxValue : parameter.value + moveAbs;
				}
			}
		}
		else if (thinkTankInfo.lastDifficulty == 2)
		{
			// Do something minor?
		}
		else
		{
			double chance = random.nextDouble();
			Parameter parameterHP = parameters.get("GlobalMonsterHP");
			parameterHP.value -= moveAbs;
			if (parameterHP.value < parameterHP.minValue)
				parameterHP.value = parameterHP.minValue;

			Parameter parameterSpeed = parameters.get("GlobalMonsterSpeed");
			parameterSpeed.value -= moveAbs / 2;
			if (parameterSpeed.value < parameterSpeed.minValue)
				parameterSpeed.value = parameterSpeed.minValue;
			
			if (chance < 0.5)
			{
				double chanceTwo = random.nextDouble();
				if (chanceTwo < 0.33)
				{
					Parameter parameter = parameters.get("GlobalTowerRange");
					parameter.value = (parameter.value + moveAbs > parameter.maxValue) ? parameter.maxValue : parameter.value + moveAbs;
				}
				else if (chanceTwo < 0.66)
				{
					Parameter parameter = parameters.get("TEDamage");
					parameter.value = (parameter.value + moveAbs > parameter.maxValue) ? parameter.maxValue : parameter.value + moveAbs;
				}
				else
				{
					Parameter parameter = parameters.get("GlobalMonsterGoldYield");
					parameter.value = (parameter.value + moveAbs > parameter.maxValue) ? parameter.maxValue : parameter.value + moveAbs;
				}
			}
			else
			{
				double chanceTwo = random.nextDouble();
				if (chanceTwo < 0.33)
				{
					Parameter parameter = parameters.get("DiggerChance");
					parameter.value = (parameter.value - moveAbs < parameter.minValue) ? parameter.minValue : parameter.value - moveAbs;
				}
				else if (chanceTwo < 0.66)
				{
					Parameter parameter = parameters.get("SuperChance");
					parameter.value = (parameter.value - moveAbs < parameter.minValue) ? parameter.minValue : parameter.value - moveAbs;
				}
				else
				{
					Parameter parameter = parameters.get("EarthquakeChance");
					parameter.value = (parameter.value - moveAbs < parameter.minValue) ? parameter.minValue : parameter.value - moveAbs;
				}

			}
		}
		// Make sure jumps are smaller each jump
		thinkTankInfo.maxJumpDistance -= thinkTankInfo.maxJumpDistance / 5;
		thinkTankInfo.maxJumpDistance = thinkTankInfo.maxJumpDistance < 0.05f ? 0.05f : thinkTankInfo.maxJumpDistance;

		calculateSpeedLevel();
	}

	private void calculateSpeedLevel()
	{
		float speedMultiplier = parameters.get("GlobalMonsterSpeed").value;
		if (speedMultiplier < 0.5f)
			thinkTankInfo.speedLevel = 1;
		else if (speedMultiplier < 0.9f)
			thinkTankInfo.speedLevel = 2;
		else if (speedMultiplier < 1.1f)
			thinkTankInfo.speedLevel = 3;
		else if (speedMultiplier < 1.4f)
			thinkTankInfo.speedLevel = 4;
		else if (speedMultiplier < 1.7f)
			thinkTankInfo.speedLevel = 5;
		else
			thinkTankInfo.speedLevel = 6;
	}

	private HashMap<String, Parameter> deepCopyParameters(HashMap<String, Parameter> originalParameters)
	{
		HashMap<String, Parameter> newParameters = new HashMap<String, Parameter>();
		Iterator<String> parameterIterator = originalParameters.keySet().iterator();
		while (parameterIterator.hasNext())
		{
			String key = parameterIterator.next();
			newParameters.put(key, new Parameter(key, originalParameters.get(key).value));
		}
		return newParameters;
	}

	private void restoreCopiedParameters(HashMap<String, Parameter> parameters, HashMap<String, Parameter> savedParameters)
	{
		Iterator<String> parameterIterator = savedParameters.keySet().iterator();
		while (parameterIterator.hasNext())
		{
			String key = parameterIterator.next();
			parameters.get(key).value = savedParameters.get(key).value;
		}
	}

	public void initializeUpgradeCost()
	{
		Iterator<String> towerStatsIterator = towerInfo.keySet().iterator();
		while (towerStatsIterator.hasNext())
		{
			TowerStats towerStats = towerInfo.get(towerStatsIterator.next());
			towerStats.upgradeCost = findUpgradeCost(towerStats, true);
		}

		towerStatsIterator = defaultTowerInfo.keySet().iterator();
		while (towerStatsIterator.hasNext())
		{
			TowerStats towerStats = defaultTowerInfo.get(towerStatsIterator.next());
			towerStats.upgradeCost = findUpgradeCost(towerStats, false);
		}
	}

	private int findUpgradeCost(TowerStats towerStats, boolean useTowerInfo)
	{
		int upgradeCost;
		if (towerStats.upgradesTo.equals("null"))
			upgradeCost = 9999;
		else
		{
			if (useTowerInfo)
			{
				upgradeCost = towerInfo.get(towerStats.upgradesTo).buildCost - towerStats.buildCost;
				System.out.println("Type: " + towerStats.type + " BuildCost: " + towerStats.buildCost + " UpgradedTower Type: "
						+ towerInfo.get(towerStats.upgradesTo).type + " UpgradedTowerBC: " + towerInfo.get(towerStats.upgradesTo).buildCost + " UpgradeCost: "
						+ upgradeCost);
			}
			else
				upgradeCost = defaultTowerInfo.get(towerStats.upgradesTo).buildCost - towerStats.buildCost;

			if (upgradeCost < 1)
				upgradeCost = 1;
		}
		return upgradeCost;
	}
}
