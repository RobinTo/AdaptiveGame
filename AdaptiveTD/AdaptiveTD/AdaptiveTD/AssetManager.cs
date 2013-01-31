using System;
using System.Collections.Generic;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework;

namespace AdaptiveTD
{
    class AssetManager
    {
        Dictionary<string, Texture2D> images = new Dictionary<string, Texture2D>();

        public Texture2D GetImage(string imageName)
        {
            if (images.ContainsKey(imageName))
            {
                return images[imageName];
            }
            else
                return null;
        }

        public void AddImage(string imageName, Texture2D texture)
        {
            if (!images.ContainsKey(imageName))
            {
                images.Add(imageName, texture);
            }
        }
    }
}
