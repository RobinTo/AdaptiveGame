package com.me.mygdxgame;

import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class Enemy extends ExtendedActor
{

	HashMap<Integer, Sprite> sprites;
	EnemyStats enemyStats;
	Rectangle healthBarYellowRectangle, healthBarRedRectangle;
	boolean slowed, dotted;
	HashMap<String, Float> floatingStats = new HashMap<String, Float>();
	float originalMoveSpeedMultiplier = 1.0f;
	float originalHealth = 1.0f;
	boolean willDigg = false;
	boolean offPath = false;
	boolean superEnemy = false;
	Vector2 lastChanged = new Vector2(0, 0);
	Vector2 targetPosition;
	Vector2 originalTargetPos;

	public void modifyOriginalMoveSpeed(float f)
	{
		originalMoveSpeedMultiplier = f;
		this.setStat("currentMoveSpeedMultiplier", f);
	}

	public void modifyOriginalHealth(float f)
	{
		this.setStat("currentHealth", (float) Math.floor(this
				.getStat("currentHealth")
				* f));
		originalHealth = this.getStat("currentHealth");
	}

	public Enemy(EnemyStats enemyStats, Vector2 startPosition,
			List<Direction> directions, Sprite enemySprite,
			Sprite redHealthBarSprite, Sprite yellowHealthBarSprite,
			Sprite superSprite, Sprite slowedSprite, Sprite dotSprite, boolean willDigg)
	{
		super(enemySprite);
		this.willDigg = willDigg;
		this.enemyStats = enemyStats;
		floatingStats.put("currentHealth", (float) enemyStats.health);
		originalHealth = this.getStat("currentHealth");
		floatingStats.put("dotTicksLeft", 0f);
		floatingStats.put("distanceTravelled", 0f);
		floatingStats.put("currentMoveSpeedMultiplier", 1.0f);
		floatingStats.put("currentSlowDuration", 0f);
		floatingStats.put("currentDotDamage", 0f);
		floatingStats.put("dotDurationBetweenTicks", 0f);
		floatingStats.put("currentDotDurationBetweenTicks", 0f);
		floatingStats.put("currentGoldYield", (float) enemyStats.goldYield);
		floatingStats.put("distanceTravelled", 0f);

		setSize(enemySprite.getWidth(), enemySprite.getHeight());

		targetPosition = new Vector2(startPosition.x * GameConstants.tileSize,
				startPosition.y * GameConstants.tileSize);
		originalTargetPos = new Vector2(startPosition.x
				* GameConstants.tileSize, startPosition.y
				* GameConstants.tileSize);
		this.sprites = new HashMap<Integer, Sprite>();
		sprites.put(0, enemySprite);
		sprites.put(1, redHealthBarSprite);
		sprites.put(2, yellowHealthBarSprite);
		sprites.put(3, superSprite);
		sprites.put(4, slowedSprite);
		sprites.put(5, dotSprite);
		this.setPosition(targetPosition.x, targetPosition.y);

		generateDirections(directions);
	}

	public void generateDirections(List<Direction> directions)
	{
		targetPosition = originalTargetPos.cpy();
		this.clearActions();
		SequenceAction seqAct = new SequenceAction();
		for (int i = 0; i < directions.size(); i++)
		{
			switch (directions.get(i))
			{
				case Up:
					targetPosition.y -= GameConstants.tileSize;
					break;
				case Down:
					targetPosition.y += GameConstants.tileSize;
					break;
				case Left:
					targetPosition.x -= GameConstants.tileSize;
					break;
				case Right:
					targetPosition.x += GameConstants.tileSize;
					break;
				default:
					break;
			}
			seqAct.addAction(Actions
					.moveTo(targetPosition.x, targetPosition.y, 1.0f / enemyStats.speed));
			//this.queueAction(Actions.moveTo(targetPosition.x, targetPosition.y, 0.4f));
		}
		addAction(seqAct);
	}

	// Basically update(gameTime)
	@Override
	public void act(float gameTime)
	{
		float x1 = this.getX();
		float y1 = this.getY();
		super.act(gameTime * floatingStats.get("currentMoveSpeedMultiplier"));
		float x2 = this.getX();
		float y2 = this.getY();

		this.editStat("distanceTravelled", (float) Math.abs(Math.sqrt(Math
				.pow(x2 - x1, 2)
				+ Math.pow(y2 - y1, 2))));

		if (floatingStats.get("currentMoveSpeedMultiplier") != originalMoveSpeedMultiplier)
		{
			editStat("currentSlowDuration", -gameTime);
			if (getStat("currentSlowDuration") <= 0)
			{
				setStat("currentMoveSpeedMultiplier", originalMoveSpeedMultiplier);
			}
		}
		if (getStat("dotTicksLeft") > 0)
		{
			editStat("currentDotDurationBetweenTicks", -gameTime);
			if (getStat("currentDotDurationBetweenTicks") <= 0)
			{
				editStat("currentHealth", -getStat("currentDotDamage"));
				setStat("currentDotDurationBetweenTicks", getStat("dotDurationBetweenTicks"));
				editStat("dotTicksLeft", -1f);
			}
		}
		healthBarRedRectangle = new Rectangle((int) getX(),
				GameConstants.screenHeight - GameConstants.tileSize
						- (int) getY() - 10, this.getWidth(), 5);
		healthBarYellowRectangle = new Rectangle((int) getX(),
				GameConstants.screenHeight - GameConstants.tileSize
						- (int) getY() - 10, (int) ((float) this.getWidth()
						* (float) getStat("currentHealth") / originalHealth), 5);

	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha)
	{
		super.draw(batch, parentAlpha);
		batch.draw(sprites.get(1), healthBarRedRectangle.getX(), GameConstants
				.morphY(healthBarRedRectangle.getY() - GameConstants.tileSize), healthBarRedRectangle
				.getWidth(), healthBarRedRectangle.getHeight());
		batch.draw(sprites.get(2), healthBarYellowRectangle.getX(), GameConstants
				.morphY(healthBarYellowRectangle.getY()
						- GameConstants.tileSize), healthBarYellowRectangle
				.getWidth(), healthBarYellowRectangle.getHeight());
		if (superEnemy)
		{
			batch.draw(sprites.get(3), super.getX(), super.getY(), super
					.getOriginX(), super.getOriginY(), sprites.get(3)
					.getWidth(), sprites.get(3).getHeight(), 1, 1, super
					.getRotation());
		}
		if (getStat("currentSlowDuration") > 0)
		{
			batch.draw(sprites.get(4), super.getX()+sprites.get(3).getWidth(), super.getY(), super
					.getOriginX(), super.getOriginY(), sprites.get(4)
					.getWidth(), sprites.get(4).getHeight(), 1, 1, super
					.getRotation());
		}
		if (getStat("dotTicksLeft") > 0)
		{
			batch.draw(sprites.get(5), super.getX()+sprites.get(3).getWidth()+sprites.get(4).getWidth(), super.getY(), super
					.getOriginX(), super.getOriginY(), sprites.get(5)
					.getWidth(), sprites.get(5).getHeight(), 1, 1, super
					.getRotation());
		}
	}

	public HashMap<Integer, Sprite> getSprites()
	{
		return sprites;
	}

	public void setSprites(HashMap<Integer, Sprite> sprites)
	{
		this.sprites = sprites;
	}

	public void editStat(String key, float value)
	{
		floatingStats.put(key, (float) (floatingStats.get(key) + value));
	}

	public void setStat(String key, float value)
	{
		if (floatingStats.containsKey(key))
			floatingStats.remove(key);
		floatingStats.put(key, value);
	}

	public float getStat(String key)
	{
		return floatingStats.get(key);
	}
}
