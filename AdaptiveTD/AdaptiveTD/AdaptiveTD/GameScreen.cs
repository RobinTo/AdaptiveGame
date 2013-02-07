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

        EventHandler eventHandler = new EventHandler();

        InputHandler input = new InputHandler();
        GUI gui;

        Map map;
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
            map = new Map(Content.Load<Texture2D>("imageZero"), Content.Load<Texture2D>("imageOne"));
            map.LoadMap("test");
            assets.AddImage("testEnemy", Content.Load<Texture2D>("testEnemy"));
            enemies.Add(new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("testEnemy"), 64, 20, 2, map.Directions));
            enemies.Add(new Enemy(new Vector2(map.StartPoint.X-2, map.StartPoint.Y), assets.GetImage("testEnemy"), 64, 2000, 2, map.Directions));

            assets.AddImage("basicTower", Content.Load<Texture2D>("arrowTower"));
            assets.AddImage("basicBullet", Content.Load<Texture2D>("blackBullet"));
            assets.AddImage("flameTower", Content.Load<Texture2D>("redTower"));
            assets.AddImage("frostTower", Content.Load<Texture2D>("blueTower"));
            assets.AddImage("flameMissile", Content.Load<Texture2D>("redBullet"));
            assets.AddImage("frostMissile", Content.Load<Texture2D>("blueBullet"));

            towerInfo.Add("basic", new TowerStats("basic", assets.GetImage("basicTower"), assets.GetImage("basicMissile"), 0.5f, 10));
            towerInfo.Add("flame", new TowerStats("flame", assets.GetImage("flameTower"), assets.GetImage("flameMissile"), 1.0f, 30));
            towerInfo.Add("frost", new TowerStats("frost", assets.GetImage("frostTower"), assets.GetImage("frostMissile"), 1.0f, 5));

            gui = new GUI(new Vector2(0, 640), towerInfo, Content.Load<Texture2D>("UIBar"));
        }

        public void Update(float gameTime)
        {
            eventHandler.NewRound();
            input.Update();
            HandleInput();
            HandleEvents();
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

            gui.Update(gameTime, input);
        }

        public void Draw(SpriteBatch spriteBatch)
        {
            map.Draw(spriteBatch);
            foreach (Enemy e in enemies)
                e.Draw(spriteBatch);
            foreach (Tower t in towers)
                t.Draw(spriteBatch);
            foreach (Missile missile in missiles)
                missile.Draw(spriteBatch);

            if (gui.building)
            {
                Vector2 position = new Vector2((float)Math.Floor(input.MousePosition.X / GameConstants.tileSize), (float)Math.Floor(input.MousePosition.Y / GameConstants.tileSize));
                spriteBatch.Draw(gui.selectedTower.TowerTexture, position*GameConstants.tileSize, Color.White);
            }

            gui.Draw(spriteBatch);
        }

        private void HandleEvents()
        {
            List<Event> events = eventHandler.Events;
            for (int i = 0; i < events.Count; i++)
            {
                Event e = events[i];
                switch (e.Type)
                {
                    case EventType.build:
                        BuildTower(towerInfo[e.TowerType], e.TilePosition);
                        break;
                }
            }
        }

        private void HandleInput()
        {
            if (input.MousePress(MouseButtons.Left))
            {
                if (gui.building)
                {
                    Vector2 position = new Vector2((float)Math.Floor(input.MousePosition.X / GameConstants.tileSize), (float)Math.Floor(input.MousePosition.Y / GameConstants.tileSize));
                    if (position.X <= map.MapWidth && position.X >= 0 && position.Y >= 0 && position.Y <= map.MapHeight)
                    {
                        Event e = new Event(EventType.build, position, gui.selectedTower.Type);
                        eventHandler.QueueEvent(e);
                    }
                }
            }
        }

        private void BuildTower(TowerStats t, Vector2 position)
        {
            bool canBuild = true;
            foreach (Tower to in towers)
            {
                if (to.TilePosition == position)
                    canBuild = false;
            }
            if(canBuild)
                towers.Add(new Tower(t.TowerTexture, t.MissileTexture, position, t.TowerReloadTime, t.Damage));
        }

    }
}