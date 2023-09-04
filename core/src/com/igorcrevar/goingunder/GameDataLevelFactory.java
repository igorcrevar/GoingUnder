package com.igorcrevar.goingunder;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.igorcrevar.goingunder.statemachine.GeneratorStateMachine;
import com.igorcrevar.goingunder.statemachine.IGeneratorStateMachine;

public class GameDataLevelFactory {
	
	private static class GameLevel0 extends GameLevel1 {
		public GameLevel0(GameManager gameManager) {
			super(gameManager);
			velocityY = -1.8f;
			friction = 3.5f;
			emptySpaceSizeInTheMiddle = 1.52f;
			emptySpaceSizeOnTheEnd = 1.48f;
			endOnScore = 4;
		}

		@Override
		protected AtlasRegion[] getTextures(GameManager gameManager) {
			return new AtlasRegion[] {
				gameManager.getTextureAtlas("game").findRegion("ob10_left"),
				gameManager.getTextureAtlas("game").findRegion("ob10_middle"),
				gameManager.getTextureAtlas("game").findRegion("ob10_right"),
			};
		}
	}
	
	private static class GameLevel1 extends AGameLevel {
		protected final AtlasRegion[] textures;
		
		public GameLevel1(GameManager gameManager) {
			friction = 3.5f;
			velocityX = 1.8f;
			velocityY = -2.2f;
			obstacleGeneratorDistance = 5.359f;
			
			emptySpaceSizeInTheMiddle = 1.34f;
			emptySpaceSizeOnTheEnd = 1.28f;
			boundariesBouncingFactor = 0.5f;
			endOnScore = 15;
			textures = getTextures(gameManager);
		}

		@Override
		protected IGeneratorStateMachine createGeneratorMachine() {
			return new GeneratorStateMachine(true);
		}

		@Override
		public AtlasRegion[] getTextures() {
			return textures;
		}

		@Override
		public float getEndPartSize() {
			return 0.075f;
		}

		@Override
		public float getPartSize() {
			return 0.3f;
		}

		protected AtlasRegion[] getTextures(GameManager gameManager) {
			return new AtlasRegion[] {
				gameManager.getTextureAtlas("game").findRegion("ob1_left"),
				gameManager.getTextureAtlas("game").findRegion("ob1_middle"),
				gameManager.getTextureAtlas("game").findRegion("ob1_right"),
			};
		}
	}

	private static class GameLevel11 extends GameLevel1 {
		public GameLevel11(GameManager gameManager) {
			super(gameManager);
			velocityY = -2.4f;
			emptySpaceSizeInTheMiddle = 1.22f;
			emptySpaceSizeOnTheEnd = 1.16f;
			endOnScore = 30;
		}

		@Override
		protected AtlasRegion[] getTextures(GameManager gameManager) {
			return new AtlasRegion[] {
				gameManager.getTextureAtlas("game").findRegion("ob11_left"),
				gameManager.getTextureAtlas("game").findRegion("ob11_middle"),
				gameManager.getTextureAtlas("game").findRegion("ob11_right"),
			};
		}
	}
	
	private static class GameLevel2 extends AGameLevel {
		private final AtlasRegion[] textures;
		
		public GameLevel2(GameManager gameManager) {
			friction = 3.5f;
			velocityX = 1.8f;
			velocityY = -2.5f;
			obstacleGeneratorDistance = 5.259f;
			
			emptySpaceSizeInTheMiddle = 1.22f;
			emptySpaceSizeOnTheEnd = 1.16f;
			boundariesBouncingFactor = 0.5f;
			endOnScore = 60;
			textures = new AtlasRegion[] {
				gameManager.getTextureAtlas("game").findRegion("ob2_left"),
				gameManager.getTextureAtlas("game").findRegion("ob2_middle"),
				gameManager.getTextureAtlas("game").findRegion("ob2_right"),
			};
		}

		@Override
		protected IGeneratorStateMachine createGeneratorMachine() {
			return new GeneratorStateMachine(true);
		}

		@Override
		public AtlasRegion[] getTextures() {
			return textures;
		}

		@Override
		public float getEndPartSize() {
			return 0.1f;
		}

		@Override
		public float getPartSize() {
			return 0.6f;
		}
	}
	
	private static class GameLevel3 extends AGameLevel {
		private final AtlasRegion[] textures;

		public GameLevel3(GameManager gameManager) {
			friction = 3.5f;
			velocityX = 1.8f;
			velocityY = -2.55f;
			obstacleGeneratorDistance = 5.259f;
			
			emptySpaceSizeInTheMiddle = 1.22f;
			emptySpaceSizeOnTheEnd = 1.16f;
			boundariesBouncingFactor = 0.5f;
			endOnScore = 90;
			textures = new AtlasRegion[] {
				gameManager.getTextureAtlas("game").findRegion("obstacle3"),
			};
		}

		@Override
		protected IGeneratorStateMachine createGeneratorMachine() {
			return new GeneratorStateMachine(false);
		}

		@Override
		public AtlasRegion[] getTextures() {
			return textures;
		}

		@Override
		public float getEndPartSize() {
			return 0.1f;
		}

		@Override
		public float getPartSize() {
			return 0.5f;
		}
	}
	
	private static class GameLevel4 extends AGameLevel {
		private final AtlasRegion[] textures;

		public GameLevel4(GameManager gameManager) {
			friction = 3.5f;
			velocityX = 1.8f;
			velocityY = -2.6f;
			obstacleGeneratorDistance = 5.159f;
			
			emptySpaceSizeInTheMiddle = 1.21f;
			emptySpaceSizeOnTheEnd = 1.15f;
			boundariesBouncingFactor = 0.4f;
			endOnScore = 120;
			textures = new AtlasRegion[] {
				gameManager.getTextureAtlas("game").findRegion("obstacle4"),
			};
		}

		@Override
		protected IGeneratorStateMachine createGeneratorMachine() {
			return new GeneratorStateMachine(false);
		}

		@Override
		public AtlasRegion[] getTextures() {
			return textures;
		}

		@Override
		public float getEndPartSize() {
			return 0.1f;
		}

		@Override
		public float getPartSize() {
			return 0.5f;
		}
	}
	
	private static class GameLevel5 extends AGameLevel {
		private final AtlasRegion[] textures;

		public GameLevel5(GameManager gameManager) {
			friction = 3.5f;
			velocityX = 1.8f;
			velocityY = -2.7f;
			obstacleGeneratorDistance = 5.05f;
			
			emptySpaceSizeInTheMiddle = 1.20f;
			emptySpaceSizeOnTheEnd = 1.14f;
			boundariesBouncingFactor = 0.4f;
			endOnScore = 0;
			textures = new AtlasRegion[] {
				gameManager.getTextureAtlas("game").findRegion("obstacle5"),
			};
		}

		@Override
		protected IGeneratorStateMachine createGeneratorMachine() {
			return new GeneratorStateMachine(false);
		}

		@Override
		public AtlasRegion[] getTextures() {
			return textures;
		}

		@Override
		public float getEndPartSize() {
			return 0.1f;
		}

		@Override
		public float getPartSize() {
			return 0.5f;
		}
	}
	
	public static AGameLevel[] createLevels(GameManager gameManager) {
		return new AGameLevel[] {
			new GameLevel0(gameManager),
			new GameLevel1(gameManager),
			new GameLevel11(gameManager),
			new GameLevel2(gameManager),
			new GameLevel3(gameManager),
			new GameLevel4(gameManager),
			new GameLevel5(gameManager)
		}; 
	}	
}
