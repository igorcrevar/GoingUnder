package com.igorcrevar.goingunder;

public interface IActivityRequestHandler {
	void showAds(boolean show);
	void rate();
	void share(int score);
	void finishGame(long score);
	void showAchievements();
	void showLeaderboards();
	boolean getSignedInGPGS();
	void loginGPGS();
	void setGameManager(GameManager gameManager);
}
