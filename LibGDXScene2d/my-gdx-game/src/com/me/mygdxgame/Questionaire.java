package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class Questionaire
{
	int happy = 0;
	int difficult = 0;
	List<TextButton> buttons = new ArrayList<TextButton>();
	
	ExtendedActor backGroundSprite;
	
	BitmapFont font;
	
	public Questionaire(Sprite bgSprite, Sprite thumbUpSprite, Sprite thumbDownSprite, Sprite thumbSideSprite, Stage stage, BitmapFont font, ButtonGenerator buttonGenerator)
	{
		this.font = font;
		backGroundSprite = new ExtendedActor(bgSprite);
		backGroundSprite.setBounds(250, 150, 800, 460);
		stage.addActor(backGroundSprite);
		TextButton thumbDownButton = buttonGenerator.createButton(thumbDownSprite, font);
		thumbDownButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				happy = 1;
				return true;
			}
		});
		thumbDownButton.setPosition(GameConstants.screenWidth/2 - thumbDownButton.getWidth()*1.5f, GameConstants.screenHeight/2+64);
		stage.addActor(thumbDownButton);
		buttons.add(thumbDownButton);

		TextButton thumbSideButton = buttonGenerator.createButton(thumbSideSprite, font);
		thumbSideButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				happy = 2;
				return true;
			}
		});
		thumbSideButton.setPosition(GameConstants.screenWidth/2 - thumbSideButton.getWidth()*0.5f, GameConstants.screenHeight/2+64);
		stage.addActor(thumbSideButton);
		buttons.add(thumbSideButton);

		TextButton thumbUpButton = buttonGenerator.createButton(thumbUpSprite, font);
		thumbUpButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
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
		smiley1Button.setPosition(GameConstants.screenWidth/2 - thumbUpButton.getWidth()*1.5f, GameConstants.screenHeight/2 - 64);
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
		smiley2Button.setPosition(GameConstants.screenWidth/2 - thumbUpButton.getWidth()*0.5f, GameConstants.screenHeight/2 - 64);
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
		smiley3Button.setPosition(GameConstants.screenWidth/2 + thumbUpButton.getWidth()*0.5f, GameConstants.screenHeight/2 - 64);
		stage.addActor(smiley3Button);
		buttons.add(smiley3Button);
	}
	
	public void draw(SpriteBatch spriteBatch)
	{
		font.setColor(Color.BLACK);
		font.draw(spriteBatch, "How fun was this game, compared to last? Less/Equal/More", 260, 600);
		font.draw(spriteBatch, "Difficulty level? Too easy / Ok / Too hard", 260, 300);
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
