package com.igorcrevar.goingunder;

import com.badlogic.gdx.math.Vector3;
import com.igorcrevar.goingunder.statemachine.IGeneratorStateMachine;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

public abstract class AGameLevel {
	private static final float DefaultFishRandomProbability = 0.006f;

	// we use public fields instead to just make population of data for every level inside of abstract copyTo method
	// because I need to change efficiently level data while test mechanic
	public float boundariesBouncingFactor;	
	public float friction;
	public float velocityX;
	public float velocityY;
	public float obstacleGeneratorDistance;
	
	public float emptySpaceSizeInTheMiddle;
	public float emptySpaceSizeOnTheEnd;
	public int endOnScore;

	public float fishRandomProbability = DefaultFishRandomProbability;

	private IGeneratorStateMachine generatorStateMachine;
	
	public abstract AtlasRegion[] getTextures();

	public abstract float getEndPartSize();

	public abstract float getPartSize();

	public abstract Vector3 getTopColor();

	public abstract Vector3 getBottomColor();

	protected abstract IGeneratorStateMachine createGeneratorMachine();

	public void copyTo(GameData data) {
		data.EmptySpaceSizeInTheMiddle = emptySpaceSizeInTheMiddle;
		data.EmptySpaceSizeOnTheEnd = emptySpaceSizeOnTheEnd;
		data.ObstacleGeneratorDistance = obstacleGeneratorDistance;
		
		data.BoundariesBouncingFactor = boundariesBouncingFactor;
		data.Friction = friction;
		data.VelocityX = velocityX;
		data.VelocityY = velocityY;

		data.FishRandomProbability = fishRandomProbability;
	}
	
	public IGeneratorStateMachine getGeneratorStateMachine() {
		if (generatorStateMachine == null) {
			generatorStateMachine = createGeneratorMachine();
		}
		
		return generatorStateMachine;
	}
	
	public boolean isEndOfLevel(int score) {
		return score >= endOnScore && endOnScore > 0;
	}
}
