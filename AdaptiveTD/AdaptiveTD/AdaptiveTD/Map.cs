using System;
using System.IO;
using System.Collections.Generic;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework;

namespace AdaptiveTD
{
    class Map
    {
        int[,] map = new int[20,10];
        Texture2D imageZero;
        Texture2D imageOne;
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
            for (int x = 0; x < map.GetUpperBound(0); x++)
            {
                for (int y = 0; y < map.GetUpperBound(1); y++)
                {
                    map[x, y] = 0;
                }
            }
        }

        public void Draw(SpriteBatch spriteBatch)
        {
            for (int x = 0; x < map.GetUpperBound(0); x++)
            {
                for (int y = 0; y < map.GetUpperBound(1); y++)
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
