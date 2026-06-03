package org.test;

public class MonsterChieftain extends Monster {
	public MonsterChieftain(MainGame game, Wave wave, float speed,
			int startHitPoints, int value) {
		super(game, wave, startHitPoints, speed, value, "assets/chieftain.png",
				32, 52, 0x9c, 0x9c);
		super.setMonsterType(MonsterType.Chieftain);
		super.setRadius(32f);
		super.setAnimationSpeedRatio(3);
	}
}