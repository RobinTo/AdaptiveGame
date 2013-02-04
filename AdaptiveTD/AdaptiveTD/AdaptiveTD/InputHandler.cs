using System;
using System.Collections.Generic;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Input;

namespace AdaptiveTD
{
    public enum MouseButtons
    {
        Left,
        Right,
        Middle
    }
    class InputHandler
    {
        KeyboardState oldKeyState = new KeyboardState();
        public KeyboardState OldKeyState
        {
            get { return oldKeyState; }
        }
        KeyboardState newKeyState = new KeyboardState();
        public KeyboardState NewKeyState
        {
            get { return newKeyState; }
        }
        MouseState oldMouseState = new MouseState();
        public MouseState OldMouseState
        {
            get { return oldMouseState; }
        }
        MouseState newMouseState = new MouseState();
        public MouseState NewMouseState
        {
            get { return newMouseState; }
        }

        public void StartUpdate()
        {
            newKeyState = Keyboard.GetState();
            newMouseState = Mouse.GetState();
        }

        public bool MousePress(MouseButtons m)
        {
            switch (m)
            {
                case MouseButtons.Left:
                    if (newMouseState.LeftButton == ButtonState.Pressed && oldMouseState.LeftButton == ButtonState.Released)
                        return true;
                    break;
                case MouseButtons.Right:
                    if (newMouseState.RightButton == ButtonState.Pressed && oldMouseState.RightButton == ButtonState.Released)
                        return true;
                    break;
                case MouseButtons.Middle:
                    if (newMouseState.MiddleButton == ButtonState.Pressed && oldMouseState.MiddleButton == ButtonState.Released)
                        return true;
                    break;
            }
            return false;
        }

        public bool MouseDown(MouseButtons m)
        {
            switch (m)
            {
                case MouseButtons.Left:
                    if (newMouseState.LeftButton == ButtonState.Pressed)
                        return true;
                    break;
                case MouseButtons.Right:
                    if (newMouseState.RightButton == ButtonState.Pressed)
                        return true;
                    break;
                case MouseButtons.Middle:
                    if (newMouseState.MiddleButton == ButtonState.Pressed)
                        return true;
                    break;
            }
            return false;
        }

        public Vector2 MousePosition()
        {
            return new Vector2(newMouseState.X, newMouseState.Y);
        }

        public void EndUpdate()
        {
            oldKeyState = newKeyState;
            oldMouseState = newMouseState;
        }

        public bool KeyPress(Keys k)
        {
            if (newKeyState.IsKeyDown(k) && !oldKeyState.IsKeyDown(k))
                return true;
            else
                return false;
        }

        public bool IsKeyDown(Keys k)
        {
            if (newKeyState.IsKeyDown(k))
                return true;
            else
                return false;
        }
    }
}
