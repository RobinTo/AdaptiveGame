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
        Direction currentDirection;
        Vector2 origin;
        public Vector2 Origin
        {
            get { return origin; }
        }

        public Enemy(Vector2 startPosition, Texture2D image, int speed, int health, int goldYield, List<Direction> directions)
        {
            this.texture = image;
            origin = new Vector2(texture.Width / 2, texture.Height / 2);
            this.position = new Vector2(startPosition.X * GameConstants.tileSize, startPosition.Y * GameConstants.tileSize);
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
            if (currentDirection == null)
            {
                currentDirection = directions[0];
                directions.RemoveAt(0);
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

        }

        public void Draw(SpriteBatch spriteBatch)
        {
            Vector2 drawPosition = position + origin;
            spriteBatch.Draw(texture, drawPosition, null, Color.White, rotation, origin, 1.0f, SpriteEffects.None, 1.0f);
        }
    }
}
