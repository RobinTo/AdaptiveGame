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
        SortedList<float, Enemy> enemyWave = new SortedList<float, Enemy>();

        EventHandler eventHandler = new EventHandler();

        InputHandler input = new InputHandler();
        GUI gui;

        Map map;
        List<Enemy> enemies = new List<Enemy>();
        Enemy targetEnemy;
        List<Tower> towers = new List<Tower>();
        AssetManager assets = new AssetManager();
        List<Missile> missiles = new List<Missile>();

        bool won;
        WinPopup winPopup;
        float TotalTime;

        int startGold = 1000;
        int currentGold = 0;

        public GameScreen()
        {

        }

        public void LoadContent(ContentManager Content, SpriteFont font)
        {
            map = new Map(Content.Load<Texture2D>("imageZero"), Content.Load<Texture2D>("imageOne"));
            map.LoadMap("test");
            assets.AddImage("testEnemy", Content.Load<Texture2D>("testEnemy"));
            assets.AddImage("healthBarRed", Content.Load<Texture2D>("healthBarRed"));
            assets.AddImage("healthBarYellow", Content.Load<Texture2D>("healthBarYellow"));

            CreateWave();
            
            assets.AddImage("basicTower", Content.Load<Texture2D>("arrowTower"));
            assets.AddImage("basicMissile", Content.Load<Texture2D>("blackBullet"));
            assets.AddImage("flameTower", Content.Load<Texture2D>("redTower"));
            assets.AddImage("frostTower", Content.Load<Texture2D>("blueTower"));
            assets.AddImage("flameMissile", Content.Load<Texture2D>("redBullet"));
            assets.AddImage("frostMissile", Content.Load<Texture2D>("blueBullet"));

            towerInfo.Add("basic", new TowerStats("basic", assets.GetImage("basicTower"), assets.GetImage("basicMissile"), 0.5f, 10, 5));
            towerInfo.Add("flame", new TowerStats("flame", assets.GetImage("flameTower"), assets.GetImage("flameMissile"), 1.0f, 30, 25));
            towerInfo.Add("frost", new TowerStats("frost", assets.GetImage("frostTower"), assets.GetImage("frostMissile"), 1.0f, 5, 500));

            gui = new GUI(new Vector2(0, 640), towerInfo, Content.Load<Texture2D>("UIBar"), Content.Load<Texture2D>("sellTowerButton"), font);

            winPopup = new WinPopup(Content.Load<SpriteFont>("Winfont"));

            currentGold = startGold;

        }

        public void Update(float gameTime)
        {

            TotalTime += gameTime;
            if (enemyWave.Count > 0)
            {
                if (TotalTime >= enemyWave.Keys[0])
                {
                    enemies.Add(enemyWave[enemyWave.Keys[0]]);
                    enemyWave.Remove(enemyWave.Keys[0]);
                }
            }
            if (!won)
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
                        currentGold += enemies[counter].GoldYield;
                        enemies.RemoveAt(counter);
                        counter--;
                    }
                }
                foreach (Tower t in towers)
                {
                    if(enemies.Count > 0)
                        t.Update(gameTime, enemies, null, missiles);
                }
                for (int counter = 0; counter < missiles.Count; counter++)
                {
                    missiles[counter].Update(gameTime);
                    if (missiles[counter].remove)
                    {
                        missiles.RemoveAt(counter);
                        counter--;
                    }
                }

                gui.Update(gameTime, input, currentGold);
            }
            if (enemies.Count <= 0 && enemyWave.Count <= 0)
                won = true;


            if (won)
            {
                input.Update();
                if (input.KeyPress(Keys.Enter))
                    RestartGame();
            }

            
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
            if (won)
                winPopup.Draw(spriteBatch);
        }

        private void RestartGame()
        {
            towers.Clear();
            enemies.Clear();
            enemyWave.Clear();
            eventHandler.Clear();
            currentGold = startGold;
            CreateWave();
            won = false;
            gui.building = false;
            TotalTime = 0;
            winPopup.Randomize();
        }

        // Currently static
        private void CreateWave()
        {
            enemyWave.Add(0.5f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("testEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 20, 1, map.Directions));
            enemyWave.Add(1.5f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("testEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 20, 1, map.Directions));
            enemyWave.Add(2.5f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("testEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 20, 1, map.Directions));
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
            if (currentGold < t.GoldCost)
                canBuild = false;
            if (canBuild)
            {
                towers.Add(new Tower(t.TowerTexture, t.MissileTexture, position, t.TowerReloadTime, t.Damage, t.GoldCost));
                currentGold -= t.GoldCost;
            }
        }

    }
}