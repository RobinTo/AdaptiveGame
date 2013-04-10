package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class View
{
	Group yellowBoxGroup;
	Label yellowBoxLabel;
	int yellowBoxYPadding = 10;
	int yellowBoxXPadding = 15;
	ExtendedActor yellowBox;
	ExtendedActor targetYellowBoxActor = null;
	int yellowBoxLines = 0;
	
	Group consoleGroup;
	Label consoleLabel;
	ExtendedActor consoleBackground;
	boolean showConsole = false;
	int consoleLines = 0;
	int consoleLinesPadding = 5;
	List<String> consoleStrings = new ArrayList<String>();
	int consoleStates = 0;
	
	List<String> towerKeys = new ArrayList<String>();
	List<Label> towerCostLabels = new ArrayList<Label>();
	
	TextButton livesButton, goldButton;
	
	BitmapFont font;
	
	public View(BitmapFont font)
	{
		this.font = font;
	}
	
	public void createUI(TextureAtlas miscAtlas, TextureAtlas towersAtlas, HashMap<String, TowerStats> towerInfo, Stage stage, ButtonGenerator buttonGenerator, ListenerGenerator listenerGenerator)
	{
		yellowBoxGroup = new Group();
		yellowBox = new ExtendedActor(miscAtlas.createSprite("YellowBox"));
		yellowBoxGroup.addActor(yellowBox);

		towerKeys.addAll(towerInfo.keySet());

		Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.BLACK);
		yellowBoxLabel = new Label("", labelStyle);
		yellowBoxLabel.setPosition(800, GameConstants.screenHeight - 40);
		yellowBoxGroup.addActor(yellowBoxLabel);

		consoleLabel = new Label("", labelStyle);

		consoleGroup = new Group();
		consoleBackground = new ExtendedActor(miscAtlas.createSprite("YellowBox"));
		consoleBackground.setColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, 80);
		consoleGroup.addActor(consoleBackground);
		consoleGroup.addActor(consoleLabel);
		stage.addActor(consoleGroup);

		consoleGroup.setVisible(showConsole);

		yellowBoxGroup.setVisible(false);

		stage.addActor(yellowBoxGroup);

		TextButton settingsButton = buttonGenerator.createButton(miscAtlas.createSprite("settingsButton"), font);
		settingsButton.addListener(listenerGenerator.createSettingsButtonListener());
		settingsButton.setPosition(GameConstants.screenWidth - 2 * GameConstants.tileSize, GameConstants.screenHeight - 100);
		stage.addActor(settingsButton);

		TextButton sellButton = buttonGenerator.createButton(miscAtlas.createSprite("sellTowerButton"), font);
		sellButton.addListener(listenerGenerator.createSellButtonListener());
		sellButton.setPosition(GameConstants.screenWidth - 3 * GameConstants.tileSize, GameConstants.screenHeight - 100);
		stage.addActor(sellButton);

		TextButton upgradeButton = buttonGenerator.createButton(miscAtlas.createSprite("upgradeTowerButton"), font);
		upgradeButton.addListener(listenerGenerator.createUpgradeButtonListener());
		upgradeButton.setPosition(GameConstants.screenWidth - 4 * GameConstants.tileSize, GameConstants.screenHeight - 100);
		stage.addActor(upgradeButton);

		for (int i = 0; i < towerKeys.size(); i++)
		{
			if (!towerInfo.get(towerKeys.get(i)).buildable)
			{
				towerKeys.remove(i);
				i--;
				continue;
			}

			TextButton eachTowerButton = buttonGenerator.createButton(towersAtlas.createSprite(towerInfo.get(towerKeys.get(i)).towerTexture), font);
			final String currentKey = towerKeys.get(i);
			eachTowerButton.addListener(listenerGenerator.createTowerButtonListener(currentKey));
			eachTowerButton.setPosition(10 + 10 * i + 64 * i, GameConstants.screenHeight - 100);
			stage.addActor(eachTowerButton);

			Label towerCostLabel = new Label("10", labelStyle);
			towerCostLabel.setText("" + towerInfo.get(towerKeys.get(i)).buildCost);
			towerCostLabel.setVisible(true);
			towerCostLabel.setPosition(55 + 10 * i + 64 * i, GameConstants.screenHeight - 125);
			stage.addActor(towerCostLabel);
			towerCostLabels.add(towerCostLabel);
		}

		livesButton = buttonGenerator.createButton(miscAtlas.createSprite("heart"), font, "" + GameConstants.startLives);
		livesButton.setPosition(10 * GameConstants.tileSize, GameConstants.screenHeight - 100);
		stage.addActor(livesButton);

		goldButton = buttonGenerator.createButton(miscAtlas.createSprite("gold"), font, "        " + GameConstants.startGold);
		goldButton.setPosition(12 * GameConstants.tileSize, GameConstants.screenHeight - 100);
		stage.addActor(goldButton);
	}
	
	public void updateYellowBoxPosition()
	{
		if (targetYellowBoxActor != null)
		{
			float x = targetYellowBoxActor.getX() + targetYellowBoxActor.getWidth() / 2 - yellowBox.getWidth() / 2;
			float y = targetYellowBoxActor.getY() + targetYellowBoxActor.getHeight();
			if (y >= GameConstants.screenHeight - yellowBox.getHeight())
			{
				y = targetYellowBoxActor.getY() - yellowBox.getHeight();
			}
			yellowBox.setPosition(x, y);
			yellowBoxLabel.setPosition(x + yellowBoxXPadding, y + (yellowBox.getHeight() - yellowBoxLines * font.getBounds(yellowBoxLabel.getText()).height));
		}
	}
	public void fadeInYellowBox(ExtendedActor targetActor, List<String> strings)
	{
		yellowBoxLines = strings.size();
		int height = 2 * yellowBoxYPadding;
		int width = 0;
		yellowBoxLabel.setText("");
		for (String s : strings)
		{
			height += 2 * (int) font.getBounds(s).height;
			yellowBoxLabel.setText(yellowBoxLabel.getText() + s + "\n");
			if (font.getBounds(s).width > width)
				width = (int) font.getBounds(s).width;
		}
		width += 2 * yellowBoxXPadding;
		yellowBox.setSize(width, height);
		targetYellowBoxActor = targetActor;
		yellowBoxGroup.setVisible(true);
		yellowBoxGroup.setColor(0, 0, 0, 60);
		yellowBoxGroup.setZIndex(1000); // Random high value, to keep it above
										// anything.
	}

	public void updateConsole() // This must be called after console strings
	// are changed for visually reflecting
	// changes.
	{
		consoleLines = consoleStrings.size();
		int height = 2 * consoleLinesPadding;
		int width = 0;
		consoleLabel.setText("");
		for (String s : consoleStrings)
		{
			height += 2 * (int) font.getBounds(s).height;
			consoleLabel.setText(consoleLabel.getText() + s + "\n");
			if (font.getBounds(s).width > width)
				width = (int) font.getBounds(s).width;
		}
		width += 2 * consoleLinesPadding;
		consoleBackground.setSize(width, height);
		consoleGroup.setZIndex(1001); // Random high value, to keep it
											// above
		// anything.
		int consoleX = 0;
		int consoleY = 0;
		consoleBackground.setPosition(consoleX, consoleY);
		consoleLabel
				.setPosition(
						consoleX + consoleLinesPadding,
						consoleY
								+ (height - consoleLines
										* font.getBounds(consoleLabel
												.getText()).height));
	}

	public void updateConsoleState(boolean goNextState, Variables oldVariables, Variables newVariables, ThinkTankInfo thinkTankInfo)
	{
		consoleStrings.clear();
		if (goNextState)
			consoleStates++;
		switch (consoleStates)
		{
			case 1:
				consoleStrings.add("x: " + newVariables.x);
				consoleStrings.add("y: " + newVariables.y);
				consoleStrings.add("z: " + newVariables.z);
				consoleStrings.add("old x: " + oldVariables.x);
				consoleStrings.add("old y: " + oldVariables.y);
				consoleStrings.add("old z: " + oldVariables.z);
				consoleStrings.add("diff x: " + (newVariables.x - oldVariables.x));
				consoleStrings.add("diff y: " + (newVariables.y - oldVariables.y));
				consoleStrings.add("diff z: " + (newVariables.z - oldVariables.z));
				consoleStrings.add("max jump distance: " + thinkTankInfo.maxJumpDistance);
				consoleGroup.setVisible(true);
				break;
			case 2:
				consoleStrings.add("last metric: " + thinkTankInfo.lastMetric);
				consoleStrings.add("current metric: " + thinkTankInfo.currentMetric);
				consoleStrings.add("challenger metric: " + thinkTankInfo.challengerMetric);
				consoleStrings.add("gameLengthMultiplier: " + thinkTankInfo.gameLengthMultiplier);

				consoleGroup.setVisible(true);
				break;
			default:
				consoleStates = 0;
				break;
		}
		if (consoleStates > 0)
			consoleGroup.setVisible(true);
		else
			consoleGroup.setVisible(false);
		updateConsole();
	}

	public void fadeOutYellowBox()
	{
		targetYellowBoxActor = null;
		yellowBoxGroup.setVisible(false);
	}
}
