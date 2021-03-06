package com.igorcrevar.goingunder;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.igorcrevar.goingunder.statemachine.IGeneratorStateMachine;

public abstract class AGameLevel {
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

	private IGeneratorStateMachine generatorStateMachine;
	
	public abstract void resolveObstacleTexture(Sprite part, GameManager gameManager);
	protected abstract IGeneratorStateMachine createGeneratorMachine();
	
	public void copyTo(GameData data) {
		data.EmptySpaceSizeInTheMiddle = emptySpaceSizeInTheMiddle;
		data.EmptySpaceSizeOnTheEnd = emptySpaceSizeOnTheEnd;
		data.ObstacleGeneratorDistance = obstacleGeneratorDistance;
		
		data.BoundariesBouncingFactor = boundariesBouncingFactor;
		data.Friction = friction;
		data.VelocityX = velocityX;
		data.VelocityY = velocityY;
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
