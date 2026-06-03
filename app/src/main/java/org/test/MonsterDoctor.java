package org.test;

public class MonsterDoctor extends Monster
{
	public MonsterDoctor(MainGame game, Wave wave, float speed, int startHitPoints, int value)
	{
		super(game, wave, startHitPoints, speed, value, "assets/doctor.png", 32, 32, 160, 160);
		super.setMonsterType(MonsterType.Doctor);
		super.setRadius(32f);
		super.setAnimationSpeedRatio( 3);
	}
}