package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

public class Map {

	boolean mapDone = false;
	
	public static final int mapWidth = 20;		// No getUpperBounds(dimension) for multidimensional arrays in java.
	public static final int mapHeight = 10;
	public int[][] getMap() {
		return map;
	}
	int[][] map = new int[mapWidth][mapHeight];
	
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
		if(x > 19 || x < 0 || y > 9 || y < 0)
			return false;
		else
			return !pathTiles.contains(map[x][y]);
	}
	
	public void loadMap(String path) throws IOException
    {
		System.out.println("Starting loadmap.");
        for (int x = 0; x < mapWidth; x++)
        {
            for (int y = 0; y < mapHeight; y++)
            {
                map[x][y] = 0;
            }
        }
        System.out.println("Done filling with zero.");
        
        Path readPath = Paths.get(path);
        Charset ENCODING = StandardCharsets.UTF_8;
        List<String> fileContent = Files.readAllLines(readPath, ENCODING);
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
                }
                yCounter++;
            }
        }
        System.out.println("Done parsing file, generating directions");
        generateDirections();
        System.out.println("Loaded map and generated directions");
    }
	
	
	public void draw(SpriteBatch spriteBatch)
	{
		 for (int x = 0; x < mapWidth; x++)
         {
             for (int y = 0; y < mapHeight; y++)
             {
                 spriteBatch.draw(textures.get(map[x][y]), (x * GameConstants.tileSize), (y * GameConstants.tileSize));
             }
         }
	}

}