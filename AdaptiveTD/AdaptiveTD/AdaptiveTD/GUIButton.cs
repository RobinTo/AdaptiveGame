using System;
using System.Collections.Generic;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;

namespace AdaptiveTD
{
    class GUIButton
    {
        Texture2D texture;
        Vector2 position;
        Color color;
        public Color Color
        {
            set { color = value; }
        }

        public GUIButton(Texture2D texture, Vector2 position)
        {
            this.texture = texture;
            this.position = position;
            color = Color.White;
        }

        public bool ButtonClicked(int x, int y)
        {
            if (x >= position.X && x <= position.X + texture.Width && y >= position.Y && y <= position.Y + texture.Height)
                return true;
            else
                return false;
        }

        public void Draw(SpriteBatch spriteBatch)
        {
            spriteBatch.Draw(texture, position, color);
        }
    }
}
