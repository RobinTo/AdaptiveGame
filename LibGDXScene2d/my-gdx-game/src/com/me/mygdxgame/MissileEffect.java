package com.me.mygdxgame;

import java.util.HashMap;

public class MissileEffect {

	MissileTarget missileTarget;
	HashMap<String, Integer> effects = new HashMap<String, Integer>();
	
	public MissileEffect(MissileTarget missileTarget, HashMap<String, Integer> effects)
	{
		this.missileTarget = missileTarget;
		this.effects = effects;
		
	}
}
