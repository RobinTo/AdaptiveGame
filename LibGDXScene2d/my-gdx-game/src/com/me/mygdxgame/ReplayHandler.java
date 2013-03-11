package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.files.FileHandle;

public class ReplayHandler {

	HashMap<Float, List<Event>> events = new HashMap<Float, List<Event>>();
	
	HashMap<Float, List<Event>> savingEvents = new HashMap<Float, List<Event>>();
	
	public ReplayHandler()
	{
		
	}
	
	public void addEvents(float totalGameTime, EventHandler e)
	{
		List<Event> eve = new ArrayList<Event>();
		for(int i = 0; i < e.events.size(); i++)
		{
			eve.add(e.events.get(i));
		}
		savingEvents.put(totalGameTime, eve);
	}
	
	public List<Event> playReplay(float totalGameTime)
	{
		Iterator<Float> i = events.keySet().iterator();
		List<Event> returnEvents = new ArrayList<Event>();
		while(i.hasNext())
		{
			float f = i.next();
			if(f <= totalGameTime)
			{
				returnEvents = events.get(f);
				events.remove(f);
			}
		}
		return returnEvents;
	}
	
	public void saveReplay(FileHandle handle)
	{
		Iterator<Float> keyIterator = savingEvents.keySet().iterator();
		
		while(keyIterator.hasNext())
		{
			float f = keyIterator.next();
			List<Event> eventList = savingEvents.get(f);
			for(int i = 0; i < eventList.size(); i++)
			{
				try
				{
					handle.writeString(f +":"+eventList.get(i).getString()+":\r\n", true);
					System.out.println(f +":"+eventList.get(i).getString()+"\n");
				}
				catch(Exception e)
				{
					System.out.println(f +":"+eventList.get(i).getString()+"\n");
					System.out.println(e.toString());
				}
			}
		}
		System.out.println("Saved Replay successfully");
	}
	
	public void loadReplay(FileHandle handle)
	{
		List<String> fileContent = GameConstants.readRawTextFile(handle);

        System.out.println("Loaded Replay file" + handle.path());
        for(int x = 0; x<fileContent.size(); x++)
        {
        	String s = fileContent.get(x);
            String[] split = s.split(":");
            if(events.containsKey(Float.parseFloat(split[0])))
            {
            	events.get(split[0]).add(new Event(split[1], Integer.parseInt(split[2]), Integer.parseInt(split[3]), split[4]));
            }
            else
            {
            	events.put(Float.parseFloat(split[0]), new ArrayList<Event>());
            	events.get(Float.parseFloat(split[0])).add(new Event(split[1], Integer.parseInt(split[2]), Integer.parseInt(split[3]), split[4]));
            }
        }
	}
}
