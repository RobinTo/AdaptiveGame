using System;
using System.Collections.Generic;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Input;
using Microsoft.Xna.Framework.Content;
using Microsoft.Xna.Framework.Graphics;
using System.IO;

namespace MapEditor
{
    class MapEditor
    {
        int[,] map = new int[20, 10];
        
        Dictionary<int, Texture2D> textures = new Dictionary<int, Texture2D>();
        Dictionary<Rectangle, int> buttons = new Dictionary<Rectangle, int>();
        Dictionary<int, string> images = new Dictionary<int, string>();
        MouseState newMouseState = new MouseState();
        MouseState oldMouseState = new MouseState();
        KeyboardState newKeyState = new KeyboardState();
        KeyboardState oldKeyState = new KeyboardState();
        Vector2 mouseTilePosition = new Vector2();
        int selectedTile = 0;

        Vector2 startButtonPos = new Vector2(20, 660);
        int buttonOffsetX = 74;

        List<int> pathIntegers = new List<int>();

        int textureCounter = 0;
        bool onMap = false;

        Texture2D behindSelectTexture;

        ContentManager Content;

        private void AddTexture(string imageName)
        {
            buttons.Add(new Rectangle((int)startButtonPos.X + (textureCounter * buttonOffsetX), (int)startButtonPos.Y, 64, 64), textureCounter);
            images.Add(textureCounter, imageName);
            textures.Add(textureCounter, Content.Load<Texture2D>(imageName));
            textureCounter++;
        }

        public void LoadContent(ContentManager Content)
        {
            this.Content = Content;
            AddTexture("imageZero");
            AddTexture("imageOne");
            for (int x = 0; x <= map.GetUpperBound(0); x++)
            {
                for (int y = 0; y <= map.GetUpperBound(1); y++)
                {
                    map[x, y] = 0;
                }
            }
            selectedTile = textures.Count - 1;
            behindSelectTexture = Content.Load<Texture2D>("UIBar");
        }

        public void Update()
        {
            newMouseState = Mouse.GetState();
            mouseTilePosition = new Vector2((float)Math.Floor(newMouseState.X / 64.0), (float)Math.Floor(newMouseState.Y / 64.0));

            if (mouseTilePosition.X >= 0 && mouseTilePosition.X <= map.GetUpperBound(0) && mouseTilePosition.Y >= 0 && mouseTilePosition.Y <= map.GetUpperBound(1))
            {
                onMap = true;

                if (newMouseState.LeftButton == ButtonState.Pressed && oldMouseState.LeftButton == ButtonState.Released)
                {
                    map[(int)mouseTilePosition.X, (int)mouseTilePosition.Y] = selectedTile;
                }
            }
            else
            {
                onMap = false;

                if (newMouseState.LeftButton == ButtonState.Pressed && oldMouseState.LeftButton == ButtonState.Released)
                {
                    foreach (KeyValuePair<Rectangle, int> button in buttons)
                    {
                        if (button.Key.Intersects(new Rectangle((int)newMouseState.X, (int)newMouseState.Y, 3, 3)))
                            selectedTile = button.Value;
                    }
                }
            }

            newKeyState = Keyboard.GetState();
            if (newKeyState.IsKeyDown(Keys.Enter) && !oldKeyState.IsKeyDown(Keys.Enter))
                SaveMap();
            if (newKeyState.IsKeyDown(Keys.P) && !oldKeyState.IsKeyDown(Keys.P))
            {
                if (pathIntegers.Contains(selectedTile))
                    pathIntegers.Remove(selectedTile);
                else
                    pathIntegers.Add(selectedTile);
            }
            oldKeyState = newKeyState;

            oldMouseState = newMouseState;
        }

        public void Draw(SpriteBatch spriteBatch)
        {
            spriteBatch.Begin();

            spriteBatch.Draw(behindSelectTexture, new Vector2(0, 640), Color.Wheat);

            for (int x = 0; x <= map.GetUpperBound(0); x++)
            {
                for (int y = 0; y <= map.GetUpperBound(1); y++)
                {
                    spriteBatch.Draw(textures[map[x, y]], new Vector2(x*64, y*64), Color.White);
                }
            }
            if (onMap)
                spriteBatch.Draw(textures[selectedTile], mouseTilePosition * 64, Color.White);

            foreach (KeyValuePair<Rectangle, int> button in buttons)
            {
                if(pathIntegers.Contains(button.Value))
                    spriteBatch.Draw(textures[button.Value], button.Key, Color.LightGray);
                else
                    spriteBatch.Draw(textures[button.Value], button.Key, Color.White);
            }

            spriteBatch.End();
        }

        public void SaveMap()
        {
            List<string> fileOutput = new List<string>();

            foreach (KeyValuePair<int, string> image in images)
            {
                fileOutput.Add("t:" + image.Key.ToString() + ":" + image.Value);
            }

            foreach (int pathInt in pathIntegers)
            {
                fileOutput.Add("p:" + pathInt.ToString());
            }

            for (int y = 0; y <= map.GetUpperBound(1); y++)
            {
                string mapLine = "m:";
                for (int x = 0; x <= map.GetUpperBound(0); x++)
                {
                    mapLine += map[x, y].ToString()+ ":";
                }
                fileOutput.Add(mapLine);
            }

            File.WriteAllLines(".\\map.txt", fileOutput);
        }
    }
}
