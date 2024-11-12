package com.igorcrevar.goingunder.collision;

import com.badlogic.gdx.math.Vector2;

public class CollisionHelper {
	/**
	 * Check if there is collision between circle and axis aligned bounding box
	 * @param circle circle
	 * @param aabb axis aligned bounding box
	 * @return true if intersection exists
	 */
	public static boolean intersect(BoundingCircle circle, BoundingBox aabb) {
		float circleR = circle.getRadius();
		Vector2 circleCenter = circle.getCenter();
		Vector2 rectCenter = aabb.getCenter();
		float rectWidth2 = aabb.getWidth() / 2.0f;
		float rectHeight2 = aabb.getHeight() / 2.0f;
		
		float circleDistanceX = Math.abs(circleCenter.x - rectCenter.x);
		float circleDistanceY = Math.abs(circleCenter.y - rectCenter.y);

	    if (circleDistanceX > rectWidth2 + circleR) {
	    	return false;
	    }
	    
	    if (circleDistanceY > rectHeight2 + circleR) { 
	    	return false; 
	    }

	    if (circleDistanceX <= rectWidth2) { 
	    	return true; 
	    }
	    
	    if (circleDistanceY <= rectHeight2) { 
	    	return true; 
	    }

	    float cornerDistance_sq = (circleDistanceX - rectWidth2) * (circleDistanceX - rectWidth2) +
	                          	  (circleDistanceY - rectHeight2) * (circleDistanceY - rectHeight2);

	    return cornerDistance_sq <= circleR * circleR;
	}
		
	/**
	 * Check if two ranges overlap. Range(min, max) is represented by Vector2(x, y)
	 * @param v1 vector
	 * @param v2 vector
	 * @return true if ranges do not overlap
	 */
	private static boolean areNotOverlap(Vector2 v1, Vector2 v2) {
		return v1.y < v2.x || v1.x > v2.y; 
	}
	
	/**
	 * Check intersection between two oriented bounding boxes. Separate axis theoreme
	 * @param obb1 first obb
	 * @param obb2 second obb
	 * @return true if there is collision between them
	 */
	public static boolean intersect(BoundingBox obb1, BoundingBox obb2) {
		Vector2[] axes1 = obb1.getAxes();
		Vector2[] axes2 = obb2.getAxes();
		// loop over the axes1
		for (Vector2 axis : axes1) {
			// project both shapes onto the axis
			Vector2 p1 = obb1.project(axis);
			Vector2 p2 = obb2.project(axis);
			// do the projections overlap?
			if (areNotOverlap(p1, p2)) {
				// then we can guarantee that the shapes do not overlap
				return false;
			}
		}
		// loop over the axes2
		for (Vector2 axis : axes2) {
			// project both shapes onto the axis
			Vector2 p1 = obb1.project(axis);
			Vector2 p2 = obb2.project(axis);
			// do the projections overlap?
			if (areNotOverlap(p1, p2)) {
				// then we can guarantee that the shapes do not overlap
				return false;
			}
		}
		// if we get here then we know that every axis had overlap on it
		// so we can guarantee an intersection
		return true;
	}
}
