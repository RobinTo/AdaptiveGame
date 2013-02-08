using System;
using System.IO;
using System.Collections.Generic;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Content;


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
        int[,] map = new int[20, 10];
        public int[,] MapTiles
        {
            get { return map; }
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
        Dictionary<int, Texture2D> textures = new Dictionary<int, Texture2D>();

        List<int> pathTiles = new List<int>();

        public Map()
        {
        }

        public bool CanBuild(int x, int y)
        {
            return pathTiles.Contains(map[x,y]) ? false : true;
        }

        public int MapWidth
        {
            get
            {
                return map.GetUpperBound(0);
            }
        }
        public int MapHeight
        {
            get
            {
                return map.GetUpperBound(1);
            }
        }
        public void LoadMap(string path, ContentManager Content)
        {
            for (int x = 0; x <= map.GetUpperBound(0); x++)
            {
                for (int y = 0; y <= map.GetUpperBound(1); y++)
                {
                    map[x, y] = 0;
                }
            }

            string[] fileContent = File.ReadAllLines(path);
            int yCounter = 0;
            foreach (string s in fileContent)
            {
                string[] split = s.Split(':');
                switch (split[0])
                {
                    case "t":
                        textures.Add(Convert.ToInt32(split[1]), Content.Load<Texture2D>(split[2]));
                        break;
                    case "p":
                        pathTiles.Add(Convert.ToInt32(split[1]));
                        break;
                    case "m":
                        for(int xCounter = 0; xCounter <= map.GetUpperBound(0); xCounter++)
                        {
                            map[xCounter, yCounter] = Convert.ToInt32(split[xCounter+1]);
                        }
                        yCounter++;
                        break;
                }
            }


            for (int i = 0; i < 20; i++)
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
                    spriteBatch.Draw(textures[map[x,y]], new Vector2(x * GameConstants.tileSize, y * GameConstants.tileSize), Color.White);
                }
            }
        }
    }
}
