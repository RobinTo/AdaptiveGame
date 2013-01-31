using System;
using System.IO;
using System.Collections.Generic;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework;

namespace AdaptiveTD
{
    public enum Direction
    {
        Up = 0,
        Down,
        Right,
        Left
    }
    class Map
    {
        int[,] map = new int[20,10];
        Texture2D imageZero;
        Texture2D imageOne;

        List<Direction> directions = new List<Direction>();

        Point startPoint = new Point(0, 5);
        public Point StartPoint
        {
            get { return startPoint; }
        }
        Point endPoint = new Point(19, 5);
        public Point EndPoint
        {
            get { return endPoint; }
        }


        public Map(Texture2D imageZero, Texture2D imageOne)
        {
            this.imageZero = imageZero;
            this.imageOne = imageOne;
        }

        public bool CanBuild(int x, int y)
        {
            return map[x, y] == 0 ? true : false;
        }


        public void LoadMap(string path)
        {
            for (int x = 0; x <= map.GetUpperBound(0); x++)
            {
                for (int y = 0; y <= map.GetUpperBound(1); y++)
                {
                    if (y == 5)
                        map[x, y] = 1;
                    else
                        map[x, y] = 0;
                }
            }
            for (int i = 0; i < 10; i++)
            {
                directions.Add(Direction.Right);
            }
        }

        public void Draw(SpriteBatch spriteBatch)
        {
            for (int x = 0; x <= map.GetUpperBound(0); x++)
            {
                for (int y = 0; y <= map.GetUpperBound(1); y++)
                {
                    switch (map[x, y])
                    {
                        case 0:
                            spriteBatch.Draw(imageZero, new Vector2(x * GameConstants.tileSize, y * GameConstants.tileSize), Color.White);
                            break;
                        case 1:
                            spriteBatch.Draw(imageOne, new Vector2(x * GameConstants.tileSize, y * GameConstants.tileSize), Color.White);
                            break;
                    }
                }
            }
        }
    }
}
