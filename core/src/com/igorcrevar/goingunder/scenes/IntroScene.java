package com.igorcrevar.goingunder.scenes;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.igorcrevar.goingunder.GameData;
import com.igorcrevar.goingunder.GameManager;
import com.igorcrevar.goingunder.IActivityRequestHandler;
import com.igorcrevar.goingunder.ISceneManager;
import com.igorcrevar.goingunder.objects.TileMesh;
import com.igorcrevar.goingunder.objects.ParticleEffect;
import com.igorcrevar.goingunder.objects.IntroSceneButtons;
import com.igorcrevar.goingunder.objects.Player;
import com.igorcrevar.goingunder.utils.BitmapFontDrawer;
import com.igorcrevar.goingunder.utils.GameHelper;
import com.igorcrevar.goingunder.utils.Mathf;
import com.igorcrevar.goingunder.utils.MyFontDrawer;
import com.igorcrevar.goingunder.utils.MyFontDrawerBatch;
import com.igorcrevar.goingunder.utils.MyFontDrawerDefaultFont;

public class IntroScene implements IScene {
	private TileMesh tileMesh;

	private final SpriteBatch spriteBatch = new SpriteBatch();
	private final GameData gameData = new GameData();
	
	private GameManager gameManager;
		
	public IntroScene(IActivityRequestHandler activityRequestHandler) {
	}
	
	@Override
	public void create(ISceneManager sceneManager) {
		gameManager = sceneManager.getGameManager();
		tileMesh = new TileMesh(gameManager);
	}
	
	@Override
	public void init(ISceneManager sceneManager) {
		gameData.setOffset(new Vector2(32*20f, 32*20f));
		gameData.setZoomFactor(0.5f);
		tileMesh.init();
	}

	@Override
	public void update(ISceneManager sceneManager, float deltaTime) {
		GameHelper.clearScreen();

		tileMesh.update(gameData, deltaTime);
		tileMesh.draw();

		if (GameData.isPressed(Input.Keys.A)) {
			gameData.incRotation(-(float)(Math.PI / 5 * deltaTime));
		} else if (GameData.isPressed(Input.Keys.D)) {
			gameData.incRotation(+(float)(Math.PI / 5 * deltaTime));
		}

		if (GameData.isPressed(Input.Keys.W)) {
			float angle = gameData.getRotation();
			gameData.incOffset(
				(float)(Math.cos(angle + Math.PI*0.5) * 164 * deltaTime),
				(float)(Math.sin(angle + Math.PI*0.5) * 164 * deltaTime)
			);
		} else if (GameData.isPressed(Input.Keys.S)) {
			float angle = gameData.getRotation();
			gameData.incOffset(
				(float)(Math.cos(angle + Math.PI*1.5) * 164 * deltaTime),
				(float)(Math.sin(angle + Math.PI*1.5) * 164 * deltaTime)
			);
		}

		if (GameData.isPressed(Input.Keys.P)) {
			gameData.incZoomFactor(-deltaTime * 0.2f);
		} else if (GameData.isPressed(Input.Keys.L)) {
			gameData.incZoomFactor(deltaTime * 0.2f);
		}
	}

	@Override
	public void leave(ISceneManager sceneManager) {
	}

	@Override
	public void processTouchDown(ISceneManager sceneManager, int x, int y) {
	}

	@Override
	public boolean processBackKey(ISceneManager sceneManager) {
		Gdx.app.exit();

		return true;
	}

	@Override
	public void dispose(ISceneManager sceneManager) {
		spriteBatch.dispose();
	}
}
