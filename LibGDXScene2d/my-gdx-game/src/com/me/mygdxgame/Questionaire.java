package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Questionaire
{
	int happy = 0;
	int difficult = 0;
	List<TextButton> buttons = new ArrayList<TextButton>();
	
	ExtendedActor backGroundSprite;
	
	BitmapFont font;
	
	public Questionaire(Sprite bgSprite, Sprite happySprite, Sprite happyNoSprite, Sprite thumbUpSprite, Sprite thumbDownSprite, Sprite thumbSideSprite, Stage stage, BitmapFont font, ButtonGenerator buttonGenerator)
	{
		this.font = new BitmapFont();
		backGroundSprite = new ExtendedActor(bgSprite);
		backGroundSprite.setBounds(250, 150, 800, 460);
		stage.addActor(backGroundSprite);
		final TextureRegion clicked = new TextureRegion(happyNoSprite);
		final TextureRegion notClicked = new TextureRegion(happySprite);
		final TextButton thumbDownButton = buttonGenerator.createButton(happySprite, font);
		final TextButton thumbSideButton = buttonGenerator.createButton(happySprite, font);
		final TextButton thumbUpButton = buttonGenerator.createButton(happySprite, font);
		
		
		thumbDownButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				thumbDownButton.getStyle().up =  new TextureRegionDrawable(clicked);
				thumbSideButton.getStyle().up =  new TextureRegionDrawable(notClicked);
				thumbUpButton.getStyle().up =  new TextureRegionDrawable(notClicked);
				thumbDownButton.getStyle().down =  new TextureRegionDrawable(clicked);
				thumbSideButton.getStyle().down =  new TextureRegionDrawable(notClicked);
				thumbUpButton.getStyle().down =  new TextureRegionDrawable(notClicked);
				happy = 1;
				return true;
			}
		});
		thumbDownButton.setPosition(GameConstants.screenWidth/2 - thumbDownButton.getWidth()*1.5f, GameConstants.screenHeight/2+64);
		stage.addActor(thumbDownButton);
		buttons.add(thumbDownButton);

		thumbSideButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				thumbDownButton.getStyle().up =  new TextureRegionDrawable(clicked);
				thumbSideButton.getStyle().up =  new TextureRegionDrawable(clicked);
				thumbUpButton.getStyle().up =  new TextureRegionDrawable(notClicked);
				thumbDownButton.getStyle().down =  new TextureRegionDrawable(clicked);
				thumbSideButton.getStyle().down =  new TextureRegionDrawable(clicked);
				thumbUpButton.getStyle().down =  new TextureRegionDrawable(notClicked);
				happy = 2;
				return true;
			}
		});
		thumbSideButton.setPosition(GameConstants.screenWidth/2 - thumbSideButton.getWidth()*0.5f, GameConstants.screenHeight/2+64);
		stage.addActor(thumbSideButton);
		buttons.add(thumbSideButton);

		thumbUpButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				thumbDownButton.getStyle().up =  new TextureRegionDrawable(clicked);
				thumbSideButton.getStyle().up =  new TextureRegionDrawable(clicked);
				thumbUpButton.getStyle().up =  new TextureRegionDrawable(clicked);
				thumbDownButton.getStyle().down =  new TextureRegionDrawable(clicked);
				thumbSideButton.getStyle().down =  new TextureRegionDrawable(clicked);
				thumbUpButton.getStyle().down =  new TextureRegionDrawable(clicked);
				happy = 3;
				return true;
			}
		});
		thumbUpButton.setPosition(GameConstants.screenWidth/2 + thumbUpButton.getWidth()*0.5f, GameConstants.screenHeight/2+64);
		stage.addActor(thumbUpButton);
		buttons.add(thumbUpButton);		

		TextButton smiley1Button = buttonGenerator.createButton(thumbDownSprite, font);
		smiley1Button.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				difficult = 1;
				return true;
			}
		});
		smiley1Button.setPosition(GameConstants.screenWidth/2 - thumbUpButton.getWidth()*1.5f, GameConstants.screenHeight/2 - 2*64);
		stage.addActor(smiley1Button);
		buttons.add(smiley1Button);
		
		TextButton smiley2Button = buttonGenerator.createButton(thumbSideSprite, font);
		smiley2Button.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				difficult = 2;
				return true;
			}
		});
		smiley2Button.setPosition(GameConstants.screenWidth/2 - thumbUpButton.getWidth()*0.5f, GameConstants.screenHeight/2 - 2*64);
		stage.addActor(smiley2Button);
		buttons.add(smiley2Button);
		
		TextButton smiley3Button = buttonGenerator.createButton(thumbUpSprite, font);
		smiley3Button.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				difficult = 3;
				return true;
			}
		});
		smiley3Button.setPosition(GameConstants.screenWidth/2 + thumbUpButton.getWidth()*0.5f, GameConstants.screenHeight/2 - 2*64);
		stage.addActor(smiley3Button);
		buttons.add(smiley3Button);
	}
	
	public void draw(SpriteBatch spriteBatch)
	{
		font.setColor(Color.BLACK);
		font.setScale(1.3f);
		font.draw(spriteBatch, "How much fun did you have?", 420, 550);
		font.draw(spriteBatch, "Difficulty level?", 420, 400);
	}
	
	public void reset()
	{
		happy = 0;
		difficult = 0;
		for (TextButton b : buttons)
		{
			b.remove();
		}
		backGroundSprite.remove();
	}
}
