package org.test;

public class MonsterPeasant extends Monster {
	public MonsterPeasant(MainGame game, Wave wave, float speed,
			int startHitPoints, int value) {
		super(game, wave, startHitPoints, speed, value, "assets/peasant.png",
				8, 8, 48, 48);
		super.setMonsterType(MonsterType.Peasant);
		super.setRadius(10f);
		super.setAnimationSpeedRatio(3);
	}
}