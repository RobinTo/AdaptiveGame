package no.uia.adaptiveTD;

import java.util.ArrayList;
import java.util.List;

public class EventHandler1 {
	List<Event> events = new ArrayList<Event>();
	
	public List<Event> getEvents() {
		return events;
	}
	public void setEvents(List<Event> events) {
		this.events = events;
	}

	List<Event> queuedEvents = new ArrayList<Event>();
	
	public void newRound()
	{
		events.clear();
		for(int i = 0; i<queuedEvents.size(); i++)
		{
			events.add(queuedEvents.get(i));
		}
		queuedEvents.clear();
	}
	
	public void queueEvent(Event e)
	{
		queuedEvents.add(e);
	}
	
	public void Clear()
	{
		events.clear();
		queuedEvents.clear();
	}
}
