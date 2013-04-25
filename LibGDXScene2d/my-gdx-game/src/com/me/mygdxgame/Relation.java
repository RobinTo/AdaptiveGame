package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Relation
{	
	String name;
	Parameter variableParameter;
	HashMap<Parameter, Float> relatedParametersAndImpact;
	
	public Relation(String name, Parameter variableParameter)
	{
		this.name = name;
		this.variableParameter = variableParameter;
		relatedParametersAndImpact = new HashMap<Parameter, Float>();
	}
	public void addRelatedParameter(Parameter relatedParameter, float impactFactor)
	{
		relatedParametersAndImpact.put(relatedParameter, impactFactor);
	}
	public void changeBalance(float distance)
	{
		// Firstly, find accepted jump distances for each related parameter,
		// including self.
		List<Float> acceptedDistances = new ArrayList<Float>();
		acceptedDistances.add(variableParameter.acceptedDistance(distance, 1.0f));
		Iterator<Parameter> parameterIterator = relatedParametersAndImpact.keySet()
				.iterator();
		while (parameterIterator.hasNext())
		{
			Parameter relatedParameter = parameterIterator.next();
			acceptedDistances.add(relatedParameter.acceptedDistance(distance,
					relatedParametersAndImpact.get(relatedParameter)));
		}

		// Secondly, find the smallest accepted jump distance
		float smallestDistance = variableParameter.maxValue;
		for (float eachDistance : acceptedDistances)
		{
			if (smallestDistance > eachDistance)
				smallestDistance = eachDistance;
		}

		// Lastly, set new values for all parameters, multiplying by impact
		// factor
		variableParameter.value += distance;
		parameterIterator = relatedParametersAndImpact.keySet().iterator();
		while (parameterIterator.hasNext())
		{
			Parameter relatedParameter = parameterIterator.next();
			relatedParameter.value += (distance * relatedParametersAndImpact
					.get(relatedParameter));
		}

	}
}
