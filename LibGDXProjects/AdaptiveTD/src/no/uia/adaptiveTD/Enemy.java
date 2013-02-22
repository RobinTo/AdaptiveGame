package no.uia.adaptiveTD;

import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class Enemy {
    EnemyStats enemyStats;
	int currentHealth, currentSpeed, directionCounter; 
    Vector2 position, targetPosition, origin;
    float rotation, distanceTravelled;
    Rectangle healthBarYellowRectangle, healthBarRedRectangle;
    List<Direction> directions;
    Direction currentDirection = Direction.None;
	HashMap<Integer, Sprite> sprites;
    
    public Enemy(Vector2 startPosition, EnemyStats enemyStats, List<Direction> directions, Sprite enemySprite, Sprite redHealthBarSprite, Sprite yellowHealthBarSprite)
    {
    	this.enemyStats = enemyStats;
    	this.directions = directions;
        origin = new Vector2(GameConstants.tileSize / 2, GameConstants.tileSize / 2);
        this.position = new Vector2(startPosition.x * GameConstants.tileSize, startPosition.y * GameConstants.tileSize);
        targetPosition = new Vector2(position.x, position.y);
        this.currentHealth = enemyStats.getHealth();
        this.currentSpeed =  enemyStats.getSpeed();
        this.sprites = new HashMap<Integer, Sprite>();
        sprites.put(0, enemySprite);
        sprites.put(1, redHealthBarSprite);
        sprites.put(2, yellowHealthBarSprite);
    }
    public void Turn(Boolean right)
    {
        if (right)
            rotation -= (float)(Math.PI / 2);
        else
            rotation += (float)(Math.PI / 2);
    }

    public void Update(float gameTime)
    {
    	System.out.println(gameTime);
        if (currentDirection == Direction.None)
        {
            if (directionCounter < directions.size())
            {
                currentDirection = directions.get(directionCounter);
                directionCounter++;
            }
            else
                currentDirection = Direction.Right;
            switch (currentDirection)
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
        }

        switch (currentDirection)
        {
            case Up:
                //position.y -= (float)(currentSpeed * gameTime - currentSpeed * gameTime * slow.Percentage / 100);
            	position.y -= (float)(currentSpeed * gameTime);
                break;
            case Down:
                position.y += (float)(currentSpeed * gameTime);
                break;
            case Left:
                position.x -= (float)(currentSpeed * gameTime);
                break;
            case Right:
                position.x += (float)(currentSpeed * gameTime);
                break;
            default:
            	break;
        }
//distanceTravelled += (float)(currentSpeed * gameTime - currentSpeed * gameTime * slow.Percentage / 100);
        distanceTravelled += (float)(currentSpeed * gameTime - currentSpeed * gameTime);

        if (Math.abs(position.x - targetPosition.x) < 1 && Math.abs(position.y - targetPosition.y) < 1)
        {
            position = new Vector2(targetPosition.x, targetPosition.y);
            currentDirection = Direction.None;
        }

        /*
        damageOverTime.DurationSinceLastTick -= gameTime;
        if (damageOverTime.DurationSinceLastTick <= 0)
        {
            damageOverTime.DurationSinceLastTick = damageOverTime.Duration / damageOverTime.Ticks;
            health -= damageOverTime.DamagePerTick;
        }
        Boolean isSlowed, isDotted, isAffected;
        damageOverTime.RemainingDuration -= gameTime;
        if (damageOverTime.RemainingDuration <= 0)
        {
            damageOverTime = new DamageOverTime(false);
            isDotted = false;
        }
        else
        {
            isDotted = true;
        }

        slow.Duration -= gameTime;
        if (slow.Duration <= 0)
        {
            slow = new Slow(false);
            isSlowed = false;
        }
        else
        {
            isSlowed = true;
        }
        isAffected = isSlowed || isDotted;
        if (isAffected)
            color = Color.LightGreen;
        else
            color = Color.White;
        */
        healthBarRedRectangle = new Rectangle((int)position.x, (int)position.y - 10, 64, 5);
        healthBarYellowRectangle = new Rectangle((int)position.x, (int)position.y - 10, (int)((float)64 * (float)currentHealth / (float)enemyStats.getHealth()), 5);
    }

    public void Draw(SpriteBatch spriteBatch)
    {
        Vector2 drawPosition = new Vector2(position.x + origin.x, position.y + origin.y);
        spriteBatch.draw(sprites.get(0), position.x, GameConstants.morphY(position.y));
        spriteBatch.draw(sprites.get(1), healthBarRedRectangle.getX(), GameConstants.morphY(healthBarRedRectangle.getY() - GameConstants.tileSize), healthBarRedRectangle.getWidth(), healthBarRedRectangle.getHeight());
        spriteBatch.draw(sprites.get(2), healthBarYellowRectangle.getX(), GameConstants.morphY(healthBarYellowRectangle.getY() - GameConstants.tileSize), healthBarYellowRectangle.getWidth(), healthBarYellowRectangle.getHeight());
        
        /*
        spriteBatch.Draw(enemyTexture, drawPosition, null, color, rotation, origin, 1.0f, SpriteEffects.None, 1.0f);
        spriteBatch.Draw(redHealthBar, healthBarRedRectangle, Color.White);
        spriteBatch.Draw(yellowHealthBar, healthBarYellowRectangle, Color.White);
        */
    }
	public EnemyStats getEnemyInfo() {
		return enemyStats;
	}
	public void setEnemyInfo(EnemyStats enemyInfo) {
		this.enemyStats = enemyInfo;
	}
	public int getCurrentHealth() {
		return currentHealth;
	}
	public void setCurrentHealth(int currentHealth) {
		this.currentHealth = currentHealth;
	}
	public int getCurrentSpeed() {
		return currentSpeed;
	}
	public void setCurrentSpeed(int currentSpeed) {
		this.currentSpeed = currentSpeed;
	}
	public int getDirectionCounter() {
		return directionCounter;
	}
	public void setDirectionCounter(int directionCounter) {
		this.directionCounter = directionCounter;
	}
	public Vector2 getPosition() {
		return position;
	}
	public void setPosition(Vector2 position) {
		this.position = position;
	}
	public Vector2 getTargetPosition() {
		return targetPosition;
	}
	public void setTargetPosition(Vector2 targetPosition) {
		this.targetPosition = targetPosition;
	}
	public Vector2 getOrigin() {
		return origin;
	}
	public void setOrigin(Vector2 origin) {
		this.origin = origin;
	}
	public float getRotation() {
		return rotation;
	}
	public void setRotation(float rotation) {
		this.rotation = rotation;
	}
	public float getDistanceTravelled() {
		return distanceTravelled;
	}
	public void setDistanceTravelled(float distanceTravelled) {
		this.distanceTravelled = distanceTravelled;
	}
	public Rectangle getHealthBarYellowRectangle() {
		return healthBarYellowRectangle;
	}
	public void setHealthBarYellowRectangle(Rectangle healthBarYellowRectangle) {
		this.healthBarYellowRectangle = healthBarYellowRectangle;
	}
	public Rectangle getHealthBarRedRectangle() {
		return healthBarRedRectangle;
	}
	public void setHealthBarRedRectangle(Rectangle healthBarRedRectangle) {
		this.healthBarRedRectangle = healthBarRedRectangle;
	}
	public List<Direction> getDirections() {
		return directions;
	}
	public void setDirections(List<Direction> directions) {
		this.directions = directions;
	}
	public Direction getCurrentDirection() {
		return currentDirection;
	}
	public void setCurrentDirection(Direction currentDirection) {
		this.currentDirection = currentDirection;
	}
	public HashMap<Integer, Sprite> getSprites() {
		return sprites;
	}
	public void setSprites(HashMap<Integer, Sprite> sprites) {
		this.sprites = sprites;
	}

}
