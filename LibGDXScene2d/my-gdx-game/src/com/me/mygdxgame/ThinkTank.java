package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.files.FileHandle;

public class ThinkTank
{
	HashMap<String, TowerStats> defaultTowerInfo = new HashMap<String, TowerStats>();
	HashMap<String, EnemyStats> defaultEnemyInfo = new HashMap<String, EnemyStats>();
	HashMap<Integer, HashMap<String, Float>> measurements = new HashMap<Integer, HashMap<String, Float>>();
	HashMap<String, Float> parameters = new HashMap<String, Float>();
	int actionCounter = 0;
	int timeBetweenMeasurements = 1;
	Variables variables, oldVariables;
	double lastMetric = 0, currentMetric = 0, challengerMetric = 0;
	double maxJumpDistance = 0.5;

	public void initializeVariables(FileHandle fileHandle)
	{
		variables = new Variables();
		oldVariables = new Variables();
		if (fileHandle.exists())
		{
			List<String> fileContent = GameConstants
					.readRawTextFile(fileHandle);

			for (int counter = 0; counter < fileContent.size(); counter++)
			{
				String s = fileContent.get(counter);
				String[] split = s.split(":");
				if (split[0].equals("x"))
				{
					variables.x = Float.valueOf(split[1]);
				} else if (split[0].equals("y"))
				{
					variables.y = Float.valueOf(split[1]);
				} else if (split[0].equals("z"))
				{
					variables.z = Float.valueOf(split[1]);
				} /*else if (split[0].equals("fdsfs")) {
					variables.fdsfs = Float.valueOf(split[1]);
					}*/
			}
		} else
		{
			variables.x = 1;
			variables.y = 1;
			variables.z = 1;
			//variables.fdsfs = 1;
			writeVariablesToDisk(fileHandle);
		}
		oldVariables.x = variables.x;
		oldVariables.y = variables.y;
		oldVariables.z = variables.z;
		calculateNewStats();
	}

	// public void measureParameters(float totalTime, int gold, int lives,
	// List<Tower> towers, List<Event> events, HashMap<String, TowerStats>
	// availableTowers){
	public void calculateNewParameters(float totalTime, int gold, int lives,
			List<Tower> towers, List<Event> events,
			HashMap<String, TowerStats> availableTowers)
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
			variety = variety / availableTowers.size();
			// parameters.put("variety", variety);
			// measurements.put((int)Math.floor(totalTime), parameters);

			HashMap<String, Float> measuredParameters = new HashMap<String, Float>();
			measuredParameters.put("totalTime", totalTime);
			measuredParameters.put("gold", (float) gold);
			measuredParameters.put("lives", (float) lives);
			float f = (float) actionCounter;
			measuredParameters.put("APM", (float) f);
			measuredParameters.put("variety", variety);
			measurements.put((int) Math.floor(totalTime), measuredParameters);

			actionCounter = 0;
		}
	}

	public void calculateVariables(int happy, int difficult)
	{
		Random rand = new Random();
		// Metric is calculated by taking a random integer value ranging from 1 to happy,
		// plus another random integer value ranging from 1 to difficult,
		// and lastly adding a random double value between -0.5 and +0.5
		// Metric will range from 1.5 to 6.5
		double happyMetric = 1 + rand.nextInt(happy);
		double difficultyMetric = 1 + rand.nextInt(difficult);
		challengerMetric = happyMetric + difficultyMetric + rand.nextDouble()
				- 0.5;
		lastMetric = currentMetric;
		oldVariables.x = variables.x;
		oldVariables.y = variables.y;
		oldVariables.z = variables.z;
		//Jump from variables if metric is higher than last one.
		if (challengerMetric > currentMetric)
		{
			// Jump between specified interval
			// So all variables are added a random value between -maxJumpDistance and +maxJumpDistance
			variables.x = calculateSingleVariable(variables.x);
			variables.y = calculateSingleVariable(variables.y);
			variables.z = calculateSingleVariable(variables.z);
			//variables.fdsfs = calculateSingleVariable(variables.fdsfs);
			currentMetric = challengerMetric;
		} else
		// Jump from oldVariables if metric is lower or equal to the last one.
		{
			// Jump between specified interval
			// So all variables are added a random value between -maxJumpDistance and +maxJumpDistance
			variables.x = calculateSingleVariable(oldVariables.x);
			variables.y = calculateSingleVariable(oldVariables.y);
			variables.z = calculateSingleVariable(oldVariables.z);
			//variables.fdsfs = calculateSingleVariable(oldVariables.fdsfs);
		}

		// Calculate sensors
		Iterator<Integer> it = measurements.keySet().iterator();
		double averageGold = 0;
		double averageLives = 0;
		double averageAPM = 0; // measurements.get(key).get("APM") returns count
								// of events in last 10 seconds.
		double maxVariety = 0;
		while (it.hasNext())
		{
			int key = it.next();

			averageGold += measurements.get(key).get("gold");
			averageLives += measurements.get(key).get("lives");
			averageAPM += measurements.get(key).get("APM");
			if (measurements.get(key).get("variety") > maxVariety)
				maxVariety += measurements.get(key).get("variety");

		}
		averageGold /= measurements.size();
		averageLives /= measurements.size();
		averageAPM /= measurements.size();

		calculateNewStats();
	}

	public void calculateNewStats()
	{
		// Changing of stats.
		Iterator<String> enemyStatsIterator = MyGdxGame.enemyInfo.keySet()
				.iterator();

		while (enemyStatsIterator.hasNext())
		{
			String s = enemyStatsIterator.next();
			MyGdxGame.enemyInfo.get(s).health = (int) (defaultEnemyInfo.get(s).health * variables.x);
			MyGdxGame.enemyInfo.get(s).goldYield = (int) (defaultEnemyInfo
					.get(s).goldYield * variables.y);
			MyGdxGame.enemyInfo.get(s).speed = defaultEnemyInfo.get(s).speed
					* variables.z;
		}

		Iterator<String> towerStatsIterator = MyGdxGame.towerInfo.keySet()
				.iterator();

		while (towerStatsIterator.hasNext())
		{
			String key = towerStatsIterator.next();
			MyGdxGame.towerInfo.get(key).buildCost = (int) (defaultTowerInfo
					.get(key).buildCost * variables.y);
			//MyGdxGame.towerInfo.get(key).range = (int)(defaultTowerInfo.get(key).range * variables.fdsfs);
		}
	}

	public void writeVariablesToDisk(FileHandle fileHandle)
	{
		fileHandle.writeString("", false);
		fileHandle.writeString("x:" + variables.x + ":\r\n", true);
		fileHandle.writeString("y:" + variables.y + ":\r\n", true);
		fileHandle.writeString("z:" + variables.z + ":\r\n", true);
		//fileHandle.writeString("fdsfs:" + variables.fdsfs + ":\r\n", true);
	}

	public void clear()
	{
		measurements.clear();
	}

	private float calculateSingleVariable(float variable)
	{
		Random random = new Random();
		float distance = (float) ((random.nextDouble() - 0.5) * 2 * maxJumpDistance);
		variable += distance;
		if (variable > 3.0)
			variable = 3.0f;
		else if (variable < 0.33)
			variable = 0.33f;
		return variable;
	}

	public Variables getVariables()
	{
		return variables;
	}

	public Variables getOldVariables()
	{
		return oldVariables;
	}
}
