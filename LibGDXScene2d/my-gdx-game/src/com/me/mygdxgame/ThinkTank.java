package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.files.FileHandle;

public class ThinkTank {

	HashMap<String, Float> parameters = new HashMap<String, Float>();
	
	public void UpdateParameters(float totalTime, int gold, int lives, List<Tower> towers, List<Event> events, HashMap<String, TowerStats> availableTowers){
		
		parameters.put("totalTime", totalTime);
		parameters.put("goldPerLife", (float)(gold/lives));
		if (parameters.containsKey("APM"))
			parameters.put("APM", parameters.get("APM") + events.size()); // Do Events/totalTime when done
		else
			parameters.put("APM", (float) events.size()); 
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
	
	public void WriteParameters(FileHandle handle)
	{
		Iterator<String> keyIterator = parameters.keySet().iterator();
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
		}
		System.out.println("Saved Replay successfully");
	}
}
