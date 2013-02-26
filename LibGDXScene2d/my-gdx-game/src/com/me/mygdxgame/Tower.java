package com.me.mygdxgame;

import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class Tower extends ExtendedActor {

	TowerStats towerStats;
	//Vector2 position, tilePosition, origin;
	float currentReloadTime, eX, eY;
	int currentLevel;
	
	//Rectangle rangeHighlightRectangle;
	//Color color;
	HashMap<Integer, Sprite> textures = new HashMap<Integer, Sprite>();

	Sprite missile;
	float shootTime = 0.5f;
	float shootTimer = 0.5f;
	
	public Tower (Sprite sprite, Sprite missile) {
		super(sprite);
		this.missile = missile;
		setOrigin(getWidth()/2, getHeight()/2);
	}
	
	public void calculateTarget(float gameTime, List<Enemy> enemies, Enemy focusFireEnemy)
	{
		Enemy targetEnemy = null;
		float distanceToTargetEnemy, rotation;
		boolean focusedAndInRange = false;
		double deltaXTarget = 0, deltaYTarget = 0;
		
		if (focusFireEnemy != null)
		{
			deltaXTarget = (focusFireEnemy.getX() + focusFireEnemy.getOriginX()) - (this.getX() + this.getOriginX());
            deltaYTarget = (focusFireEnemy.getY() + focusFireEnemy.getOriginY()) - (this.getY() + this.getOriginY());
            distanceToTargetEnemy = (float)Math.sqrt(Math.pow(deltaXTarget, 2) + Math.pow(deltaYTarget, 2));

            if (distanceToTargetEnemy <= this.towerStats.getRange())
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
                double deltaXCandidate = (candidateEnemy.getX() + candidateEnemy.getOriginX()) - (this.getX() + this.getOriginX());
                double deltaYCandidate = (candidateEnemy.getY() + candidateEnemy.getOriginY()) - (this.getY() + this.getOriginY());
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
        	eX = targetEnemy.getX() + targetEnemy.getOriginX();
        	eY = targetEnemy.getY() + targetEnemy.getOriginY();
        }
        
		
		/*
        currentReloadTime = (currentReloadTime < 0) ? 0 : currentReloadTime;

        if (targetEnemy != null && currentReloadTime <= 0)
        {
        	missiles.add(new Missile(textures.get(1), new Vector2(this.position.x + this.origin.x, this.position.y + this.origin.y),
    				distanceToTargetEnemy, rotation, 1024.0f, targetEnemy,
    				towerStats.getDamage(currentLevel)));
            currentReloadTime = towerStats.reloadTime;
        }
        */
	}
	
	@Override
	public void act(float gameTime)
	{
		super.act(gameTime);
		
		shootTimer -= gameTime;
		if(shootTimer <= 0)
		{
			shootTimer = shootTime;
			this.getParent().addActor(new Missile(missile, new Vector2(getX()+getOriginX(), getY()+getOriginY()), new Vector2(eX, eY)));
		}
	}
}
