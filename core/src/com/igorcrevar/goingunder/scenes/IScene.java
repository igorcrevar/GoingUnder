package com.igorcrevar.goingunder.scenes;

import com.igorcrevar.goingunder.ISceneManager;

public interface IScene {
	void create(ISceneManager sceneManager);

	void init(ISceneManager sceneManager);

	void update(ISceneManager sceneManager, float deltaTime);

	void dispose(ISceneManager sceneManager);

	void leave(ISceneManager sceneManager);

	void processTouchDown(ISceneManager sceneManager, int x, int y);

	boolean processBackKey(ISceneManager sceneManager);
}
