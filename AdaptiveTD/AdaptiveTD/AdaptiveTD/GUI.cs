using System;
using System.Collections.Generic;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;

namespace AdaptiveTD
{
    class GUI
    {
        Dictionary<GUIButton, TowerStats> towerButtons = new Dictionary<GUIButton, TowerStats>();
        Dictionary<string, TowerStats> towers = new Dictionary<string, TowerStats>();

        public GUIButton sellTowerButton;

        public TowerStats selectedTower;
        public bool building = false;
        Vector2 position;
        Vector2 startingDrawPos = new Vector2(20, 20);
        int buttonPadding = 10;

        Texture2D GUITexture;
        public TowerStats selected;
        bool isSelected = false;

        SpriteFont font;
        int currentGold = 0;
        int currentLives = 0;

        public GUI(Vector2 position, Dictionary<string, TowerStats> towers, Texture2D GUITexture, Texture2D sellTowerButtonTexture, SpriteFont font)
        {
            this.towers = towers;
            this.position = position;
            this.font = font;
            List<Keys> digits = new List<Keys>();
            digits.Add(Keys.D1);
            digits.Add(Keys.D2);
            digits.Add(Keys.D3);
            digits.Add(Keys.D4);
            digits.Add(Keys.D5);
            digits.Add(Keys.D6);
            digits.Add(Keys.D7);
            digits.Add(Keys.D8);
            digits.Add(Keys.D9);
            int counter = 0;
            foreach (KeyValuePair<string, TowerStats> tower in towers)
            {
                towerButtons.Add(new GUIButton(tower.Value.TowerTexture, new Vector2(startingDrawPos.X + position.X + towerButtons.Count * buttonPadding, position.Y + startingDrawPos.Y), digits[counter]), tower.Value);
                startingDrawPos.X += tower.Value.TowerTexture.Width;
                counter++;
            }
            sellTowerButton = new GUIButton(sellTowerButtonTexture, new Vector2(startingDrawPos.X + 400, position.Y + startingDrawPos.Y), Keys.S);
            this.GUITexture = GUITexture;
        }



        public void Update(float gameTime, InputHandler input, int currentLives, int currentGold, Tower selected, EventHandler eventHandler)
        {
            if (selected != null)
            {
                this.selected = towers[selected.TowerStats.Type];
                isSelected = true;
            }
            else
                isSelected = false;

            this.currentGold = currentGold;
            this.currentLives = currentLives;
            Vector2 hitPosition = input.MousePosition;
            
            /*
            if ((input.MousePress(MouseButtons.Left) && sellTowerButton.ButtonClicked(hitPosition.X, hitPosition.Y)) || input.KeyPress(sellTowerButton.KeyBinding)) 
            {
                if (selected != null)
                {
                    Event e = new Event(EventType.sell, selected.TilePosition, selected.Type);
                    eventHandler.QueueEvent(e);
                }
            }
            */
            
            bool hitAny = false;
            foreach (KeyValuePair<GUIButton, TowerStats> button in towerButtons)
            {
                if ((button.Key.ButtonClicked(hitPosition.X, hitPosition.Y) && input.MousePress(MouseButtons.Left)) || input.KeyPress(button.Key.KeyBinding))
                {
                    selectedTower = button.Value;
                    button.Key.Color = Color.Red;
                    building = true;
                    hitAny = true;
                }
                else
                    button.Key.Color = Color.White;
            }
            if (!hitAny && input.MousePress(MouseButtons.Left))
                building = false;
            
            else if (input.MousePress(MouseButtons.Right))
            {
                building = false;
            }
            if (!building)
            {
                foreach (KeyValuePair<GUIButton, TowerStats> button in towerButtons)
                {
                    button.Key.Color = Color.White;
                }
            }
        }

        public void Draw(SpriteBatch spriteBatch)
        {
            spriteBatch.Draw(GUITexture, position, Color.White);

            foreach (KeyValuePair<GUIButton, TowerStats> tower in towerButtons)
            {
                tower.Key.Draw(spriteBatch);
            }
            spriteBatch.DrawString(font, "Gold: " + currentGold, position + new Vector2(700, 10), Color.White);
            spriteBatch.DrawString(font, "Lives: " + currentLives, position + new Vector2(700, 30), Color.White);

            if (isSelected)
            {
                sellTowerButton.Draw(spriteBatch);
                DrawInfo(spriteBatch, selected);
            }
        }

        private void DrawInfo(SpriteBatch spriteBatch, TowerStats towerStats)
        {
            Vector2 infoPosition = new Vector2(startingDrawPos.X + 600, startingDrawPos.Y + position.Y);
            spriteBatch.Draw(towerStats.TowerTexture, infoPosition, Color.White);
            spriteBatch.DrawString(font, "Cost: " + towerStats.GoldCost, new Vector2(infoPosition.X + 66, infoPosition.Y), Color.Black);
            spriteBatch.DrawString(font, "Damage: " + towerStats.Damage, new Vector2(infoPosition.X + 66, infoPosition.Y+30), Color.Black);
            spriteBatch.DrawString(font, "Reload Time: " + towerStats.ReloadTime, new Vector2(infoPosition.X + 66, infoPosition.Y+60), Color.Black);
        }
    }
}
