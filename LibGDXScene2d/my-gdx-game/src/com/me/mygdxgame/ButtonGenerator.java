package com.me.mygdxgame;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class ButtonGenerator
{
	public TextButton createButton(Sprite buttonSprite, BitmapFont font)
	{
		TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
		TextureRegion buttonStyleUp = new TextureRegion(buttonSprite);
		TextureRegion buttonStyleDown = new TextureRegion(buttonSprite);
		buttonStyle.font = font;
		buttonStyle.up = new TextureRegionDrawable(buttonStyleUp);
		buttonStyle.down = new TextureRegionDrawable(buttonStyleDown);
		TextButton button = new TextButton("", buttonStyle);
		
		return button;
	}
	public TextButton createButton(Sprite buttonSprite, BitmapFont font, String text)
	{
		TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
		TextureRegion buttonStyleUp = new TextureRegion(buttonSprite);
		TextureRegion buttonStyleDown = new TextureRegion(buttonSprite);
		buttonStyle.font = font;
		buttonStyle.up = new TextureRegionDrawable(buttonStyleUp);
		buttonStyle.down = new TextureRegionDrawable(buttonStyleDown);
		TextButton button = new TextButton(text, buttonStyle);
		
		return button;
	}
}
