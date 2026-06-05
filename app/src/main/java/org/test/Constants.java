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

	public static final int InitialRemainingLives = 1000;

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

	public static LFont font(int pixelSize) {
		return LFont.getFont(pixelSize);
	}
}
