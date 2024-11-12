package com.igorcrevar.goingunder.collision;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.igorcrevar.goingunder.GameData;
import com.igorcrevar.goingunder.objects.Player;
import com.igorcrevar.goingunder.objects.obstacles.ObstacleObject;

public class CollisionResolver {
	private final BoundingBox[] obstacleBoundingBoxes = new BoundingBox[20];
	private int currentNumberOfObstacleBBs = 0;
	
	private final BoundingBox obstacleBoundingBox = new BoundingBox();
	//private final BoundingBox playerBoundingBox = new BoundingBox(0.0282f, -0.161f, 0.752f, 0.252f, 0.0f);
	private final BoundingBox playerBoundingBox = new BoundingBox(0.0f, -0.25f, 0.92f, 0.25f, 0.0f);
	private final BoundingCircle playerBoundingSphere = new BoundingCircle(0.0f, 0.08f, 0.3f);
	private final BoundingCircle playerBoundingSphere2 = new BoundingCircle(0.0f, -0.3f, 0.2f);
	
	public CollisionResolver() {
		for (int i = 0; i < obstacleBoundingBoxes.length; ++i) {
			obstacleBoundingBoxes[i] = new BoundingBox();
		}
	}
	
	private void draw(ShapeRenderer shapeRenderer, BoundingBox aabb, float cameraY) {
		Vector2[] t = aabb.getVertices();
		shapeRenderer.setColor(1, 1, 1, 1);
		for (int i = 0; i < 4; ++i) {
			Vector2 a = t[i];
			Vector2 b = t[(i + 1) & 3];
			shapeRenderer.line(a.x, a.y - cameraY, b.x, b.y - cameraY);
		}		
	}

	private void draw(ShapeRenderer shapeRenderer, BoundingCircle aabb, float cameraY) {
		shapeRenderer.setColor(1, 1, 1, 1);
		shapeRenderer.circle(aabb.getCenter().x, aabb.getCenter().y - cameraY, aabb.getRadius(), 60);
	}

	public void draw(ShapeRenderer shapeRenderer, GameData gameData) {
		float cameraY = gameData.getCameraYCenter();
		shapeRenderer.begin(ShapeType.Line);
		draw(shapeRenderer, playerBoundingBox, cameraY);
		draw(shapeRenderer, playerBoundingSphere, cameraY);
		draw(shapeRenderer, playerBoundingSphere2, cameraY);
		for (int i = 0; i < currentNumberOfObstacleBBs; ++i) {
			draw(shapeRenderer, obstacleBoundingBoxes[i], cameraY);
		}
		shapeRenderer.end();
	}
	
	public boolean detect(Player player, ArrayList<ObstacleObject> obstacles, boolean drawCollisions) {
		// update bounding boxes
		float angle = (float)(player.getAngle() / 180.f * Math.PI);
		playerBoundingBox.update(player.getX(), player.getY(), angle);
		playerBoundingSphere.update(player.getX(), player.getY(), angle);
		playerBoundingSphere2.update(player.getX(), player.getY(), angle);
		
		// just for debug
		if (drawCollisions) {
			currentNumberOfObstacleBBs = 0;
			for (ObstacleObject oo : obstacles) {
				for (int i = 0; i < oo.getPartsCount(); ++i) {
					Rectangle r = oo.getBoundRectangle(i);
					obstacleBoundingBoxes[currentNumberOfObstacleBBs++].populateFromRectangle(r);
				}
			}
		}
		// end just for debug
		
		Rectangle playerRect = player.getBoundingRectangle();
		for (int i = obstacles.size() - 1; i >= 0; --i) {
			ObstacleObject obstacle = obstacles.get(i);
			
			// detect intersection
			for (int j = 0; j < obstacle.getPartsCount(); ++j) {
				Rectangle r = obstacle.getBoundRectangle(j);
				if (r.overlaps(playerRect)) {
					obstacleBoundingBox.populateFromRectangle(r);
					return CollisionHelper.intersect(playerBoundingBox, obstacleBoundingBox) ||
						   CollisionHelper.intersect(playerBoundingSphere, obstacleBoundingBox) ||
						   CollisionHelper.intersect(playerBoundingSphere2, obstacleBoundingBox);
				}
			}
		}
		
		return false;
	}
}
