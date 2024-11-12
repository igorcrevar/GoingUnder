package com.igorcrevar.goingunder.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.igorcrevar.goingunder.GameManager;
import com.igorcrevar.goingunder.IActivityRequestHandler;
import com.igorcrevar.goingunder.ISceneManager;
import com.igorcrevar.goingunder.scenes.SceneConstants;

public class EndGameButtons extends GameButtons {
	private static final float ButtonsYPos = 500;

	private final GameManager gameManager;

	public EndGameButtons(final ISceneManager sceneManager,
						  final IActivityRequestHandler activityRequestHandler,
						  final GameManager gameManager) {
		super(3, 1080.0f);
		this.gameManager = gameManager;

		addButton(new GameButton(getTextureRegion("intro"), 90, ButtonsYPos, 240, 240) {
			@Override
			protected boolean onClick() {
				sceneManager.setScene(SceneConstants.IntroScene);
				return true;
			}
		});

		addButton(new GameButton(getTextureRegion("playagain"), 750, ButtonsYPos, 240, 240) {
			@Override
			protected boolean onClick() {
				sceneManager.startGame();
				return true;
			}
		});

		addButton(new GameButton(getTextureRegion("share"), 420, ButtonsYPos, 240, 240) {
			@Override
			protected boolean onClick() {
				activityRequestHandler.share(gameManager.getCurrentScore());
				return true;
			}
		});
	}

	private TextureRegion getTextureRegion(String name) {
		return gameManager.getTextureAtlas("widgets").findRegion(name);
	}
}
