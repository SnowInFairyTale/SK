package org.test;

public class TowerAirDefence extends Tower
{
	public TowerAirDefence(MainGame game)
	{
		super(game, TowerType.AirDefence, Capability.Air, "assets/towers/air_tower.png");
		super.setTowerLevels(new TowerLevel[] {new TowerLevel(10, 140f, 10, 0.5f, 0.2f, 0f), new TowerLevel(10, 160f, 0x12, 0.5f, 0.2f, 3f), new TowerLevel(15, 180f, 0x19, 0.5f, 0.2f, 3f), new TowerLevel(20, 200f, 0x23, 0.5f, 0.2f, 3f), new TowerLevel(0x19, 200f, 50, 0.5f, 0.2f, 3f)});
		super.SetValuesFromTowerLevel(0);
		super.SetInitialValue();
	}
}