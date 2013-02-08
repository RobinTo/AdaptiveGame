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
        List<string> loseStrings = new List<string>();
        string selectedWinString;
        string selectedLoseString;
        Vector2 screenDimensions;
        Vector2 position = new Vector2();

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

            loseStrings.Add("Haha!");
            loseStrings.Add("Human intelligence is no match for me.");
            loseStrings.Add("Ah, the familiar, sweet taste of victory!");
            loseStrings.Add("I win, again.");

            this.screenDimensions = new Vector2(GameConstants.screenWidth, GameConstants.screenHeight);
            Randomize();
        }

        public void Randomize()
        {
            selectedWinString = winStrings[new Random().Next(0, winStrings.Count)];
            selectedLoseString = loseStrings[new Random().Next(0, loseStrings.Count)];
        }

        public void Draw(bool won, SpriteBatch spriteBatch)
        {
            if (won)
            {
                position.X = (screenDimensions.X / 2) - (font.MeasureString(selectedWinString).X / 2);
                position.Y = (screenDimensions.Y / 2) - font.MeasureString(selectedWinString).Y;
                spriteBatch.DrawString(font, selectedWinString, position, Color.Black);
            }
            else
            {
                position.X = (screenDimensions.X / 2) - (font.MeasureString(selectedLoseString).X / 2);
                position.Y = (screenDimensions.Y / 2) - font.MeasureString(selectedLoseString).Y;
                spriteBatch.DrawString(font, selectedLoseString, position, Color.Black);
            }
        }
    }
}
