package com.me.mygdxgame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MyGdxGame implements ApplicationListener {
	
	HashMap<Float, Enemy> enemyWave = new HashMap<Float, Enemy>();
    List<Float> waveTime = new ArrayList<Float>();
	
	List<Enemy> enemies = new ArrayList<Enemy>();
	List<Tower> towers = new ArrayList<Tower>();
	
	Stage stage;
	ExtendedActor actor;
	
	Map map;	
	TextureAtlas mapTilesAtlas;	// Map tiles
	TextureAtlas enemiesAtlas;	// Enemies
	TextureAtlas miscAtlas;		// Various small stuff like bullets, health bar, sell and upgrade buttons
	TextureAtlas towersAtlas;	// Towers
	
	SpriteBatch spriteBatch;
	
	BitmapFont font;
	
	String buildingTower = "";
	Sprite buildingTowerSprite = null;
	
	boolean building = false;
	
	float totalTime = 0;
	
	double timer = 0;
	int uC=0;
	int uT=0;
	
	boolean wasTouched = true;
	Vector2 touchedTile = new Vector2(0,0);
	
	@Override
	public void create() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		//Gdx.graphics.setDisplayMode((int)w, (int)h, true);
		spriteBatch = new SpriteBatch();
		
		font = new BitmapFont();
		
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		
		Texture text = new Texture(Gdx.files.internal("data/libgdx.png"));
		text.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		Sprite sprite = new Sprite(text);
		sprite.setPosition(5, 5);


		mapTilesAtlas = new TextureAtlas(Gdx.files.internal("images/mapTiles.atlas"));
		enemiesAtlas = new TextureAtlas(Gdx.files.internal("images/enemies.atlas"));
		miscAtlas = new TextureAtlas(Gdx.files.internal("images/misc.atlas"));
		towersAtlas = new TextureAtlas(Gdx.files.internal("images/towers.atlas"));
		
		map = new Map(mapTilesAtlas);
		try
		{
			map.loadMap(Gdx.files.getLocalStoragePath() + ".//bin//Maps/map.txt");
			System.out.println("Create done.");
		}
		catch(IOException ioe)
		{
			System.out.println(ioe.getMessage());
			// Don't care for now
		}

		buildTower("arrowTower", new Vector2(1,1));
		createWave();
		
		// UI Creation
		Table uiTable = new Table();
		uiTable.setSize(GameConstants.screenWidth, 128);
		uiTable.setPosition(0, GameConstants.screenHeight-128);
		uiTable.setColor(Color.BLACK);
		TextButton.TextButtonStyle arrowTowerButtonStyle = new TextButton.TextButtonStyle();
		TextureRegion upStyle = new TextureRegion(towersAtlas.createSprite("arrowTower"));
		TextureRegion downStyle = new TextureRegion(towersAtlas.createSprite("arrowTower"));
		arrowTowerButtonStyle.font = font;
		arrowTowerButtonStyle.up = new TextureRegionDrawable(upStyle);
		arrowTowerButtonStyle.down = new TextureRegionDrawable(downStyle);
		TextButton arrowTowerButton = new TextButton("test", arrowTowerButtonStyle);
		arrowTowerButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				building = true;
				buildingTower = "arrowTower";
				buildingTowerSprite = towersAtlas.createSprite("arrowTower");
				return true;
			}
		});
		uiTable.add(arrowTowerButton);
		stage.addActor(uiTable);
		// -----------
		
	}

	@Override
	public void dispose() {
        stage.dispose();
	}

	@Override
	public void render() {	
        Gdx.gl.glClearColor(0, 0,255, 1);	
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        
        totalTime += Gdx.graphics.getDeltaTime();
        
        /* Remove commenting here for FPS
        timer += Gdx.graphics.getDeltaTime();
        uC++;
        if(timer >= 1)
        {
        	System.out.println(uC);
        	uC = 0;
        	timer = 0;
        }*/
        handleInput();
        checkWave(totalTime);
        if(enemies.size() > 0)
        {
        	for(int i = 0; i < towers.size(); i++)
        	{
        		towers.get(i).calculateTarget(Gdx.graphics.getDeltaTime(), enemies, null);
        	}
        }
		
        stage.act(Gdx.graphics.getDeltaTime());
        
        spriteBatch.begin();
        map.draw(spriteBatch);
        if(building)
        {
        	spriteBatch.draw(buildingTowerSprite, touchedTile.x*64, touchedTile.y*64);
        }
        spriteBatch.end();
        stage.draw();
        
	}

	@Override
	public void resize(int width, int height) {
        stage.setViewport(width, height, true);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
	
	private void checkWave(float totalTime)
	{
		if(waveTime.size() > 0)
		{
			if (waveTime.get(0) <= totalTime) {
				stage.addActor(enemyWave.get(waveTime.get(0)));
				System.out.println("Spawned enemy: " + waveTime.get(0) + " at " + totalTime + " Size: " + stage.getActors().size);
				enemies.add(enemyWave.get(waveTime.get(0)));
				enemyWave.remove(waveTime.get(0));
				waveTime.remove(0);
			}
		}
	}
	
	private void createWave()
	{
		addEnemyToWave(0.5f, createEnemy("testEnemy"));
		addEnemyToWave(10.5f, createEnemy("fastEnemy"));
		addEnemyToWave(5.5f, createEnemy("toughEnemy"));
		Collections.sort(waveTime);
	}
	
	private void addEnemyToWave(float time, Enemy enemy)
	{
		enemyWave.put(time, enemy);
		waveTime.add(time);
	}
	
	// Eventually take an enemyInfo id, and create appropriate.
	private Enemy createEnemy(String spriteString)
	{
		return new Enemy(map.startPoint, map.directions, enemiesAtlas.createSprite(spriteString));
	}
	
	public void buildTower(String towerID, Vector2 tilePosition)
	{
		Tower t = createTower(towerID, tilePosition);
		stage.addActor(t);
		towers.add(t);
	}
	
	// Eventually take a towerInfo id, and create appropriate.
	private Tower createTower(String towerID, Vector2 tilePosition)
	{
		Tower t = new Tower(towersAtlas.createSprite(towerID), miscAtlas.createSprite("blackBullet"));
		t.setPosition(tilePosition.x*GameConstants.tileSize, tilePosition.y * GameConstants.tileSize);
		return t;
	}
	
	private void handleInput()
	{
		touchedTile = new Vector2((float)Math.floor((Gdx.input.getX()/GameConstants.tileSize)), (float)Math.floor(((stage.getHeight()-Gdx.input.getY())/GameConstants.tileSize)));
		if(wasTouched && !Gdx.input.isTouched())
		{
			if(building)
			{
				if(touchedTile.x<=20 && touchedTile.y<=10)
					buildTower(buildingTower, touchedTile);
				building = false;
			}
		}
		if(Gdx.input.isTouched())
		{
			wasTouched = true;
		}
	}
}
