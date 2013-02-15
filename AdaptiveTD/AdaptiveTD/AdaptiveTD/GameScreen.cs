using System;
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
        bool useReplay = false;                                  // Use replay?
        string replayString = ".\\Replay12022013132536.txt";    // Path to replay file to use, if useReplay is true.
        bool savedParameters = false;


        Dictionary<string, TowerStats> towerInfo = new Dictionary<string, TowerStats>();
        Dictionary<string, EnemyInfo> enemyInfo = new Dictionary<string, EnemyInfo>();
        SortedList<float, Enemy> enemyWave = new SortedList<float, Enemy>();

        Texture2D targetCircle;

        IOParametersXML ioParametersXML;

        EventHandler eventHandler = new EventHandler();

        InputHandler input = new InputHandler();
        GUI gui;

        Map map;
        List<Enemy> enemies = new List<Enemy>();
        
        Enemy targetEnemy;
        Enemy selectedEnemy;

        WaveHandler waveHandler = new WaveHandler();
        Dictionary<float, string> enemyBaseWave = new Dictionary<float, string>();

        List<Tower> towers = new List<Tower>();
        AssetManager assets = new AssetManager();
        List<Missile> missiles = new List<Missile>();
        Tower selectedTower;

        bool gameOver;
        WinPopup winPopup;
        float TotalTime;

        int startGold = 3000;
        int currentGold = 0;

        int currentLives = 5;
        int startingLives = 5;

        LoginScreen loginScreen;

        public GameScreen()
        {

        }

        public void LoadContent(ContentManager Content, SpriteFont font, LoginScreen loginScreen)
        {
            this.loginScreen = loginScreen;
            map = new Map();
            map.LoadMap(".\\Content\\map.txt", Content);
            targetCircle = Content.Load<Texture2D>("targetCircle");
            assets.AddImage("testEnemy", Content.Load<Texture2D>("testEnemy"));
            assets.AddImage("toughEnemy", Content.Load<Texture2D>("toughEnemy"));
            assets.AddImage("fastEnemy", Content.Load<Texture2D>("fastEnemy"));

            assets.AddImage("redHealthBar", Content.Load<Texture2D>("healthBarRed"));
            assets.AddImage("yellowHealthBar", Content.Load<Texture2D>("healthBarYellow"));

            assets.AddImage("basicTower", Content.Load<Texture2D>("arrowTower"));
            assets.AddImage("basicMissile", Content.Load<Texture2D>("blackBullet"));
            assets.AddImage("flameTower", Content.Load<Texture2D>("redTower"));
            assets.AddImage("frostTower", Content.Load<Texture2D>("blueTower"));
            assets.AddImage("flameMissile", Content.Load<Texture2D>("redBullet"));
            assets.AddImage("frostMissile", Content.Load<Texture2D>("blueBullet"));
            assets.AddImage("rangeHighlight", Content.Load<Texture2D>("rangeHighlight"));

            towerInfo.Add("basic1", new TowerStats("basic1", "basicTower", "basicMissile", 0.5f, 1, 5, 7, 3, 3, new DamageOverTime(false), new Slow(false), new AreaOfEffect(0)));
            towerInfo.Add("flame1", new TowerStats("flame1", "flameTower", "flameMissile", 1.0f, 2, 20, 14, 3, 2, new DamageOverTime(2, 3, 6f), new Slow(false), new AreaOfEffect(0)));
            towerInfo.Add("frost1", new TowerStats("frost1", "frostTower", "frostMissile", 1.0f, 0, 15, 10, 3, 3, new DamageOverTime(false), new Slow(30, 2f), new AreaOfEffect(0)));
            towerInfo.Add("flameAoE1", new TowerStats("flameAoE1", "flameTower", "flameMissile", 2.0f, 5, 30, 14, 3, 2, new DamageOverTime(false), new Slow(false), new AreaOfEffect(128)));
            towerInfo.Add("basic2", new TowerStats("basic2", "basicTower", "basicMissile", 0.5f, 2, 10, 7, 3, 3, new DamageOverTime(false), new Slow(false), new AreaOfEffect(0)));
            towerInfo.Add("flame2", new TowerStats("flame2", "flameTower", "flameMissile", 1.0f, 4, 20, 14, 3, 2, new DamageOverTime(4, 3, 6f), new Slow(false), new AreaOfEffect(0)));
            towerInfo.Add("frost2", new TowerStats("frost2", "frostTower", "frostMissile", 1.0f, 0, 15, 10, 3, 3, new DamageOverTime(false), new Slow(50, 2f), new AreaOfEffect(0)));
            towerInfo.Add("flameAoE2", new TowerStats("flameAoE2", "flameTower", "flameMissile", 2.0f, 10, 20, 14, 3, 2, new DamageOverTime(false), new Slow(false), new AreaOfEffect(128)));
            towerInfo.Add("basic3", new TowerStats("basic3", "basicTower", "basicMissile", 0.5f, 3, 10, 0, 3, 3, new DamageOverTime(false), new Slow(false), new AreaOfEffect(0)));
            towerInfo.Add("flame3", new TowerStats("flame3", "flameTower", "flameMissile", 1.0f, 6, 20, 0, 3, 2, new DamageOverTime(6, 3, 6f), new Slow(false), new AreaOfEffect(0)));
            towerInfo.Add("frost3", new TowerStats("frost3", "frostTower", "frostMissile", 1.0f, 0, 15, 0, 3, 3, new DamageOverTime(false), new Slow(70, 2f), new AreaOfEffect(0)));
            towerInfo.Add("flameAoE3", new TowerStats("flameAoE3", "flameTower", "flameMissile", 2.0f, 15, 2, 0, 3, 2, new DamageOverTime(false), new Slow(false), new AreaOfEffect(128)));

            enemyInfo.Add("basic", new EnemyInfo("basic", 20, 64, 2, "testEnemy", "redHealthBar", "yellowHealthBar"));
            enemyInfo.Add("tough", new EnemyInfo("tough", 40, 32, 5, "toughEnemy", "redHealthBar", "yellowHealthBar"));
            enemyInfo.Add("fast", new EnemyInfo("tough", 30, 128, 3, "fastEnemy", "redHealthBar", "yellowHealthBar"));

            CreateWave(); // After all enemies are added to enemyInfo.

            assets.AddImage("loginBackground", Content.Load<Texture2D>("loginPopup"));

            gui = new GUI(new Vector2(0, 640), towerInfo, Content.Load<Texture2D>("UIBar"), Content.Load<Texture2D>("sellTowerButton"), Content.Load<Texture2D>("upgradeTowerButton"), font, assets);
            
            winPopup = new WinPopup(Content.Load<SpriteFont>("Winfont"));

            currentGold = startGold;
            if (useReplay && File.Exists(replayString))
                replayHandler.LoadReplay(replayString);
            else
                useReplay = false;

            ioParametersXML = new IOParametersXML();

            //towerInfo = ioParametersXML.ReadParameters(Content, loginScreen.SavePath);
        }

        public void Update(float gameTime)
        {
            if (targetEnemy != null && targetEnemy.Health <= 0)
                targetEnemy = null;
            if (!gameOver)
            {
                if (useReplay)
                {
                    NextUpdate next = replayHandler.GetNextUpdate();
                    gameTime = next.Gametime;
                    if (gameTime < 0)                   // If no more updates in replay, use a fixed 60fps step.
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
                if (saveReplay)
                {
                    replayHandler.Update(gameTime, TotalTime, eventHandler.Events);
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
                    if (enemies.Count > 0)
                    {
                        t.Update(gameTime, enemies, targetEnemy, missiles);
                        if (gui.building)
                            t.Color = Color.White;
                    }
                }
                for (int counter = 0; counter < missiles.Count; counter++)
                {
                    missiles[counter].Update(gameTime, enemies);
                    if (missiles[counter].remove)
                    {
                        missiles.RemoveAt(counter);
                        counter--;
                    }
                }

                gui.Update(gameTime, input, currentLives, currentGold, selectedTower, eventHandler);
            }
            if (enemies.Count <= 0 && enemyWave.Count <= 0)
                gameOver = true;


            if (gameOver)
            {
                if (saveReplay && !saved && !useReplay)
                {
                    replayHandler.SaveReplay();
                    saved = true;
                }
                input.Update();
                if (input.KeyPress(Keys.Enter) || input.KeyPress(Keys.Space))
                    RestartGame();

                if (!savedParameters)
                {
                    ioParametersXML.SaveParameters(towerInfo, enemyInfo, loginScreen.SavePath);
                    savedParameters = true;
                }
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

            if (targetEnemy != null)
                spriteBatch.Draw(targetCircle, targetEnemy.Position, Color.White);

            if (gui.building)
            {
                Vector2 position = new Vector2((float)Math.Floor(input.MousePosition.X / GameConstants.tileSize), (float)Math.Floor(input.MousePosition.Y / GameConstants.tileSize));
                spriteBatch.Draw(assets.GetImage(gui.selectedTower.TowerTexture), position*GameConstants.tileSize, Color.White);
                spriteBatch.Draw(assets.GetImage("rangeHighlight"), new Rectangle((int)position.X * GameConstants.tileSize + GameConstants.tileSize / 2 - gui.selectedTower.Range, (int)position.Y * GameConstants.tileSize + GameConstants.tileSize / 2 - gui.selectedTower.Range, gui.selectedTower.Range * 2, gui.selectedTower.Range * 2), new Rectangle(0, 0, 64, 64), Color.White); // Kan optimaliseres.
            }

            if (selectedTower != null)
                spriteBatch.Draw(assets.GetImage("rangeHighlight"), selectedTower.RangeHighlightRectangle, new Rectangle(0, 0, 64, 64), Color.White); //Kan evt lage rectangelet før draw-metoden.

            gui.Draw(spriteBatch);
            
            if (currentLives <= 0)
            {
                winPopup.Draw(false, spriteBatch);
                gameOver = true;
            }
            else if (gameOver)
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
            gameOver = false;
            gui.building = false;
            TotalTime = 0;
            winPopup.Randomize();
            selectedTower = null;
            currentLives = startingLives;
            saved = false;
            savedParameters = false;
            replayHandler.Clear();
            targetEnemy = null;
            if (useReplay)
                replayHandler.LoadReplay(replayString);
        }

        // Currently static
        private void CreateWave()
        {
            enemyBaseWave = waveHandler.GenerateNextWave(loginScreen.SavePath);/*Use parameters for wave caculation*/

            foreach (KeyValuePair<float, string> kV in enemyBaseWave)
            {
                SpawnEnemy(kV.Key, kV.Value);
            }
            /*enemyWave.Add(0.5f, new Enemy(startPoint, enemyInfo["basic"], map.Directions));
            enemyWave.Add(1.5f, new Enemy(startPoint, enemyInfo["basic"], map.Directions));
            enemyWave.Add(2.5f, new Enemy(startPoint, enemyInfo["basic"], map.Directions));
            enemyWave.Add(4.0f, new Enemy(startPoint, enemyInfo["tough"], map.Directions));
            enemyWave.Add(4.5f, new Enemy(startPoint, enemyInfo["basic"], map.Directions));
            enemyWave.Add(5.5f, new Enemy(startPoint, enemyInfo["basic"], map.Directions));
            enemyWave.Add(5.9f, new Enemy(startPoint, enemyInfo["basic"], map.Directions));
            enemyWave.Add(6.5f, new Enemy(startPoint, enemyInfo["basic"], map.Directions));
            enemyWave.Add(7.0f, new Enemy(startPoint, enemyInfo["basic"], map.Directions));
            enemyWave.Add(8.5f, new Enemy(startPoint, enemyInfo["basic"], map.Directions));
            enemyWave.Add(8.9f, new Enemy(startPoint, enemyInfo["tough"], map.Directions));
            enemyWave.Add(9.0f, new Enemy(startPoint, enemyInfo["tough"], map.Directions));
            enemyWave.Add(10.5f, new Enemy(startPoint, enemyInfo["tough"], map.Directions));
            enemyWave.Add(11.5f, new Enemy(startPoint, enemyInfo["tough"], map.Directions));
            enemyWave.Add(13.0f, new Enemy(startPoint, enemyInfo["fast"], map.Directions));
            enemyWave.Add(14.0f, new Enemy(startPoint, enemyInfo["fast"], map.Directions));
            enemyWave.Add(15.0f, new Enemy(startPoint, enemyInfo["fast"], map.Directions));
            enemyWave.Add(16.0f, new Enemy(startPoint, enemyInfo["fast"], map.Directions));
            */
        }

        private void SpawnEnemy(float time, string enemyType)
        {
            enemyWave.Add(time, new Enemy(map.StartPoint, enemyInfo[enemyType], map.Directions, assets.GetImage(enemyInfo[enemyType].EnemyTexture), assets.GetImage(enemyInfo[enemyType].YellowHealthBar), assets.GetImage(enemyInfo[enemyType].RedHealthBar)));
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
                    case EventType.upgrade:
                        for (int t = 0; t < towers.Count; t++)
                        {
                            if (towers[t].TilePosition == e.TilePosition)
                            {
                                UpgradeTower(e.TilePosition);
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
                else if (gui.upgradeTowerButton.ButtonClicked(input.MousePosition.X, input.MousePosition.Y))
                {
                    if (selectedTower != null)
                    {
                        Event e = new Event(EventType.upgrade, selectedTower.TilePosition, selectedTower.TowerStats.Type);
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

                    foreach (Enemy e in enemies)
                    {
                        if (input.NewMouseState.X > e.Position.X && input.NewMouseState.Y > e.Position.Y && input.NewMouseState.X < e.Position.X + GameConstants.tileSize && input.NewMouseState.Y < e.Position.Y + GameConstants.tileSize)
                        {
                            selectedEnemy = e;
                            gui.SelectedEnemy = enemyInfo[e.Type];
                            break;
                        }
                    }
                }
            }
            else if(input.MousePress(MouseButtons.Right))
            {
                targetEnemy = null;
                foreach (Enemy e in enemies)
                {
                    if (input.NewMouseState.X > e.Position.X && input.NewMouseState.Y > e.Position.Y && input.NewMouseState.X < e.Position.X + GameConstants.tileSize && input.NewMouseState.Y < e.Position.Y + GameConstants.tileSize)
                    {
                        targetEnemy = e;
                        break;
                    }
                }
            }
            if (input.KeyPress(Keys.D1))
            {
                gui.selectedTower = towerInfo["basic1"];
                gui.building = true;
                selectedTower = null;
            }
            else if (input.KeyPress(Keys.D2))
            {
                gui.selectedTower = towerInfo["flame2"];
                gui.building = true;
                selectedTower = null;
            }
            else if (input.KeyPress(Keys.D3))
            {
                gui.selectedTower = towerInfo["frost3"];
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
            else if (input.KeyPress(gui.upgradeTowerButton.KeyBinding))
            {
                if (selectedTower != null)
                {
                    Event e = new Event(EventType.upgrade, selectedTower.TilePosition, selectedTower.TowerStats.Type);
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

                selectedTower = new Tower(t, position, assets.GetImage(t.TowerTexture), assets.GetImage(t.MissileTexture));
                selectedTower.Color = Color.Firebrick;
                towers.Add(selectedTower);
                currentGold -= t.GoldCost;
            }
        }

        private void UpgradeTower(Vector2 position)
        {
            bool canUpgrade = true;
            if (currentGold < selectedTower.TowerStats.UpgradeCost)
                canUpgrade = false;
            if (selectedTower.CurrentLevel == selectedTower.TowerStats.MaxLevel)
                canUpgrade = false;

            if (canUpgrade)
            {
                selectedTower.CurrentLevel ++;
                selectedTower.TowerStats = towerInfo[selectedTower.TowerStats.Type.Substring(0,selectedTower.TowerStats.Type.Length-1)  + selectedTower.CurrentLevel];
                currentGold -= selectedTower.TowerStats.UpgradeCost;

            }
        }
    }
}