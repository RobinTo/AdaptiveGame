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
	
	public Questionaire(Sprite buttonSprite, Stage stage, BitmapFont font)
	{
		TextButton.TextButtonStyle starButtonStyle = new TextButton.TextButtonStyle();
		TextureRegion upStyleStar = new TextureRegion(buttonSprite);
		TextureRegion downStyleStar = new TextureRegion(buttonSprite);
		starButtonStyle.font = font;
		starButtonStyle.up = new TextureRegionDrawable(upStyleStar);
		starButtonStyle.down = new TextureRegionDrawable(downStyleStar);
		
		TextButton star1Button = new TextButton("", starButtonStyle);
		star1Button.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				happy = 1;
				return true;
			}
		});
		star1Button.setPosition(GameConstants.screenWidth/2, GameConstants.screenHeight/2);
		stage.addActor(star1Button);
		buttons.add(star1Button);
		
		TextButton star2Button = new TextButton("", starButtonStyle);
		star2Button.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				happy = 2;
				return true;
			}
		});
		star2Button.setPosition(GameConstants.screenWidth/2 + 2*GameConstants.tileSize, GameConstants.screenHeight/2);
		stage.addActor(star2Button);
		buttons.add(star2Button);
		
		TextButton star3Button = new TextButton("", starButtonStyle);
		star3Button.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				happy = 3;
				return true;
			}
		});
		star3Button.setPosition(GameConstants.screenWidth/2 + 4*GameConstants.tileSize, GameConstants.screenHeight/2);
		stage.addActor(star3Button);
		buttons.add(star3Button);
		
		TextButton.TextButtonStyle smileyButtonStyle = new TextButton.TextButtonStyle();
		TextureRegion upStyleSmiley = new TextureRegion(buttonSprite);
		TextureRegion downStyleSmiley = new TextureRegion(buttonSprite);
		smileyButtonStyle.font = font;
		smileyButtonStyle.up = new TextureRegionDrawable(upStyleSmiley);
		smileyButtonStyle.down = new TextureRegionDrawable(downStyleSmiley);
		
		TextButton smiley1Button = new TextButton("", smileyButtonStyle);
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
		
		TextButton smiley2Button = new TextButton("", starButtonStyle);
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
		
		TextButton smiley3Button = new TextButton("", starButtonStyle);
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
