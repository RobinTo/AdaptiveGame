package no.uia.adaptiveTD;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class GUIButton {

	Sprite sprite;
	Vector2 position;
	
	Color color;
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	Keys keybinding;
	public Keys getKeybinding() {
		return keybinding;
	}
	
	public GUIButton(Sprite sprite, Vector2 position, Keys keybinding)
	{
		this.sprite = sprite;
		this.position = position;
		color = Color.WHITE;
		this.keybinding = keybinding;
	}
	
	public boolean buttonClicked(float x, float y)
	{
        if (x >= position.x && x <= position.x + sprite.getWidth() && y >= position.y && y <= position.y + sprite.getHeight())
        	return true;
        else
        	return false;
	}
	
	public void draw(SpriteBatch spriteBatch)
	{
		spriteBatch.draw(sprite, position.x, position.y);
	}
}
