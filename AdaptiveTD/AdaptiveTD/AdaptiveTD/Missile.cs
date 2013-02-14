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
        AreaOfEffect areaOfEffect;

        public Missile(Texture2D selfTexture, Vector2 start, float distanceToTarget, float angle, float totalVelocity, Enemy targetEnemy, int damage, DamageOverTime damageOverTime, Slow slow, AreaOfEffect areaOfEffect)
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
            this.areaOfEffect = areaOfEffect;

            velocity.X = (float)(totalVelocity * Math.Cos(angle));
            velocity.Y = (float)(totalVelocity * Math.Sin(angle));
        }

        public void Update(float gameTime, List<Enemy> enemies)
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
                if (areaOfEffect.Radius > 0)
                    DoAoE(areaOfEffect.Radius, damage, enemies);
            }
        }

        private void DoAoE(float radius, int damage, List<Enemy> enemies)
        {
            foreach (Enemy candidateEnemy in enemies) //Algoritme kan improves ved å ikke teste på første element
            {
                double deltaXCandidate = (candidateEnemy.Position.X + candidateEnemy.Origin.X) - (this.position.X);
                double deltaYCandidate = (candidateEnemy.Position.Y + candidateEnemy.Origin.Y) - (this.position.Y);
                double distanceToCandidate = Math.Sqrt(Math.Pow(deltaXCandidate, 2) + Math.Pow(deltaYCandidate, 2));

                if (distanceToCandidate <= radius)
                {
                    candidateEnemy.Health -= damage;
                }
            }
        }

        public void Draw(SpriteBatch spriteBatch)
        {
            spriteBatch.Draw(selfTexture, position, Color.White);
        }
    }
}
