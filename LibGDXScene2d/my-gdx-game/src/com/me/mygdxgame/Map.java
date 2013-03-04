package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;

public class Map {

	boolean mapDone = false;
	
	public static final int mapWidth = 20;		// No getUpperBounds(dimension) for multidimensional arrays in java.
	public static final int mapHeight = 10;
	public int[][] getMap() {
		return map;
	}
	int[][] map = new int[mapWidth][mapHeight];
	MapTile[][] mapActors = new MapTile[mapWidth][mapHeight];
	
	public MapTile getMapTileActor(int x, int y)
	{
		if(x < 20 && x >= 0 && y < 10 && y >= 0)
			return mapActors[x][y];
		else 
			return null;
	}
	
	public ArrayList<Direction> getDirections() {
		return directions;
	}
	ArrayList<Direction> directions = new ArrayList<Direction>();
	
	Vector2 startPoint = new Vector2(-1, 0);
	
	HashMap<Integer, Sprite> textures = new HashMap<Integer, Sprite>();
	ArrayList<Integer> pathTiles = new ArrayList<Integer>();
	
	TextureAtlas mapTilesAtlas;
	
	public Map(TextureAtlas mapTiles)
	{
		this.mapTilesAtlas = mapTiles;
	}
	
	private void addSprite(int id, Sprite sprite)
	{
		textures.put(id, sprite);
	}
	
	private void generateDirections()
	{
		mapDone = false;
		for(int y=0; y<mapHeight; y++)
		{
			if(pathTiles.contains(map[0][y]))
			{
				startPoint.y = y;
			}
		}
		Vector2 currentPosition = new Vector2(startPoint.x, startPoint.y);
		while(!mapDone)
		{
			if(directions.size() > 0)
				currentPosition = nextStep(currentPosition, directions.get(directions.size()-1));
			else
				currentPosition = nextStep(currentPosition, Direction.Right);
		}
	}
	
	private Vector2 nextStep(Vector2 currentTile, Direction lastDirection)
	{
		Vector2 newTile = new Vector2(currentTile.x, currentTile.y);
		boolean incremented = false;
		if (!incremented && currentTile.x > 0 && pathTiles.contains(map[(int)currentTile.x - 1][(int)currentTile.y]))
        {
            if (lastDirection != Direction.Right)
            {
                directions.add(Direction.Left);
                newTile.x -= 1;
                incremented = true;
            }
        }
        if (!incremented && currentTile.x < mapWidth-1 && pathTiles.contains(map[(int)currentTile.x + 1][(int)currentTile.y]))
        {
            if (lastDirection != Direction.Left)
            {
                directions.add(Direction.Right);
                newTile.x += 1;
                incremented = true;
            }
        }
        if (!incremented && currentTile.y > 0 && pathTiles.contains(map[(int)currentTile.x][(int)currentTile.y - 1]))
        {
            if (lastDirection != Direction.Down)
            {
                directions.add(Direction.Up);
                newTile.y -= 1;
                incremented = true;
            }
        }
        if (!incremented && currentTile.y < (mapHeight-1) && pathTiles.contains(map[(int)currentTile.x][(int)currentTile.y + 1]))
        {
            if (lastDirection != Direction.Up)
            {
                directions.add(Direction.Down);
                newTile.y += 1;
                incremented = true;
            }
        }
		
        if (!incremented && currentTile.x + 1 >= mapWidth) // given our decided layout that maps end to the right
        {
            directions.add(Direction.Right);
            mapDone = true;
            newTile.x++;
        }

		return newTile;
	}
	
	public boolean canBuild(int x, int y)
	{
		if(x<20 && x >= 0 && y < 10 && y >= 0)
			return !pathTiles.contains(map[x][y]);
		else
			return false;
	}
	
	public Group loadMap(FileHandle handle)
    {
		Group actorGroup = new Group();
		System.out.println("Starting loadmap.");
        for (int x = 0; x < mapWidth; x++)
        {
            for (int y = 0; y < mapHeight; y++)
            {
                map[x][y] = 0;
                mapActors[x][y] = null;
            }
        }
        System.out.println("Done filling with zero.");
        
        List<String> fileContent = GameConstants.readRawTextFile(handle);
        int yCounter = 0;
        System.out.println("Loaded file");
        for(int x = 0; x<fileContent.size(); x++)
        {
        	String s = fileContent.get(x);
            String[] split = s.split(":");
            if(split[0].equals("t"))
            {
                textures.put(Integer.parseInt(split[1]), mapTilesAtlas.createSprite(split[2]));
            }
            else if(split[0].equals("p"))
            {
                pathTiles.add(Integer.parseInt(split[1]));
            	
            }
            else if(split[0].equals("m"))
            {
                for(int xCounter = 0; xCounter < mapWidth; xCounter++)
                {
                    map[xCounter][yCounter] = Integer.parseInt(split[xCounter+1]);
                    MapTile mapTile = new MapTile(textures.get(map[xCounter][yCounter]), GameConstants.tileSize*xCounter, GameConstants.tileSize*yCounter);
                    actorGroup.addActor(mapTile);
                    mapActors[xCounter][yCounter] = mapTile;
                }
                yCounter++;
            }
        }
        System.out.println("Done parsing file, generating directions");
        generateDirections();
        System.out.println("Loaded map and generated directions");
        return actorGroup;
    }

}