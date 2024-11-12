package com.igorcrevar.goingunder;

import java.util.HashMap;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.igorcrevar.goingunder.objects.IGameObject;
import com.igorcrevar.goingunder.objects.Player;
import com.igorcrevar.goingunder.objects.StaticBackground;
import com.igorcrevar.goingunder.utils.BitmapFontDrawer;

public class GameManager {
	public enum Status {
		Resuming, Paused, Active, NotActive
	}

	private final static boolean ShaderProgramPedantic = true;
	private final static float MusicVolume = 0.9f;
	private final static int BitmapFontDrawerFakeXResolution = 1080;
	private final static String GameSaves = "game_saves";
	private final static String TopScore = "top_score";
	private final static String TopFishPunchCount = "top_fish_punch_cnt";
	private final static String SoundOn = "sound_on";
	private final static String TotalGamesPlayed = "total_games_played";
	private final static String TotalScoreSum = "total_score_sum";

	private final AssetManager assetManager = new AssetManager();
	private int currentScore;
	private int topScore;
	private boolean isSoundOn;
	private int topFishPunchCount;
	private int fishPunchCount;
	private int totalScoreSum;
	private int totalGamesPlayed;
	private Status gameStatus;
	private int moveSound;

	private BitmapFontDrawer bitmapFontDrawer;

	private IGameObject background;
	private Player player;

	private final HashMap<String, ShaderProgram> shaders = new HashMap<>();

	public GameManager() {
		ShaderProgram.pedantic = ShaderProgramPedantic; // turn on/of pedantic compiler
		gameStatus = Status.NotActive;
		currentScore = 0;
		fishPunchCount = 0;
		//load top score, etc
		Preferences prefs = Gdx.app.getPreferences(GameSaves);
		topScore = prefs.getInteger(TopScore);
		topFishPunchCount = prefs.getInteger(TopFishPunchCount);
		isSoundOn = prefs.getBoolean(SoundOn, true);
		totalGamesPlayed = prefs.getInteger(TotalGamesPlayed, 0);
		totalScoreSum = prefs.getInteger(TotalScoreSum, 0);

		// add all needed assets to queue
		assetManager.load("fonts/arial64.fnt", BitmapFont.class);
		assetManager.load("atlases/game.atlas", TextureAtlas.class);
		assetManager.load("atlases/widgets.atlas", TextureAtlas.class);
		assetManager.load("sounds/coin.wav", Sound.class);
		assetManager.load("sounds/move.wav", Sound.class);
		assetManager.load("sounds/move2.wav", Sound.class);
		assetManager.load("sounds/die.wav", Sound.class);
		assetManager.load("sounds/punch.wav", Sound.class);
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
		} else {
			stopIntroMusic();
		}
	}

	public void setGameStatus(Status status) {
		gameStatus = status;
	}

	public Status getGameStatus() {
		return gameStatus;
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
			prefs.putInteger(TopScore, topScore); // Save top score!!!!
		}

		if (topFishPunchCount < fishPunchCount) {
			topFishPunchCount = fishPunchCount;
			prefs.putInteger(TopFishPunchCount, topFishPunchCount); // Save top fish punch count
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

	public void incFishPunchCount() {
		fishPunchCount++;
	}

	public void resetGame() {
		currentScore = 0;
		fishPunchCount = 0;
	}

	public int getCurrentScore() {
		return currentScore;
	}

	public int getTopScore() {
		return topScore;
	}

	public int getTopFishPunchCount() {
		return topFishPunchCount;
	}

	public int getTotalScoreSum() {
		return totalScoreSum;
	}

	public int getTotalGamesPlayed() {
		return totalGamesPlayed;
	}

	public int getFishPunchCount() {
		return fishPunchCount;
	}

	public boolean getIsSoundOn() {
		return isSoundOn;
	}

	/**
	 * Update loading of assets
	 *
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
		String fullName = String.format("atlases/%s.atlas", fileName);
		return assetManager.get(fullName, TextureAtlas.class);
	}

	public Sound getSound(String fileName) {
		return assetManager.get("sounds/" + fileName, Sound.class);
	}

	public ShaderProgram getShaderProgram(String fileName) {
		ShaderProgram sp = shaders.get(fileName);
		if (sp != null) {
			return sp;
		}

		sp = new ShaderProgram(
				Gdx.files.internal(String.format("shaders/%s.vsh", fileName)),
				Gdx.files.internal(String.format("shaders/%s.fsh", fileName)));
		// check there's no shader compile errors
		if (!sp.isCompiled()) {
			throw new IllegalStateException(sp.getLog());
		}

		shaders.put(fileName, sp);

		return sp;
	}

	public void playIntroMusic() {
		if (isSoundOn) {
			Music music = assetManager.get("sounds/intro.ogg", Music.class);
			music.setVolume(MusicVolume);
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

	public void playPunchSound() {
		if (isSoundOn) {
			getSound("punch.wav").play();
		}
	}

	public void playMoveSound() {
		if (isSoundOn) {
			moveSound = (moveSound + 1) & 1;
			switch (moveSound) {
				case 0:
					getSound("move.wav").play();
					break;
				case 1:
					getSound("move2.wav").play();
					break;
			}
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
			player = new Player(new TextureRegion[]{
					getTextureAtlas("game").findRegion("player1"),
					getTextureAtlas("game").findRegion("player2"),
					getTextureAtlas("game").findRegion("player3"),
					getTextureAtlas("game").findRegion("player2"),
			}, new TextureRegion[]{
					getTextureAtlas("game").findRegion("player_die")
			});
		}

		return player;
	}
}