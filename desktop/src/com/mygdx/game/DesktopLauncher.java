package com.mygdx.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mygdx.game.GameScreen;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
		cfg.setWindowedMode(1440, 720);
		cfg.setTitle("MonetkaGame");
		new Lwjgl3Application(new Drop(), cfg);
	}
}
