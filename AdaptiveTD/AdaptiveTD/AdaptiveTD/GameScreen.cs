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

        Map m;
        List<Enemy> enemies = new List<Enemy>();
        Enemy targetEnemy;
        List<Tower> towers = new List<Tower>();
        AssetManager assets = new AssetManager();
        List<Missile> missiles = new List<Missile>();

        public GameScreen()
        {

        }

        public void LoadContent(ContentManager Content)
        {
            m = new Map(Content.Load<Texture2D>("imageZero"), Content.Load<Texture2D>("imageOne"));
            m.LoadMap("test");
            assets.addImage("testEnemy", Content.Load<Texture2D>("testEnemy"));
            enemies.Add(new Enemy(new Vector2(m.StartPoint.X, m.StartPoint.Y), assets.getImage("testEnemy"), 64, 20, 2, m.Directions));
            towers.Add(new Tower(Content.Load<Texture2D>("arrowTower"), Content.Load<Texture2D>("blackBullet"), new Vector2(5, 3), 0.5f));
            towers.Add(new Tower(Content.Load<Texture2D>("arrowTower"), Content.Load<Texture2D>("blackBullet"), new Vector2(0, 0), 0.5f));
        }

        public void Update(GameTime gameTime)
        {
            foreach (Enemy e in enemies)
                e.Update(gameTime);
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
        }
    }
}
