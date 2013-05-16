package com.me.mygdxgame;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class ListenerGenerator
{
	MyGdxGame myGdxGame;
	
	public ListenerGenerator(MyGdxGame myGdxGame)
	{
		this.myGdxGame = myGdxGame;
	}
	
	public InputListener createCleanButtonListener()
	{
		return new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button)
			{
				myGdxGame.thinkTank.clean(myGdxGame.parameterSavePath, myGdxGame.relationsSavePath);
				myGdxGame.resetGame();
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

				if (myGdxGame.gameProcessor.selectedTower != null)
				{
					myGdxGame.gameProcessor.currentGold += myGdxGame.gameProcessor.selectedTower.towerStats.sellPrice;
					myGdxGame.hud.goldButton.setText("        " + myGdxGame.gameProcessor.currentGold);
					myGdxGame.gameProcessor.selectedTower.remove();
					myGdxGame.gameProcessor.towers.remove(myGdxGame.gameProcessor.selectedTower);
					myGdxGame.gameProcessor.selectTower(null, myGdxGame.thinkTank.towerInfo);
					myGdxGame.hud.yellowBoxLabel.setText("");
					myGdxGame.hud.fadeOutYellowBox();
					myGdxGame.assetManager.sounds.get("sellTower").play();
				}
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
				Tower selectedTower = myGdxGame.gameProcessor.selectedTower;
				if (selectedTower == null)
					return true;
				myGdxGame.eventHandler.queueEvent(new Event("upgrade", (int) (selectedTower.getX() / GameConstants.tileSize), (int) (selectedTower.getY() / GameConstants.tileSize), "x"));
				return true;
//				Tower selectedTower = myGdxGame.gameProcessor.selectedTower;
//				if (selectedTower == null)
//					return true;
//				if (selectedTower.towerStats.upgradesTo.equals("null"))
//				{
//					myGdxGame.assetManager.sounds.get("maxedOut").play();
//					myGdxGame.gameProcessor.selectTower(selectedTower, myGdxGame.thinkTank.towerInfo);
//					return true;
//				}
//					
//				int upgradeCost = selectedTower.towerStats.upgradeCost;
//				boolean canAfford = myGdxGame.gameProcessor.currentGold >= upgradeCost ? true : false;
//				if (canAfford)
//				{
//					myGdxGame.gameProcessor.currentGold -= upgradeCost;
//					myGdxGame.hud.goldButton.setText("        " + myGdxGame.gameProcessor.currentGold);
//					myGdxGame.eventHandler.queueEvent(new Event("upgrade", (int) (selectedTower.getX() / GameConstants.tileSize), (int) (selectedTower.getY() / GameConstants.tileSize), "x"));
//					myGdxGame.assetManager.sounds.get("upgradeTower").play();
//				}
//				else
//				{
//					myGdxGame.assetManager.sounds.get("notEnoughMoney").play();
//					myGdxGame.gameProcessor.selectTower(selectedTower, myGdxGame.thinkTank.towerInfo);
//				}
				
			}
		};
	}
	public InputListener createTowerButtonListener(final String currentKey)
	{
		return new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				myGdxGame.eventHandler.queueEvent(new Event("selectTower", 0, 0, currentKey));
				return true;
				/*
				myGdxGame.building = true;
				myGdxGame.buildingTower = myGdxGame.thinkTank.towerInfo.get(currentKey).type;
				myGdxGame.buildingTowerSprite = myGdxGame.assetManager.towersAtlas.createSprite(myGdxGame.thinkTank.towerInfo.get(currentKey).towerTexture);
				myGdxGame.towerName = currentKey;
				myGdxGame.temporaryTowerActor = new MapTile(myGdxGame.assetManager.towersAtlas.createSprite(myGdxGame.thinkTank.towerInfo.get(myGdxGame.towerName).towerTexture), -64, 0);
				myGdxGame.stage.addActor(myGdxGame.temporaryTowerActor);
				myGdxGame.hud.yellowBoxLabel.setText(myGdxGame.towerName);
				return true;
				*/
			}
		};
	}
	public InputListener createWallButtonListener()
	{
		return new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				Tower selectedTower = myGdxGame.gameProcessor.selectedTower;
				if (myGdxGame.gameProcessor.selectedTower == null)
					return true;
				myGdxGame.eventHandler.queueEvent(new Event("wall", (int) (selectedTower.getX() / GameConstants.tileSize), (int) (selectedTower.getY() / GameConstants.tileSize), "x"));
				return true;
//				if (myGdxGame.gameProcessor.selectedTower == null)
//					return true;
//				int wallCost = myGdxGame.gameProcessor.selectedTower.towerStats.buildCost * 2;
//				boolean canAfford = myGdxGame.gameProcessor.currentGold >= wallCost ? true : false;
//				if (!myGdxGame.gameProcessor.selectedTower.wall)
//				{
//					if (canAfford)
//					{
//						myGdxGame.gameProcessor.currentGold -= wallCost;
//						myGdxGame.hud.goldButton.setText("        "
//								+ myGdxGame.gameProcessor.currentGold);
//						myGdxGame.gameProcessor.selectedTower.wall = true;
//						myGdxGame.assetManager.sounds.get("upgradeTower")
//						.play();
//					}
//					else
//					{
//						myGdxGame.assetManager.sounds.get("notEnoughMoney")
//								.play();
//					}
//				}
//				else
//				{
//					myGdxGame.assetManager.sounds.get("maxedOut")
//					.play();
//				}
//				return true;
			}
		};
	}
}
