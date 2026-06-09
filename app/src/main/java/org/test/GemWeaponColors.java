package org.test;

import com.loon.core.graphics.LColor;

/** Flying weapon tint for gem towers only; non-gem towers use raw sprite art. */
public final class GemWeaponColors {

	/** Purple gem weapon color. */
	public static final LColor PURPLE = new LColor(0.58f, 0.28f, 0.92f, 1f);
	/** Red gem — same red previously applied to spears. */
	public static final LColor RED = new LColor(1f, 0f, 0f, 1f);
	/** Gold gem — same gold previously applied to axes. */
	public static final LColor GOLD = new LColor(0.95f, 0.72f, 0.15f, 1f);

	private GemWeaponColors() {
	}

	/**
	 * @return recolor target for {@link TextureRecolor#recolorBlackTo}, or
	 *         {@code null} when the tower has no gem (keep asset colors).
	 */
	public static LColor getRecolor(GemType gem) {
		switch (gem) {
		case Purple:
			return PURPLE;
		case Red:
			return RED;
		case Gold:
			return GOLD;
		default:
			return null;
		}
	}
}
