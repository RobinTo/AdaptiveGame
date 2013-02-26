package com.me.mygdxgame;

import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.badlogic.gdx.math.Interpolation.*;

public class Enemy extends ExtendedActor{
	
	HashMap<Integer, Sprite> sprites;	

    public Enemy(Vector2 startPosition, List<Direction> directions, Sprite enemySprite)
    {
    	super(enemySprite);
    	setSize(enemySprite.getWidth(), enemySprite.getHeight());
    	
        Vector2 targetPosition = new Vector2(startPosition.x * GameConstants.tileSize, startPosition.y * GameConstants.tileSize);
        this.sprites = new HashMap<Integer, Sprite>();
        sprites.put(0, enemySprite);
        this.setPosition(targetPosition.x, targetPosition.y);

        SequenceAction seqAct = new SequenceAction();
        while(directions.size() > 0)
        {
        	switch (directions.get(0))
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
        	directions.remove(0);
        }
        addAction(seqAct);
    }
    
    // Basically update(gameTime)
    @Override
    public void act(float gameTime)
    {
    	super.act(gameTime);
    }
    
	public HashMap<Integer, Sprite> getSprites() {
		return sprites;
	}
	public void setSprites(HashMap<Integer, Sprite> sprites) {
		this.sprites = sprites;
	}
}
