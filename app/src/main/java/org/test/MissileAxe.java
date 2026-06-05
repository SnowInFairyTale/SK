package org.test;

import loon.core.graphics.LColor;

public class MissileAxe extends Missile {

	/** Non-black pixels (blade, handle art) → gold; black halo unchanged. */
	private static final LColor AXE_RECOLOR = new LColor(0.95f, 0.72f, 0.15f, 1f);

	public MissileAxe(MainGame game, Monster targetMonster, Tower tower) {
		super(game, MissileType.AXE, "assets/axe.png", tower.getPosition(),
				targetMonster, tower.getDamage(), 8, 8, 32, 32);

	}

	@Override
	protected LColor getBlackRecolorTarget() {
		return AXE_RECOLOR;
	}

	private int privateDamage;

	@Override
	public int getDamage() {
		return privateDamage;
	}

	@Override
	public void setDamage(int value) {
		privateDamage = value;
	}
}