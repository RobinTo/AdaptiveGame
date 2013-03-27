package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.files.FileHandle;

public class ThinkTank {

	HashMap<Integer, HashMap<String, Float>> measurements = new HashMap<Integer, HashMap<String, Float>>();
	HashMap<String, Float> parameters = new HashMap<String, Float>();
	int actionCounter = 0;
	int timeBetweenMeasurements = 10;
	
	//public void measureParameters(float totalTime, int gold, int lives, List<Tower> towers, List<Event> events, HashMap<String, TowerStats> availableTowers){
		public void measureParameters(float totalTime, int gold, int lives, List<Tower> towers, List<Event> events, HashMap<String, TowerStats> availableTowers){
		actionCounter += events.size();
		if(Math.floor(totalTime)%timeBetweenMeasurements == 0 && !measurements.containsKey(Math.floor(totalTime)))
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
			measuredParameters.put("APM", (float) actionCounter);
			measuredParameters.put("lives", (float)lives);
			measuredParameters.put("variety", variety);
			measurements.put((int)Math.floor(totalTime), measuredParameters);
			
			actionCounter = 0;
		}
	}
	
	public void calculateResults(int happy, int difficult)
	{
		System.out.println("Happy: " + happy + "\nDifficult: " + difficult);
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
