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
        TowerStats towerStats;
        public TowerStats TowerStats
        {
            get { return towerStats; }
            set { towerStats = value; }
        }
        Vector2 position, tilePosition, origin;
        public Vector2 Origin
        {
            get { return origin; }
            set { origin = value; }
        }
        public Vector2 Position
        {
            get { return position; }
            set { position = value; }
        }
        public Vector2 TilePosition
        {
            get { return tilePosition; }
        }
        float rotation, distanceToTargetEnemy;
        Enemy targetEnemy;
        float currentReloadTime;
        Rectangle rangeHighlightRectangle;
        public Rectangle RangeHighlightRectangle
        {
            get { return rangeHighlightRectangle; }
            set { rangeHighlightRectangle = value; }
        }
        Color color;
        public Color Color
        {
            get { return color; }
            set { color = value; }
        }


        public Tower(TowerStats towerStats, Vector2 tilePosition)
        {
            this.towerStats = towerStats;
            this.currentReloadTime = towerStats.ReloadTime;
            this.position = new Vector2(tilePosition.X * 64, tilePosition.Y * 64);
            this.origin = new Vector2(32, 32);
            this.rotation = 0.0f;
            this.color = Color.White;
            this.tilePosition = tilePosition;
            this.rangeHighlightRectangle = new Rectangle((int)position.X + (int)origin.X - towerStats.Range, (int)position.Y + (int)origin.Y - towerStats.Range, towerStats.Range * 2, towerStats.Range * 2);
        }

        private void Shoot(List<Missile> missiles)
        {
            missiles.Add(new Missile(towerStats.MissileTexture, this.position + this.origin, distanceToTargetEnemy, rotation, 1024.0f, targetEnemy, towerStats.Damage));
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
                if (distanceToTargetEnemy > towerStats.Range)
                {
                    targetEnemy = null;
                }
                else
                {
                    targetEnemy = closestEnemy;
                    double deltaX = (closestEnemy.Position.X + closestEnemy.Origin.X) - (this.position.X + this.origin.X);
                    double deltaY = (closestEnemy.Position.Y + closestEnemy.Origin.Y) - (this.position.Y + this.origin.Y);
                    rotation = (float)Math.Atan2(deltaY, deltaX);
                }
            }
            else
            {
                targetEnemy = focusFireEnemy;
                double deltaX = (focusFireEnemy.Position.X + focusFireEnemy.Origin.X) - (this.position.X + this.origin.X);
                double deltaY = (focusFireEnemy.Position.Y + focusFireEnemy.Origin.Y) - (this.position.Y + this.origin.Y);
                distanceToTargetEnemy = (float)Math.Sqrt(Math.Pow(deltaX, 2) + Math.Pow(deltaY, 2));

                rotation = (float)Math.Atan2(deltaY, deltaX);
            } //Må ha en else if på om targetEnemy er instansiert.
            
            currentReloadTime -= (float)gameTime;
            currentReloadTime = (currentReloadTime < 0) ? 0 : currentReloadTime;

            if (targetEnemy != null && currentReloadTime <= 0)
            {
                this.Shoot(missiles);
                currentReloadTime = towerStats.ReloadTime;
            }
        }

        public void Draw(SpriteBatch spriteBatch)
        {
            spriteBatch.Draw(towerStats.TowerTexture, position + origin, null, color, rotation, origin, 1.0f, SpriteEffects.None, 1.0f);
        }
    }

    public struct TowerStats
    {
        string type;
        public string Type
        {
            get { return type; }
            set { type = value; }
        }
        Texture2D towerTexture;
        public Texture2D TowerTexture
        {
            get { return towerTexture; }
            set { towerTexture = value; }
        }
        Texture2D missileTexture;
        public Texture2D MissileTexture
        {
            get { return missileTexture; }
            set { missileTexture = value; }
        }
        float reloadTime;
        public float ReloadTime
        {
            get { return reloadTime; }
            set { reloadTime = value; }
        }
        int damage;
        public int Damage
        {
            get { return damage; }
            set { damage = value; }
        }
        int goldCost;
        public int GoldCost
        {
            get { return goldCost; }
            set { goldCost = value; }
        }
        int range;
        public int Range
        {
            get { return range; }
            set { range = value; }
        }

        public TowerStats(string type, Texture2D towerTexture, Texture2D missileTexture, float ReloadTime, int damage, int goldCost, int range)
        {
            this.type = type;
            this.towerTexture = towerTexture;
            this.missileTexture = missileTexture;
            this.reloadTime = ReloadTime;
            this.damage = damage;
            this.goldCost = goldCost;
            this.range = range * GameConstants.tileSize;
        }
    }
 
}
