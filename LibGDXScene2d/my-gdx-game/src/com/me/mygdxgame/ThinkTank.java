package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.files.FileHandle;

public class ThinkTank {
	/*
	 * This class has two objectives:
	 * 1. Reading and writing parameters to disk. A parameter is an in-game modifiable value, such as fast-monster health and arrowtower damage. (Vi bruker enemystats og towerstats som parametere.
	 * 2. Calculating a new set of parameters based on another set of sensors. A sensor is in-game statistics about the player, such as APM, gold left and lives left. 
	 */
	
	HashMap<Integer, HashMap<String, Float>> measurements = new HashMap<Integer, HashMap<String, Float>>();
	HashMap<String, Float> parameters = new HashMap<String, Float>();
	int actionCounter = 0;
	int timeBetweenMeasurements = 1;
	float x, y, z, fdsfs;
	
	public void initializeVariables()
	{
		x = 0;
		y = 0;
		z = 0;
		fdsfs = 0;
	}
	
	//public void measureParameters(float totalTime, int gold, int lives, List<Tower> towers, List<Event> events, HashMap<String, TowerStats> availableTowers){
	public void calculateNewParameters(float totalTime, int gold, int lives, List<Tower> towers, List<Event> events, HashMap<String, TowerStats> availableTowers){
		actionCounter += events.size();
		if(events.size() > 0)
			System.out.println(events.size());
		if(Math.floor(totalTime)%timeBetweenMeasurements == 0 && !measurements.containsKey((int)Math.floor(totalTime)))
		{
			/*parameters = new HashMap<String, Float>();
			parameters.put("totalTime", totalTime);
			parameters.put("gold", (float) gold);
			parameters.put("lives", (float) lives);
			parameters.put("APM", (float) actionCounter);*/

			float variety = 0;
			List<String> types = new ArrayList<String>();
			for(Tower t : towers)
			{
				if(!types.contains(t.towerStats.type))
				{
					variety++;
					types.add(t.towerStats.type);
				}
			}
			variety = variety/availableTowers.size();
			//parameters.put("variety", variety);
			//measurements.put((int)Math.floor(totalTime), parameters);
			
			HashMap<String, Float> measuredParameters = new HashMap<String, Float>();
			measuredParameters.put("totalTime", totalTime);
			measuredParameters.put("gold", (float) gold);
			measuredParameters.put("lives", (float) lives);
			float f = (float)actionCounter;
			measuredParameters.put("APM", (float) f);
			measuredParameters.put("variety", variety);
			measurements.put((int)Math.floor(totalTime), measuredParameters);
			
			actionCounter = 0;
		}
	}
	
	public void calculateVariables(int happy, int difficult)
	{
		Iterator<Integer> it = measurements.keySet().iterator();
		double averageGold = 0;
		double averageLives = 0;
		double averageAPM = 0; // measurements.get(key).get("APM") returns count of events in last 10 seconds.
		double maxVariety = 0;
		while(it.hasNext())
		{
			int key = it.next();
			
			averageGold += measurements.get(key).get("gold");
			averageLives += measurements.get(key).get("lives");
			averageAPM += measurements.get(key).get("APM");
			if(measurements.get(key).get("variety") > maxVariety)
				maxVariety += measurements.get(key).get("variety");
			
		}
		System.out.println("APM BEFORE DIVIDE"+ averageAPM);
		averageGold /= measurements.size();
		averageLives /= measurements.size();
		averageAPM  /= measurements.size();	
		// -----------------
	}
	
	public void calculateNewStats(double averageLives, double averageGold, double averageAPM, double maxVariety)
	{
		// Changing of stats.
				Iterator<String> enemyStatsIterator = MyGdxGame.enemyInfo.keySet().iterator();
				
				while(enemyStatsIterator.hasNext())
				{
					String s = enemyStatsIterator.next();
					if(averageLives == GameConstants.startLives)
					{
						MyGdxGame.enemyInfo.get(s).health += 100;
					}
					else
					{
						MyGdxGame.enemyInfo.get(s).health -= (MyGdxGame.enemyInfo.get(s).health/100)*10;
					}
					if(averageGold > 20)
					{
						MyGdxGame.enemyInfo.get(s).goldYield -= (int)Math.floor((MyGdxGame.enemyInfo.get(s).goldYield/100)*10);
					}
					else
					{
						MyGdxGame.enemyInfo.get(s).goldYield += (int)Math.floor((MyGdxGame.enemyInfo.get(s).goldYield/100)*10);
					}
					if(averageAPM > 0.5)
					{
						MyGdxGame.enemyInfo.get(s).speed += (int)Math.floor((MyGdxGame.enemyInfo.get(s).speed/100)*10);
					}
					else
					{
						MyGdxGame.enemyInfo.get(s).speed -= (int)Math.floor((MyGdxGame.enemyInfo.get(s).speed/100)*10);
					}
				}
				
				Iterator<String> towerStatsIterator = MyGdxGame.towerInfo.keySet().iterator();
				
				while(towerStatsIterator.hasNext())
				{
					String key = towerStatsIterator.next();
					if(maxVariety > 3)
					{
						MyGdxGame.towerInfo.get(key).range += (MyGdxGame.towerInfo.get(key).range/100)*10;
					}
					else
					{
						MyGdxGame.towerInfo.get(key).range -= (MyGdxGame.towerInfo.get(key).range/100)*10;
					}
					
				}

	}
	
	public void readParameters(FileHandle handle)
	{
		HashMap<String, Float> newParameters = new HashMap<String, Float>();
		List<String> fileContent = GameConstants.readRawTextFile(handle);

        System.out.println("Loaded parameter file" + handle.path());
        
        for (int x = 0; x<fileContent.size(); x++)
        {
        	String s = fileContent.get(x);
            String[] split = s.split(":");
            newParameters.put(split[0], Float.valueOf(split[1]));
            System.out.println("Reading parameter: " + split[0] + ":"+ split[1]);
        }
        parameters = newParameters;

        System.out.println("Successfully loaded parameters" + handle.path());
	}
	
	public void writeParameters(FileHandle handle)
	{
		Iterator<String> keyIterator = parameters.keySet().iterator();
		handle.writeString("", false);
		System.out.println(parameters.isEmpty());
		while(keyIterator.hasNext())
		{
			String s = keyIterator.next();
			Float parameter = parameters.get(s);
				try
				{
					handle.writeString(s +":"+parameter+":\r\n", true);
					System.out.println("Writing parameter: " + s +":"+parameter+"\n");
				}
				catch(Exception e)
				{
					System.out.println("Writing parameter: " + s +":"+parameter+"\n");
					System.out.println(e.toString());
				}
		}
		System.out.println("Saved parameters successfully");
	}
	
	public void clear()
	{
		measurements.clear();
	}
}
