package org.test;

public class MonsterChieftain extends Monster {
	public MonsterChieftain(MainGame game, Wave wave, float speed,
			int startHitPoints, int value) {
		super(game, wave, startHitPoints, speed, value, "assets/chieftain.png", 8,
				13, Constants.s(0x27), Constants.s(0x27));
		super.setMonsterType(MonsterType.Chieftain);
		super.setRadius(32f);
		super.setAnimationSpeedRatio(3);
	}
}