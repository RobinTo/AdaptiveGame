package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ThinkTank {

	HashMap<Integer, HashMap<String, Float>> measurements = new HashMap<Integer, HashMap<String, Float>>();
	
	public void UpdateParameters(float totalTime, int gold, int lives, List<Tower> towers, List<Event> events, HashMap<String, TowerStats> availableTowers){

		if(Math.floor(totalTime)%10 == 0 && !measurements.containsKey(Math.floor(totalTime)))
		{
			HashMap<String, Float> parameters = new HashMap<String, Float>();
			parameters.put("totalTime", totalTime);
			parameters.put("goldPerLife", (float)(gold/lives));
			parameters.put("APM", parameters.get("APM") + events.size()); // Do Events/totalTime when done
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
		}
	}
}
