package com.me.mygdxgame;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class Missile extends ExtendedActor
{

	MissileEffect missileEffect;
	float timeToHitTarget;
	String impactSound;
	int startX, startY;
	Enemy targetEnemy;

	public Missile(Sprite sprite, String impactSound, Vector2 startPosition,
			Vector2 targetPosition, float timeToHitTarget, MissileEffect effects)
	{
		super(sprite);
		this.startX = (int)startPosition.x;
		this.startY = (int)startPosition.y;
		this.impactSound = impactSound;
		this.timeToHitTarget = timeToHitTarget;
		this.missileEffect = effects;
		this.setTouchable(Touchable.disabled);
		setPosition(startPosition.x, startPosition.y);
		addAction(sequence(Actions.moveTo(targetPosition.x, targetPosition.y,
				timeToHitTarget), run(new Runnable()
		{
			public void run()
			{
				remove();
			}
		})));
	}

}
