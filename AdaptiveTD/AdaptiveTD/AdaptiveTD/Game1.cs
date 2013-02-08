using System;
using System.Collections.Generic;
using System.Linq;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Audio;
using Microsoft.Xna.Framework.Content;
using Microsoft.Xna.Framework.GamerServices;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using Microsoft.Xna.Framework.Media;

namespace AdaptiveTD
{
    /// <summary>
    /// This is the main type for your game
    /// </summary>
    public class Game1 : Microsoft.Xna.Framework.Game
    {
        GraphicsDeviceManager graphics;
        SpriteBatch spriteBatch;
        SpriteFont font;

        int updates = 0;
        int oldUpdates = 0;
        float updateTimer = 1.0f;

        GameScreen gameScreen;

        bool onlyUpdates = false;
        bool showUpdatesPerSecond = false;

        public Game1()
        {
            graphics = new GraphicsDeviceManager(this);
            Content.RootDirectory = "Content";
            gameScreen = new GameScreen();
            graphics.PreferredBackBufferWidth = GameConstants.screenWidth;
            graphics.PreferredBackBufferHeight = GameConstants.screenHeight;
            graphics.IsFullScreen = true;
            IsMouseVisible = true;

            if (onlyUpdates)
            {
                IsFixedTimeStep = false;
                graphics.SynchronizeWithVerticalRetrace = false;
            }
        }

        /// <summary>
        /// Allows the game to perform any initialization it needs to before starting to run.
        /// This is where it can query for any required services and load any non-graphic
        /// related content.  Calling base.Initialize will enumerate through any components
        /// and initialize them as well.
        /// </summary>
        protected override void Initialize()
        {
            // TODO: Add your initialization logic here

            base.Initialize();
        }

        /// <summary>
        /// LoadContent will be called once per game and is the place to load
        /// all of your content.
        /// </summary>
        /// 
        protected override void LoadContent()
        {
            // Create a new SpriteBatch, which can be used to draw textures.
            spriteBatch = new SpriteBatch(GraphicsDevice);
            font = Content.Load<SpriteFont>("spriteFont");
            gameScreen.LoadContent(Content, font);
            
            // TODO: use this.Content to load your game content here
        }

        /// <summary>
        /// UnloadContent will be called once per game and is the place to unload
        /// all content.
        /// </summary>
        protected override void UnloadContent()
        {
            // TODO: Unload any non ContentManager content here
        }

        /// <summary>
        /// Allows the game to run logic such as updating the world,
        /// checking for collisions, gathering input, and playing audio.
        /// </summary>
        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        float gameTimeFloat;
        protected override void Update(GameTime gameTime)
        {
            gameTimeFloat = (float)gameTime.ElapsedGameTime.TotalSeconds;

            // Allows the game to exit
            if (Keyboard.GetState().IsKeyDown(Keys.Escape))
                this.Exit();

            // TODO: Add your update logic here
            gameScreen.Update(gameTimeFloat);

            if (showUpdatesPerSecond)
            {
                updates++;
                updateTimer -= gameTimeFloat;
                if (updateTimer <= 0)
                {
                    oldUpdates = updates;
                    updates = 0;
                    updateTimer = 1.0f;
                }
            }

            base.Update(gameTime);
        }

        /// <summary>
        /// This is called when the game should draw itself.
        /// </summary>
        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        ///
        protected override void Draw(GameTime gameTime)
        {
            if (!onlyUpdates || showUpdatesPerSecond)
            {

                GraphicsDevice.Clear(Color.CornflowerBlue);
                // TODO: Add your drawing code here
                spriteBatch.Begin();
                gameScreen.Draw(spriteBatch);
                if(showUpdatesPerSecond)
                    spriteBatch.DrawString(font, oldUpdates.ToString(), Vector2.Zero, Color.Black);
                spriteBatch.End();

                base.Draw(gameTime);
            }
        }
    }
}
