package com.hedgecoon.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.hedgecoon.game.AdsHandler;
import com.hedgecoon.game.Main;

public class DesktopLauncher implements AdsHandler {
	private static DesktopLauncher application;
	public static void main (String[] arg) {
		if (application == null) {
			application = new DesktopLauncher();
		}

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		config.height = 720;
		//config.fullscreen = true;
		/*config.width = 1920;
		config.height = 1200;*/
		new LwjglApplication(new Main(application), config);
	}

	@Override
	public void showAds(boolean show) {

	}
}
