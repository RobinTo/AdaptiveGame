package com.me.mygdxgame;

import java.io.IOException;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class MyGdxGame implements ApplicationListener {
	
	Stage stage;
	ExtendedActor actor;
	
	Map map;	
	TextureAtlas mapTilesAtlas;	// Map tiles
	TextureAtlas enemiesAtlas;	// Enemies
	TextureAtlas miscAtlas;		// Various small stuff like bullets, health bar, sell and upgrade buttons
	TextureAtlas towersAtlas;	// Towers
	
	SpriteBatch spriteBatch;
	Enemy e;
	Tower t;
	Missile m;
	
	double timer = 0;
	int uC=0;
	int uT=0;
	
	@Override
	public void create() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		spriteBatch = new SpriteBatch();
		
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
		e = new Enemy(map.startPoint, map.directions, enemiesAtlas.createSprite("testEnemy"));
		
		t = new Tower(towersAtlas.createSprite("arrowTower"));
		t.setPosition(64, 64);

		m = new Missile(miscAtlas.createSprite("blackBullet"), new Vector2(t.getX(), t.getY()), new Vector2(e.getX()+e.getWidth()/2, e.getY()+e.getHeight()/2));
		
		stage.addActor(t);
		stage.addActor(e);
		stage.addActor(m);
	}

	@Override
	public void dispose() {
        stage.dispose();
	}

	@Override
	public void render() {	
        Gdx.gl.glClearColor(0, 0,255, 1);	
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        
        timer += Gdx.graphics.getDeltaTime();
        uC++;
        if(timer >= 1)
        {
        	System.out.println(uC);
        	uC = 0;
        	timer = 0;
    		m = new Missile(miscAtlas.createSprite("blackBullet"), new Vector2(t.getX(), t.getY()), new Vector2(e.getX()+e.getWidth()/2, e.getY()+e.getHeight()/2));
    		stage.addActor(m);
        }
        
        t.calculateTarget(e);
        stage.act(Gdx.graphics.getDeltaTime());
        
        spriteBatch.begin();
        map.draw(spriteBatch);
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
}
