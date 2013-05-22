package com.me.mygdxgame;

import java.util.HashMap;

public class MissileEffect {

	MissileTarget missileTarget;
	HashMap<String, FloatingBoolean> writtenEffects = new HashMap<String, FloatingBoolean>();
	
	public MissileEffect(MissileTarget missileTarget, HashMap<String, FloatingBoolean> effects)
	{
		this.missileTarget = missileTarget;
		this.writtenEffects = effects;
		
	}
}
