package com.me.mygdxgame;

import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class Tower extends ExtendedActor {

	TowerStats towerStats;

	float enemyX, enemyY;
	int currentLevel, currentSellPrice;
	HashMap<Integer, Sprite> textures = new HashMap<Integer, Sprite>();
	
	float currentReloadTimer;	
	boolean missileInTheAir;
	
	List<DamagePacket> activeShots;
	
	Enemy targetEnemy;
	boolean canShoot = false;
	
	MissileEffect effects;
	
	public Tower (TowerStats towerStats, Sprite towerSprite, Sprite missileSprite) {
		super(towerSprite);
		this.towerStats = towerStats;
		this.currentSellPrice = towerStats.sellPrice;
		this.currentReloadTimer = towerStats.reloadTime;
		this.targetEnemy = null;
		textures.put(0, towerSprite);
		textures.put(3, missileSprite);
		currentLevel = 1;
		effects = towerStats.missileEffects;
		
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

                if (distanceToCandidate <= towerStats.range && (targetEnemy == null || candidateEnemy.getStat("distanceTravelled") > targetEnemy.getStat("distanceTravelled")))
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
		if(currentReloadTimer <= 0)
		{
			canShoot = true;
		}
		else
		{
			currentReloadTimer -= gameTime;
		}
	}
	
	public Missile shoot()
	{
		if(targetEnemy != null)
		{

			Missile m;
			
			if(effects.missileTarget.targetingStrategy == TargetingStrategy.Circle)
			{
				((TargetCircle)effects.missileTarget).x1 = (int)targetEnemy.getX();
				((TargetCircle)effects.missileTarget).y1 = (int) targetEnemy.getY();
				m = new Missile(textures.get(3), new Vector2(getX()+getOriginX(), getY()+getOriginY()), new Vector2(enemyX, enemyY), 0.2f, effects);
			}
			else if (effects.missileTarget.targetingStrategy == TargetingStrategy.Line)
			{
				((TargetLine)effects.missileTarget).x1 = (int) this.getX();
				((TargetLine)effects.missileTarget).y1 = (int) this.getY();
				int endX = (int)(this.getX() + towerStats.range * Math.cos(Math.toRadians(this.getRotation())));
				int endY = (int)(this.getY() + towerStats.range * Math.sin(Math.toRadians(this.getRotation())));
				((TargetLine)effects.missileTarget).x2 = endX;
				((TargetLine)effects.missileTarget).y2 = endY;
				
				m = new Missile(textures.get(3), new Vector2(getX()+getOriginX(), getY()+getOriginY()), new Vector2(getX()+getOriginX(), getY()+getOriginY()), 0.1f, effects);
				int dX =((TargetLine)effects.missileTarget).x2 -  ((TargetLine)effects.missileTarget).x1;
				int dY = ((TargetLine)effects.missileTarget).y2 - 	((TargetLine)effects.missileTarget).y1;
				int width = (int)Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2)); 
				m.setWidth(width);
				m.setHeight(m.getHeight()*2);
				m.setRotation(this.getRotation());
			}
			else if(effects.missileTarget.targetingStrategy == TargetingStrategy.CircleOnSelf)
			{
				m = new Missile(textures.get(3), new Vector2(getX()+getOriginX()-towerStats.range, getY()+getOriginY()-towerStats.range), new Vector2(getX()+getOriginX()-towerStats.range, getY()+getOriginY()-towerStats.range), 0.2f, effects);
		
				m.setWidth(towerStats.range*2);
				m.setHeight(towerStats.range*2);
			}
			else
			{
				((TargetSingle)effects.missileTarget).targetEnemy = targetEnemy;
				m = new Missile(textures.get(3), new Vector2(getX()+getOriginX(), getY()+getOriginY()), new Vector2(enemyX, enemyY), 0.2f, effects);
			}
			currentReloadTimer = towerStats.reloadTime;
			canShoot = false;
			return m;
		}
		else
			return null;
	}
	
	public boolean upgrade(TowerStats newTowerStats)
	{	
		currentLevel++;
		towerStats = newTowerStats;
		currentSellPrice += newTowerStats.sellPrice; 
		//super.setSprite(textures.get(newTowerStats.towerTexture));
		return true;
	}
}
