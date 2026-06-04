package com.example.towerdefencegles;

import org.test.Constants;
import org.test.MainGame;

import loon.LGame;
import loon.core.graphics.opengl.LTexture;


public class MainActivity extends LGame {

	@Override
	public void onGamePaused() {
	}

	@Override
	public void onGameResumed() {
	}

	@Override
	public void onMain() {
		LTexture.ALL_LINEAR = true;
		LSetting setting = new LSetting();
		setting.width = Constants.ScreenWidth;
		setting.height = Constants.ScreenHeight;
		setting.showFPS = false;
		setting.fps = 30;
		setting.landscape = false;
		setting.mode = LMode.Ratio;
		register(setting, MainGame.class);
	}

}
