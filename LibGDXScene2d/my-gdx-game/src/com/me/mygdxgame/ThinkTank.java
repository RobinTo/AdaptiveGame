package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.files.FileHandle;

public class ThinkTank {
	/*
	 * This class has two objectives: 1. Reading and writing parameters to disk.
	 * A parameter is an in-game modifiable value, such as fast-monster health
	 * and arrowtower damage. (Vi bruker enemystats og towerstats som
	 * parametere. 2. Calculating a new set of parameters based on another set
	 * of sensors. A sensor is in-game statistics about the player, such as APM,
	 * gold left and lives left.
	 */

	HashMap<String, TowerStats> defaultTowerInfo = new HashMap<String, TowerStats>();
    HashMap<String, EnemyStats> defaultEnemyInfo = new HashMap<String, EnemyStats>();
	HashMap<Integer, HashMap<String, Float>> measurements = new HashMap<Integer, HashMap<String, Float>>();
	HashMap<String, Float> parameters = new HashMap<String, Float>();
	int actionCounter = 0;
	int timeBetweenMeasurements = 1;
	float x, y, z, fdsfs;

	public void initializeVariables(FileHandle fileHandle) {
		if (fileHandle.exists()) {
			List<String> fileContent = GameConstants
					.readRawTextFile(fileHandle);

			for (int counter = 0; counter < fileContent.size(); counter++) {
				String s = fileContent.get(counter);
				String[] split = s.split(":");
				if (split[0] == "x") {
					x = Float.valueOf(split[1]);
				} else if (split[0] == "y") {
					y = Float.valueOf(split[1]);
				} else if (split[0] == "z") {
					z = Float.valueOf(split[1]);
				} else if (split[0] == "fdsfs") {
					fdsfs = Float.valueOf(split[1]);
				}
			}
		}
		else
		{
			x = 1;
			y = 1;
			z = 1;
			fdsfs = 1;
			writeVariablesToDisk(fileHandle);
		}
	}

	// public void measureParameters(float totalTime, int gold, int lives,
	// List<Tower> towers, List<Event> events, HashMap<String, TowerStats>
	// availableTowers){
	public void calculateNewParameters(float totalTime, int gold, int lives,
			List<Tower> towers, List<Event> events,
			HashMap<String, TowerStats> availableTowers) {
		actionCounter += events.size();
		if (events.size() > 0)
			System.out.println(events.size());
		if (Math.floor(totalTime) % timeBetweenMeasurements == 0
				&& !measurements.containsKey((int) Math.floor(totalTime))) {
			/*
			 * parameters = new HashMap<String, Float>();
			 * parameters.put("totalTime", totalTime); parameters.put("gold",
			 * (float) gold); parameters.put("lives", (float) lives);
			 * parameters.put("APM", (float) actionCounter);
			 */

			float variety = 0;
			List<String> types = new ArrayList<String>();
			for (Tower t : towers) {
				if (!types.contains(t.towerStats.type)) {
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

	public void calculateVariables(int happy, int difficult) {
		Iterator<Integer> it = measurements.keySet().iterator();
		double averageGold = 0;
		double averageLives = 0;
		double averageAPM = 0; // measurements.get(key).get("APM") returns count
								// of events in last 10 seconds.
		double maxVariety = 0;
		while (it.hasNext()) {
			int key = it.next();

			averageGold += measurements.get(key).get("gold");
			averageLives += measurements.get(key).get("lives");
			averageAPM += measurements.get(key).get("APM");
			if (measurements.get(key).get("variety") > maxVariety)
				maxVariety += measurements.get(key).get("variety");

		}
		System.out.println("APM BEFORE DIVIDE" + averageAPM);
		averageGold /= measurements.size();
		averageLives /= measurements.size();
		averageAPM /= measurements.size();
		
		Random rand = new Random();
		x = (float)((rand.nextDouble() + 0.1 )*3.0);
		y = (float)((rand.nextDouble() + 0.1 )*3.0);
		z = (float)((rand.nextDouble() + 0.1 )*3.0);
		fdsfs = (float)((rand.nextDouble() + 0.1 )*3.0);
		
		// -----------------
		calculateNewStats();
	}

	public void calculateNewStats() {
		// Changing of stats.
		Iterator<String> enemyStatsIterator = MyGdxGame.enemyInfo.keySet()
				.iterator();

		while (enemyStatsIterator.hasNext()) {
			String s = enemyStatsIterator.next();
			MyGdxGame.enemyInfo.get(s).health = (int)(defaultEnemyInfo.get(s).health * x);
			System.out.println(s + " : " + MyGdxGame.enemyInfo.get(s).health + " : Multiplier: " + x);
				MyGdxGame.enemyInfo.get(s).goldYield = (int)(defaultEnemyInfo.get(s).goldYield * y);
				MyGdxGame.enemyInfo.get(s).speed = defaultEnemyInfo.get(s).speed * z;
		}

		Iterator<String> towerStatsIterator = MyGdxGame.towerInfo.keySet()
				.iterator();

		while (towerStatsIterator.hasNext()) {
			String key = towerStatsIterator.next();
				MyGdxGame.towerInfo.get(key).range = (int)(defaultTowerInfo.get(key).range * fdsfs);
		}
	}

	public void writeVariablesToDisk(FileHandle fileHandle) {
		fileHandle.writeString("", false);
		fileHandle.writeString("x:" + x + ":\r\n", true);
		fileHandle.writeString("y:" + y + ":\r\n", true);
		fileHandle.writeString("z:" + z + ":\r\n", true);
		fileHandle.writeString("fdsfs:" + fdsfs + ":\r\n", true);
	}

	public void clear() {
		measurements.clear();
	}
}
