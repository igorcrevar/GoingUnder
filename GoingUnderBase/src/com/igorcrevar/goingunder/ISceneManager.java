package com.igorcrevar.goingunder;

public interface ISceneManager {
	final String GameLoadingScene = "GameLoadingScene";
	final String IntroScene = "IntroScene";
	final String GameScene = "GameScene";
	final String TutorialScene = "TutorialScene";
	GameManager getGameManager();
	void setScene(String sceneName);
	void startGame();
	void finishGame();
}
