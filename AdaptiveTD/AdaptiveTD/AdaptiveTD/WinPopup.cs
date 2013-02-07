using System;
using System.Collections.Generic;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;

namespace AdaptiveTD
{
    class WinPopup
    {
        SpriteFont font;
        List<string> winStrings = new List<string>();
        string selectedString;
        Vector2 screenDimensions;
        Vector2 position;

        public WinPopup(SpriteFont font)
        {
            this.font = font;
            winStrings.Add("Well done!");
            winStrings.Add("Experiment complete!");
            winStrings.Add("You win! This time...");
            winStrings.Add("I hate you.");
            winStrings.Add("Good job, just one more, then there will be cake!");
            winStrings.Add("Are you even human?");
            winStrings.Add("Splendid!");
            winStrings.Add("BG NOOB TEAM! Rematch 1v1?");
            this.screenDimensions = new Vector2(GameConstants.screenWidth, GameConstants.screenHeight);
            Randomize();
        }

        public void Randomize()
        {
            selectedString = winStrings[new Random().Next(0, winStrings.Count)];
            position = new Vector2();
            position.X = (screenDimensions.X/2) - (font.MeasureString(selectedString).X / 2);
            position.Y = (screenDimensions.Y / 2) - font.MeasureString(selectedString).Y;
        }

        public void Draw(SpriteBatch spriteBatch)
        {
            spriteBatch.DrawString(font, selectedString, position, Color.Black);
        }
    }
}
