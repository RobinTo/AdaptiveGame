using System;
using System.Collections.Generic;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;

namespace AdaptiveTD
{
    public struct TowerStats
    {
        Texture2D towerTexture;
        public Texture2D TowerTexture
        {
            get { return towerTexture; }
            set { towerTexture = value; }
        }
        Texture2D missileTexture;

        public Texture2D MissileTexture
        {
            get { return missileTexture; }
            set { missileTexture = value; }
        }
        float towerReloadTime;

        public float TowerReloadTime
        {
            get { return towerReloadTime; }
            set { towerReloadTime = value; }
        }
        int damage;
        public int Damage
        {
            get { return damage; }
            set { damage = value; }
        }
        public TowerStats(Texture2D towerTexture, Texture2D missileTexture, float towerReloadTime, int damage)
        {
            this.towerTexture = towerTexture;
            this.missileTexture = missileTexture;
            this.towerReloadTime = towerReloadTime;
            this.damage = damage;
        }
    }
    class GUI
    {
        Dictionary<GUIButton, TowerStats> towerButtons = new Dictionary<GUIButton, TowerStats>();
        Dictionary<string, TowerStats> towers = new Dictionary<string, TowerStats>();

        Vector2 position;
        Vector2 startingDrawPos = new Vector2(20, 20);
        int buttonPadding = 10;

        Texture2D GUITexture;

        public GUI(Vector2 position, Dictionary<string, TowerStats> towers, Texture2D GUITexture)
        {
            this.position = position;
            this.towers = towers;
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
            foreach (KeyValuePair<GUIButton, TowerStats> button in towerButtons)
            {
                if (button.Key.ButtonClicked(hitPosition.X, hitPosition.Y))
                    button.Key.Color = Color.Red;
                else
                    button.Key.Color = Color.White;
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
