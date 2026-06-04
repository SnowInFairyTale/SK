package org.test;

import loon.core.geom.Vector2f;
import loon.core.graphics.LFont;

public final class Constants {

	/** Logical scale factor matching 4x upscaled assets (320x480 -> 1280x1920). */
	public static final int DisplayScale = 4;

	/**
	 * One cell in png/monsterinfo*, png/towerinfo*, and root monsterinfo* sheets
	 * (1x design 40px; textures were downscaled 50% from the former 80px cells).
	 */
	public static final int InfoSpriteCell = 40;

	public static int infoSpriteCell() {
		return s(InfoSpriteCell);
	}

	/** 1x=20px: center a halved info sprite in the old 80px layout slot on full-size backgrounds. */
	public static float infoSpriteLayoutInset() {
		return s(20f);
	}

	/** 1x px: optional nudge for info-screen body text and sprites (not the back label). */
	public static final float InfoScreenOffsetY = 0f;

	public static float infoScreenLayoutOffsetY() {
		return s(InfoScreenOffsetY);
	}

	/** 1x=435: back button label on screen_monsters / towers_2 (no body offset). */
	public static float infoScreenBackY() {
		return s(435f);
	}

	/**
	 * Draw scale on monster/tower info screens: half-res cells sampled at
	 * {@link #infoSpriteCell()}, displayed at the former full 80px (320px) size.
	 */
	public static final float InfoScreenSpriteDrawScale = 2f;

	public static float infoScreenSpriteDrawScale() {
		return InfoScreenSpriteDrawScale;
	}

	/** Same 2× draw as info screens for toolbar / next-wave preview on half-res sheets. */
	public static float toolbarInfoSpriteDrawScale() {
		return InfoScreenSpriteDrawScale;
	}

	/** 1x=-2,-34: monster/tower toolbar sprite offset from panel origin. */
	public static float toolbarSpriteOffsetX() {
		return s(-2f);
	}

	public static float toolbarSpriteOffsetY() {
		return s(-34f);
	}

	/** 1x=200,-4: next-wave preview (root monsterinfo* sheet, 12 columns). */
	public static float nextWavePreviewX() {
		return s(200f);
	}

	public static float nextWavePreviewY() {
		return s(-4f);
	}

	public static int infoScreenY(int logicalY) {
		return s(logicalY) + (int) infoScreenLayoutOffsetY();
	}

	public static float infoScreenY(float logicalY) {
		return s(logicalY) + infoScreenLayoutOffsetY();
	}

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

	/** Menu row hit area (matches DrawLevelText / menu label rows). */
	public static final float MENU_HIT_W = 440f;
	public static final float MENU_HIT_H = 164f;

	public static Vector2f menuHitSize() {
		return new Vector2f(MENU_HIT_W, MENU_HIT_H);
	}

	public static Vector2f menuHitAtCenterY(float centerY) {
		return new Vector2f(ScreenCenterX - MENU_HIT_W / 2f, centerY - MENU_HIT_H / 2f);
	}

	/** Center of a 2×2 tower footprint from grid corner (was 20f at 320×480). */
	public static final float TOWER_GRID_CENTER_OFFSET = s(20f);

	/** Tower building sprite anchor from logical position (was 26, 38). */
	public static final float TOWER_DRAW_OFFSET_X = s(26f);
	public static final float TOWER_DRAW_OFFSET_Y = s(38f);

	/** Top HUD strip (4× asset widths: heart 284, wave 712, cash 284). */
	public static final float HUD_HEART_X = 0f;
	public static final float HUD_HEART_Y = 0f;
	public static final float HUD_WAVE_X = 284f;
	public static final float HUD_WAVE_Y = 0f;
	public static final float HUD_CASH_X = 996f;
	public static final float HUD_CASH_Y = 0f;
}
