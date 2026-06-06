package org.test;

import loon.core.geom.Vector2f;
import loon.core.graphics.LFont;

/**
 * Native 2x layout (640×960). All pixel positions, sizes, and font sizes are
 * screen pixels — no scale conversion.
 */
public final class Constants {

	public static final int SCREEN_WIDTH = 640;
	public static final int SCREEN_HEIGHT = 960;

	/** Path grid: 18×19 cells, 40px per cell. */
	public static final int GridHeight = 0x13;
	public static final int GridSize = 40;
	public static final int GridWidth = 0x12;
	public static final int HorizontalGridOffset = -40;
	public static final int VerticalGridOffset = 80;

	public static final int InitialRemainingLives = 20;
	public static final int InitialCash = 5000;

	public static final int InitialPurpleGems = 0;
	public static final int InitialRedGems = 0;
	public static final int InitialGoldGems = 0;

	/** Radians — not pixels. */
	public static final float PiOver8 = 0.3926991f;
	public static final float SecondsPerFrame = 0.03333334f;

	// --- Toolbar layout (screen pixels) ---

	/** Bottom monster info bar top-left. */
	public static final float MONSTER_TOOLBAR_X = 20f;
	public static final float MONSTER_TOOLBAR_Y = 840f;
	/** Monster preview sprite offset from toolbar anchor. */
	public static final float MONSTER_TOOLBAR_SPRITE_OFFSET_X = -4f;
	public static final float MONSTER_TOOLBAR_SPRITE_OFFSET_Y = -68f;

	/** Bottom tower info bar top-left. */
	public static final float TOWER_TOOLBAR_X = 20f;
	public static final float TOWER_TOOLBAR_Y = 840f;
	/** Tower preview sprite offset from toolbar anchor. */
	public static final float TOWER_TOOLBAR_SPRITE_OFFSET_X = -4f;
	public static final float TOWER_TOOLBAR_SPRITE_OFFSET_Y = -68f;

	/** Info-screen animated sprite frame size (towerinfo / monsterinfo sheets). */
	public static final int INFO_SPRITE_SIZE = 160;

	// --- Main / select-level menu button labels (screen pixels) ---

	public static final float MENU_BTN_CENTER_X = 320f;
	public static final float MENU_BTN_HEIGHT = 100f;
	public static final float MENU_BTN_ROW_1_Y = 415f;
	public static final float MENU_BTN_ROW_2_Y = 535f;
	public static final float MENU_BTN_ROW_3_Y = 655f;
	public static final float MENU_BTN_ROW_4_Y = 775f;

	/** Win / lose menu button hit rect: x=192, width=280. */
	public static final float RESULT_MENU_BTN_CENTER_X = 332f;
	public static final float RESULT_MENU_BTN_TOP_Y = 792f;
	public static final float RESULT_MENU_BTN_HEIGHT = 100f;
	/** Win / lose menu label only — negative moves text up (screen pixels). */
	public static final float RESULT_MENU_BTN_LABEL_OFFSET_Y = -20f;

	// Instruction screen bottom buttons (screen pixels)

	public static final float INTRO_BTN_TOP_Y = 850f;
	/** Slightly taller than hit height (76) — nudges label down a few pixels. */
	public static final float INTRO_BTN_TEXT_HEIGHT = 86f;
	public static final float INTRO_BTN_TOWERS_CENTER_X = 124f;
	public static final float INTRO_BTN_ENEMIES_CENTER_X = 358f;
	public static final float INTRO_BTN_MENU_CENTER_X = 556f;

	// TextSprite button label vertical align (screen pixels)

	/** Matches jar {@code drawString} using {@code getHeight() - 2}. */
	public static final float TEXT_LABEL_DRAWSTRING_HEIGHT_ADJUST = 2f;

	/**
	 * Top offset added in {@code TextSprite.drawCenteredInButton} so rasterized
	 * labels line up with {@link Utils#DrawButtonLabel} / {@code drawString}.
	 */
	public static float textLabelButtonTopOffset(LFont font, float imageHeight) {
		float fontHeight = font.getHeight();
		return (fontHeight / 2f) - TEXT_LABEL_DRAWSTRING_HEIGHT_ADJUST
				+ font.getAscent() + (imageHeight / 2f);
	}

	// Tower / monster info screens — shared back button (screen pixels)

	public static final float INFO_BACK_HIT_LEFT = 220f;
	public static final float INFO_BACK_HIT_WIDTH = 240f;
	public static final float INFO_BACK_HIT_HEIGHT = 76f;
	public static final float INFO_BACK_BTN_TOP_Y = 844f;
	public static final float INFO_BACK_BTN_CENTER_X = INFO_BACK_HIT_LEFT
			+ INFO_BACK_HIT_WIDTH / 2f;
	public static final float INFO_BACK_BTN_TEXT_HEIGHT = 90f;
	public static final int INFO_BACK_FONT_SIZE = 32;
	/** Overlay layer: above preview animations (50), below nothing else. */
	public static final int INFO_OVERLAY_DRAW_ORDER = 150;

	public static LFont font(int pixelSize) {
		return LFont.getFont(pixelSize);
	}
}
