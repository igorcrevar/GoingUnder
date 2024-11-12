package com.igorcrevar.goingunder.collision;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Oriented boundary box or AA boundary box
 *
 * @author crewce
 */
public class BoundingBox {
	private float centerX;
	private float centerY;
	private float width;
	private float height;
	private float originalAngle;

	private Vector2[] tVertices;
	private Vector2 tCenter;

	private Vector2[] axes;
	private final Vector2 tmpCachePoint = new Vector2();

	public BoundingBox() {
		this(0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
	}

	public BoundingBox(float centerX, float centerY, float width, float height, float originalAngle) {
		this.centerX = centerX;
		this.centerY = centerY;
		this.width = width;
		this.height = height;
		this.originalAngle = originalAngle;
		initVertices();
	}

	public void populateFromRectangle(Rectangle rect) {
		width = rect.getWidth();
		height = rect.getHeight();
		originalAngle = 0.0f;
		centerX = width / 2.0f;
		centerY = height / 2.0f;
		this.update(rect.x, rect.y, 0.0f);
	}

	private void initVertices() {
		tCenter = new Vector2();
		tVertices = new Vector2[4];
		for (int i = 0; i < tVertices.length; ++i) {
			tVertices[i] = new Vector2();
		}

		axes = new Vector2[4];
		for (int i = 0; i < axes.length; ++i) {
			axes[i] = new Vector2();
		}
	}

	public Vector2[] getVertices() {
		return tVertices;
	}

	public Vector2 getCenter() {
		return tCenter;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public void update(float x, float y, float ownerAngle) {
		// first calculate new current center
		tCenter.set(centerX, centerY);
		if (ownerAngle != 0.0f) {
			tCenter.rotateRad(ownerAngle);
		}

		tCenter.x += x;
		tCenter.y += y;

		// than calculate vertices
		float h2 = height / 2.0f;
		float w2 = width / 2.0f;
		tVertices[0].set(-w2, -h2);
		tVertices[1].set(w2, -h2);
		tVertices[2].set(w2, h2);
		tVertices[3].set(-w2, h2);

		// rotate vertices (original angle + new angle)
		// add center to each
		for (Vector2 tVertex : tVertices) {
			tVertex.rotateRad(originalAngle + ownerAngle);
			tVertex.x += tCenter.x;
			tVertex.y += tCenter.y;
		}
	}

	public Vector2[] getAxes() {
		// loop over the vertices
		for (int i = 0; i < tVertices.length; i++) {
			// get the current vertex
			tmpCachePoint.set(tVertices[i]);
			// get the next vertex
			int secondIndex = (i + 1) % tVertices.length;
			Vector2 p2 = tVertices[secondIndex];
			// subtract the two to get the edge vector
			Vector2 edge = tmpCachePoint.sub(p2);
			// get either perpendicular vector
			// (x, y) => (-y, x) or (y, -x)
			axes[i].set(-edge.y, edge.x).nor();
		}

		return axes;
	}

	public Vector2 project(Vector2 axis) {
		float min = axis.dot(tVertices[0]);
		float max = min;
		for (int i = 1; i < tVertices.length; i++) {
			// NOTE: the axis must be normalized to get accurate projections
			float p = axis.dot(tVertices[i]);
			if (p < min) {
				min = p;
			} else if (p > max) {
				max = p;
			}
		}

		tmpCachePoint.set(min, max);
		return tmpCachePoint;
	}
}
