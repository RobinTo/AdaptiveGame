package no.uia.adaptiveTD;

import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Tower1 {

	TowerStats towerStats;
	Vector2 position, tilePosition, origin;
	float rotation, distanceToTargetEnemy, currentReloadTime;
	Enemy targetEnemy;
	int currentLevel;
	Rectangle rangeHighlightRectangle;
	Color color;
	HashMap<Integer, Sprite> textures = new HashMap<Integer, Sprite>();

	public Tower1(TowerStats towerStats, Vector2 tilePosition,
			Sprite towerTexture1, Sprite towerTexture2, Sprite towerTexture3, Sprite missileTexture) {
		this.towerStats = towerStats;
		textures.put(0, towerTexture1);
		textures.put(1, towerTexture2);
		textures.put(2, towerTexture3);
		textures.put(3, missileTexture);
		this.currentReloadTime = towerStats.getReloadTime();
		this.position = new Vector2(tilePosition.x * 64, tilePosition.y * 64);
		this.origin = new Vector2(GameConstants.tileSize / 2,
				GameConstants.tileSize / 2);
		this.rotation = 0.0f;
		this.color = Color.WHITE;
		this.tilePosition = tilePosition;
		this.rangeHighlightRectangle = new Rectangle((int) position.x
				+ (int) origin.x - towerStats.getRange(), (int) position.y
				+ (int) origin.y - towerStats.getRange(),
				towerStats.getRange() * 2, towerStats.getRange() * 2);
		currentLevel = 1;
	}

	private void Shoot(List<Missile> missiles) {
		missiles.add(new Missile(textures.get(1), new Vector2(this.position.x + this.origin.x, this.position.y + this.origin.y),
				distanceToTargetEnemy, rotation, 1024.0f, targetEnemy,
				towerStats.getDamage(currentLevel)));
	}

	public void Update(float gameTime, List<Enemy> enemies, Enemy focusFireEnemy, List<Missile> missiles)
        {
            Boolean wasInRange = false;
            if (focusFireEnemy != null)
            {
                targetEnemy = focusFireEnemy;
                double deltaX = (focusFireEnemy.position.x + focusFireEnemy.origin.y) - (this.position.y + this.origin.x);
                double deltaY = (focusFireEnemy.position.y + focusFireEnemy.origin.x) - (this.position.x + this.origin.y);
                distanceToTargetEnemy = (float)Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));

                if (distanceToTargetEnemy > towerStats.range)
                    wasInRange = false;
                else
                {
                    rotation = (float)Math.atan2(deltaY, deltaX);
                    wasInRange = true;
                }
            } //Må ha en else if på om targetEnemy er instansiert.
            if (!wasInRange)
            {
                Enemy closestEnemy = null;
                
                for (Enemy candidateEnemy : enemies) //Algoritme kan improves ved å ikke teste på første element
                {
                    double deltaXCandidate = (candidateEnemy.position.x + candidateEnemy.origin.x) - (this.position.x + this.origin.x);
                    double deltaYCandidate = (candidateEnemy.position.y + candidateEnemy.origin.y) - (this.position.y + this.origin.y);
                    double distanceToCandidate = Math.sqrt(Math.pow(deltaXCandidate, 2) + Math.pow(deltaYCandidate, 2));

                    if (distanceToCandidate <= towerStats.range && (closestEnemy == null || candidateEnemy.distanceTravelled > closestEnemy.distanceTravelled))
                    {
                        closestEnemy = candidateEnemy;
                        distanceToTargetEnemy = (float)distanceToCandidate;
                    }
                }
                if (closestEnemy != null)
                {
                    targetEnemy = closestEnemy;
                    double deltaX = (closestEnemy.position.x + closestEnemy.origin.x) - (this.position.x + this.origin.x);
                    double deltaY = (closestEnemy.position.y + closestEnemy.origin.y) - (this.position.y + this.origin.y);
                    rotation = (float)Math.atan2(deltaY, deltaX);
                }
                else
                    targetEnemy = null;
            }
            
            currentReloadTime -= (float)gameTime;
            currentReloadTime = (currentReloadTime < 0) ? 0 : currentReloadTime;

            if (targetEnemy != null && currentReloadTime <= 0)
            {
                this.Shoot(missiles);
                currentReloadTime = towerStats.reloadTime;
            }
        }

	public void Draw(SpriteBatch spriteBatch) {
		spriteBatch.draw(textures.get(0), position.x, position.y, origin.x, origin.y,
				GameConstants.tileSize, GameConstants.tileSize, 1, 1, rotation);

		// spriteBatch.Draw(textures[], position + origin, null, color,
		// rotation, origin, 1.0f, SpriteEffects.None, 1.0f);
	}

	public TowerStats getTowerStats() {
		return towerStats;
	}

	public void setTowerStats(TowerStats towerStats) {
		this.towerStats = towerStats;
	}

	public Vector2 getPosition() {
		return position;
	}

	public void setPosition(Vector2 position) {
		this.position = position;
	}

	public Vector2 getTilePosition() {
		return tilePosition;
	}

	public void setTilePosition(Vector2 tilePosition) {
		this.tilePosition = tilePosition;
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

	public float getDistanceToTargetEnemy() {
		return distanceToTargetEnemy;
	}

	public void setDistanceToTargetEnemy(float distanceToTargetEnemy) {
		this.distanceToTargetEnemy = distanceToTargetEnemy;
	}

	public float getCurrentReloadTime() {
		return currentReloadTime;
	}

	public void setCurrentReloadTime(float currentReloadTime) {
		this.currentReloadTime = currentReloadTime;
	}

	public Enemy getTargetEnemy() {
		return targetEnemy;
	}

	public void setTargetEnemy(Enemy targetEnemy) {
		this.targetEnemy = targetEnemy;
	}

	public int getCurrentLevel() {
		return currentLevel;
	}

	public void setCurrentLevel(int currentLevel) {
		this.currentLevel = currentLevel;
	}

	public Rectangle getRangeHighlightRectangle() {
		return rangeHighlightRectangle;
	}

	public void setRangeHighlightRectangle(Rectangle rangeHighlightRectangle) {
		this.rangeHighlightRectangle = rangeHighlightRectangle;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public HashMap<Integer, Sprite> getTextures() {
		return textures;
	}

	public void setTextures(HashMap<Integer, Sprite> textures) {
		this.textures = textures;
	}
	
}
