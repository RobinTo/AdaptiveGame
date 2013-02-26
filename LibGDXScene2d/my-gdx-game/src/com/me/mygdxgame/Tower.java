package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class Tower extends ExtendedActor {

	TowerStats towerStats;

	float enemyX, enemyY;
	int currentLevel;
	HashMap<Integer, Sprite> textures = new HashMap<Integer, Sprite>();
	
	float currentReloadTimer;	
	float currentTimeToHitTarget;
	boolean missileInTheAir;
	
	List<Enemy> targetedEnemies;
	Enemy targetEnemy;
	
	public Tower (TowerStats towerStats, Sprite towerSprite1, Sprite towerSprite2, Sprite towerSprite3, Sprite missileSprite) {
		super(towerSprite1);
		this.towerStats = towerStats;
		this.currentReloadTimer = towerStats.reloadTime;
		this.targetEnemy = null;
		textures.put(0, towerSprite1);
		textures.put(1, towerSprite2);
		textures.put(2, towerSprite3);
		textures.put(3, missileSprite);
		currentLevel = 1;
		currentTimeToHitTarget = 0;
		missileInTheAir = false;
		targetedEnemies = new ArrayList<Enemy>();
		setOrigin(getWidth()/2, getHeight()/2);
	}
	
	public void calculateTarget(float gameTime, List<Enemy> enemies, Enemy focusFireEnemy)
	{
		targetEnemy = null;
		float distanceToTargetEnemy, rotation;
		boolean focusedAndInRange = false;
		double deltaXTarget = 0, deltaYTarget = 0;
		
		if (focusFireEnemy != null)
		{
			deltaXTarget = (focusFireEnemy.getX() + focusFireEnemy.getWidth()/2) - (this.getX() + this.getWidth()/2);
            deltaYTarget = (focusFireEnemy.getY() + focusFireEnemy.getHeight()/2) - (this.getY() + this.getHeight()/2);
            distanceToTargetEnemy = (float)Math.sqrt(Math.pow(deltaXTarget, 2) + Math.pow(deltaYTarget, 2));

            if (distanceToTargetEnemy <= 1000)
            {
            	focusedAndInRange = true;
            	targetEnemy = focusFireEnemy;
            }
            else
            {
            	focusedAndInRange = false;
            }
		}
		if (!focusedAndInRange)
		{
			for (Enemy candidateEnemy : enemies)
            {
                double deltaXCandidate = (candidateEnemy.getX() + candidateEnemy.getWidth()/2) - (this.getX() + this.getWidth()/2);
                double deltaYCandidate = (candidateEnemy.getY() + candidateEnemy.getHeight()/2) - (this.getY() + this.getHeight()/2);
                double distanceToCandidate = Math.sqrt(Math.pow(deltaXCandidate, 2) + Math.pow(deltaYCandidate, 2));

                if (distanceToCandidate <= towerStats.range && (targetEnemy == null || candidateEnemy.distanceTravelled > targetEnemy.distanceTravelled))
                {
                	deltaXTarget = deltaXCandidate;
                	deltaYTarget = deltaYCandidate;
                    targetEnemy = candidateEnemy;
                    distanceToTargetEnemy = (float)distanceToCandidate;
                }
            }
		}
        if (targetEnemy != null)
        {
        	rotation = (float)Math.toDegrees(Math.atan2(deltaYTarget, deltaXTarget));
        	addAndClearActions(Actions.rotateTo(rotation));

        	enemyX = targetEnemy.getX() + targetEnemy.getWidth()/2;
        	enemyY = targetEnemy.getY() + targetEnemy.getHeight()/2;

        }
        
	}
	
	@Override
	public void act(float gameTime)
	{
		super.act(gameTime);
		
		currentReloadTimer -= gameTime;
		if(currentReloadTimer <= 0 && targetEnemy != null)
		{
			currentReloadTimer = towerStats.reloadTime;
			this.getParent().addActor(new Missile(textures.get(3), new Vector2(getX()+getOriginX(), getY()+getOriginY()), new Vector2(enemyX, enemyY), 0.2f));
			missileInTheAir = true;
			targetedEnemies.add(targetEnemy);
			currentTimeToHitTarget = 0.2f;
		}
		
		currentTimeToHitTarget -= gameTime;
		if (missileInTheAir && currentTimeToHitTarget <= 0)
		{
			missileInTheAir = false;
			targetedEnemies.get(0).currentHealth -= towerStats.getDamage(currentLevel);
			targetedEnemies.get(0).currentMoveSpeedMultiplier = 1.0f - ((float)towerStats.getSlowPercentage(currentLevel)/100.0f);
			targetedEnemies.get(0).currentSlowDuration = towerStats.getSlowDuration(currentLevel);
			//add dot
			//add aoe
			if (targetedEnemies.get(0).currentHealth <= 0) {
				targetedEnemies.remove(0);
				targetEnemy = null;
			}
		}
	}
	
	public boolean upgrade()
	{
		if (currentLevel == towerStats.maxLevel)
			return false;
		
		currentLevel++;
		super.setSprite(textures.get(currentLevel - 1));
		return true;
	}
}
