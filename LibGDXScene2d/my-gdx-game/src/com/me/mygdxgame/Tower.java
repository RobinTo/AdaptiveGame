package com.me.mygdxgame;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class Tower extends ExtendedActor {

	
	public Tower (Sprite sprite) {
		super(sprite);
		setOrigin(getWidth()/2, getHeight()/2);
		setVisible(true);
	}
	
	public void calculateTarget(Enemy closestEnemy)
	{
		double deltaX = (closestEnemy.getX()) - (this.getX());
        double deltaY = (closestEnemy.getY()) - (this.getY());
        float rotation = (float)Math.toDegrees(Math.atan2(deltaY, deltaX));
        addAndClearActions(Actions.rotateTo(rotation));
	}
	
	@Override
	public void act(float gameTime)
	{
		super.act(gameTime);
		
	}
}
