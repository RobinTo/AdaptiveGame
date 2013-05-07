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
//				myGdxGame.thinkTank.clean(myGdxGame.parameterSavePath);
//				myGdxGame.resetGame();
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
				if (myGdxGame.gameProcessor.selectedTower == null)
					return true;
				if (myGdxGame.gameProcessor.selectedTower.towerStats.upgradesTo.equals("null"))
					return true;
				int upgradeCost = myGdxGame.thinkTank.towerInfo.get(myGdxGame.gameProcessor.selectedTower.towerStats.upgradesTo).buildCost - myGdxGame.gameProcessor.selectedTower.towerStats.buildCost;
				boolean canAfford = myGdxGame.gameProcessor.currentGold >= upgradeCost ? true : false;
				if (canAfford)
				{
					myGdxGame.gameProcessor.currentGold -= upgradeCost;
					myGdxGame.hud.goldButton.setText("        " + myGdxGame.gameProcessor.currentGold);
					myGdxGame.eventHandler.queueEvent(new Event("upgrade", (int) (myGdxGame.gameProcessor.selectedTower.getX() / GameConstants.tileSize), (int) (myGdxGame.gameProcessor.selectedTower.getY() / GameConstants.tileSize), "x"));
				}

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
				myGdxGame.building = true;
				myGdxGame.buildingTower = myGdxGame.thinkTank.towerInfo.get(currentKey).type;
				myGdxGame.buildingTowerSprite = myGdxGame.assetManager.towersAtlas.createSprite(myGdxGame.thinkTank.towerInfo.get(currentKey).towerTexture);
				myGdxGame.towerName = currentKey;
				myGdxGame.temporaryTowerActor = new MapTile(myGdxGame.assetManager.towersAtlas.createSprite(myGdxGame.thinkTank.towerInfo.get(myGdxGame.towerName).towerTexture), -64, 0);
				myGdxGame.stage.addActor(myGdxGame.temporaryTowerActor);
				myGdxGame.hud.yellowBoxLabel.setText(myGdxGame.towerName);
				return true;
			}
		};
	}
}
