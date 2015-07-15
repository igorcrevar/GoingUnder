import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;
import com.igorcrevar.goingunder.GameListener;
import com.igorcrevar.goingunder.GameManager;
import com.igorcrevar.goingunder.IActivityRequestHandler;

public class DesktopRunner {
	public static void main(String[] arg) {
		int i = 1;
		if (i == 0) {
			createAtlas();
		}
		else if (i == 2) {
			createAtlasWidgets();
		}
		else {
			runGame();
		}
	}
	
	private static void createAtlasWidgets() {
		Settings settings = new Settings();
		settings.minHeight = 512;
		settings.minWidth = 512;
		settings.maxHeight = 512;
		settings.maxWidth = 512;
		settings.paddingY = 2;
		settings.paddingX = 2;
		settings.wrapY = TextureWrap.Repeat;
		TexturePacker2.process(settings, 
				//"D:\\gamepictures\\widgets\\",
				"D:\\gamepictures\\widgets\\redjavolak",				
				"D:/MySelf/Android/Workspace/HgRepo/GoingUnder/assets/atlases", "widgets");
	}
	
	private static void createAtlas() {
		Settings settings = new Settings();
		settings.minHeight = 512;
		settings.minWidth = 512;
		settings.maxHeight = 512;
		settings.maxWidth = 512;
		settings.paddingY = 2;
		settings.paddingX = 2;
		settings.wrapY = TextureWrap.Repeat;
		TexturePacker2.process(settings, 
				"D:\\gamepictures\\2\\",
				"D:/MySelf/Android/Workspace/HgRepo/GoingUnder/assets/atlases", "game");
	}
	
	private static void runGame() {
		GameListener gameListener = new GameListener(new IActivityRequestHandler() {
			private boolean isLoggedIn;
			@Override
			public void showAds(boolean show) {
			}

			@Override
			public void rate() {
				System.out.println("rate");
			}

			@Override
			public void share(int score) {
				System.out.println("share " + Integer.toString(score));
			}

			@Override
			public void finishGame(long score) {
				System.out.println("Finish game called " + score);
			}

			@Override
			public void showAchievements() {
				System.out.println("showAchievements");
			}

			@Override
			public void showLeaderboards() {
				System.out.println("showLeaderboards");
			}

			@Override
			public boolean getSignedInGPGS() {
				return isLoggedIn;
			}

			@Override
			public void loginGPGS() {
				System.out.println("loginGPGS");
				isLoggedIn = true;
			}

			@Override
			public void setGameManager(GameManager gameManager) {
				// TODO Auto-generated method stub
				
			}
		});
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Going under";
		
		cfg.width = 384;
		
		cfg.width = 480;
		
		//cfg.width = 1080;
		cfg.height = (int)(800.0f / 480 * cfg.width);
		// cfg.height = (int)(1920.0f / 1080 * cfg.width);

		
	    new LwjglApplication(gameListener, cfg);
	}
}
