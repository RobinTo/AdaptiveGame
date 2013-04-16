package com.me.mygdxgame;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class MapNode
{
	int id;
	int x, y;
	
	// Contains all nodes you can get to from this node, and directions to get there.
	HashMap<MapNode, List<Direction>> childNodes = new HashMap<MapNode, List<Direction>>();
	Random rand = new Random();
	
	public MapNode(int nodeID, int x, int y)
	{
		id = nodeID;
		this.x = x;
		this.y = y;
	}
	
	public void addChildNode(MapNode node, List<Direction> directions)
	{
		childNodes.put(node, directions);
	}
	
	public List<Direction> getDirectionsToChild(int nodeID)
	{
		Iterator<MapNode> it = childNodes.keySet().iterator();
		while(it.hasNext())
		{
			MapNode child = it.next();
			if(child.id == nodeID)
				return childNodes.get(child);
		}
		return null;
	}
	
	public int getRandomChildNodeID(int excludeID)
	{
		int r = rand.nextInt(childNodes.size());
		Iterator<MapNode> it = childNodes.keySet().iterator();
		for(int i = 0; i < r; i++)
			it.next();
		int idToReturn = it.next().id;
		if(idToReturn != excludeID)
			return idToReturn;
		else
			return getRandomChildNodeID(excludeID);
	}
}
