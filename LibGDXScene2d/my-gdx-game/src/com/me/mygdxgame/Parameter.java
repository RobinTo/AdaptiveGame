package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Parameter
{
	public enum Type
	{
		GamePlay, Difficulty;
	}
	
	String name;
	float value;
	float minValue, maxValue;
	Type type;
	HashMap<Parameter, Float> relatedParameters;
	
	public Parameter()
	{
		name = "";
		value = 0.0f;
		minValue = 0.0f;
		maxValue = 3.0f;
		type = Type.GamePlay;
		relatedParameters = new HashMap<Parameter, Float>();
	}
	public Parameter(String name, float value, Type type)
	{
		this.name = name;
		this.value = value;
		minValue = 0.0f;
		maxValue = 3.0f;
		this.type = type;
		relatedParameters = new HashMap<Parameter, Float>();
	}
	public Parameter(String name, float value, float minValue, float maxValue, Type type)
	{
		this.name = name;
		this.value = value;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.type = type;
		relatedParameters = new HashMap<Parameter, Float>();
	}
	public void addRelation(Parameter relatedParameter, float impactFactor)
	{
		relatedParameters.put(relatedParameter, impactFactor);
	}
	public void jump(float distance)
	{
		//Firstly, find accepted jump distances for each related parameter, including self.
		List<Float> acceptedDistances = new ArrayList<Float>();
		acceptedDistances.add(this.acceptedDistance(distance, 1.0f));
		Iterator<Parameter> parameterIterator = relatedParameters.keySet().iterator();
		while (parameterIterator.hasNext())
		{
			Parameter relatedParameter = parameterIterator.next();
			acceptedDistances.add(relatedParameter.acceptedDistance(distance, relatedParameters.get(relatedParameter)));
		}
		
		//Secondly, find the smallest accepted jump distance
		float smallestDistance = maxValue;
		for (float eachDistance : acceptedDistances)
		{
			if (smallestDistance > eachDistance)
				smallestDistance = eachDistance;
		}
		
		//Lastly, set new values for all parameters, multiplying by impact factor
		this.value += distance;
		parameterIterator = relatedParameters.keySet().iterator();
		while (parameterIterator.hasNext())
		{
			Parameter relatedParameter = parameterIterator.next();
			relatedParameter.value += (distance * relatedParameters.get(relatedParameter));
		}
	}
	public float acceptedDistance(float distance, float impactFactor)
	{
		float acceptedDistance;
		float newValue = value + distance * impactFactor;
		if (newValue > maxValue)
		{
			acceptedDistance = maxValue - value;
		}
		else if (newValue < minValue)
		{
			acceptedDistance = value - minValue;
		}	
		else
		{
			acceptedDistance = newValue - value;
		}
		acceptedDistance /= impactFactor;
		
		return acceptedDistance;
	}
}
