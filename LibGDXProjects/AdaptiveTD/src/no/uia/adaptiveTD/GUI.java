package no.uia.adaptiveTD;

import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class GUI {
	HashMap<GUIButton, TowerStats> towerButtons = new HashMap<GUIButton, TowerStats>();
	HashMap<String, TowerStats> towers = new HashMap<String, TowerStats>();
	
	public GUIButton sellTowerButton, upgradeTowerButton;
	
	public TowerStats selectedTower;
	public boolean building = false;
	
	Sprite background;
	
	Vector2 towerButtonPos;
	
	
}
