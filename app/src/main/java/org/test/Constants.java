package org.test;

import loon.core.graphics.LFont;

public final class Constants {

	/** Logical scale factor matching 4x upscaled assets (320x480 -> 1280x1920). */
	public static final int DisplayScale = 4;

	public static final int GridHeight = 0x13;
	public static final int GridSize = 20 * DisplayScale;
	public static final int GridWidth = 0x12;
	public static final int HorizontalGridOffset = -20 * DisplayScale;
	public static final int InitialRemainingLives = 20;
	public static final float PiOver8 = 0.3926991f;
	public static final float SecondsPerFrame = 0.03333334f;
	public static final int VerticalGridOffset = 40 * DisplayScale;

	public static final int ScreenWidth = 320 * DisplayScale;
	public static final int ScreenHeight = 480 * DisplayScale;
	public static final float ScreenCenterX = ScreenWidth / 2f;

	public static int s(int value) {
		return value * DisplayScale;
	}

	public static float s(float value) {
		return value * DisplayScale;
	}

	public static LFont uiFont(int baseSize) {
		return LFont.getFont(baseSize * DisplayScale);
	}
}
