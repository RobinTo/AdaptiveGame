package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.files.FileHandle;
import com.me.mygdxgame.Parameter.Type;

public class ThinkTank
{
	HashMap<String, TowerStats> defaultTowerInfo = new HashMap<String, TowerStats>();
	HashMap<String, EnemyStats> defaultEnemyInfo = new HashMap<String, EnemyStats>();
	HashMap<String, TowerStats> towerInfo = new HashMap<String, TowerStats>();
	HashMap<String, EnemyStats> enemyInfo = new HashMap<String, EnemyStats>();
	HashMap<Integer, HashMap<String, Float>> measurements = new HashMap<Integer, HashMap<String, Float>>();
	HashMap<String, Parameter> parameters = new HashMap<String, Parameter>();
	int actionCounter = 0;
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
			List<String> fileContent = GameConstants
					.readRawTextFile(fileHandle);

			for (int counter = 0; counter < fileContent.size(); counter++)
			{
				String line = fileContent.get(counter);
				String[] split = line.split(":");
				parameters.put(split[0], new Parameter(split[0], Float.valueOf(split[1]), Float.valueOf(split[2]), Float.valueOf(split[3]), Type.valueOf(split[4])));
			}
		}
		else
		{
			// Create new default parameters
			parameters.put("GlobalMonsterHP", new Parameter("GlobalMonsterHP", 1.0f, Type.Difficulty));
			parameters.put("TEDotDamage", new Parameter("TEDotDamage", 1.0f, Type.GamePlay));
			parameters.put("TEDotTicks", new Parameter("TEDotTicks", 1.0f, Type.GamePlay));
			parameters.put("TESlowPercentage", new Parameter("TESlowPercentage", 1.0f, 0.1f, 1.4f, Type.GamePlay));
			parameters.put("TESlowDuration", new Parameter("TESlowDuration", 1.0f, Type.GamePlay));
			parameters.put("GlobalReloadTime", new Parameter("GlobalReloadTime", 1.0f, Type.GamePlay));
			parameters.put("TEDamage", new Parameter("TEDamage", 1.0f, 0.01f, 10.0f, Type.GamePlay));
			parameters.put("GlobalBuildCost", new Parameter("GlobalBuildCost", 1.0f, Type.GamePlay));
			parameters.put("GlobalMonsterSpeed", new Parameter("GlobalMonsterSpeed", 1.0f, 0.1f, 10.0f, Type.GamePlay));
			parameters.put("GlobalMonsterGoldYield", new Parameter("GlobalMonsterGoldYield", 1.0f, Type.GamePlay));
			parameters.put("GlobalTowerRange", new Parameter("GlobalTowerRange", 1.0f, 0.1f, 10.0f, Type.GamePlay));
		}
		
		setNewStats();
	}
	
	public void initializeRelations(FileHandle fileHandle)
	{
		if (fileHandle.exists())
		{
			// Fetch relations from disk
			List<String> fileContent = GameConstants
					.readRawTextFile(fileHandle);

			for (int counter = 0; counter < fileContent.size(); counter++)
			{
				String line = fileContent.get(counter);
				String[] split = line.split(":");
				parameters.get(split[0]).addRelation(parameters.get(split[1]), Float.valueOf(split[2]));
			}
		}
		else
		{
			//Create new gameplay relations
			parameters.get("GlobalMonsterHP").addRelation(parameters.get("TEDamage"), 0.5f);
			parameters.get("GlobalMonsterHP").addRelation(parameters.get("GlobalMonsterSpeed"), -0.5f);
			parameters.get("GlobalMonsterSpeed").addRelation(parameters.get("GlobalTowerRange"), (1.0f/3.0f));
			parameters.get("GlobalMonsterSpeed").addRelation(parameters.get("GlobalReloadTime"), -(1.0f/3.0f));
			parameters.get("GlobalMonsterSpeed").addRelation(parameters.get("GlobalMonsterHP"), -(1.0f/3.0f));
			parameters.get("GlobalBuildCost").addRelation(parameters.get("GlobalMonsterGoldYield"), 1.0f);
			parameters.get("GlobalTowerRange").addRelation(parameters.get("GlobalMonsterSpeed"), 1.0f);
			parameters.get("GlobalReloadTime").addRelation(parameters.get("GlobalMonsterSpeed"), -0.5f);
			parameters.get("GlobalReloadTime").addRelation(parameters.get("TEDamage"), 0.5f);
			parameters.get("GlobalMonsterGoldYield").addRelation(parameters.get("GlobalBuildCost"), 1.0f);
			parameters.get("TESlowPercentage").addRelation(parameters.get("TESlowDuration"), -1.0f);
			parameters.get("TESlowDuration").addRelation(parameters.get("TESlowPercentage"), -1.0f);
			parameters.get("TEDotTicks").addRelation(parameters.get("TEDotDamage"), -1.0f);
			parameters.get("TEDotDamage").addRelation(parameters.get("TEDotTicks"), -1.0f);
			parameters.get("TEDamage").addRelation(parameters.get("GlobalReloadTime"), 0.5f);
			parameters.get("TEDamage").addRelation(parameters.get("GlobalMonsterHP"), 0.5f);
		}
	}

	public void calculateNewParameters(float totalTime, int gold, int lives,
			List<Tower> towers, List<Event> events)
	{
		actionCounter += events.size();
		if (Math.floor(totalTime) % timeBetweenMeasurements == 0
				&& !measurements.containsKey((int) Math.floor(totalTime)))
		{
			/*
			 * parameters = new HashMap<String, Float>();
			 * parameters.put("totalTime", totalTime); parameters.put("gold",
			 * (float) gold); parameters.put("lives", (float) lives);
			 * parameters.put("APM", (float) actionCounter);
			 */

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
			// parameters.put("variety", variety);
			// measurements.put((int)Math.floor(totalTime), parameters);

			HashMap<String, Float> measuredParameters = new HashMap<String, Float>();
			measuredParameters.put("totalTime", totalTime);
			//measuredParameters.put("gold", (float) gold);
			//measuredParameters.put("lives", (float) lives);
			float f = (float) actionCounter;
			measuredParameters.put("APM", (float) f);
			measuredParameters.put("variety", variety);
			measurements.put((int) Math.floor(totalTime), measuredParameters);

			actionCounter = 0;
		}
	}

	public void calculateVariables(int happy, int difficult, int livesLeft)
	{
		Random rand = new Random();
		
		thinkTankInfo.challengerMetric = rand.nextDouble()*(happy+difficult)*thinkTankInfo.gameLengthMultiplier;
		thinkTankInfo.gameLengthMultiplier += 0.02;
		thinkTankInfo.lastMetric = thinkTankInfo.currentMetric;
		//Jump from variables if metric is higher than last one.
		if (thinkTankInfo.challengerMetric > thinkTankInfo.currentMetric)
		{
			// Jump between specified interval
			// So all variables are added a random value between
			// -maxJumpDistance and +maxJumpDistance
			jump(parameters);
			thinkTankInfo.currentMetric = thinkTankInfo.challengerMetric;
		} else
		// Jump from oldVariables if metric is lower or equal to the last one.
		{
			// Jump between specified interval
			// So all variables are added a random value between -maxJumpDistance and +maxJumpDistance
			jump(parameters);
		}

		// Calculate sensors
		Iterator<Integer> it = measurements.keySet().iterator();
		//double averageGold = 0;
		//double averageLives = 0;
		double averageAPM = 0; // measurements.get(key).get("APM") returns count
								// of events in last 10 seconds.
		double maxVariety = 0;
		while (it.hasNext())
		{
			int key = it.next();

			//averageGold += measurements.get(key).get("gold");
			//averageLives += measurements.get(key).get("lives");
			averageAPM += measurements.get(key).get("APM");
			if (measurements.get(key).get("variety") > maxVariety)
				maxVariety += measurements.get(key).get("variety");

		}
		//averageGold /= measurements.size();
		//averageLives /= measurements.size();
		averageAPM /= measurements.size();

		thinkTankInfo.playerLevel =  ((double)livesLeft/(double)GameConstants.startLives + 0.05*averageAPM + 0.2*maxVariety);
		thinkTankInfo.playerLevel = (thinkTankInfo.playerLevel > 1.1) ? 1.1 : thinkTankInfo.playerLevel; //PlayerLevel not above 1.1
		thinkTankInfo.playerLevel = (thinkTankInfo.playerLevel < 0.1) ? 0.1 : thinkTankInfo.playerLevel; //PlayerLevel not below 0.1
		
		setNewStats();
	}

	public void writeParametersToDisk(FileHandle fileHandle)
	{
		fileHandle.writeString("", false);
		Iterator<String> parameterIterator = parameters.keySet()
				.iterator();
		while (parameterIterator.hasNext())
		{
			String key = parameterIterator.next();
			Parameter parameter = parameters.get(key);
			fileHandle.writeString(parameter.name + ":" + parameter.value + ":" + parameter.minValue + ":" + parameter.maxValue + ":" + parameter.type + ":\r\n", true);
		}
	}
	public void writeRelationsToDisk(FileHandle fileHandle)
	{
		fileHandle.writeString("", false);
		Iterator<String> parameterIterator = parameters.keySet()
				.iterator();
		while (parameterIterator.hasNext())
		{
			String key = parameterIterator.next();
			Parameter parameter = parameters.get(key);
			Iterator<Parameter> relatedParametersIterator = parameter.relatedParameters.keySet().iterator();
			while (relatedParametersIterator.hasNext())
			{
				Parameter relatedParameter = relatedParametersIterator.next();
				System.out.println(relatedParameter.name);
				fileHandle.writeString(parameter.name + ":" + relatedParameter.name + ":" + parameter.relatedParameters.get(relatedParameter) + ":\r\n", true);
			}
		}
	}

	public void clear()
	{
		measurements.clear();
	}

	private void setNewStats()
	{
		// Changing of stats.
		Iterator<String> enemyStatsIterator = enemyInfo.keySet()
				.iterator();
	
		while (enemyStatsIterator.hasNext())
		{
			String key = enemyStatsIterator.next();
			enemyInfo.get(key).goldYield = (int)(defaultEnemyInfo.get(key).goldYield * parameters.get("GlobalMonsterGoldYield").value);
			enemyInfo.get(key).health = (int)(defaultEnemyInfo.get(key).health * parameters.get("GlobalMonsterHP").value);
			enemyInfo.get(key).speed = defaultEnemyInfo.get(key).speed * parameters.get("GlobalMonsterSpeed").value;
			
		}
	
		Iterator<String> towerStatsIterator = towerInfo.keySet().iterator();
		while (towerStatsIterator.hasNext())
		{
			String key = towerStatsIterator.next();
			towerInfo.get(key).buildCost = (int) (defaultTowerInfo.get(key).buildCost * parameters.get("GlobalBuildCost").value);
			towerInfo.get(key).range = (int) (defaultTowerInfo.get(key).range * parameters.get("GlobalTowerRange").value);
			towerInfo.get(key).reloadTime = defaultTowerInfo.get(key).reloadTime * parameters.get("GlobalReloadTime").value;
			
			towerInfo.get(key).sellPrice = (int) (defaultTowerInfo.get(key).sellPrice * parameters.get("GlobalBuildCost").value);//Linked parameter
			
			HashMap<String, FloatingBoolean> effects = towerInfo.get(key).missileEffects.effects;
			if (effects.containsKey("currentHealth"))
			{
				effects.put("currentHealth", new FloatingBoolean(effects
						.get("currentHealth").b, effects.get("currentHealth").f * parameters.get("TEDamage").value));
			}
			if (effects.containsKey("currentMoveSpeedMultiplier"))
			{
				float newMoveSpeedMultiplier = 1.0f - (1.0f - effects.get("currentMoveSpeedMultiplier").f) * parameters.get("TESlowPercentage").value;
				effects.put("currentMoveSpeedMultiplier", new FloatingBoolean(effects
						.get("currentMoveSpeedMultiplier").b, newMoveSpeedMultiplier));
				
			}
			if (effects.containsKey("currentSlowDuration"))
			{
				effects.put("currentSlowDuration", new FloatingBoolean(effects
						.get("currentSlowDuration").b, effects.get("currentSlowDuration").f * parameters.get("TESlowDuration").value));
			}
			if (effects.containsKey("dotTicksLeft"))
			{
				effects.put("dotTicksLeft", new FloatingBoolean(effects
						.get("dotTicksLeft").b, effects.get("dotTicksLeft").f * parameters.get("TEDotTicks").value));
			}
			if (effects.containsKey("currentDotDamage"))
			{
				effects.put("currentDotDamage", new FloatingBoolean(effects
						.get("currentDotDamage").b, effects.get("currentDotDamage").f * parameters.get("TEDotDamage").value));
			}
		}
	}

	private void jump(HashMap<String, Parameter> parameters)
	{
		Random random = new Random();
		Iterator<String> parameterIterator = parameters.keySet().iterator();
		while (parameterIterator.hasNext())
		{
			String key = parameterIterator.next();
			Parameter parameter = parameters.get(key);
			if (parameter.type == Parameter.Type.GamePlay)
			{
				float distance = (float) ((random.nextDouble() - 0.5) * 2 * thinkTankInfo.maxJumpDistance);
				parameter.jump(distance);
			}
			else if (parameter.type == Parameter.Type.Difficulty)
			{
				float randomNumber = random.nextFloat();
				randomNumber = randomNumber/2.5f + 0.2f;
				parameter.value = ((float)thinkTankInfo.playerLevel + randomNumber); // DO NOT use parameter.jump, because this will make related parameters to change too.
			}
		}
	}
}
