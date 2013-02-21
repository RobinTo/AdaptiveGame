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

        public GUIButton sellTowerButton, upgradeTowerButton;

        public TowerStats selectedTower;
        public bool building = false;
        Vector2 position;
        Vector2 startingDrawPos = new Vector2(20, 20);
        int buttonPadding = 10;

        Texture2D GUITexture;
        public TowerStats selected;
        bool isSelected = false;
        bool towerButtonIsSelected = false;

        SpriteFont font;
        int currentGold = 0;
        int currentLives = 0;
        EnemyInfo selectedEnemy;
        bool isEnemySelected = false;

        AssetManager assets;

        public EnemyInfo SelectedEnemy
        {
            get { return selectedEnemy; }
            set { selectedEnemy = value; isEnemySelected = true; }
        }

        public GUI(Vector2 position, Dictionary<string, TowerStats> towers, Texture2D GUITexture, Texture2D sellTowerButtonTexture, Texture2D upgradeTowerTexture, SpriteFont font, AssetManager assets)
        {
            this.towers = towers;
            this.position = position;
            this.font = font;
            this.assets = assets;
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

            int i = 0;
            using (Dictionary<String, TowerStats>.KeyCollection.Enumerator enumerator = towers.Keys.GetEnumerator())
            {
                while (i < towers.Count / 3)
                {
                    enumerator.MoveNext();
                    TowerStats towerStats = towers[enumerator.Current];
                    towerButtons.Add(new GUIButton(assets.GetImage(towerStats.TowerTexture), new Vector2(startingDrawPos.X + position.X + towerButtons.Count * buttonPadding, position.Y + startingDrawPos.Y), digits[i]), towerStats);
                    startingDrawPos.X += assets.GetImage(towerStats.TowerTexture).Width;
                    
                    i++;
                }

            }
            
            
            sellTowerButton = new GUIButton(sellTowerButtonTexture, new Vector2(startingDrawPos.X + 324, position.Y + startingDrawPos.Y), Keys.S);
            upgradeTowerButton = new GUIButton(upgradeTowerTexture, new Vector2(startingDrawPos.X + 250, position.Y + startingDrawPos.Y), Keys.U);
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
            if(input.MousePress(MouseButtons.Left))
            {
                foreach (KeyValuePair<GUIButton, TowerStats> button in towerButtons)
                {
                    if ((button.Key.ButtonClicked(hitPosition.X, hitPosition.Y) && input.MousePress(MouseButtons.Left)) || input.KeyPress(button.Key.KeyBinding))
                    {
                        selectedTower = button.Value;
                        building = true;
                        hitAny = true;
                        towerButtonIsSelected = true;
                        this.selected = selectedTower;
                    }
                }
                if (!hitAny)
                {
                    building = false;
                    towerButtonIsSelected = false;
                }
            }
            else if (input.MousePress(MouseButtons.Right))
            {
                building = false;
                towerButtonIsSelected = false;
            }

            foreach (KeyValuePair<GUIButton, TowerStats> button in towerButtons)
            {
                if (input.KeyPress(button.Key.KeyBinding))
                {
                    selectedTower = button.Value;
                    building = true;
                    hitAny = true;
                    towerButtonIsSelected = true;
                    this.selected = selectedTower;
                }
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

            if (isSelected || towerButtonIsSelected)
            {
                if (!building)
                {
                    sellTowerButton.Draw(spriteBatch);
                    upgradeTowerButton.Draw(spriteBatch);
                }
                DrawInfo(spriteBatch, selected);
            }
            if (isEnemySelected)
                DrawEnemyInfo(spriteBatch, selectedEnemy);
        }

        private void DrawInfo(SpriteBatch spriteBatch, TowerStats towerStats)
        {
            Vector2 infoPosition = new Vector2(startingDrawPos.X + 550, startingDrawPos.Y + position.Y);
            spriteBatch.Draw(assets.GetImage(towerStats.TowerTexture), infoPosition, Color.White);
            spriteBatch.DrawString(font, "Cost: " + towerStats.GoldCost, new Vector2(infoPosition.X + 66, infoPosition.Y), Color.Black);
            spriteBatch.DrawString(font, "Damage: " + towerStats.Damage, new Vector2(infoPosition.X + 66, infoPosition.Y+30), Color.Black);
            spriteBatch.DrawString(font, "Reload Time: " + towerStats.ReloadTime, new Vector2(infoPosition.X + 66, infoPosition.Y+60), Color.Black);
        }

        private void DrawEnemyInfo(SpriteBatch spriteBatch, EnemyInfo enemyInfo)
        {
            Vector2 infoPosition = new Vector2(startingDrawPos.X + 800, startingDrawPos.Y + position.Y);
            spriteBatch.Draw(assets.GetImage(enemyInfo.EnemyTexture), infoPosition, Color.White);
            infoPosition.X += 66;
            spriteBatch.DrawString(font, "Health: " + enemyInfo.Health, infoPosition, Color.Black);
            infoPosition.Y += 30;
            spriteBatch.DrawString(font, "Speed: " + enemyInfo.Speed, infoPosition, Color.Black);
            infoPosition.Y += 30;
            spriteBatch.DrawString(font, "Gold: " + enemyInfo.GoldYield, infoPosition, Color.Black);
        }
    }
}
