using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Input;
using Microsoft.Xna.Framework.Graphics;

namespace AdaptiveTD
{
    class LoginScreen
    {
        Texture2D backgroundTexture;
        Vector2 position;
        Vector2 namePosition;
        SpriteFont font;
        string name = "";

        string test = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
        string basePath = Path.Combine(Environment.GetFolderPath(
            Environment.SpecialFolder.ApplicationData), "AdaptiveTD\\");
        string savePath;
        public string SavePath
        {
            get
            {
                if (nameSet)
                    return savePath;
                else
                    return "null";
            }
        }

        List<Keys> acceptedKeys = new List<Keys>();
        bool nameSet = false;
        public string Name
        {
            get
            {
                if (nameSet)
                    return name;
                else
                    return "null";
            }
        }

        public void LogOut()
        {
            name = "";
            savePath = "";
            nameSet = false;
        }

        public LoginScreen(Texture2D backround, Vector2 position, Vector2 namePosition, SpriteFont font)
        {
            this.backgroundTexture = backround;
            this.position = position;
            this.namePosition = namePosition;
            this.font = font;
            #region Alphabet
            acceptedKeys.Add(Keys.A);
            acceptedKeys.Add(Keys.B);
            acceptedKeys.Add(Keys.C);
            acceptedKeys.Add(Keys.D);
            acceptedKeys.Add(Keys.E);
            acceptedKeys.Add(Keys.F);
            acceptedKeys.Add(Keys.G);
            acceptedKeys.Add(Keys.H);
            acceptedKeys.Add(Keys.I);
            acceptedKeys.Add(Keys.J);
            acceptedKeys.Add(Keys.K);
            acceptedKeys.Add(Keys.L);
            acceptedKeys.Add(Keys.M);
            acceptedKeys.Add(Keys.N);
            acceptedKeys.Add(Keys.O);
            acceptedKeys.Add(Keys.P);
            acceptedKeys.Add(Keys.K);
            acceptedKeys.Add(Keys.R);
            acceptedKeys.Add(Keys.S);
            acceptedKeys.Add(Keys.T);
            acceptedKeys.Add(Keys.U);
            acceptedKeys.Add(Keys.V);
            acceptedKeys.Add(Keys.W);
            acceptedKeys.Add(Keys.X);
            acceptedKeys.Add(Keys.Y);
            acceptedKeys.Add(Keys.Z);
#endregion
        }

        public void Update(InputHandler input)
        {
            HandleInput(input);
        }
        public void Draw(SpriteBatch spriteBatch)
        {
            spriteBatch.Draw(backgroundTexture, position, Color.White);
            spriteBatch.DrawString(font, name, namePosition, Color.Black);
        }

        public void HandleInput(InputHandler input)
        {
            List<Keys> pressedKeys = new List<Keys>();
            foreach (Keys k in acceptedKeys)
            {
                if (input.KeyPress(k))
                {
                    if (input.IsKeyDown(Keys.LeftShift) || input.IsKeyDown(Keys.RightShift))
                        name += (char)k.ToString().ToUpper()[0];
                    else
                        name += (char)k.ToString().ToLower()[0];
                }
            }

            if (input.KeyPress(Keys.Back) && name.Length > 0)
                name = name.Substring(0, name.Length-1);

            if (input.KeyPress(Keys.Enter) && name.Length > 1)
            {
                nameSet = true;
                savePath = Path.Combine(basePath, name + "\\");
                if (!Directory.Exists(savePath))
                    Directory.CreateDirectory(savePath);
            }
        }

    }
}
