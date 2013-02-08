﻿using System;
using System.Collections.Generic;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using Microsoft.Xna.Framework.Content;
using System.IO;

namespace AdaptiveTD
{
    class GameScreen
    {
        bool saveReplay = false;                                // Save replay from game? Does not save regardless if useReplay is true.
        bool saved = false;                                     // 
        ReplayHandler replayHandler = new ReplayHandler();      // Replay handler, saving, loading, etc. of replays.
        bool useReplay = true;                                  // Use replay?
        string replayString = ".\\Replay08022013220115.txt";    // Path to replay file to use, if useReplay is true.

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

        int startGold = 200;
        int currentGold = 0;

        int currentLives = 5;
        int startingLives = 5;

        public GameScreen()
        {

        }

        public void LoadContent(ContentManager Content, SpriteFont font)
        {
            map = new Map();
            map.LoadMap(".\\Content\\map.txt", Content);
            assets.AddImage("testEnemy", Content.Load<Texture2D>("testEnemy"));
            assets.AddImage("toughEnemy", Content.Load<Texture2D>("toughEnemy"));

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

            towerInfo.Add("basic", new TowerStats("basic", assets.GetImage("basicTower"), assets.GetImage("basicMissile"), 0.5f, 2, 10, 3, new DamageOverTime(false), new Slow(false)));
            towerInfo.Add("flame", new TowerStats("flame", assets.GetImage("flameTower"), assets.GetImage("flameMissile"), 1.0f, 6, 20, 2, new DamageOverTime(3, 4, 6f), new Slow(false)));
            towerInfo.Add("frost", new TowerStats("frost", assets.GetImage("frostTower"), assets.GetImage("frostMissile"), 1.0f, 0, 15, 3, new DamageOverTime(false), new Slow(50, 2f)));

            gui = new GUI(new Vector2(0, 640), towerInfo, Content.Load<Texture2D>("UIBar"), Content.Load<Texture2D>("sellTowerButton"), font);

            winPopup = new WinPopup(Content.Load<SpriteFont>("Winfont"));

            currentGold = startGold;
            if (useReplay)
                replayHandler.LoadReplay(replayString);
        }

        public void Update(float gameTime)
        {

            
            if (!won)
            {
                if (useReplay)
                {
                    NextUpdate next = replayHandler.GetNextUpdate();
                    gameTime = next.Gametime;
                    if (gameTime == 0)                  // If no more updates in replay, use a fixed 60fps step.
                        gameTime = (float)(1.0 / 60.0); // To be able to run simulations past ending time in the original game in replay.
                    eventHandler.Events = next.Events;
                }
                else
                {
                    eventHandler.NewRound();
                    input.Update();
                    HandleInput();
                }

                TotalTime += gameTime;
                if (enemyWave.Count > 0)
                {
                    if (TotalTime >= enemyWave.Keys[0])
                    {
                        enemies.Add(enemyWave[enemyWave.Keys[0]]);
                        enemyWave.Remove(enemyWave.Keys[0]);
                    }
                }
                if(saveReplay)
                {
                    replayHandler.Update(TotalTime, eventHandler.Events);
                }

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
                if (saveReplay && !saved && !useReplay)
                {
                    replayHandler.SaveReplay();
                    saved = true;
                }
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
                spriteBatch.Draw(assets.GetImage("rangeHighlight"), new Rectangle((int)position.X * GameConstants.tileSize + GameConstants.tileSize / 2 - gui.selectedTower.Range, (int)position.Y * GameConstants.tileSize + GameConstants.tileSize / 2 - gui.selectedTower.Range, gui.selectedTower.Range * 2, gui.selectedTower.Range * 2), new Rectangle(0, 0, 64, 64), Color.White); // Kan optimaliseres.
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
            saved = false;
            replayHandler.Clear();
            if (useReplay)
                replayHandler.LoadReplay(replayString);
        }

        // Currently static
        private void CreateWave()
        {
            enemyWave.Add(0.5f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("testEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 20, 1, map.Directions));
            enemyWave.Add(1.5f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("testEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 20, 1, map.Directions));
            enemyWave.Add(3.0f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("testEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 20, 1, map.Directions));
            enemyWave.Add(4.5f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("testEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 20, 1, map.Directions));
            enemyWave.Add(6.0f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("testEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 20, 1, map.Directions));
            enemyWave.Add(7.5f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("testEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 20, 1, map.Directions));
            enemyWave.Add(9.0f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("toughEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 40, 1, map.Directions));
            enemyWave.Add(10.5f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("testEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 20, 1, map.Directions));
            enemyWave.Add(12.0f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("testEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 20, 1, map.Directions));
            enemyWave.Add(13.5f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("testEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 20, 1, map.Directions));
            enemyWave.Add(15.0f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("testEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 20, 1, map.Directions));
            enemyWave.Add(16.5f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("testEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 20, 1, map.Directions));
            enemyWave.Add(18.0f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("testEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 20, 1, map.Directions));
            enemyWave.Add(19.0f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("toughEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 40, 1, map.Directions));
            enemyWave.Add(21.0f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("toughEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 40, 1, map.Directions));
            enemyWave.Add(22.0f, new Enemy(new Vector2(map.StartPoint.X, map.StartPoint.Y), assets.GetImage("toughEnemy"), assets.GetImage("healthBarYellow"), assets.GetImage("healthBarRed"), 64, 40, 1, map.Directions));
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
                                selectedTower = null;
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
                selectedTower = null;
            }
            else if (input.KeyPress(Keys.D2))
            {
                gui.selectedTower = towerInfo["flame"];
                gui.building = true;
                selectedTower = null;
            }
            else if (input.KeyPress(Keys.D3))
            {
                gui.selectedTower = towerInfo["frost"];
                gui.building = true;
                selectedTower = null;
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
                selectedTower = new Tower(t, position);
                towers.Add(selectedTower);
                currentGold -= t.GoldCost;
            }
        }

    }
}