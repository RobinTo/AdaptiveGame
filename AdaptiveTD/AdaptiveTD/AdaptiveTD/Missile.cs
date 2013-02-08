using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;

namespace AdaptiveTD
{
    class Missile
    {
        Texture2D selfTexture;
        Vector2 velocity, position, oldPosition;
        float travelledDistance, distanceToTarget;
        public bool remove;
        int damage;
        Enemy targetEnemy;
        DamageOverTime damageOverTime;
        Slow slow;

        public Missile(Texture2D selfTexture, Vector2 start, float distanceToTarget, float angle, float totalVelocity, Enemy targetEnemy, int damage, DamageOverTime damageOverTime, Slow slow)
        {
            this.selfTexture = selfTexture;
            position = start;
            this.distanceToTarget = distanceToTarget;
            this.travelledDistance = 0.0f;
            this.remove = false;
            this.targetEnemy = targetEnemy;
            this.damage = damage;
            this.damageOverTime = damageOverTime;
            this.slow = slow;

            velocity.X = (float)(totalVelocity * Math.Cos(angle));
            velocity.Y = (float)(totalVelocity * Math.Sin(angle));
        }

        public void Update(float gameTime)
        {
            oldPosition = position;
            position += velocity * (float)gameTime;
            travelledDistance += (float)(Math.Sqrt(Math.Pow(position.X - oldPosition.X, 2) + Math.Pow(position.Y - oldPosition.Y, 2)));
            if (travelledDistance >= distanceToTarget)
            {
                remove = true;
                targetEnemy.Health -= damage;
                targetEnemy.DamageOverTime = damageOverTime;
                targetEnemy.Slow = slow;
            }
        }

        public void Draw(SpriteBatch spriteBatch)
        {
            spriteBatch.Draw(selfTexture, position, Color.White);
        }
    }
}
