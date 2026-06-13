package com.example.towerdefencegles;

import java.util.ArrayList;

import org.test.Constants;
import org.test.MainGame;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.loon.LGame;
import com.loon.LSetting;
import com.loon.core.graphics.opengl.LTexture;


public class MainActivity extends LGame {

	private static final int BOTTOM_GESTURE_PROTECTION_PX = 180;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FrameLayout layout = getFrameLayout();
		if (layout != null) {
			layout.setBackgroundColor(Color.BLACK);
		}
		applyTouchProtection();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			applyTouchProtection();
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_UP) {
				moveTaskToBack(true);
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}

	@Override
	public void onGamePaused() {
	}

	@Override
	public void onGameResumed() {
		applyTouchProtection();
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

	private void applyTouchProtection() {
		final View decor = getWindow().getDecorView();
		decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

		final FrameLayout layout = getFrameLayout();
		if ((layout == null) || (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)) {
			return;
		}
		layout.post(new Runnable() {
			@Override
			public void run() {
				int width = layout.getWidth();
				int height = layout.getHeight();
				if ((width <= 0) || (height <= 0)) {
					return;
				}
				ArrayList<Rect> rects = new ArrayList<Rect>();
				rects.add(new Rect(0, Math.max(0, height
						- BOTTOM_GESTURE_PROTECTION_PX), width, height));
				layout.setSystemGestureExclusionRects(rects);
				if (decor != layout) {
					decor.setSystemGestureExclusionRects(rects);
				}
			}
		});
	}

}
