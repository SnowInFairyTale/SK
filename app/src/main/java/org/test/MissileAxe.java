package org.test;

public class MissileAxe extends Missile {
	private final MainGame owner;
	private final boolean cleave;

	public MissileAxe(MainGame game, Monster targetMonster, Tower tower) {
		super(game, MissileType.AXE, "assets/axe.png", tower.getPosition(),
				targetMonster, tower.getDamage(), 8, 8, 32, 32,
				tower.getWeaponGemType());
		this.owner = game;
		this.cleave = tower.rollSkillTrigger();
	}

	@Override
	protected void onImpact(Monster target) {
		if (this.cleave) {
			CleaveEffect.trigger(this.owner, target, this.getDirection(), this.getDamage());
		}
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
