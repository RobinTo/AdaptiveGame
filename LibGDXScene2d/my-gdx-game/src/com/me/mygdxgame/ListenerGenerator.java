package com.me.mygdxgame;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class ListenerGenerator
{
	EventHandler eventHandler;
	GameProcessor gameProcessor;
	ThinkTank thinkTank;
	
	public ListenerGenerator(EventHandler eventHandler, GameProcessor gameProcessor, ThinkTank thinkTank)
	{
		this.eventHandler = eventHandler;
		this.gameProcessor = gameProcessor;
		this.thinkTank = thinkTank;
	}
	
	public InputListener createCleanButtonListener()
	{
		return new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button)
			{
				eventHandler.queueEvent(new Event("clean", 0, 0, "x"));
				return true;
			}
		};
	}
	public InputListener createSellButtonListener()
	{
		return new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				Tower selectedTower = gameProcessor.selectedTower;
				if (selectedTower == null)
					return true;
				eventHandler.queueEvent(new Event("sell", (int) (selectedTower.getX() / GameConstants.tileSize), (int) (selectedTower.getY() / GameConstants.tileSize), "x"));
				return true;
			}
		};
	}
	public InputListener createUpgradeButtonListener()
	{
		return new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				Tower selectedTower = gameProcessor.selectedTower;
				if (selectedTower == null)
					return true;
				eventHandler.queueEvent(new Event("upgrade", (int) (selectedTower.getX() / GameConstants.tileSize), (int) (selectedTower.getY() / GameConstants.tileSize), "x"));
				return true;
			}
		};
	}
	public InputListener createTowerButtonListener(final String currentKey)
	{
		return new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				eventHandler.queueEvent(new Event("selectTower", 0, 0, currentKey));
				return true;
			}
		};
	}
	public InputListener createWallButtonListener()
	{
		return new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				Tower selectedTower = gameProcessor.selectedTower;
				if (gameProcessor.selectedTower == null)
					return true;
				eventHandler.queueEvent(new Event("wall", (int) (selectedTower.getX() / GameConstants.tileSize), (int) (selectedTower.getY() / GameConstants.tileSize), "x"));
				return true;
			}
		};
	}
}
