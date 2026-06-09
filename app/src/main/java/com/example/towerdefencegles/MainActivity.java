package com.example.towerdefencegles;

import org.test.Constants;
import org.test.MainGame;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.loon.LGame;
import com.loon.LSetting;
import com.loon.core.graphics.opengl.LTexture;


public class MainActivity extends LGame {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FrameLayout layout = getFrameLayout();
		if (layout != null) {
			layout.setBackgroundColor(Color.BLACK);
		}
	}

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
		setting.width = Constants.SCREEN_WIDTH;
		setting.height = Constants.SCREEN_HEIGHT;
		setting.showFPS = false;
		setting.fps = 30;
		setting.landscape = false;
		setting.mode = LMode.Ratio;
		register(setting, MainGame.class);
	}

}
