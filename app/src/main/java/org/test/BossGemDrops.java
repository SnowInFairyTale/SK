package org.test;

import loon.utils.MathUtils;

public final class BossGemDrops {

	private BossGemDrops() {
	}

	/** 25% none, 40% purple, 25% red, 10% gold. */
	public static GemType roll() {
		int roll = MathUtils.random(0, 99);
		if (roll < 25) {
			return GemType.None;
		}
		if (roll < 65) {
			return GemType.Purple;
		}
		if (roll < 90) {
			return GemType.Red;
		}
		return GemType.Gold;
	}
}
