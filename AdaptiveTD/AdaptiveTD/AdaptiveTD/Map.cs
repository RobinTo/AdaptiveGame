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
        bool mapDone = false;

        List<Direction> directions = new List<Direction>();

        public List<Direction> Directions
        {
            get { return directions; }
        }

        Point startPoint = new Point(-1, 0);
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
        private void GenerateDirections()
        {
            mapDone = false;
            for (int y = 0; y <= map.GetUpperBound(1); y++)
            {
                if (pathTiles.Contains(map[0, y]))
                {
                    startPoint.Y = y;
                }
            }
            Vector2 currentPosition = new Vector2(startPoint.X, startPoint.Y);
            while (!mapDone)
            {
                if(directions.Count > 0)
                    currentPosition = NextStep(currentPosition, directions[directions.Count - 1]);
                else
                    currentPosition = NextStep(currentPosition, Direction.Right);
            }

        }

        private Vector2 NextStep(Vector2 currentTile, Direction lastDir)
        {
            Vector2 newTile = new Vector2(currentTile.X, currentTile.Y);
            bool incremented = false;
            if (!incremented && currentTile.X > 0 && pathTiles.Contains(map[(int)currentTile.X - 1, (int)currentTile.Y]))
            {
                if (lastDir != Direction.Right)
                {
                    Directions.Add(Direction.Left);
                    newTile.X -= 1;
                    incremented = true;
                }
            }
            if (!incremented && currentTile.X < map.GetUpperBound(0) && pathTiles.Contains(map[(int)currentTile.X + 1, (int)currentTile.Y]))
            {
                if (lastDir != Direction.Left)
                {
                    Directions.Add(Direction.Right);
                    newTile.X += 1;
                    incremented = true;
                }
            }
            if (!incremented && currentTile.Y > 0 && pathTiles.Contains(map[(int)currentTile.X, (int)currentTile.Y - 1]))
            {
                if (lastDir != Direction.Down)
                {
                    Directions.Add(Direction.Up);
                    newTile.Y -= 1;
                    incremented = true;
                }
            }
            if (!incremented && currentTile.Y < map.GetUpperBound(1) && pathTiles.Contains(map[(int)currentTile.X, (int)currentTile.Y + 1]))
            {
                if (lastDir != Direction.Up)
                {
                    Directions.Add(Direction.Down);
                    newTile.Y += 1;
                    incremented = true;
                }
            }
            if (!incremented && currentTile.X + 1 > map.GetUpperBound(0)) // given our decided layout that maps end to the right
            {
                directions.Add(Direction.Right);
                mapDone = true;
                newTile.X++;
            }

            return newTile;
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


            GenerateDirections();
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
