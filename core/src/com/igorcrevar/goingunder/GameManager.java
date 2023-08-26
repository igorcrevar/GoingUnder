package com.igorcrevar.goingunder;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.igorcrevar.goingunder.objects.IGameObject;
import com.igorcrevar.goingunder.objects.Player;
import com.igorcrevar.goingunder.objects.StaticBackground;
import com.igorcrevar.goingunder.utils.BitmapFontDrawer;

public class GameManager {
	public enum Status {
		Resuming, Paused, Active, NotActive
	}
	private final static int BitmapFontDrawerFakeXResolution = 1080;
	private final static String GameSaves = "game_saves";
	private final static String TopScore = "top_score";
	private final static String SoundOn = "sound_on";
	private final static String TotalGamesPlayed = "total_games_played";
	private final static String TotalScoreSum = "total_score_sum";

	private AssetManager assetManager = new AssetManager();
	private int currentScore;
	private int topScore;
	private boolean isSoundOn;
	private int totalScoreSum;
	private int totalGamesPlayed;
	private Status gameStatus;

	private BitmapFontDrawer bitmapFontDrawer;
	
	private IGameObject background;
	private Player player;
	
	public GameManager() {
		gameStatus = Status.NotActive;
		topScore = 0;
		currentScore = 0;
		//load top score, etc
		Preferences prefs = Gdx.app.getPreferences(GameSaves);
		topScore = prefs.getInteger(TopScore);
		isSoundOn = prefs.getBoolean(SoundOn, true);
		totalGamesPlayed = prefs.getInteger(TotalGamesPlayed, 0);
		totalScoreSum = prefs.getInteger(TotalScoreSum, 0);
		
		// add all needed assets to queue
		assetManager.load("fonts/arial64.fnt", BitmapFont.class);
		assetManager.load("atlases/game.atlas", TextureAtlas.class);
		assetManager.load("atlases/widgets.atlas", TextureAtlas.class);
		assetManager.load("sounds/coin.wav", Sound.class);
		assetManager.load("sounds/move.wav", Sound.class);
		assetManager.load("sounds/die.wav", Sound.class);
		assetManager.load("sounds/intro.ogg", Music.class);
	}
	
	public void setSoundOn(boolean value) {
		isSoundOn = value;
		Preferences prefs = Gdx.app.getPreferences(GameSaves);
		prefs.putBoolean(SoundOn, value);
		prefs.flush();
		// setSoundOn can only be called from intro
		if (isSoundOn) {
			playIntroMusic();
		}
		else {
			stopIntroMusic();
		}
	}
	
	public void setGameStatus(Status status) {
		gameStatus = status;
		if (status == Status.Active) {
			currentScore = 0;
		}
	}

	public Status getGameStatus() {
		return gameStatus;
	}

	public boolean isGameNotActive() {
		return gameStatus == Status.NotActive;
	}

	public boolean isGameActive() {
		return gameStatus == Status.Active;
	}
	
	public void saveScoreEtc() {
		Preferences prefs = Gdx.app.getPreferences(GameSaves);
		// sum of all scores ever
		totalScoreSum += currentScore;
		prefs.putInteger(TotalScoreSum, totalScoreSum);
		// games played by player
		totalGamesPlayed++;
		prefs.putInteger(TotalGamesPlayed, totalGamesPlayed);
		// save top score
		if (topScore < currentScore) {
			topScore = currentScore;
			// Save top score!!!!
			prefs.putInteger(TopScore, topScore);
		}
		
		// hack / do not want to kill my ssd
		if (Gdx.app.getType() != ApplicationType.Desktop) {
			prefs.flush();
		}
	}
	
	public void savePreferences() {
		// hack / on desktop when disposing app, preferences should be saved (otherwise I'm killing my ssd)
		if (Gdx.app.getType() == ApplicationType.Desktop) {
			Preferences prefs = Gdx.app.getPreferences(GameSaves);
			prefs.flush();
		}
	}
	
	public void addScore(int value) {
		currentScore += value;
		playCoinSound();
	}

	public int getCurrentScore() {
		return currentScore;
	}
	
	public int getTopScore() {
		return topScore;
	}
	
	public int getTotalScoreSum() {
		return totalScoreSum;
	}
	
	public int getTotalGamesPlayed() {
		return totalGamesPlayed;
	}
	
	public boolean getIsSoundOn() {
		return isSoundOn;
	}
	
	/**
	 * Update loading of assets
	 * @return true if loading is finished
	 */
	public boolean updateLoading() {
		return assetManager.update();
	}
	
	public void dispose() {
		assetManager.dispose();
		bitmapFontDrawer.dispose();
	}
	
	public TextureAtlas getTextureAtlas(String fileName) {
		String fullName = "atlases/" + fileName + ".atlas";
		return assetManager.get(fullName, TextureAtlas.class);
	}
	
	public Texture getTexture(String fileName) {
		return assetManager.get("textures/" + fileName, Texture.class);
	}
	
	public Sound getSound(String fileName) {
		return assetManager.get("sounds/" + fileName, Sound.class);
	}
	
	public void playIntroMusic() {
		if (isSoundOn) {
			Music music = assetManager.get("sounds/intro.ogg", Music.class);
			music.setLooping(true);
			music.play();
		}
	}
	
	public void stopIntroMusic() {
		Music music = assetManager.get("sounds/intro.ogg", Music.class);
		music.stop();
	}
	
	public void playCoinSound() {
		if (isSoundOn) {
			getSound("coin.wav").play();
		}
	}
	
	public void playMoveSound() {
		if (isSoundOn) {
			getSound("move.wav").play();
		}
	}
	
	public void playDieSound() {
		if (isSoundOn) {
			getSound("die.wav").play();
		}
	}
	
	private BitmapFont getBitmapFont() {
		return assetManager.get("fonts/arial64.fnt", BitmapFont.class);
	}
	
	public BitmapFontDrawer getBitmapFontDrawer() {
		// this method will be initialized from only one thread, so we are good to go
		if (this.bitmapFontDrawer == null &&
				assetManager.isLoaded("fonts/arial64.fnt", BitmapFont.class)) {
			this.bitmapFontDrawer = new BitmapFontDrawer(getBitmapFont(), BitmapFontDrawerFakeXResolution);
		}

		return this.bitmapFontDrawer;
	}
	
	public IGameObject getBackground() {
		if (background == null) {
			background = new StaticBackground(this);
		}
		return background;
	}
	
	public Player getPlayer() {
		if (player == null) {
			player = new Player(new TextureRegion[] {
				getTextureAtlas("game").findRegion("player1"),
				getTextureAtlas("game").findRegion("player2"),
				getTextureAtlas("game").findRegion("player3"),
				getTextureAtlas("game").findRegion("player2"),
			}, new TextureRegion[] {
				getTextureAtlas("game").findRegion("player_die")
			});
		}
		
		return player;
	}
}