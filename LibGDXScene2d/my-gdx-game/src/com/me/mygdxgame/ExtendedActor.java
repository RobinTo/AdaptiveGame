package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class ExtendedActor extends Actor {
	TextureRegion region;
	
	List<Action> queuedActions = new ArrayList<Action>();
	
	public ExtendedActor (Sprite sprite) {
        region = new TextureRegion(sprite);
		setSize(sprite.getWidth(),sprite.getHeight());
        
        getNextAction();
	}

	public void getNextAction()
	{
		if (this.getActions().size <= 1) {
			if (queuedActions.size() > 0) {
				addAction(sequence(queuedActions.get(0), run(new Runnable() {
					public void run() {
						getNextAction();
					}
				})));
				queuedActions.remove(0);
			}
		}
	}
	
	public void addAndClearActions(Action action)
	{
		clearActions();
		addAction(action);
	}
	
	public void addSimultaneousAction(Action action)
	{
		addAction(action);
	}
	
	public void queueAction(Action action)
	{
		action.setActor(this);
		if(this.getActions().size == 0)
			addAction(sequence(action, run(new Runnable() {
				public void run() {
					getNextAction();
				}
			})));
		else
			queuedActions.add(action);
	}
	
	@Override
	public void draw (SpriteBatch batch, float parentAlpha) {
	        Color color = getColor();
	        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
	        batch.draw(region, getX(), getY(), getOriginX(), getOriginY(),
	                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
	}
	
}
