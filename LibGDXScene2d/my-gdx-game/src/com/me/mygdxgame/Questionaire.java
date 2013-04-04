package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Questionaire
{
	static int happy = 0;
	static int difficult = 0;
	List<TextButton> buttons = new ArrayList<TextButton>();
	
	public Questionaire(Sprite thumbUpSprite, Sprite thumbDownSprite, Sprite thumbSideSprite, Stage stage, BitmapFont font)
	{
		TextButton thumbDownButton = ButtonGenerator.createButton(thumbDownSprite, font);
		thumbDownButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				happy = 3;
				return true;
			}
		});
		thumbDownButton.setPosition(GameConstants.screenWidth/2, GameConstants.screenHeight/2);
		stage.addActor(thumbDownButton);
		buttons.add(thumbDownButton);

		TextButton thumbSideButton = ButtonGenerator.createButton(thumbSideSprite, font);
		thumbSideButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				happy = 2;
				return true;
			}
		});
		thumbSideButton.setPosition(GameConstants.screenWidth/2 + 2*GameConstants.tileSize, GameConstants.screenHeight/2);
		stage.addActor(thumbSideButton);
		buttons.add(thumbSideButton);

		TextButton thumbUpButton = ButtonGenerator.createButton(thumbUpSprite, font);
		thumbUpButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				happy = 1;
				return true;
			}
		});
		thumbUpButton.setPosition(GameConstants.screenWidth/2 + 4*GameConstants.tileSize, GameConstants.screenHeight/2);
		stage.addActor(thumbUpButton);
		buttons.add(thumbUpButton);		

		TextButton smiley1Button = ButtonGenerator.createButton(thumbDownSprite, font);
		smiley1Button.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				difficult = 1;
				return true;
			}
		});
		smiley1Button.setPosition(GameConstants.screenWidth/2, GameConstants.screenHeight/2 - 2*GameConstants.tileSize);
		stage.addActor(smiley1Button);
		buttons.add(smiley1Button);
		
		TextButton smiley2Button = ButtonGenerator.createButton(thumbSideSprite, font);
		smiley2Button.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				difficult = 2;
				return true;
			}
		});
		smiley2Button.setPosition(GameConstants.screenWidth/2 + 2*GameConstants.tileSize, GameConstants.screenHeight/2 - 2*GameConstants.tileSize);
		stage.addActor(smiley2Button);
		buttons.add(smiley2Button);
		
		TextButton smiley3Button = ButtonGenerator.createButton(thumbUpSprite, font);
		smiley3Button.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				difficult = 3;
				return true;
			}
		});
		smiley3Button.setPosition(GameConstants.screenWidth/2 + 4*GameConstants.tileSize, GameConstants.screenHeight/2 - 2*GameConstants.tileSize);
		stage.addActor(smiley3Button);
		buttons.add(smiley3Button);
	}
	
	public void reset()
	{
		happy = 0;
		difficult = 0;
		for (TextButton b : buttons)
		{
			b.remove();
		}
	}
}
