package org.test;

public class MissileAxe extends Missile {

	public MissileAxe(MainGame game, Monster targetMonster, Tower tower) {
		super(game, MissileType.AXE, "assets/axe.png", tower.getPosition(),
				targetMonster, tower.getDamage(), 8, 32, 0x40, 0x40);

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