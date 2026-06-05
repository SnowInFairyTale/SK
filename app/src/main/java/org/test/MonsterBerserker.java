package org.test;

public class MonsterBerserker extends Monster
{
	public MonsterBerserker(MainGame game, Wave wave, float speed, int startHitPoints, int value)
	{
		super(game, wave, startHitPoints, speed, value, "assets/berserker.png", 8, 8, 64, 64);
		super.setMonsterType( MonsterType.Berserker);
		super.setRadius(10f);
		super.setAnimationSpeedRatio( 3);
	}
}