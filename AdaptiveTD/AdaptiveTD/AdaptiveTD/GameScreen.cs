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

        public GameScreen()
        {

        }

        public void LoadContent(ContentManager Content)
        {
            m = new Map(Content.Load<Texture2D>("imageZero"), Content.Load<Texture2D>("imageOne"));
        }

        public void Update(GameTime gameTime)
        {
            
        }

        public void Draw(SpriteBatch spriteBatch)
        {
            m.Draw(spriteBatch);
        }
    }
}
