package no.uia.adaptiveTD;

import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Missile {
	HashMap<Integer, Sprite> sprites;
    Vector2 velocity, position, oldPosition;
    float travelledDistance, distanceToTarget;
    public Boolean remove;
    int damage;
    Enemy targetEnemy;
    
    public Missile(Sprite missileSprite, Vector2 start, float distanceToTarget, float angle, float totalVelocity, Enemy targetEnemy, int damage){
        position = start;
        this.distanceToTarget = distanceToTarget;
        this.travelledDistance = 0.0f;
        this.remove = false;
        this.targetEnemy = targetEnemy;
        this.damage = damage;
        this.sprites = new HashMap<Integer, Sprite>();
        this.sprites.put(0, missileSprite);
        velocity.x = (float)(totalVelocity * Math.cos(angle));
        velocity.y = (float)(totalVelocity * Math.sin(angle));
    }

    public void Update(float gameTime, List<Enemy> enemies)
    {
        oldPosition = position;
        position.x += velocity.x * (float)gameTime;
        position.y += velocity.y * (float)gameTime;
        travelledDistance += (float)(Math.sqrt(Math.pow(position.x - oldPosition.x, 2) + Math.pow(position.y - oldPosition.y, 2)));
        if (travelledDistance >= distanceToTarget)
        {
            remove = true;
            targetEnemy.currentHealth -= damage;
            /*
            targetEnemy.DamageOverTime = damageOverTime;
            targetEnemy.Slow = slow;
            if (areaOfEffect.Radius > 0)
                DoAoE(areaOfEffect.Radius, damage, enemies);
                */
        }
    }

    private void DoAoE(float radius, int damage, List<Enemy> enemies)
    {
        for (Enemy candidateEnemy : enemies) //Algoritme kan improves ved å ikke teste på første element
        {
            double deltaXCandidate = (candidateEnemy.getPosition().x + candidateEnemy.getOrigin().x) - (this.position.x);
            double deltaYCandidate = (candidateEnemy.getPosition().y + candidateEnemy.getOrigin().y) - (this.position.y);
            double distanceToCandidate = Math.sqrt(Math.pow(deltaXCandidate, 2) + Math.pow(deltaYCandidate, 2));

            if (distanceToCandidate <= radius)
            {
                candidateEnemy.currentHealth -= damage;
            }
        }
    }

    public void Draw(SpriteBatch spriteBatch)
    {
        spriteBatch.draw(sprites.get(0), position.x, position.y);
    }

}

 

