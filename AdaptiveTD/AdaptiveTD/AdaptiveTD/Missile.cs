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

        public Missile(Texture2D selfTexture, Vector2 start, float distanceToTarget, float angle, float totalVelocity, Enemy targetEnemy, int damage)
        {
            this.selfTexture = selfTexture;
            position = start;
            this.distanceToTarget = distanceToTarget;
            this.travelledDistance = 0.0f;
            this.remove = false;
            this.targetEnemy = targetEnemy;
            this.damage = damage;

            velocity.X = (float)(totalVelocity * Math.Cos(angle));
            velocity.Y = (float)(totalVelocity * Math.Sin(angle));
        }

        public void Update(GameTime gameTime)
        {
            oldPosition = position;
            position += velocity * (float)gameTime.ElapsedGameTime.TotalSeconds;
            travelledDistance += (float)(Math.Sqrt(Math.Pow(position.X - oldPosition.X, 2) + Math.Pow(position.Y - oldPosition.Y, 2)));
            if (travelledDistance >= distanceToTarget)
            {
                remove = true;
                targetEnemy.Health -= damage;
            }
        }

        public void Draw(SpriteBatch spriteBatch)
        {
            spriteBatch.Draw(selfTexture, position, Color.White);
        }
    }
}
