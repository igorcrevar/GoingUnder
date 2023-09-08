package com.igorcrevar.goingunder;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.igorcrevar.goingunder.GoingUnderGame;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(32 * 20, 32 * 20);
		config.setForegroundFPS(60);
		config.setTitle("GoingUnder");
		new Lwjgl3Application(new GoingUnderGame(), config);
	}
}
