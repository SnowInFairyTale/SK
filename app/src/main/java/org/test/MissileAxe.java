package org.test;

public class MissileAxe extends Missile {

	public MissileAxe(MainGame game, Monster targetMonster, Tower tower) {
		super(game, MissileType.AXE, "assets/axe.png", tower.getPosition(),
				targetMonster, tower.getDamage(), 8, 8, Constants.s(0x10),
				Constants.s(0x10));

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