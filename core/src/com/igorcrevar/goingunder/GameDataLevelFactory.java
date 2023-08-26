package com.igorcrevar.goingunder;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.igorcrevar.goingunder.statemachine.GeneratorStateMachine;
import com.igorcrevar.goingunder.statemachine.IGeneratorStateMachine;

public class GameDataLevelFactory {
	
	private static class GameLevel0 extends GameLevel1 {
		public GameLevel0() {
			super();
			friction = 3.5f;
			emptySpaceSizeInTheMiddle = 1.35f;
			emptySpaceSizeOnTheEnd = 1.31f;
			endOnScore = 4;
		}
	}
	
	private static class GameLevel1 extends AGameLevel {
		public GameLevel1() {
			friction = 3.5f;
			velocityX = 1.8f;
			velocityY = -2.4f;
			obstacleGeneratorDistance = 5.359f;
			
			emptySpaceSizeInTheMiddle = 1.22f;
			emptySpaceSizeOnTheEnd = 1.16f;
			boundariesBouncingFactor = 0.5f;
			endOnScore = 30;
		}

		@Override
		protected IGeneratorStateMachine createGeneratorMachine() {
			return new GeneratorStateMachine(true);
		}

		@Override
		public void resolveObstacleTexture(Sprite part, GameManager gameManager) {
			TextureAtlas atlas = gameManager.getTextureAtlas("game");
			if (part.getWidth() > 2.0f) {
				part.setRegion(atlas.findRegion("obstacle1"));
			} else {
				part.setRegion(atlas.findRegion("obstacle1_small"));
			}	
		}
	}
	
	private static class GameLevel2 extends AGameLevel {
		public GameLevel2() {
			friction = 3.5f;
			velocityX = 1.8f;
			velocityY = -2.5f;
			obstacleGeneratorDistance = 5.259f;
			
			emptySpaceSizeInTheMiddle = 1.22f;
			emptySpaceSizeOnTheEnd = 1.16f;
			boundariesBouncingFactor = 0.5f;
			endOnScore = 60;			
		}

		@Override
		protected IGeneratorStateMachine createGeneratorMachine() {
			return new GeneratorStateMachine(true);
		}

		@Override
		public void resolveObstacleTexture(Sprite part, GameManager gameManager) {
			part.setRegion(gameManager.getTextureAtlas("game").findRegion("obstacle2"));
		}
	}
	
	private static class GameLevel3 extends AGameLevel {
		public GameLevel3() {
			friction = 3.5f;
			velocityX = 1.8f;
			velocityY = -2.55f;
			obstacleGeneratorDistance = 5.259f;
			
			emptySpaceSizeInTheMiddle = 1.22f;
			emptySpaceSizeOnTheEnd = 1.16f;
			boundariesBouncingFactor = 0.5f;
			endOnScore = 90;			
		}

		@Override
		protected IGeneratorStateMachine createGeneratorMachine() {
			return new GeneratorStateMachine(false);
		}

		@Override
		public void resolveObstacleTexture(Sprite part, GameManager gameManager) {
			part.setRegion(gameManager.getTextureAtlas("game").findRegion("obstacle3"));
		}
	}
	
	private static class GameLevel4 extends AGameLevel {
		public GameLevel4() {
			friction = 3.5f;
			velocityX = 1.8f;
			velocityY = -2.6f;
			obstacleGeneratorDistance = 5.159f;
			
			emptySpaceSizeInTheMiddle = 1.21f;
			emptySpaceSizeOnTheEnd = 1.15f;
			boundariesBouncingFactor = 0.4f;
			endOnScore = 120;			
		}

		@Override
		protected IGeneratorStateMachine createGeneratorMachine() {
			return new GeneratorStateMachine(false);
		}

		@Override
		public void resolveObstacleTexture(Sprite part, GameManager gameManager) {
			part.setRegion(gameManager.getTextureAtlas("game").findRegion("obstacle4"));
		}
	}
	
	private static class GameLevel5 extends AGameLevel {
		public GameLevel5() {
			friction = 3.5f;
			velocityX = 1.8f;
			velocityY = -2.7f;
			obstacleGeneratorDistance = 5.05f;
			
			emptySpaceSizeInTheMiddle = 1.20f;
			emptySpaceSizeOnTheEnd = 1.14f;
			boundariesBouncingFactor = 0.4f;
			endOnScore = 0;			
		}

		@Override
		protected IGeneratorStateMachine createGeneratorMachine() {
			return new GeneratorStateMachine(false);
		}

		@Override
		public void resolveObstacleTexture(Sprite part, GameManager gameManager) {
			part.setRegion(gameManager.getTextureAtlas("game").findRegion("obstacle5"));
		}
	}
	
	public static AGameLevel[] createLevels() {
		return new AGameLevel[] {
			new GameLevel0(), new GameLevel1(), new GameLevel2(), new GameLevel3(), new GameLevel4(), new GameLevel5()
		}; 
	}	
}
