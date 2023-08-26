package com.igorcrevar.goingunder.collision;

import com.badlogic.gdx.math.Vector2;

public class BoundingSphere {
	private float centerX;
	private float centerY;
	private float radius;
	
	private float ownerAngle;
	private Vector2 tCenter = new Vector2();
	
	public BoundingSphere(float centerX, float centerY, float r) {
		this.ownerAngle = 0.0f;
		this.centerX = centerX;
		this.centerY = centerY;
		this.radius = r;
	}
	
	public float getRadius() {
		return radius;
	}
	
	public float getOwnerAngle() {
		return ownerAngle;
	}
	
	public void setOwnerAngle(float v) {
		ownerAngle = v;
	}
	
	/**
	 * Returns current center of sphere (after update call)
	 * @return Vector2
	 */
	public Vector2 getCenter() {
		return tCenter;
	}
	
	public void update(float x, float y, float angle) {
		this.ownerAngle = angle;
		tCenter.set(centerX, centerY);
		if (angle != 0.0f) {
			tCenter.rotateRad(angle);
		}
		
		tCenter.x += x;
		tCenter.y += y;
	}
}
