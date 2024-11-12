package com.igorcrevar.goingunder.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.igorcrevar.goingunder.GoingUnderGame;

public class HtmlLauncher extends GwtApplication {

	@Override
	public GwtApplicationConfiguration getConfig() {
		// Resizable application, uses available space in browser
		// return new GwtApplicationConfiguration(true);
		// Fixed size application:
		return new GwtApplicationConfiguration(720, 1280);
	}

	@Override
	public ApplicationListener createApplicationListener() {
		return new GoingUnderGame();
	}
}