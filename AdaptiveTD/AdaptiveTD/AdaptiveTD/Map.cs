using System;
using System.IO;
using System.Collections.Generic;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework;

namespace AdaptiveTD
{
    public enum Direction
    {
        None = 0,
        Up,
        Down,
        Right,
        Left
    }
    class Map
    {
        int[,] mapTiles = new int[20, 10];
        public int[,] MapTiles
        {
            get { return mapTiles; }
        }
        Texture2D imageZero;
        Texture2D imageOne;

        List<Direction> directions = new List<Direction>();

        public List<Direction> Directions
        {
            get { return directions; }
        }

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
            return mapTiles[x, y] == 0 ? true : false;
        }

        public int MapWidth
        {
            get
            {
                return mapTiles.GetUpperBound(0);
            }
        }
        public int MapHeight
        {
            get
            {
                return mapTiles.GetUpperBound(1);
            }
        }
        public void LoadMap(string path)
        {
            for (int x = 0; x <= mapTiles.GetUpperBound(0); x++)
            {
                for (int y = 0; y <= mapTiles.GetUpperBound(1); y++)
                {
                    if (y == 5)
                        mapTiles[x, y] = 1;
                    else
                        mapTiles[x, y] = 0;
                }
            }
            for (int i = 0; i < 20; i++)
            {
                directions.Add(Direction.Right);
            }
        }

        public void Draw(SpriteBatch spriteBatch)
        {
            for (int x = 0; x <= mapTiles.GetUpperBound(0); x++)
            {
                for (int y = 0; y <= mapTiles.GetUpperBound(1); y++)
                {
                    switch (mapTiles[x, y])
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
