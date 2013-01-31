using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;

namespace AdaptiveTD
{
    class Tower
    {
        Texture2D towerTexture;
        float rotation;
        Vector2 position, origin;
        Rectangle selfRectangle;

        public Tower(Texture2D towerTexture, Vector2 tilePosition)
        {
            this.towerTexture = towerTexture;
            this.position = new Vector2(tilePosition.X * 64, tilePosition.Y * 64);
            this.origin = new Vector2(32, 32);
            this.selfRectangle = new Rectangle((int)position.X, (int)position.Y, 64, 64);
            this.rotation = 0.0f;
        }

        public void Shoot()
        {

        }

        public void Update(GameTime gameTime, Enemy? targetEnemy)
        {
            if (targetEnemy == null)
                return;
            else
                return;  
//                rotation = 

//            targetEnemy.position
//x1 = 0.5
//y1 = 0.0

//x2 = -0.5
//y2 = -1.0

//deltax = x2 - x1
//deltay = y2 - y1

//angle_rad = atan2(deltay,deltax)
        }

        public void Draw(SpriteBatch spriteBatch)
        {
            spriteBatch.Draw(towerTexture, position, selfRectangle, Color.White, rotation, origin, 1.0f, SpriteEffects.None, 1.0f);

        }


    }
}
