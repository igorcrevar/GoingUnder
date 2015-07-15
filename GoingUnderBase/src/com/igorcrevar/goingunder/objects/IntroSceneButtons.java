package com.igorcrevar.goingunder.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.igorcrevar.goingunder.GameData;
import com.igorcrevar.goingunder.GameManager;
import com.igorcrevar.goingunder.IActivityRequestHandler;
import com.igorcrevar.goingunder.ISceneManager;
import com.igorcrevar.goingunder.utils.GameHelper;

public class IntroSceneButtons  {
	// buttons
	private GameButton[] buttons;
	private final GameManager gameManager;
	private final GameButton soundButton;
	
	public IntroSceneButtons(final ISceneManager sceneManager, final IActivityRequestHandler activityRequestHandler, final GameManager gameManager) {
		this.gameManager = gameManager;
		buttons = new GameButton[5];
		
		float upButtonsY = 620;
		float lowerButtonsY = 380;
		
		soundButton = addButton(new GameButton(getTextureForSoundButton(), 50, upButtonsY, 200, 200) {
			@Override
			protected void onClick() {
				gameManager.setSoundOn(!gameManager.getIsSoundOn());
				soundButton.changeTexture(getTextureForSoundButton());
			}
		});
				
		addButton(new GameButton(getTextureRegion("rate"), 50, lowerButtonsY, 200, 200) {
			@Override
			protected void onClick() {
				activityRequestHandler.rate();
			}
		});	
		
		addButton(new GameButton(getTextureRegion("ach"), 300, upButtonsY, 200, 200) {
			@Override
			protected void onClick() {
				activityRequestHandler.showAchievements();
			}
		});
		
		addButton(new GameButton(getTextureRegion("highscore"), 300, lowerButtonsY, 200, 200) {
			@Override
			protected void onClick() {
				activityRequestHandler.showLeaderboards();
			}
		});		
		
		addButton(new GameButton(getTextureRegion("play"), 630, upButtonsY - 20, 400, 400) {
			@Override
			protected void onClick() {
				sceneManager.startGame();
			}
		});	
	}
	
	private GameButton addButton(GameButton gb) {
		for (int i = 0; i < buttons.length; ++i) {
			if (buttons[i] == null) {
				buttons[i] = gb;
				break;
			}
		}
		
		return gb;
	}
	
	public boolean check(GameData gameData, float x, float y) {
		x = GameHelper.screenX2OtherX(x, 1080);
		y = GameHelper.screenY2OtherY(y, 1920);
		for (GameButton gb : buttons) {
			if (gb.check(x, y)) {
				return true;
			}
		}
		
		return false;
	}
		
	public void draw(SpriteBatch spriteBatch) {
		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, 1080, 1920);
		spriteBatch.begin();
		for (GameButton gb : buttons) {
			gb.draw(spriteBatch);
		}
		spriteBatch.end();
	}
	
	private TextureRegion getTextureForSoundButton() {
		if (gameManager.getIsSoundOn()) {
			return getTextureRegion("soundon");
		}
		
		return getTextureRegion("soundoff");
	}
	
	private TextureRegion getTextureRegion(String name) {
		return gameManager.getTextureAtlas("widgets").findRegion(name);
	}
}
