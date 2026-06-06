package org.test;

public class TowerAirDefence extends Tower
{
	public TowerAirDefence(MainGame game)
	{
		super(game, TowerType.AirDefence, Capability.Air, "assets/towers/air_tower.png");
		super.setTowerLevels(new TowerLevel[] {
				new TowerLevel(10, 140f, 10, 0.5f, 0.2f, 0f),
				new TowerLevel(10, 160f, 18, 0.5f, 0.2f, 3f),
				new TowerLevel(15, 180f, 25, 0.5f, 0.2f, 3f),
				new TowerLevel(20, 200f, 35, 0.5f, 0.2f, 3f),
				new TowerLevel(25, 200f, 50, 0.5f, 0.2f, 3f),
				new TowerLevel(25, 200f, 55, 0.5f, 0.2f, 3f) });
		super.SetValuesFromTowerLevel(0);
		super.SetInitialValue();
	}
}