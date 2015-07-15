package com.igorcrevar.goingunder.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.igorcrevar.goingunder.IActivityRequestHandler;
import com.igorcrevar.goingunder.ISceneManager;
import com.igorcrevar.goingunder.utils.GameHelper;

public class GameLoadingScene implements IScene {
	private SpriteBatch spriteBatch = new SpriteBatch();
	private boolean isDisposed;
	
	private IActivityRequestHandler activityRequestHandler;
	
	public GameLoadingScene(IActivityRequestHandler activityRequestHandler) {
		this.activityRequestHandler = activityRequestHandler;
	}
	
	@Override
	public void create(ISceneManager sceneManager) {		
	}
	
	@Override
	public void init(ISceneManager sceneManager) {
		activityRequestHandler.showAds(true);
	}

	@Override
	public void update(ISceneManager sceneManager, float deltaTime) {
		GameHelper.clearScreen();
		if (sceneManager.getGameManager().updateLoading()) {
			sceneManager.setScene(ISceneManager.IntroScene);
			return;
		}
		
		if (sceneManager.getGameManager().isBitmapFontLoaded()) {
			spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, 1080, 1920);
			spriteBatch.begin();
			// draw score
			BitmapFont font = sceneManager.getGameManager().getBitmapFont();
			font.setScale(1.0f);
			font.setColor(Color.WHITE);
			String txt = "loading...";
			font.draw(spriteBatch, txt, (1080 - font.getBounds(txt).width) / 2.0f, (1920 - font.getBounds(txt).height) / 2.0f);
			spriteBatch.end();
			return;
		}
	}

	@Override
	public void leave(ISceneManager sceneManager) {
		dispose(sceneManager);
	}

	@Override
	public void processTouchDown(ISceneManager sceneManager, int x, int y) {
	}
	
	@Override
	public void processTouchUp(ISceneManager sceneManager, int x, int y) {
	}

	@Override
	public void dispose(ISceneManager sceneManager) {
		if (!isDisposed) {
			spriteBatch.dispose();
			isDisposed = true;
		}
	}
}