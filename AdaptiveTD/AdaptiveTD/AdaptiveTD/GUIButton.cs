using System;
using System.Collections.Generic;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;

namespace AdaptiveTD
{
    class GUIButton
    {
        Texture2D texture;
        Vector2 position;
        Color color;
        Keys keyBinding;
        public Keys KeyBinding
        {
            get { return keyBinding; }
        }
        public Color Color
        {
            set { color = value; }
        }

        public GUIButton(Texture2D texture, Vector2 position, Keys keyBinding)
        {
            this.texture = texture;
            this.position = position;
            color = Color.White;
            this.keyBinding = keyBinding;
        }

        public bool ButtonClicked(float x, float y)
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
