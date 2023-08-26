package com.igorcrevar.goingunder.collision;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.igorcrevar.goingunder.GameData;
import com.igorcrevar.goingunder.objects.Player;
import com.igorcrevar.goingunder.objects.obstacles.ObstacleObject;

public class CollisionResolver {
	private BoundingBox[] obstacleBoundingBoxes = new BoundingBox[20];
	private int currentNumberOfObstacleBBs = 0;
	
	private BoundingBox obstacleBoundingBox = new BoundingBox();
	//private BoundingBox playerBoundingBox = new BoundingBox(0.0282f, -0.161f, 0.752f, 0.252f, 0.0f); 
	private BoundingBox playerBoundingBox = new BoundingBox(0.0f, -0.25f, 0.92f, 0.25f, 0.0f);
	private BoundingSphere playerBoundingSphere = new BoundingSphere(0.0f, 0.08f, 0.3f);
	private BoundingSphere playerBoundingSphere2 = new BoundingSphere(0.0f, -0.3f, 0.2f);
	
	public CollisionResolver() {
		for (int i = 0; i < obstacleBoundingBoxes.length; ++i) {
			obstacleBoundingBoxes[i] = new BoundingBox();
		}
	}
	
	public void draw(ShapeRenderer shapeRenderer, BoundingBox aabb, GameData gameData) {
		Vector2[] t = aabb.getVertices();
		shapeRenderer.setColor(1, 1, 1, 1);
		for (int i = 0; i < 4; ++i) {
			Vector2 a = t[i];
			Vector2 b = t[(i + 1) % 4];
			shapeRenderer.line(a.x, a.y  - gameData.CameraYPosition, b.x, b.y - gameData.CameraYPosition);
		}		
	}	
	public void draw(ShapeRenderer shapeRenderer, BoundingSphere aabb, GameData gameData) {
		shapeRenderer.setColor(1, 1, 1, 1);
		shapeRenderer.circle(aabb.getCenter().x, aabb.getCenter().y - gameData.CameraYPosition, aabb.getRadius(), 60);
	}
	public void draw(ShapeRenderer shapeRenderer, GameData gameData) {
		draw(shapeRenderer, playerBoundingBox, gameData);
		draw(shapeRenderer, playerBoundingSphere, gameData);
		draw(shapeRenderer, playerBoundingSphere2, gameData);
		for (int i = 0; i < currentNumberOfObstacleBBs; ++i) {
			draw(shapeRenderer, obstacleBoundingBoxes[i], gameData);
		}
	}
	
	public boolean detect(Player player, ArrayList<ObstacleObject> obstacles) {
		// update bounding boxes
		float angle = (float)(player.getAngle() / 180.f * Math.PI);
		playerBoundingBox.update(player.getX(), player.getY(), angle);
		playerBoundingSphere.update(player.getX(), player.getY(), angle);
		playerBoundingSphere2.update(player.getX(), player.getY(), angle);
		
		// just for debug
		/*currentNumberOfObstacleBBs = 0;
		for (ObstacleObject oo : obstacles) {
			for (int i = 0; i < oo.getPartsCount(); ++i) {
				Rectangle r = oo.getBoundRectangle(i);
				obstacleBoundingBoxes[currentNumberOfObstacleBBs++].populateFromRectangle(r);
			}
		}*/
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
