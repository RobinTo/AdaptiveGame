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
        Texture2D towerTexture, missileTexture;
        float rotation, distanceToTargetEnemy;
        Vector2 position, origin;
        float reloadTime, towerReloadTime;
        Enemy targetEnemy;
        int damage;

        public Tower(Texture2D towerTexture, Texture2D missileTexture, Vector2 tilePosition, float towerReloadTime, int damage)
        {
            this.towerTexture = towerTexture;
            this.missileTexture = missileTexture;
            this.position = new Vector2(tilePosition.X * 64, tilePosition.Y * 64);
            this.origin = new Vector2(32, 32);
            this.rotation = 0.0f;
            this.towerReloadTime = towerReloadTime;
            this.reloadTime = towerReloadTime;
            this.damage = damage;
        }

        private void Shoot(List<Missile> missiles)
        {
            missiles.Add(new Missile(missileTexture, this.position + this.origin, distanceToTargetEnemy, rotation, 1024.0f, targetEnemy, damage));
        }

        public void Update(float gameTime, List<Enemy> enemies, Enemy focusFireEnemy, List<Missile> missiles)
        {
            if (focusFireEnemy == null)
            {
                Enemy closestEnemy = enemies[0];
                foreach (Enemy candidateEnemy in enemies) //Algoritme kan improves ved å ikke teste på første element
                {
                    double deltaXClosest = (closestEnemy.Position.X + closestEnemy.Origin.X) - (this.position.X + this.origin.X);
                    double deltaYClosest = (closestEnemy.Position.Y + closestEnemy.Origin.Y) - (this.position.Y + this.origin.Y);
                    double distanceToClosest = Math.Sqrt(Math.Pow(deltaXClosest, 2) + Math.Pow(deltaYClosest, 2));

                    double deltaXCandidate = (candidateEnemy.Position.X + candidateEnemy.Origin.X) - (this.position.X + this.origin.X);
                    double deltaYCandidate = (candidateEnemy.Position.Y + candidateEnemy.Origin.Y) - (this.position.Y + this.origin.Y);
                    double distanceToCandidate = Math.Sqrt(Math.Pow(deltaXCandidate, 2) + Math.Pow(deltaYCandidate, 2));

                    if (distanceToCandidate <= distanceToClosest)
                    {
                        closestEnemy = candidateEnemy;
                        distanceToTargetEnemy = (float)distanceToCandidate;
                    }
                }
                targetEnemy = closestEnemy;

                double deltaX = (closestEnemy.Position.X + closestEnemy.Origin.X) - (this.position.X + this.origin.X);
                double deltaY = (closestEnemy.Position.Y + closestEnemy.Origin.Y) - (this.position.Y + this.origin.Y);
                rotation = (float)Math.Atan2(deltaY, deltaX);
            }
            else
            {
                targetEnemy = focusFireEnemy;
                double deltaX = (focusFireEnemy.Position.X + focusFireEnemy.Origin.X) - (this.position.X + this.origin.X);
                double deltaY = (focusFireEnemy.Position.Y + focusFireEnemy.Origin.Y) - (this.position.Y + this.origin.Y);
                distanceToTargetEnemy = (float)Math.Sqrt(Math.Pow(deltaX, 2) + Math.Pow(deltaY, 2));

                rotation = (float)Math.Atan2(deltaY, deltaX);
            } //Må ha en else if på om targetEnemy er instansiert.

            reloadTime -= (float)gameTime;
            if (reloadTime <= 0)
            {
                this.Shoot(missiles);
                reloadTime = towerReloadTime;
            }
        }

        public void Draw(SpriteBatch spriteBatch)
        {
            spriteBatch.Draw(towerTexture, position + origin, null, Color.White, rotation, origin, 1.0f, SpriteEffects.None, 1.0f);

        }


    }
}
