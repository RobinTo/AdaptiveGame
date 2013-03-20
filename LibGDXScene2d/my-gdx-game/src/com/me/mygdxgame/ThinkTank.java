package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.files.FileHandle;

public class ThinkTank {

	HashMap<Integer, HashMap<String, Float>> measurements = new HashMap<Integer, HashMap<String, Float>>();
	int actionCounter = 0;
	int timeBetweenMeasurements = 10;
	
	public void updateParameters(float totalTime, int gold, int lives, List<Tower> towers, List<Event> events, HashMap<String, TowerStats> availableTowers){

		actionCounter += events.size();
		if(Math.floor(totalTime)%timeBetweenMeasurements == 0 && !measurements.containsKey(Math.floor(totalTime)))
		{
			HashMap<String, Float> parameters = new HashMap<String, Float>();
			parameters.put("totalTime", totalTime);
			parameters.put("gold", (float) gold);
			parameters.put("lives", (float) lives);
			parameters.put("APM", (float) actionCounter);
			parameters.put("lives", (float)lives);
			
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
			parameters.put("variety", variety);
			measurements.put((int)Math.floor(totalTime), parameters);
			
			actionCounter = 0;
		}
	}
	
	public void calculateResults(int happy, int difficult)
	{
		System.out.println("Happy: " + happy + "\nDifficult: " + difficult);
	}
	
	public void writeParameters(FileHandle handle)
	{
		/*Iterator<String> keyIterator = parameters.keySet().iterator();
		handle.writeString("Parameter List\r\n\r\n", false);
		while(keyIterator.hasNext())
		{
			String s = keyIterator.next();
			Float parameter = parameters.get(s);
			
				try
				{
					handle.writeString(s +":"+parameter+":\r\n", true);
					System.out.println(s +":"+parameter+"\n");
				}
				catch(Exception e)
				{
					System.out.println(s +":"+parameter+"\n");
					System.out.println(e.toString());
				}
		}*/
		System.out.println("Saved Replay successfully");
	}
	
	public void clear()
	{
		measurements.clear();
	}
}
