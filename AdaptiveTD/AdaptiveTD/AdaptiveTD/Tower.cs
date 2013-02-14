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
            missiles.Add(new Missile(towerStats.MissileTexture, this.position + this.origin, distanceToTargetEnemy, rotation, 1024.0f, targetEnemy, towerStats.Damage, towerStats.DamageOverTime, towerStats.Slow, towerStats.AreaOfEffect));
        }

        public void Update(float gameTime, List<Enemy> enemies, Enemy focusFireEnemy, List<Missile> missiles)
        {
            bool wasInRange = false;
            if (focusFireEnemy != null)
            {
                targetEnemy = focusFireEnemy;
                double deltaX = (focusFireEnemy.Position.X + focusFireEnemy.Origin.X) - (this.position.X + this.origin.X);
                double deltaY = (focusFireEnemy.Position.Y + focusFireEnemy.Origin.Y) - (this.position.Y + this.origin.Y);
                distanceToTargetEnemy = (float)Math.Sqrt(Math.Pow(deltaX, 2) + Math.Pow(deltaY, 2));

                if (distanceToTargetEnemy > towerStats.Range)
                    wasInRange = false;
                else
                {
                    rotation = (float)Math.Atan2(deltaY, deltaX);
                    wasInRange = true;
                }
            } //Må ha en else if på om targetEnemy er instansiert.
            if (!wasInRange)
            {
                Enemy closestEnemy = null;
                
                foreach (Enemy candidateEnemy in enemies) //Algoritme kan improves ved å ikke teste på første element
                {
                    double deltaXCandidate = (candidateEnemy.Position.X + candidateEnemy.Origin.X) - (this.position.X + this.origin.X);
                    double deltaYCandidate = (candidateEnemy.Position.Y + candidateEnemy.Origin.Y) - (this.position.Y + this.origin.Y);
                    double distanceToCandidate = Math.Sqrt(Math.Pow(deltaXCandidate, 2) + Math.Pow(deltaYCandidate, 2));

                    if (distanceToCandidate <= towerStats.Range && (closestEnemy == null || candidateEnemy.DistanceTravelled > closestEnemy.DistanceTravelled))
                    {
                        closestEnemy = candidateEnemy;
                        distanceToTargetEnemy = (float)distanceToCandidate;
                    }
                }
                if (closestEnemy != null)
                {
                    targetEnemy = closestEnemy;
                    double deltaX = (closestEnemy.Position.X + closestEnemy.Origin.X) - (this.position.X + this.origin.X);
                    double deltaY = (closestEnemy.Position.Y + closestEnemy.Origin.Y) - (this.position.Y + this.origin.Y);
                    rotation = (float)Math.Atan2(deltaY, deltaX);
                }
                else
                    targetEnemy = null;
            }
            
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
        DamageOverTime damageOverTime;
        public DamageOverTime DamageOverTime
        {
            get { return damageOverTime; }
            set { damageOverTime = value; }
        }
        Slow slow;
        public Slow Slow
        {
            get { return slow; }
            set { slow = value; }
        }
        AreaOfEffect areaOfEffect;

        public AreaOfEffect AreaOfEffect
        {
            get { return areaOfEffect; }
            set { areaOfEffect = value; }
        }


        public TowerStats(string type, Texture2D towerTexture, Texture2D missileTexture, float ReloadTime, int damage, int goldCost, int range, DamageOverTime damageOverTime, Slow slow, AreaOfEffect areaOfEffect)
        {
            this.type = type;
            this.towerTexture = towerTexture;
            this.missileTexture = missileTexture;
            this.reloadTime = ReloadTime;
            this.damage = damage;
            this.goldCost = goldCost;
            this.range = range * GameConstants.tileSize;
            this.damageOverTime = damageOverTime;
            this.slow = slow;
            this.areaOfEffect = areaOfEffect;
        }
    }

    public struct AreaOfEffect
    {
        float radius;
        public float Radius
        {
            get { return radius; }
            set { radius = value; }
        }
        public AreaOfEffect(float radius)
        {
            this.radius = radius;
        }
    }

    public struct DamageOverTime
    {
        int damagePerTick;

        public int DamagePerTick
        {
            get { return damagePerTick; }
            set { damagePerTick = value; }
        }
        int ticks;

        public int Ticks
        {
            get { return ticks; }
            set { ticks = value; }
        }
        float duration;

        public float Duration
        {
            get { return duration; }
            set { duration = value; }
        }
        float durationSinceLastTick;

        float remainingDuration;
        public float RemainingDuration
        {
            get { return remainingDuration; }
            set { remainingDuration = value; }
        }

        public float DurationSinceLastTick
        {
            get { return durationSinceLastTick; }
            set { durationSinceLastTick = value; }
        }

        public DamageOverTime(bool parameter)
        {
            this.damagePerTick = 0;
            this.ticks = 2;
            this.duration = 0;
            this.remainingDuration = 0;
            this.durationSinceLastTick = duration / ticks;
        }
        public DamageOverTime(int damagePerTick, int ticks, float duration)
        {
            this.damagePerTick = damagePerTick;
            this.ticks = ticks;
            this.duration = duration;
            this.remainingDuration = duration;
            this.durationSinceLastTick = duration / ticks;
        }
    }

    public struct Slow
    {
        int percentage;

        public int Percentage
        {
            get { return percentage; }
            set { percentage = value; }
        }
        float duration;

        public float Duration
        {
            get { return duration; }
            set { duration = value; }
        }

        public Slow(bool parameter)
        {
            this.percentage = 0;
            this.duration = 0;
        }
        public Slow(int percentage, float duration)
        {
            this.percentage = percentage;
            this.duration = duration;
        }
    }
 
}
