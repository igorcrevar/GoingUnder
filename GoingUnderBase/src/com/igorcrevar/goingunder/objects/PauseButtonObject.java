package com.igorcrevar.goingunder.objects;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.igorcrevar.goingunder.GameData;
import com.igorcrevar.goingunder.GameManager;
import com.igorcrevar.goingunder.utils.GameHelper;
import com.igorcrevar.goingunder.utils.Mathf;


public class PauseButtonObject {
	private static final float PosX = 50f;
	private static final float PosY = 1800f;
	private static final float Width = 160.0f;
	private static final float Height = 120.0f;
	private static final float ShrinkFactorX = 40.0f;
	private static final float ShrinkFactorY = 20.0f;
	
	private enum GameActiveStatus {
		Resuming, Paused, Active
	};
	
	private float resumingCounter;
	private Sprite pauseButton = new Sprite();
	private GameActiveStatus status;
	private GameManager gameManager;
	
	public void init(GameManager gameManager) {
		status = GameActiveStatus.Active;
		this.gameManager = gameManager;
		resumingCounter = 0.0f;
		pauseButton.setBounds(PosX + ShrinkFactorX, PosY + ShrinkFactorY, Width - 2 * ShrinkFactorX, Height - 2 * ShrinkFactorY);
		pauseButton.setRegion(gameManager.getTextureAtlas("game").findRegion("pause"));
	}

	public boolean isGameActive() {
		return status == GameActiveStatus.Active;
	}
	
	public void draw(BitmapFont font, SpriteBatch batch) {		
		if (status != GameActiveStatus.Resuming) {
			pauseButton.draw(batch);
		}
		else {
			int cnt = 3 - (int)(resumingCounter);			
			String txt = cnt <= 0 ? "Go!" : String.valueOf(cnt);
			float tmp = resumingCounter - (int)resumingCounter;
			font.setScale(Mathf.lerp(2.5f, 1.0f, tmp));
			float middlePos = (1f / GameData.AspectRatio * 1920.0f) / 2.0f - font.getBounds(txt).width / 2.0f;
			font.draw(batch, txt, middlePos, 1200f);
			font.setScale(1.0f);
		}
	}

	public boolean isTouched(GameData gameData, int x, int y) {
//		float xx = GameHelper.screenX2WorldX(gameData, x);
//		float yy = GameHelper.screenY2WorldY(gameData, y);
		float xx = GameHelper.screenX2OtherX(x, 1f / GameData.AspectRatio * 1920.0f);
		float yy = GameHelper.screenY2OtherY(y, 1920f);
		if (GameHelper.tapPointInsideRectangle(xx, yy, PosX, PosY + Height, Width, Height)) {
			if (status == GameActiveStatus.Active) {
				pauseButton.setRegion(gameManager.getTextureAtlas("game").findRegion("resume"));
				status = GameActiveStatus.Paused;
			}
			else if (status == GameActiveStatus.Paused) {
				pauseButton.setRegion(gameManager.getTextureAtlas("game").findRegion("pause"));
				status = GameActiveStatus.Resuming;
				resumingCounter = 0.0f;
			}
			return true;
		}
		
		return false;
	}

	public void update(float deltaTime) {
		if (status == GameActiveStatus.Resuming) {
			resumingCounter += deltaTime;
			if (resumingCounter >= 4f) {
				status = GameActiveStatus.Active;
			}
		}
	}
}
