package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class HitDetector {

	public static synchronized List<Enemy> getEnemiesInCircle(List<Enemy> enemies, int originX, int originY, int radius)
	{
		List<Enemy> intersectingEnemies = new ArrayList<Enemy>();
		Circle circle = new Circle(originX, originY, radius);
		for (Enemy enemy : enemies)
		{
			Rectangle enemyRectangle = new Rectangle(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());
			if (Intersector.overlapCircleRectangle(circle, enemyRectangle))
				intersectingEnemies.add(enemy);
		}
		return intersectingEnemies;
	}
	
	public static synchronized List<Enemy> getEnemiesOnLine(List<Enemy> enemies, Vector2 startPoint, Vector2 endPoint)
	{
		List<Enemy> intersectingEnemies = new ArrayList<Enemy>();
		for (Enemy enemy : enemies)
		{
			if (Intersector.distanceLinePoint(startPoint, endPoint, new Vector2(enemy.getX(), enemy.getY())) <= enemy.getWidth())
				intersectingEnemies.add(enemy);
		}
		return intersectingEnemies;
	}
}
