package com.igorcrevar.goingunder.collision;

import com.badlogic.gdx.math.Vector2;

public class BoundingCircle {
	private final float centerX;
	private final float centerY;
	private final float radius;
	
	private final Vector2 tCenter = new Vector2();
	
	public BoundingCircle(float centerX, float centerY, float r) {
		this.centerX = centerX;
		this.centerY = centerY;
		this.radius = r;
	}
	
	public float getRadius() {
		return radius;
	}

	/**
	 * Returns current center of sphere (after update call)
	 * @return Vector2
	 */
	public Vector2 getCenter() {
		return tCenter;
	}
	
	public void update(float x, float y, float angle) {
		tCenter.set(centerX, centerY);
		if (angle != 0.0f) {
			tCenter.rotateRad(angle);
		}
		
		tCenter.x += x;
		tCenter.y += y;
	}
}
