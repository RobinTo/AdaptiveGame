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
        LoginScreen loginScreen;
        bool loggedIn = false;
        InputHandler input = new InputHandler();
        bool onlyUpdates = false;
        bool showUpdatesPerSecond = false;

        public Game1()
        {
            graphics = new GraphicsDeviceManager(this);
            Content.RootDirectory = "Content";
            gameScreen = new GameScreen();
            graphics.PreferredBackBufferWidth = GameConstants.screenWidth;
            graphics.PreferredBackBufferHeight = GameConstants.screenHeight;
            graphics.IsFullScreen = false;
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

            Texture2D loginBackground = Content.Load<Texture2D>("loginPopup");
            loginScreen = new LoginScreen(loginBackground, new Vector2(GameConstants.screenWidth / 2 - loginBackground.Width / 2, GameConstants.screenHeight / 2 - loginBackground.Height / 2), new Vector2(580, 360), font);

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

            if (loginScreen.Name == "null")
            {
                input.Update();
                loginScreen.Update(gameTimeFloat, input);
                if (loginScreen.Name != "null")
                {
                    gameScreen.LoadContent(Content, font, loginScreen);
                    loggedIn = true;
                }
            }
            else
            {
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
                if(!loggedIn)
                    loginScreen.Draw(spriteBatch);
                if(loggedIn)
                    gameScreen.Draw(spriteBatch);
                if(showUpdatesPerSecond)
                    spriteBatch.DrawString(font, oldUpdates.ToString(), Vector2.Zero, Color.Black);
                spriteBatch.End();

                base.Draw(gameTime);
            }
        }
    }
}
