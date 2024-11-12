package com.igorcrevar.goingunder.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.igorcrevar.goingunder.GameManager;
import com.igorcrevar.goingunder.IActivityRequestHandler;
import com.igorcrevar.goingunder.ISceneManager;

public class IntroSceneButtons extends GameButtons {
	private final GameManager gameManager;
	private final GameButton soundButton;

	public IntroSceneButtons(final ISceneManager sceneManager, final IActivityRequestHandler activityRequestHandler, final GameManager gameManager) {
		super(5, 1080.0f);
		this.gameManager = gameManager;

		float upButtonsY = 620;
		float lowerButtonsY = 380;

		addButton(new GameButton(getTextureForSoundButton(), 50, upButtonsY, 200, 200) {
			@Override
			protected boolean onClick() {
				gameManager.setSoundOn(!gameManager.getIsSoundOn());
				soundButton.changeTexture(getTextureForSoundButton());
				return true;
			}
		});

		addButton(new GameButton(getTextureRegion("rate"), 50, lowerButtonsY, 200, 200) {
			@Override
			protected boolean onClick() {
				activityRequestHandler.rate();
				return true;
			}
		});

		addButton(new GameButton(getTextureRegion("ach"), 300, upButtonsY, 200, 200) {
			@Override
			protected boolean onClick() {
				activityRequestHandler.showAchievements();
				return true;
			}
		});

		addButton(new GameButton(getTextureRegion("highscore"), 300, lowerButtonsY, 200, 200) {
			@Override
			protected boolean onClick() {
				activityRequestHandler.showLeaderboards();
				return true;
			}
		});

		addButton(new GameButton(getTextureRegion("play"), 630, upButtonsY - 20, 400, 400) {
			@Override
			protected boolean onClick() {
				sceneManager.startGame();
				return true;
			}
		});

		soundButton = gameButtons[0];
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
