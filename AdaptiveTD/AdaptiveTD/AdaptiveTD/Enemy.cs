using System;
using System.Collections.Generic;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;

namespace AdaptiveTD
{
    class Enemy
    {
        Texture2D enemyTexture, healthBarRedTexture, healthBarYellowTexture;
        int health, maxHealth;
        public int Health
        {
            get { return health; }
            set { health = value; }
        }
        int normalSpeed, currentSpeed;
        int goldYield;
        public int GoldYield
        {
            get { return goldYield; }
        }
        Vector2 targetPosition;
        Vector2 position;
        public Vector2 Position
        {
            get { return position; }
            set { position = value; }
        }
        List<Direction> directions = new List<Direction>();
        float rotation;
        Direction currentDirection = Direction.None;
        Vector2 origin;
        public Vector2 Origin
        {
            get { return origin; }
        }
        float distanceTravelled;
        public float DistanceTravelled
        {
            get { return distanceTravelled; }
        }
        int directionCounter = 0;
        Rectangle healthBarYellowRectangle, healthBarRedRectangle;
        Slow slow;
        public Slow Slow
        {
            get { return slow; }
            set { slow = value; }
        }
        DamageOverTime damageOverTime;
        public DamageOverTime DamageOverTime
        {
            get { return damageOverTime; }
            set { damageOverTime = value; }
        }
        Color color;
        public Color Color
        {
            get { return color; }
            set { color = value; }
        }
        
        public Enemy(Vector2 startPosition, EnemyInfo enemyInfo, List<Direction> directions)
        {
            this.enemyTexture = enemyInfo.EnemyTexture;
            this.healthBarRedTexture = enemyInfo.RedHealthBar;
            this.healthBarYellowTexture = enemyInfo.YellowHealthBar;
            origin = new Vector2(enemyTexture.Width / 2, enemyTexture.Height / 2);
            this.position = new Vector2(startPosition.X * GameConstants.tileSize, startPosition.Y * GameConstants.tileSize);
            targetPosition = position;
            this.health = enemyInfo.Health;
            this.maxHealth = health;
            this.normalSpeed = enemyInfo.Speed;
            this.currentSpeed = normalSpeed;
            this.goldYield = enemyInfo.GoldYield;
            this.directions = directions;
            color = Color.White;
            slow = new Slow(false);
            damageOverTime = new DamageOverTime(false);
        }

        public void Turn(bool right)
        {
            if (right)
                rotation -= (float)(Math.PI / 2);
            else
                rotation += (float)(Math.PI / 2);
        }

        public virtual void OnDeath()
        {

        }

        public void Update(float gameTime)
        {
            if (currentDirection == Direction.None)
            {
                if (directionCounter < directions.Count)
                {
                    currentDirection = directions[directionCounter];
                    directionCounter++;
                }
                else
                    currentDirection = Direction.Right;
                switch (currentDirection)
                {
                    case Direction.Up:
                        targetPosition += new Vector2(0, -GameConstants.tileSize);
                        break;
                    case Direction.Down:
                        targetPosition += new Vector2(0, GameConstants.tileSize);
                        break;
                    case Direction.Left:
                        targetPosition += new Vector2(-GameConstants.tileSize, 0);
                        break;
                    case Direction.Right:
                        targetPosition += new Vector2(GameConstants.tileSize, 0);
                        break;
                }
            }

            switch (currentDirection)
            {
                case Direction.Up:
                    position.Y -= (float)(currentSpeed * gameTime * slow.Percentage / 100);
                    break;
                case Direction.Down:
                    position.Y += (float)(currentSpeed * gameTime * slow.Percentage / 100);
                    break;
                case Direction.Left:
                    position.X -= (float)(currentSpeed * gameTime * slow.Percentage / 100);
                    break;
                case Direction.Right:
                    position.X += (float)(currentSpeed * gameTime * slow.Percentage / 100);
                    break;
            }

            distanceTravelled += (float)(currentSpeed * gameTime);

            if (Math.Abs(position.X - targetPosition.X) < 1 && Math.Abs(position.Y - targetPosition.Y) < 1)
            {
                position = targetPosition;
                currentDirection = Direction.None;
            }

            damageOverTime.DurationSinceLastTick -= gameTime;
            if (damageOverTime.DurationSinceLastTick <= 0)
            {
                damageOverTime.DurationSinceLastTick = damageOverTime.Duration / damageOverTime.Ticks;
                health -= damageOverTime.DamagePerTick;
            }
            damageOverTime.RemainingDuration -= gameTime;
            if (damageOverTime.RemainingDuration <= 0)
            {
                damageOverTime = new DamageOverTime(false);
                color = Color.White;
            }
            else
            {
                color = Color.LightBlue;
            }

            slow.Duration -= gameTime;
            if (slow.Duration <= 0)
            {
                slow = new Slow(false);
                color = Color.White;
            }
            else
            {
                color = Color.LightBlue;
            }
            healthBarRedRectangle = new Rectangle((int)position.X, (int)position.Y - 10, 64, 5);
            healthBarYellowRectangle = new Rectangle((int)position.X, (int)position.Y - 10, (int)((float)64 * (float)health / (float)maxHealth), 5);
        }

        public void Draw(SpriteBatch spriteBatch)
        {
            Vector2 drawPosition = position + origin;
            spriteBatch.Draw(enemyTexture, drawPosition, null, color, rotation, origin, 1.0f, SpriteEffects.None, 1.0f);
            spriteBatch.Draw(healthBarRedTexture, healthBarRedRectangle, Color.White);
            spriteBatch.Draw(healthBarYellowTexture, healthBarYellowRectangle, Color.White);
            
        }

    }

    public struct EnemyInfo
    {
        int health;

        public int Health
        {
            get { return health; }
            set { health = value; }
        }
        int speed;

        public int Speed
        {
            get { return speed; }
            set { speed = value; }
        }

        int goldYield;

        public int GoldYield
        {
            get { return goldYield; }
            set { goldYield = value; }
        }

        Texture2D redHealthBar;

        public Texture2D RedHealthBar
        {
            get { return redHealthBar; }
            set { redHealthBar = value; }
        }
        Texture2D yellowHealthBar;

        public Texture2D YellowHealthBar
        {
            get { return yellowHealthBar; }
            set { yellowHealthBar = value; }
        }
        Texture2D enemyTexture;

        public Texture2D EnemyTexture
        {
            get { return enemyTexture; }
            set { enemyTexture = value; }
        }

        public EnemyInfo(int health, int speed, int goldYield, Texture2D enemyTexture, Texture2D redHealthBar, Texture2D yellowHealthBar)
        {
            this.health = health;
            this.speed = speed;
            this.goldYield = goldYield;
            this.enemyTexture = enemyTexture;
            this.yellowHealthBar = yellowHealthBar;
            this.redHealthBar = redHealthBar;
        }
    }
}
