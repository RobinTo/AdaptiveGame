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
            winStrings.Add("Today you, tomorrow me.");
            winStrings.Add("You win! This time...");
            winStrings.Add("Good job, just one more, then there will be cake!");
            winStrings.Add("Impossible to carry these stupid monsters.");
            winStrings.Add("You only win because I lagged!");

            loseStrings.Add("Winning isn't everything, it's the only thing.");
            loseStrings.Add("Human intelligence is no match for me.");
            loseStrings.Add("Ah, the familiar, sweet taste of victory!");
            loseStrings.Add("I win, again.");
            loseStrings.Add("Veni Vidi Vici!");
            loseStrings.Add("No soup for you!");

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
                spriteBatch.DrawString(font, selectedWinString, position, Color.Orange);
            }
            else
            {
                position.X = (screenDimensions.X / 2) - (font.MeasureString(selectedLoseString).X / 2);
                position.Y = (screenDimensions.Y / 2) - font.MeasureString(selectedLoseString).Y;
                spriteBatch.DrawString(font, selectedLoseString, position, Color.Orange);
            }
        }
    }
}
