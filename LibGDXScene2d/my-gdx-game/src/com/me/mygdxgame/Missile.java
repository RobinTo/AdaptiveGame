package com.me.mygdxgame;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class Missile extends ExtendedActor{

	public Missile(Sprite sprite, Vector2 startPosition, Vector2 targetPosition, float timeToHitTarget) {
		super(sprite);
		
		setPosition(startPosition.x, startPosition.y);
		addAction(sequence(Actions.moveTo(targetPosition.x, targetPosition.y, timeToHitTarget), run(new Runnable() {
			public void run() {
				remove();
			}
		})));
	}
	
	

}
