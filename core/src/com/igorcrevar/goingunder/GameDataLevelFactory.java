package com.igorcrevar.goingunder;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.igorcrevar.goingunder.statemachine.GeneratorStateMachine;
import com.igorcrevar.goingunder.statemachine.IGeneratorStateMachine;

public class GameDataLevelFactory {

	private static class GameLevel0 extends GameLevel1 {
		public GameLevel0(GameManager gameManager) {
			super();
			velocityY = -1.6f;
			friction = 3.5f;
			emptySpaceSizeInTheMiddle = 1.52f;
			emptySpaceSizeOnTheEnd = 1.48f;
			endOnScore = 2;
			fishRandomProbability = 0.0f;
			textures = new AtlasRegion[]{
					gameManager.getTextureAtlas("game").findRegion("ob10_left"),
					gameManager.getTextureAtlas("game").findRegion("ob10_middle"),
					gameManager.getTextureAtlas("game").findRegion("ob10_right"),
			};
			colors = new Vector3[]{
					new Vector3(1f / 255f, 68f / 255f, 210f / 255f), new Vector3(0f, 151f / 255f, 1f)
			};
		}
	}

	private static class GameLevel1 extends AGameLevel {
		protected AtlasRegion[] textures;
		protected Vector3[] colors;

		protected GameLevel1() {
			friction = 3.5f;
			velocityX = 1.8f;
			velocityY = -2.05f;
			obstacleGeneratorDistance = 5.359f;

			emptySpaceSizeInTheMiddle = 1.34f;
			emptySpaceSizeOnTheEnd = 1.28f;
			boundariesBouncingFactor = 0.5f;
			endOnScore = 10;
			colors = new Vector3[]{
					new Vector3(1f / 255f, 64f / 255f, 208f / 255f), new Vector3(0f, 149f / 255f, 0.99f)
			};
		}

		public GameLevel1(GameManager gameManager) {
			this();
			fishRandomProbability = 0.0003f;
			textures = new AtlasRegion[]{
					gameManager.getTextureAtlas("game").findRegion("ob1_left"),
					gameManager.getTextureAtlas("game").findRegion("ob1_middle"),
					gameManager.getTextureAtlas("game").findRegion("ob1_right"),
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
			return 0.075f;
		}

		@Override
		public float getPartSize() {
			return 0.3f;
		}

		@Override
		public Vector3 getTopColor() {
			return colors[0];
		}

		@Override
		public Vector3 getBottomColor() {
			return colors[1];
		}
	}

	private static class GameLevel11 extends GameLevel1 {
		public GameLevel11(GameManager gameManager) {
			super();
			velocityY = -2.35f;
			emptySpaceSizeInTheMiddle = 1.22f;
			emptySpaceSizeOnTheEnd = 1.16f;
			endOnScore = 30;
			fishRandomProbability = 0.001f;
			textures = new AtlasRegion[]{
					gameManager.getTextureAtlas("game").findRegion("ob11_left"),
					gameManager.getTextureAtlas("game").findRegion("ob11_middle"),
					gameManager.getTextureAtlas("game").findRegion("ob11_right"),
			};
			colors = new Vector3[]{
					new Vector3(1f / 255f, 58f / 255f, 200f / 255f), new Vector3(0f, 131f / 255f, 245f / 255f)
			};
		}
	}

	private static class GameLevel2 extends AGameLevel {
		private final AtlasRegion[] textures;
		private final Vector3[] colors;

		public GameLevel2(GameManager gameManager) {
			friction = 3.5f;
			velocityX = 1.8f;
			velocityY = -2.5f;
			obstacleGeneratorDistance = 5.259f;

			emptySpaceSizeInTheMiddle = 1.22f;
			emptySpaceSizeOnTheEnd = 1.16f;
			boundariesBouncingFactor = 0.5f;
			endOnScore = 60;
			fishRandomProbability = 0.0015f;
			textures = new AtlasRegion[]{
					gameManager.getTextureAtlas("game").findRegion("ob2_left"),
					gameManager.getTextureAtlas("game").findRegion("ob2_middle"),
					gameManager.getTextureAtlas("game").findRegion("ob2_right"),
			};
			colors = new Vector3[]{
					new Vector3(1f / 255f, 68f / 255f, 190f / 255f), new Vector3(0f, 121f / 255f, 235f / 255f)
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

		@Override
		public Vector3 getTopColor() {
			return colors[0];
		}

		@Override
		public Vector3 getBottomColor() {
			return colors[1];
		}
	}

	private static class GameLevel3 extends AGameLevel {
		private final AtlasRegion[] textures;
		private final Vector3[] colors;

		public GameLevel3(GameManager gameManager) {
			friction = 3.5f;
			velocityX = 1.8f;
			velocityY = -2.55f;
			obstacleGeneratorDistance = 5.259f;

			emptySpaceSizeInTheMiddle = 1.22f;
			emptySpaceSizeOnTheEnd = 1.16f;
			boundariesBouncingFactor = 0.5f;
			endOnScore = 90;
			fishRandomProbability = 0.0025f;
			textures = new AtlasRegion[]{
					gameManager.getTextureAtlas("game").findRegion("ob3_left"),
					gameManager.getTextureAtlas("game").findRegion("ob3_middle"),
					gameManager.getTextureAtlas("game").findRegion("ob3_right"),
			};
			colors = new Vector3[]{
					new Vector3(1f / 255f, 48f / 255f, 180f / 255f), new Vector3(0f, 81f / 255f, 225f / 255f)
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
			return 0.6f;
		}

		@Override
		public Vector3 getTopColor() {
			return colors[0];
		}

		@Override
		public Vector3 getBottomColor() {
			return colors[1];
		}
	}

	private static class GameLevel4 extends AGameLevel {
		private final AtlasRegion[] textures;
		private final Vector3[] colors;

		public GameLevel4(GameManager gameManager) {
			friction = 3.5f;
			velocityX = 1.8f;
			velocityY = -2.6f;
			obstacleGeneratorDistance = 5.159f;

			emptySpaceSizeInTheMiddle = 1.21f;
			emptySpaceSizeOnTheEnd = 1.15f;
			boundariesBouncingFactor = 0.4f;
			fishRandomProbability = 0.0035f;
			endOnScore = 120;
			textures = new AtlasRegion[]{
					gameManager.getTextureAtlas("game").findRegion("obstacle4"),
			};
			colors = new Vector3[]{
					new Vector3(1f / 255f, 38f / 255f, 170f / 255f), new Vector3(0f, 71f / 255f, 215f / 255f)
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

		@Override
		public Vector3 getTopColor() {
			return colors[0];
		}

		@Override
		public Vector3 getBottomColor() {
			return colors[1];
		}
	}

	private static class GameLevel5 extends AGameLevel {
		private final AtlasRegion[] textures;
		private final Vector3[] colors;

		public GameLevel5(GameManager gameManager) {
			friction = 3.5f;
			velocityX = 1.8f;
			velocityY = -2.7f;
			obstacleGeneratorDistance = 5.05f;

			emptySpaceSizeInTheMiddle = 1.20f;
			emptySpaceSizeOnTheEnd = 1.14f;
			boundariesBouncingFactor = 0.4f;
			endOnScore = 0;
			fishRandomProbability = 0.0045f;
			textures = new AtlasRegion[]{
					gameManager.getTextureAtlas("game").findRegion("obstacle5"),
			};
			colors = new Vector3[]{
					new Vector3(1f / 255f, 20f / 255f, 155f / 255f), new Vector3(0f, 61f / 255f, 195f / 255f)
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

		@Override
		public Vector3 getTopColor() {
			return colors[0];
		}

		@Override
		public Vector3 getBottomColor() {
			return colors[1];
		}
	}

	public static AGameLevel[] createLevels(GameManager gameManager) {
		return new AGameLevel[]{
				new GameLevel0(gameManager),
				new GameLevel1(gameManager),
				new GameLevel11(gameManager),
				new GameLevel2(gameManager),
				new GameLevel3(gameManager),
				new GameLevel4(gameManager),
				new GameLevel5(gameManager)
		};
	}

	public static AGameLevel[] createDefaultLevel(GameManager gameManager) {
		return new AGameLevel[]{new GameLevel0(gameManager)};
	}
}
