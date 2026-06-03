package org.test;

public class MonsterBerserker extends Monster
{
	public MonsterBerserker(MainGame game, Wave wave, float speed, int startHitPoints, int value)
	{
		super(game, wave, startHitPoints, speed, value, "assets/berserker.png", 32, 32, 0x80, 0x80);
		super.setMonsterType( MonsterType.Berserker);
		super.setRadius( 5f);
		super.setAnimationSpeedRatio( 3);
	}
}