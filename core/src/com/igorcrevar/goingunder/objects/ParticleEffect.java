package com.igorcrevar.goingunder.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.igorcrevar.goingunder.GameData;
import com.igorcrevar.goingunder.GameManager;

public class ParticleEffect implements IGameObject {
	private final float RandomProbability = 0.03f;
	private final int MaxParticles = 60;
	private Particle[] particles;
	private int countAlive;
	private boolean isEnabled;
	private Player playerObject;
	private GameData gameData;
	
	private float timer;
	
	class Particle {
		public Sprite sprite = new Sprite();
		private float timer;
		private float velocityY;
		private float timeToLive;
		private float alpha;
		private float angle;
		private float amplitude;
		private float initialPositionX;
		
		public Particle(TextureRegion textureRegion) {
			sprite.setRegion(textureRegion);
		}
		
		public void init(float x, float y, GameData gameData, boolean isLeft) {
			initialPositionX = x;
			// left
			if (isLeft) {
				initialPositionX += -gameData.PlayerSizeX / 2.0f + (float)Math.random() * 0.1f;
			}
			// right
			else {
				initialPositionX += +gameData.PlayerSizeX / 2.0f - (float)Math.random() * 0.1f;
			}
			
			sprite.setSize(gameData.BubbleSize, gameData.BubbleSize);
			sprite.setPosition(initialPositionX, y);
			
			this.velocityY = gameData.BubbleSpeedY + (float)Math.random() * gameData.BubbleSpeedYPotential;
			this.timeToLive = (float)Math.random() + 1.0f;
			this.timer = 0.0f;
			this.alpha = 0.0f;
			this.angle = (float)Math.random() * 90.0f;
			this.amplitude = 0.1f + 0.1f * (float)Math.random();
		}
		
		public boolean update(float deltaTime) {
			float x = initialPositionX + (float)(Math.sin(angle) * amplitude);
			float y = sprite.getY() + velocityY * deltaTime;
			sprite.setPosition(x, y);
			timer += deltaTime;
			alpha = Math.min(timer / timeToLive, 1.0f);
			angle += 5 * deltaTime;
			if (angle > 360.0f) angle = angle - 360.0f;
			return timer <= timeToLive;
		}
		
		public void draw(SpriteBatch spriteBatch) {
			sprite.draw(spriteBatch, 1 - alpha);
		}
	}
	
	public ParticleEffect(GameManager gameManager, Player player, GameData gameData) {
		this.gameData = gameData;
		this.playerObject = player;
		this.particles = new Particle[MaxParticles];
		for (int i = 0; i < particles.length; ++i) {
			particles[i] = new Particle(gameManager.getTextureAtlas("game").findRegion("bubble"));
		}
		this.countAlive = 0;
	}	
	
	@Override
	public void init(Object data) {
		countAlive = 0;
		isEnabled = true;
		timer = 0;     
	}

	@Override
	public void update(float deltaTime) {
		if (isEnabled) {
			// every n seconds fire bubble from player position
			if (timer == 0.0f) {
				addNew(playerObject.getX(), playerObject.getY());
			}

			// randomly fire bubble anywhere on screen
			if (Math.random() < RandomProbability) {
				float x = 2 * gameData.CameraHalfWidth * (float) Math.random() - gameData.CameraHalfWidth;
				float y = 2 * gameData.CameraHalfHeight * (float) Math.random() - gameData.CameraHalfHeight
					+ gameData.CameraYPosition;
				addNew(x, y);
			}
		}
		
		int i = countAlive - 1;
		while (i >= 0) {
			boolean keepIt = particles[i].update(deltaTime);
			if (!keepIt) {
				--countAlive;
				if (countAlive > 1) {
					Particle tmp = particles[i]; 
					particles[i] = particles[countAlive];
					particles[countAlive] = tmp;
				}
			}
			
			--i;
		}
		
		timer += deltaTime;
		if (timer > 0.5f) {
			timer = 0.0f;
		}
	}

	@Override
	public void draw(SpriteBatch spriteBatch) {
		for (int i = 0; i < countAlive; ++i) {
			particles[i].draw(spriteBatch);
		}
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public void setIsEnabled(boolean value) {
		isEnabled = value;
	}

	public void addNew() {
		int leftCnt = 2 + (int)(Math.random() * 2.1f); 
		int rightCnt = 2 + (int)(Math.random() * 2.1f);
		addNew(playerObject.getX(), playerObject.getY(), leftCnt, rightCnt);
	}
	
	private void addNew(float x, float y) {
		int leftCnt = (int)(Math.random() * 2.1f); 
		int rightCnt = (int)(Math.random() * 2.1f);
		addNew(x, y, leftCnt, rightCnt);
	}
	
	private void addNew(float x, float y, int cntLeft, int cntRight) {
		for (int i = 0; i < cntLeft + cntRight; ++i) {
			boolean isLeft = i < cntLeft;
			int pos = i + countAlive < MaxParticles ? i + countAlive : i;
			particles[pos].init(x, y, gameData, isLeft);
		}
		
		countAlive += cntLeft + cntRight;
		if (countAlive > MaxParticles) {
			countAlive = MaxParticles;
		}
	}
}
