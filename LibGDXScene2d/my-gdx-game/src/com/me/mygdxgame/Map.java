package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.xml.ws.Endpoint;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;

public class Map
{

	boolean mapDone = false;

	public static int mapWidth = 20; // No getUpperBounds(dimension) for
										// multidimensional arrays in java.
	public static int mapHeight = 10;

	public int[][] getMap()
	{
		return map;
	}

	int[][] map;
	MapTile[][] mapActors;

	public MapTile getMapTileActor(int x, int y)
	{
		if (x < 20 && x >= 0 && y < 10 && y >= 0)
			return mapActors[x][y];
		else
			return null;
	}

	public ArrayList<Direction> getDirections()
	{
		return directions;
	}

	ArrayList<Direction> directions = new ArrayList<Direction>();

	Vector2 startPoint = new Vector2(-1, 0);

	HashMap<Integer, Sprite> textures = new HashMap<Integer, Sprite>();
	ArrayList<Integer> pathTiles = new ArrayList<Integer>();
	ArrayList<Integer> unbuildableTiles = new ArrayList<Integer>();
	HashMap<String, Integer> texturesByName = new HashMap<String, Integer>();
	TextureAtlas mapTilesAtlas;

	ArrayList<MapNode> mapNodes = new ArrayList<MapNode>();
	MapNode endNode;

	public Map(TextureAtlas mapTiles)
	{
		this.mapTilesAtlas = mapTiles;
		mapHeight *= 2;
		mapWidth *= 2;
		GameConstants.tileSize /= 2;
		map = new int[mapWidth][mapHeight];
		mapActors = new MapTile[mapWidth][mapHeight];
	}

	private void addSprite(int id, Sprite sprite)
	{
		textures.put(id, sprite);
	}

	public void findStartPoint()
	{
		mapDone = false;
		for (int y = 0; y < mapHeight; y++)
		{
			if (pathTiles.contains(map[0][y]))
			{
				startPoint.y = y;
			}
		}
	}

	List<List<Direction>> directionsLister = new ArrayList<List<Direction>>();
	Random rand = new Random();

	public List<Direction> getDirectionList()
	{
		List<Direction> tempDirList = new ArrayList<Direction>();
		tempDirList.add(Direction.Left);
		int x = 0;
		int y = (int) startPoint.y;
		while (x < mapWidth)
		{
			System.out.println(x + ":" + y);
			if (mapActors[x][y].possibleDirections.size() > 0)
			{
				tempDirList.add(mapActors[x][y].possibleDirections.get(rand.nextInt(mapActors[x][y].possibleDirections.size())));
				System.out.println("Changed dir!");
			}
			// If is lager than 0
			Direction lastDir = tempDirList.get(tempDirList.size() - 1);
			if (lastDir == Direction.Right)
			{
				x++;
				System.out.println("x++");
			} else if (lastDir == Direction.Left)
			{
				x--;
				System.out.println("x--");
			} else if (lastDir == Direction.Up)
			{
				y--;
				System.out.println("y++");
			} else if (lastDir == Direction.Down)
			{
				y++;
				System.out.println("y--");
			} else
			{
				x++;
				System.out.println("rand x++");
			}
		}

		return tempDirList;
	}

	public void generateDirections()
	{
		for (int x = 0; x < mapWidth; x++)
		{
			for (int y = 0; y < mapHeight; y++)
			{
				if (pathTiles.contains(map[x][y]))
				{
					for (Vector2 p : nextMultipleSteps(new Vector2(x, y)))
					{
						mapActors[x][y].possibleDirections.add(getDirection(new Vector2(x, y), p));
					}
				}
			}
		}
	}

	int nodeCounter = 0;
	List<Vector2> checkedPoints = new ArrayList<Vector2>();

	public void findNodeRecursively(Vector2 currentPosition)
	{
		System.out.println("Checking " + currentPosition.x + " , " + currentPosition.y);
		checkedPoints.add(currentPosition);
		List<Vector2> nextSteps = nextMultipleSteps(currentPosition);

		if (nextSteps.size() > 2)
		{
			mapNodes.add(new MapNode(nodeCounter, (int) currentPosition.x, (int) currentPosition.y));
			nodeCounter++;
			System.out.println("New Node!");
		}

		for (Vector2 point : nextSteps)
		{
			if (!checkedPoints.contains(point) && point.x < mapWidth)
			{
				findNodeRecursively(point);
				directions.add(getDirection(currentPosition, point));
			}
			if (point.x >= mapWidth)
				endNode = new MapNode(nodeCounter, (int) point.x, (int) point.y);
		}
	}

	private Direction getDirection(Vector2 startPoint, Vector2 endPoint)
	{
		if (startPoint.x < endPoint.x)
			return Direction.Right;
		if (startPoint.y < endPoint.y)
			return Direction.Down;
		if (startPoint.x > endPoint.x)
			return Direction.Left;
		if (startPoint.y > endPoint.y)
			return Direction.Up;

		return Direction.None;
	}

	private List<Vector2> nextMultipleSteps(Vector2 currentTile)
	{
		Vector2 newTile = new Vector2(currentTile.x, currentTile.y);
		List<Vector2> availableTiles = new ArrayList<Vector2>();

		boolean done = false;
		if (currentTile.x + 1 >= mapWidth)
		{
			System.out.println("Done.");
			availableTiles.add(new Vector2(newTile.x + 1, newTile.y));
			done = true;
		}

		System.out.println("x-:" + (currentTile.x - 1));
		if (!done && currentTile.x > 0 && pathTiles.contains(map[(int) currentTile.x - 1][(int) currentTile.y]))
		{
			availableTiles.add(new Vector2(newTile.x - 1, newTile.y));
		}
		System.out.println("x+:" + (currentTile.x + 1));
		if (!done && currentTile.x < mapWidth - 1 && pathTiles.contains(map[(int) currentTile.x + 1][(int) currentTile.y]))
		{
			availableTiles.add(new Vector2(newTile.x + 1, newTile.y));
		}
		System.out.println("y-: " + (currentTile.y - 1));
		if (!done && currentTile.y > 0 && currentTile.x > 0 && pathTiles.contains(map[(int) currentTile.x][(int) currentTile.y - 1]))
		{
			availableTiles.add(new Vector2(newTile.x, newTile.y - 1));
		}
		System.out.println("y+: " + currentTile.y + 1);
		if (!done && currentTile.y < (mapHeight - 1) && currentTile.x > 0 && pathTiles.contains(map[(int) currentTile.x][(int) currentTile.y + 1]))
		{
			availableTiles.add(new Vector2(newTile.x, newTile.y + 1));
		}

		return availableTiles;
	}

	private Vector2 nextStep(Vector2 currentTile, Direction lastDirection)
	{
		Vector2 newTile = new Vector2(currentTile.x, currentTile.y);
		boolean incremented = false;
		if (!incremented && currentTile.x > 0 && pathTiles.contains(map[(int) currentTile.x - 1][(int) currentTile.y]))
		{
			if (lastDirection != Direction.Right)
			{
				directions.add(Direction.Left);
				newTile.x -= 1;
				incremented = true;
			}
		}
		if (!incremented && currentTile.x < mapWidth - 1 && pathTiles.contains(map[(int) currentTile.x + 1][(int) currentTile.y]))
		{
			if (lastDirection != Direction.Left)
			{
				directions.add(Direction.Right);
				newTile.x += 1;
				incremented = true;
			}
		}
		if (!incremented && currentTile.y > 0 && pathTiles.contains(map[(int) currentTile.x][(int) currentTile.y - 1]))
		{
			if (lastDirection != Direction.Down)
			{
				directions.add(Direction.Up);
				newTile.y -= 1;
				incremented = true;
			}
		}
		if (!incremented && currentTile.y < (mapHeight - 1) && pathTiles.contains(map[(int) currentTile.x][(int) currentTile.y + 1]))
		{
			if (lastDirection != Direction.Up)
			{
				directions.add(Direction.Down);
				newTile.y += 1;
				incremented = true;
			}
		}

		if (!incremented && currentTile.x + 1 >= mapWidth) // given our decided
															// layout that maps
															// end to the right
		{
			directions.add(Direction.Right);
			mapDone = true;
			newTile.x++;
		}

		return newTile;
	}

	public boolean canBuild(int x, int y)
	{
		if (x < mapWidth && x >= 0 && y < mapHeight && y >= 0)
			return (!pathTiles.contains(map[x][y]) && !unbuildableTiles.contains(map[x][y]));
		else
			return false;
	}

	public Group loadMap(FileHandle handle)
	{
		Group actorGroup = new Group();
		for (int x = 0; x < mapWidth; x++)
		{
			for (int y = 0; y < mapHeight; y++)
			{
				map[x][y] = 0;
				mapActors[x][y] = null;
			}
		}

		List<String> fileContent = GameConstants.readRawTextFile(handle);
		System.out.println("Loaded file");
		for (int x = 0; x < fileContent.size(); x++)
		{
			String s = fileContent.get(x);
			String[] split = s.split(":");
			if (split[0].equals("t"))
			{
				textures.put(Integer.parseInt(split[1]), mapTilesAtlas.createSprite(split[2]));
				texturesByName.put(split[2], Integer.parseInt(split[1]));
			} else if (split[0].equals("p"))
			{
				pathTiles.add(Integer.parseInt(split[1]));

			} else if (split[0].equals("u"))
			{
				unbuildableTiles.add(Integer.parseInt(split[1]));

			}
		}
		actorGroup.clear();
		actorGroup = generateNewMap();
		System.out.println("Loaded map and generated directions");
		return actorGroup;
	}

	public Group regenerateMap()
	{
		return generateNewMap();
	}

	public Group generateNewMap()
	{
		Group actorGroup = new Group();
		for (int x = 0; x < mapWidth; x++)
		{
			for (int y = 0; y < mapHeight; y++)
			{
				map[x][y] = 0;
				mapActors[x][y] = null;
			}
		}
		while (!eatPath())
		{
			for (int x = 0; x < mapWidth; x++)
			{
				for (int y = 0; y < mapHeight; y++)
				{
					map[x][y] = 0;
					mapActors[x][y] = null;
				}
			}
		}
		for (int y = 0; y < mapHeight; y++)
		{
			for (int x = 0; x < mapWidth; x++)
			{
				MapTile mapTile = new MapTile(textures.get(map[x][y]), GameConstants.tileSize * x, GameConstants.tileSize * y);
				mapTile.setSize(GameConstants.tileSize, GameConstants.tileSize);
				actorGroup.addActor(mapTile);
				mapActors[x][y] = mapTile;
			}
		}

		checkedPoints.clear();
		nodeCounter = 0;
		findStartPoint();
		ArrayList<Direction> tempDirections = new ArrayList<Direction>();
		for (int i = directions.size() - 1; i >= 0; i--)
		{
			tempDirections.add(directions.get(i));
		}
		//directions = tempDirections;

		return actorGroup;
	}

	// 0 left, 1 up, 2 right, 3 down
	public boolean eatPath()
	{
		Random rand = new Random();
		int yPos = rand.nextInt(mapHeight - 1);
		int xPos = 0;
		int textureInt = texturesByName.get("Horizontal");
		map[xPos][yPos] = textureInt;

		boolean done = false;
		int testTries = 0;
		directions.clear();
		directions.add(Direction.Right);
		while (!done)
		{
			double d = rand.nextDouble();
			if (xPos > 0 && d < 0.3)
			{
				yPos++;
				if (checkTileToEat(xPos, yPos, textureInt))
				{
					map[xPos][yPos] = textureInt;
					directions.add(Direction.Down);
					testTries = 0;
				} else
				{
					yPos--;
					testTries++;
				}
			} else if (xPos > 0 && d < 0.6)
			{
				yPos--;
				if (checkTileToEat(xPos, yPos, textureInt))
				{
					map[xPos][yPos] = textureInt;
					directions.add(Direction.Up);
					testTries = 0;
				} else
				{
					yPos++;
					testTries++;
				}
			} else if (xPos > 1 && yPos < mapHeight - 3 && yPos > 2 && d < 0.8)
			{
				xPos--;
				if (checkTileToEat(xPos, yPos, textureInt))
				{
					map[xPos][yPos] = textureInt;
					directions.add(Direction.Left);
					testTries = 0;
				} else
				{
					xPos++;
					testTries++;
				}
			} else
			{
				xPos++;
				if (xPos > mapWidth - 1)
				{
					done = true;
					directions.add(Direction.Right);
				}
				else if (checkTileToEat(xPos, yPos, textureInt))
				{
					map[xPos][yPos] = textureInt;
					directions.add(Direction.Right);
					testTries = 0;
				} else
				{
					xPos--;
					testTries++;
				}
			}
			if (xPos == mapWidth - 1)
			{
				done = true;
				directions.add(Direction.Right);
			}
			if (testTries > 10)
			{
				for (int y = 0; y < mapHeight; y++)
				{
					for (int x = 0; x < mapWidth; x++)
					{
						System.out.print(map[x][y]);
					}
					System.out.print("\n");
				}
				System.out.println("Fail!");
				return false;
			}
		}
		System.out.println("Great success!");
		for (int y = 0; y < mapHeight; y++)
		{
			for (int x = 0; x < mapWidth; x++)
			{
				System.out.print(map[x][y]);
			}
			System.out.print("\n");
		}
		return true;
	}

	public boolean checkTileToEat(int xPos, int yPos, int textureInt)
	{
		if (xPos <= mapWidth - 1 && yPos <= mapHeight - 1 && xPos >= 0 && yPos >= 0)
		{
			if (map[xPos][yPos] == textureInt)
			{
				return false;
			}
			int neighbourCounter = 0;
			if (xPos > 0 && map[xPos - 1][yPos] == textureInt)
				neighbourCounter++;
			if (xPos < mapWidth - 1 && map[xPos + 1][yPos] == textureInt)
				neighbourCounter++;
			if (yPos > 0 && map[xPos][yPos - 1] == textureInt)
				neighbourCounter++;
			if (yPos < mapHeight - 1 && map[xPos][yPos + 1] == textureInt)
				neighbourCounter++;

			if (neighbourCounter > 1)
				return false;
		} else
			return false;
		return true;
	}
}