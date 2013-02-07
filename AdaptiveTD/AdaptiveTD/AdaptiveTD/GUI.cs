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

        public TowerStats selectedTower;
        public bool building = false;
        Vector2 position;
        Vector2 startingDrawPos = new Vector2(20, 20);
        int buttonPadding = 10;

        Texture2D GUITexture;

        public GUI(Vector2 position, Dictionary<string, TowerStats> towers, Texture2D GUITexture)
        {
            this.position = position;
            foreach (KeyValuePair<string, TowerStats> tower in towers)
            {
                towerButtons.Add(new GUIButton(tower.Value.TowerTexture, new Vector2(startingDrawPos.X + position.X + towerButtons.Count * buttonPadding, position.Y + startingDrawPos.Y)), tower.Value);
                startingDrawPos.X += tower.Value.TowerTexture.Width;
            }
            this.GUITexture = GUITexture;
        }

        public void Update(float gameTime, InputHandler input)
        {
            Vector2 hitPosition = input.MousePosition;
            bool hitAny = false;
            if (input.MousePress(MouseButtons.Left) && hitPosition.Y >= position.Y)
            {
                foreach (KeyValuePair<GUIButton, TowerStats> button in towerButtons)
                {
                    if (button.Key.ButtonClicked(hitPosition.X, hitPosition.Y))
                    {
                        selectedTower = button.Value;
                        button.Key.Color = Color.Red;
                        building = true;
                        hitAny = true;
                    }
                    else
                        button.Key.Color = Color.White;
                }
                if (!hitAny)
                    building = false;
            }
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
        }
    }
}
