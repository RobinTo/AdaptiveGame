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
        Tower selectedTower;

        bool won;
        WinPopup winPopup;
        float TotalTime;

        int startGold = 20;
        int currentGold = 0;

        int currentLives = 5;
        int startingLives = 5;

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
            assets.AddImage("rangeHighlight", Content.Load<Texture2D>("rangeHighlight"));

            towerInfo.Add("basic", new TowerStats("basic", assets.GetImage("basicTower"), assets.GetImage("basicMissile"), 0.5f, 6, 10, 3));
            towerInfo.Add("flame", new TowerStats("flame", assets.GetImage("flameTower"), assets.GetImage("flameMissile"), 1.0f, 12, 20, 2));
            towerInfo.Add("frost", new TowerStats("frost", assets.GetImage("frostTower"), assets.GetImage("frostMissile"), 5.0f, 25, 15, 3));

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
                    else if (enemies[counter].Position.X >= GameConstants.screenWidth)
                    {
                        currentLives--;
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

                gui.Update(gameTime, input, currentLives, currentGold, selectedTower, eventHandler);
            }
            if (enemies.Count <= 0 && enemyWave.Count <= 0)
                won = true;


            if (won)
            {
                input.Update();
                if (input.KeyPress(Keys.Enter) || input.KeyPress(Keys.Space))
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

            if (selectedTower != null)
                spriteBatch.Draw(assets.GetImage("rangeHighlight"), selectedTower.RangeHighlightRectangle, new Rectangle(0, 0, 64, 64), Color.White); //Kan evt lage rectangelet før draw-metoden.

            gui.Draw(spriteBatch);
            
            if (currentLives <= 0)
            {
                winPopup.Draw(false, spriteBatch);
                won = true;
            }
            else if (won)
            {
                winPopup.Draw(true, spriteBatch);
            }
        }

        private void RestartGame()
        {
            towers.Clear();
            enemies.Clear();
            enemyWave.Clear();
            missiles.Clear();
            eventHandler.Clear();
            currentGold = startGold;
            CreateWave();
            won = false;
            gui.building = false;
            TotalTime = 0;
            winPopup.Randomize();
            selectedTower = null;
            currentLives = startingLives;
        }

        // Currently static
        private void CreateWave()
        {
            enemyWave.Add(0.5f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("testEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 20, 1, map.Directions));
            enemyWave.Add(1.5f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("testEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 20, 1, map.Directions));
            enemyWave.Add(2.5f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("testEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 20, 1, map.Directions));
            enemyWave.Add(3.5f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("testEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 20, 1, map.Directions));
            enemyWave.Add(3.8f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("testEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 20, 1, map.Directions));
            enemyWave.Add(4.5f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("testEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 20, 1, map.Directions));
            enemyWave.Add(5.5f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("testEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 20, 1, map.Directions));
            enemyWave.Add(6.5f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("testEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 20, 1, map.Directions));
            enemyWave.Add(6.7f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("testEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 20, 1, map.Directions));
            enemyWave.Add(7.5f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("testEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 20, 1, map.Directions));
            enemyWave.Add(7.2f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("testEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 20, 1, map.Directions));
            enemyWave.Add(1.0f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("testEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 20, 1, map.Directions));
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
                    case EventType.sell:
                        for(int t = 0; t<towers.Count; t++)
                        {
                            if (towers[t].TilePosition == e.TilePosition)
                            {
                                currentGold += towerInfo[towers[t].TowerStats.Type].GoldCost / 2;
                                towers.RemoveAt(t);
                            }
                        }
                        break;
                }
            }
        }

        private void HandleInput()
        {
            if (input.MousePress(MouseButtons.Left))
            {
                Vector2 tileClicked = new Vector2((float)Math.Floor(input.MousePosition.X / GameConstants.tileSize), (float)Math.Floor(input.MousePosition.Y / GameConstants.tileSize));
                if (gui.building)
                {

                    if (tileClicked.X <= map.MapWidth && tileClicked.X >= 0 && tileClicked.Y >= 0 && tileClicked.Y <= map.MapHeight)
                    {
                        Event e = new Event(EventType.build, tileClicked, gui.selectedTower.Type);
                        eventHandler.QueueEvent(e);
                    }
                }
                else if (gui.sellTowerButton.ButtonClicked(input.MousePosition.X, input.MousePosition.Y))
                {
                    if (selectedTower != null)
                    {
                        Event e = new Event(EventType.sell, selectedTower.TilePosition, selectedTower.TowerStats.Type);
                        eventHandler.QueueEvent(e);
                    }
                }
                else
                {
                    bool towerSelected = false;

                    foreach (Tower t in towers)
                    {
                        if (t.TilePosition == tileClicked)
                        {
                            selectedTower = t;
                            t.Color = Color.Firebrick;
                            towerSelected = true;
                        }
                        else
                            t.Color = Color.White;
                    }
                    if (!towerSelected)
                        selectedTower = null;
                }
            }
            if (input.KeyPress(Keys.D1))
            {
                gui.selectedTower = towerInfo["basic"];
                gui.building = true;
            }
            else if (input.KeyPress(Keys.D2))
            {
                gui.selectedTower = towerInfo["flame"];
                gui.building = true;
            }
            else if (input.KeyPress(Keys.D3))
            {
                gui.selectedTower = towerInfo["frost"];
                gui.building = true;
            }
            else if (input.KeyPress(gui.sellTowerButton.KeyBinding))
            {
                if (selectedTower != null)
                {
                    Event e = new Event(EventType.sell, selectedTower.TilePosition, selectedTower.TowerStats.Type);
                    eventHandler.QueueEvent(e);
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
            if (map.MapTiles[(int)position.X, (int)position.Y] == 1)
                canBuild = false;
            if (canBuild)
            {
                towers.Add(new Tower(t, position));
                currentGold -= t.GoldCost;
            }
        }

    }
}