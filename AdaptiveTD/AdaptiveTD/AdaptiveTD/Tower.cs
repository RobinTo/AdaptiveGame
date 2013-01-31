using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;

namespace AdaptiveTD
{
    class Tower
    {
        Texture2D towerTexture;
        float rotation;
        Vector2 position, origin;

        public Tower(Texture2D towerTexture, Vector2 tilePosition)
        {
            this.towerTexture = towerTexture;
            this.position = new Vector2(tilePosition.X * 64, tilePosition.Y * 64);
            this.origin = new Vector2(32, 32);
            this.rotation = 0.0f;
        }

        public void Shoot()
        {

        }

        public void Update(GameTime gameTime, List<Enemy> enemies, Enemy targetEnemy)
        {
            if (targetEnemy == null)
            {
                Enemy closestEnemy = enemies[0];
                foreach (Enemy candidateEnemy in enemies) //Algoritme kan improves ved å ikke teste på første element
                {
                    double deltaXClosest = (closestEnemy.Position.X + closestEnemy.Origin.X) - (this.position.X + this.origin.X);
                    double deltaYClosest = (closestEnemy.Position.Y + closestEnemy.Origin.Y) - (this.position.Y + this.origin.Y);
                    double distanceToClosest = Math.Sqrt(Math.Pow(deltaXClosest, 2) + Math.Pow(deltaYClosest, 2));

                    double deltaXCandidate = (closestEnemy.Position.X + closestEnemy.Origin.X) - (this.position.X + this.origin.X);
                    double deltaYCandidate = (closestEnemy.Position.Y + closestEnemy.Origin.Y) - (this.position.Y + this.origin.Y);
                    double distanceToCandidate = Math.Sqrt(Math.Pow(deltaXCandidate, 2) + Math.Pow(deltaYCandidate, 2));

                    if (distanceToCandidate < distanceToClosest)
                        closestEnemy = candidateEnemy;
                }

                double deltaX = (closestEnemy.Position.X + closestEnemy.Origin.X) - (this.position.X + this.origin.X);
                double deltaY = (closestEnemy.Position.Y + closestEnemy.Origin.Y) - (this.position.Y + this.origin.Y);
                rotation = (float)Math.Atan2(deltaY, deltaX);
            }
            else
            {
                double deltaX = (targetEnemy.Position.X + targetEnemy.Origin.X) - (this.position.X + this.origin.X);
                double deltaY = (targetEnemy.Position.Y + targetEnemy.Origin.Y) - (this.position.Y + this.origin.Y);
                rotation = (float)Math.Atan2(deltaY, deltaX);
            }
          
        }

        public void Draw(SpriteBatch spriteBatch)
        {
            spriteBatch.Draw(towerTexture, position + origin, null, Color.White, rotation, origin, 1.0f, SpriteEffects.None, 1.0f);

        }


    }
}
