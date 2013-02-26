package com.me.mygdxgame;

public class DamagePacket {
	float timeToHit;
	Enemy targetEnemy;
	
	public DamagePacket(float timeToHit, Enemy targetEnemy){
		this.timeToHit = timeToHit;
		this.targetEnemy = targetEnemy;
	}
}
