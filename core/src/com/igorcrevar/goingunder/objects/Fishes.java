package com.igorcrevar.goingunder.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.igorcrevar.goingunder.GameData;
import com.igorcrevar.goingunder.GameManager;
import com.igorcrevar.goingunder.utils.Mathf;

public class Fishes implements IGameObject {
	private final static float FishSizeX = 0.7f;
	private final static int MaxFishes = 6;

	private class Fish {
		private final Sprite gameObject = new Sprite();

		private float speed;
		private float endX;
		private float endY;
		private float startX;
		private float startY;
		private int currentFrame;
		private float startTime;
		private boolean dirChanged;

		public Fish() {
		}

		public void set(GameData gd, TextureRegion[] textures, float time) {
			float sizeY = (float) textures[0].getRegionHeight() / textures[0].getRegionWidth() * FishSizeX;
			boolean bottomFish = Math.random() > 0.3f; // better chance for bottom
			boolean isMovingRight = Math.random() > 0.5;

			this.speed = 0.3f + (float) Math.random() * 0.12f;

			// if upper part then go downwards. bottom part has more chances to go upwards
			if (bottomFish) {
				this.startY = (float) (-gameData.CameraHalfHeight * Math.random());
				this.endY = this.startY + (float) (Math.random() * gd.CameraHalfHeight * 0.75) - 0.15f * gd.CameraHalfHeight;
			} else {
				this.startY = (float) (gameData.CameraHalfHeight * 0.6f * Math.random());
				this.endY = this.startY - (float) (Math.random() * gd.CameraHalfHeight * 0.55);
			}

			this.startY += gd.getCameraYCenter(); // inc for camera position
			this.endY += gd.getCameraYCenter(); // inc for camera position

			if (isMovingRight) {
				this.startX = -gd.CameraHalfWidth - FishSizeX;
				this.endX = gd.CameraHalfWidth;
			} else {
				this.endX = -gd.CameraHalfWidth - FishSizeX;
				this.startX = gd.CameraHalfWidth;
			}

			this.updateRotation();
			this.currentFrame = -100;
			this.updateAnimation(textures, time);
			this.gameObject.setOrigin(FishSizeX / 2, sizeY / 2);
			this.gameObject.setSize(FishSizeX, sizeY);
			this.gameObject.setPosition(this.startX, this.startY);
			this.startTime = time;
			this.dirChanged = false;
		}

		public boolean isPlayerHit(float playerX, float playerY, float playerVelocity, float dist, float time) {
			float realTime = (time - startTime) * speed;
			float xDiff = gameObject.getX() + gameObject.getWidth() / 2f - playerX;
			float yDiff = gameObject.getY() + gameObject.getHeight() / 2f - playerY;
			float playerDist = xDiff * xDiff + yDiff * yDiff;

			// turn if near player!
			if (playerDist < dist && !dirChanged) {
				this.dirChanged = true;
				this.speed = this.speed * Math.max(playerVelocity, 1.2f) / realTime;
				this.endX = this.startX;
				this.endY = (this.endY - this.startY) * realTime + gameObject.getY();
				this.startX = gameObject.getX();
				this.startY = gameObject.getY();
				this.startTime = time;
				this.currentFrame = -100;
				this.updateRotation();

				return true;
			}

			return false;
		}

		public boolean update(GameData gd, TextureRegion[] textures, float time) {
			float realTime = (time - startTime) * speed;
			float x = Mathf.lerp(this.startX, this.endX, realTime);
			float y = Mathf.lerp(this.startY, this.endY, realTime);

			gameObject.setPosition(x, y);
			updateAnimation(textures, time);

			return y <= gd.getCameraTop() && realTime <= 1.0f;
		}

		private void updateRotation() {
			double angle = Math.atan2(this.endY - this.startY, Math.abs(this.endX - this.startX));
			if (this.endX < this.startX) {
				angle = 2 * Math.PI - angle;
			}
			this.gameObject.setRotation((float) (angle / Math.PI * 180f));
		}

		private void updateAnimation(TextureRegion[] textures, float time) {
			// calculate current fame
			int desiredFrame = (int) Math.floor(((time * 2) % 1.0f) * textures.length);
			// only swap textures if needed
			if (desiredFrame != currentFrame) {
				currentFrame = desiredFrame;
				gameObject.setRegion(textures[currentFrame]);
				gameObject.setFlip(startX < endX, false);
			}
		}

		public void draw(SpriteBatch sb) {
			gameObject.draw(sb);
		}
	}

	private final Fish[] fishes = new Fish[MaxFishes];
	private final TextureRegion[] animation = new TextureRegion[3];
	private int aliveFishes;
	private float animationTimer;
	private boolean isEnabled;
	private GameData gameData;
	private final GameManager gameManager;

	public Fishes(GameManager gameManager) {
		this.gameManager = gameManager;
		animation[0] = gameManager.getTextureAtlas("game").findRegion("fish11");
		animation[1] = gameManager.getTextureAtlas("game").findRegion("fish12");
		animation[2] = gameManager.getTextureAtlas("game").findRegion("fish13");
		for (int i = 0; i < MaxFishes; i++) {
			fishes[i] = new Fish();
		}
	}

	@Override
	public void init(Object data) {
		this.gameData = (GameData) data;
		this.animationTimer = 0.0f;
		this.aliveFishes = 0;
		this.isEnabled = true;
	}

	@Override
	public void update(float deltaTime) {
		final Player player = this.gameManager.getPlayer();
		final float playerVelocity = (Math.abs(player.getVelocityX()) / this.gameData.MaxVelocityX) * 2.5f;
		final float dist = gameData.PlayerSizeX / 2.0f + FishSizeX / 2.0f;
		this.animationTimer += deltaTime;

		for (int i = 0; i < this.aliveFishes; i++) {
			Fish fish = this.fishes[i];
			boolean isAlive = fish.update(gameData, animation, animationTimer);
			if (!isAlive) {
				this.aliveFishes--;
				this.fishes[i] = this.fishes[this.aliveFishes];
				this.fishes[this.aliveFishes] = fish;
			} else if (fish.isPlayerHit(player.getX(), player.getY(), playerVelocity, dist, animationTimer) &&
					this.gameManager.isGameActive()) {
				this.gameManager.incFishPunchCount();
				this.gameManager.playPunchSound();
			}
		}

		// randomly generate fish
		if (this.aliveFishes < MaxFishes && isEnabled && Math.random() < gameData.FishRandomProbability) {
			this.fishes[this.aliveFishes].set(gameData, animation, animationTimer);
			this.aliveFishes++;
		}
	}

	@Override
	public void draw(SpriteBatch spriteBatch) {
		for (int i = 0; i < this.aliveFishes; i++) {
			this.fishes[i].draw(spriteBatch);
		}
	}

	@Override
	public boolean isEnabled() {
		return this.isEnabled;
	}

	@Override
	public void setIsEnabled(boolean value) {
		this.isEnabled = value;
	}
}

