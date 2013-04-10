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
	
	public InputListener createSettingsButtonListener()
	{
		return new InputListener()
		{
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button)
			{
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

				if (myGdxGame.selectedTower != null)
				{
					myGdxGame.currentGold += myGdxGame.selectedTower.towerStats.sellPrice;
					myGdxGame.view.goldButton.setText("        " + myGdxGame.currentGold);
					myGdxGame.selectedTower.remove();
					myGdxGame.towers.remove(myGdxGame.selectedTower);
					myGdxGame.selectTower(null);
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
				if (myGdxGame.selectedTower == null)
					return true;
				if (myGdxGame.selectedTower.towerStats.upgradesTo.equals("null"))
					return true;
				int upgradeCost = myGdxGame.thinkTank.towerInfo.get(myGdxGame.selectedTower.towerStats.upgradesTo).buildCost - myGdxGame.selectedTower.towerStats.buildCost;
				boolean canAfford = myGdxGame.currentGold >= upgradeCost ? true : false;
				if (canAfford)
				{
					myGdxGame.currentGold -= upgradeCost;
					myGdxGame.view.goldButton.setText("        " + myGdxGame.currentGold);
					myGdxGame.eventHandler.queueEvent(new Event("upgrade", (int) (myGdxGame.selectedTower.getX() / GameConstants.tileSize), (int) (myGdxGame.selectedTower.getY() / GameConstants.tileSize), "x"));
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
				myGdxGame.buildingTowerSprite = myGdxGame.towersAtlas.createSprite(myGdxGame.thinkTank.towerInfo.get(currentKey).towerTexture);
				myGdxGame.towerName = currentKey;
				myGdxGame.temporaryTowerActor = new MapTile(myGdxGame.towersAtlas.createSprite(myGdxGame.thinkTank.towerInfo.get(myGdxGame.towerName).towerTexture), -64, 0);
				myGdxGame.stage.addActor(myGdxGame.temporaryTowerActor);
				myGdxGame.view.yellowBoxLabel.setText(myGdxGame.towerName);
				return true;
			}
		};
	}
}
