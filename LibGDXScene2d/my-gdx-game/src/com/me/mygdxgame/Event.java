package com.me.mygdxgame;

public class Event {

	String eventType;
	int x, y;
	String tower;
	
	public Event(String eventType, int x, int y, String tower)
	{
		this.eventType = eventType;
		this.x = x;
		this.y = y;
		this.tower = tower;
	}
	
	public String getString()
	{
		return eventType + ":" + x + ":" + y + ":" + tower;
	}
}
