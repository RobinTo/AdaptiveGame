using System;
using System.Collections.Generic;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using Microsoft.Xna.Framework.Content;

namespace AdaptiveTD
{
    class GameScreen
    {

        Dictionary<string, TowerStats> towerInfo = new Dictionary<string, TowerStats>();

        GUI gui;

        Map m;
        List<Enemy> enemies = new List<Enemy>();
        Enemy targetEnemy;
        List<Tower> towers = new List<Tower>();
        AssetManager assets = new AssetManager();
        List<Missile> missiles = new List<Missile>();

        Dictionary<float, Enemy> enemyWave = new Dictionary<float, Enemy>();

        public GameScreen()
        {

        }

        public void LoadContent(ContentManager Content)
        {
            m = new Map(Content.Load<Texture2D>("imageZero"), Content.Load<Texture2D>("imageOne"));
            m.LoadMap("test");
            assets.AddImage("testEnemy", Content.Load<Texture2D>("testEnemy"));
            enemies.Add(new Enemy(new Vector2(m.StartPoint.X, m.StartPoint.Y), assets.GetImage("testEnemy"), 64, 20, 2, m.Directions));
            enemies.Add(new Enemy(new Vector2(m.StartPoint.X-2, m.StartPoint.Y), assets.GetImage("testEnemy"), 64, 2000, 2, m.Directions));

            assets.AddImage("basicTower", Content.Load<Texture2D>("arrowTower"));
            assets.AddImage("basicBullet", Content.Load<Texture2D>("blackBullet"));
            assets.AddImage("flameTower", Content.Load<Texture2D>("redTower"));
            assets.AddImage("frostTower", Content.Load<Texture2D>("blueTower"));
            assets.AddImage("flameMissile", Content.Load<Texture2D>("redBullet"));
            assets.AddImage("frostMissile", Content.Load<Texture2D>("blueBullet"));

            towerInfo.Add("basic", new TowerStats(assets.GetImage("basicTower"), assets.GetImage("basicMissile"), 0.5f, 10));
            towerInfo.Add("flame", new TowerStats(assets.GetImage("flameTower"), assets.GetImage("flameMissile"), 1.0f, 30));
            towerInfo.Add("frost", new TowerStats(assets.GetImage("frostTower"), assets.GetImage("frostMissile"), 1.0f, 5));

            gui = new GUI(new Vector2(0, 640), towerInfo, Content.Load<Texture2D>("UIBar"));
        }

        public void Update(GameTime gameTime)
        {
            for (int counter = 0; counter < enemies.Count; counter++)
            {
                enemies[counter].Update(gameTime);
                if (enemies[counter].Health <= 0)
                {
                    enemies.RemoveAt(counter);
                    counter--;
                }
            }
            foreach (Tower t in towers)
                t.Update(gameTime, enemies, null, missiles);
            for (int counter = 0; counter < missiles.Count; counter++)
            {
                missiles[counter].Update(gameTime);
                if (missiles[counter].remove)
                {
                    missiles.RemoveAt(counter);
                    counter--;
                }
            }

            gui.Update(gameTime);
        }

        public void Draw(SpriteBatch spriteBatch)
        {
            m.Draw(spriteBatch);
            foreach (Enemy e in enemies)
                e.Draw(spriteBatch);
            foreach (Tower t in towers)
                t.Draw(spriteBatch);
            foreach (Missile missile in missiles)
                missile.Draw(spriteBatch);

            gui.Draw(spriteBatch);
        }
    }
}
