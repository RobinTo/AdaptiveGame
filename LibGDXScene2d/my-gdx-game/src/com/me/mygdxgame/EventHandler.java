package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.List;

public class EventHandler {

	List<Event> events = new ArrayList<Event>();
	List<Event> queuedEvents = new ArrayList<Event>();
	public EventHandler()
	{
		
	}
	
	public void update()
	{
		events.clear();
		for(int i = 0; i<queuedEvents.size(); i++)
			events.add(queuedEvents.get(i));
		queuedEvents.clear();
	}
	
	public void queueEvent(Event e)
	{
		queuedEvents.add(e);
	}
}
