package com.me.mygdxgame;

public class Parameter
{
	String name;
	float value;
	float minValue, maxValue;
	
	public Parameter()
	{
		name = "";
		value = 1.0f;
		minValue = 0.1f;
		maxValue = 3.0f;
	}
	public Parameter(String name, float value)
	{
		this.name = name;
		this.value = value;
		minValue = 0.1f;
		maxValue = 3.0f;
	}
	public Parameter(String name, float value, float minValue, float maxValue)
	{
		this.name = name;
		this.value = value;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	public float acceptedDistance(float distance, float impactFactor)
	{
		float acceptedDistance;
		float newValue = value + (distance * impactFactor);
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
