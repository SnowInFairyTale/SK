package org.test;

import loon.core.geom.Vector2f;

public class MonsterPeon extends Monster {
	public MonsterPeon(MainGame game, Wave wave, float speed,
			int startHitPoints, int value) {
		super(game, wave, startHitPoints, speed, value, "assets/peon.png", 8,
				8, 48, 48);
		this.Init();
	}

	public MonsterPeon(MainGame game, Wave wave, float speed,
			int startHitPoints, int value, Vector2f gridPosition) {
		super(game, wave, startHitPoints, speed, value, "assets/peon.png", 8,
				8, 48, 48, gridPosition);
		this.Init();
	}

	private void Init() {
		super.setMonsterType(MonsterType.Peon);
		super.setRadius(10f);
		super.setAnimationSpeedRatio(3);
	}
}