using System;
using System.Collections.Generic;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;

namespace AdaptiveTD
{
    class Enemy
    {
        Texture2D texture;
        int health;
        public int Health
        {
            get { return health; }
            set { health = value; }
        }
        int speed;
        int goldYield;
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

        public Enemy(Vector2 startPosition, Texture2D image, int speed, int health, int goldYield, List<Direction> directions)
        {
            this.texture = image;
            origin = new Vector2(texture.Width / 2, texture.Height / 2);
            this.position = new Vector2(startPosition.X * GameConstants.tileSize, startPosition.Y * GameConstants.tileSize);
            targetPosition = position;
            this.health = health;
            this.speed = speed;
            this.goldYield = goldYield;
            this.directions = directions;
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

        public void Update(GameTime gameTime)
        {
            if (currentDirection == Direction.None)
            {
                if (directions.Count > 0)
                {
                    currentDirection = directions[0];
                    directions.RemoveAt(0);
                }
                else
                    currentDirection = Direction.Right;
                switch (currentDirection)
                {
                    case Direction.Up:
                        targetPosition += new Vector2(0, -64);
                        break;
                    case Direction.Down:
                        targetPosition += new Vector2(0, 64);
                        break;
                    case Direction.Left:
                        targetPosition += new Vector2(-64, 0);
                        break;
                    case Direction.Right:
                        targetPosition += new Vector2(64, 0);
                        break;
                }
            }

            switch (currentDirection)
            {
                case Direction.Up:
                    position.Y -= (float)(speed * gameTime.ElapsedGameTime.TotalSeconds);
                    break;
                case Direction.Down:
                    position.Y += (float)(speed * gameTime.ElapsedGameTime.TotalSeconds);
                    break;
                case Direction.Left:
                    position.X -= (float)(speed * gameTime.ElapsedGameTime.TotalSeconds);
                    break;
                case Direction.Right:
                    position.X += (float)(speed * gameTime.ElapsedGameTime.TotalSeconds);
                    break;
            }

            distanceTravelled += (float)(speed * gameTime.ElapsedGameTime.TotalSeconds);

            if (Math.Abs(position.X - targetPosition.X) < 1 && Math.Abs(position.Y - targetPosition.Y) < 1)
            {
                position = targetPosition;
                currentDirection = Direction.None;
            }
        }

        public void Draw(SpriteBatch spriteBatch)
        {
            Vector2 drawPosition = position + origin;
            spriteBatch.Draw(texture, drawPosition, null, Color.White, rotation, origin, 1.0f, SpriteEffects.None, 1.0f);
        }
    }
}
