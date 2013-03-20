package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ThinkTank {

	HashMap<String, Float> parameters = new HashMap<String, Float>();
	
	public void UpdateParameters(float totalTime, int gold, int lives, List<Tower> towers, List<Event> events, HashMap<String, TowerStats> availableTowers){
		
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
	}
}
