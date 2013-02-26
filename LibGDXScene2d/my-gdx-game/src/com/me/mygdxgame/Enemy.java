package com.me.mygdxgame;

import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class Enemy extends ExtendedActor{
	
	HashMap<Integer, Sprite> sprites;
	float distanceTravelled, currentMoveSpeedMultiplier, currentSlowDuration;
	EnemyStats enemyStats;
	int currentHealth;
	Rectangle healthBarYellowRectangle, healthBarRedRectangle;
	
    public Enemy(EnemyStats enemyStats, Vector2 startPosition, List<Direction> directions, Sprite enemySprite, Sprite redHealthBarSprite, Sprite yellowHealthBarSprite)
    {
    	super(enemySprite);
    	this.enemyStats = enemyStats;
    	currentHealth = enemyStats.health;
    	currentMoveSpeedMultiplier = 1.0f;
    	setSize(enemySprite.getWidth(), enemySprite.getHeight());
    	
        Vector2 targetPosition = new Vector2(startPosition.x * GameConstants.tileSize, startPosition.y * GameConstants.tileSize);
        this.sprites = new HashMap<Integer, Sprite>();
        sprites.put(0, enemySprite);
        sprites.put(1, redHealthBarSprite);
        sprites.put(2, yellowHealthBarSprite);
        this.setPosition(targetPosition.x, targetPosition.y);

        SequenceAction seqAct = new SequenceAction();
        for(int i = 0; i<directions.size(); i++)
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
        	seqAct.addAction(Actions.moveTo(targetPosition.x, targetPosition.y, 0.7f));
        	//this.queueAction(Actions.moveTo(targetPosition.x, targetPosition.y, 0.4f));
        }
        addAction(seqAct);
    }
    
    // Basically update(gameTime)
    @Override
    public void act(float gameTime)
    {
    	super.act(gameTime * currentMoveSpeedMultiplier);
    	if (currentSlowDuration > 0)
    		currentSlowDuration -= gameTime;
    	if (currentSlowDuration <= 0)
    		currentMoveSpeedMultiplier = 1.0f;
    	healthBarRedRectangle = new Rectangle((int)getX(), GameConstants.screenHeight - GameConstants.tileSize - (int)getY() - 10, 64, 5);
        healthBarYellowRectangle = new Rectangle((int)getX(), GameConstants.screenHeight - GameConstants.tileSize - (int)getY() - 10, (int)((float)64 * (float)currentHealth / (float)enemyStats.getHealth()), 5);
   
    }
    
    @Override
	public void draw (SpriteBatch batch, float parentAlpha) {
    	super.draw(batch, parentAlpha);
    	batch.draw(sprites.get(1), healthBarRedRectangle.getX(), GameConstants.morphY(healthBarRedRectangle.getY() - GameConstants.tileSize), healthBarRedRectangle.getWidth(), healthBarRedRectangle.getHeight());
        batch.draw(sprites.get(2), healthBarYellowRectangle.getX(), GameConstants.morphY(healthBarYellowRectangle.getY() - GameConstants.tileSize), healthBarYellowRectangle.getWidth(), healthBarYellowRectangle.getHeight());
        
	}
    
	public HashMap<Integer, Sprite> getSprites() {
		return sprites;
	}
	public void setSprites(HashMap<Integer, Sprite> sprites) {
		this.sprites = sprites;
	}
}
