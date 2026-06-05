package org.test;

public class TowerSpear extends Tower
{
	public TowerSpear(MainGame game)
	{
		super(game, TowerType.Spear, Capability.AirGround, "assets/towers/turbo_tower.png");
		super.setTowerLevels( new TowerLevel[] {new TowerLevel(15, 140f, 5, 0.5f, 0.2f, 0f), new TowerLevel(12, 140f, 10, 0.5f, 0.2f, 8f), new TowerLevel(0x17, 140f, 0x12, 0.5f, 0.2f, 10f), new TowerLevel(0x23, 140f, 0x22, 0.5f, 0.2f, 14f), new TowerLevel(0x4b, 140f, 0x41, 0.5f, 0.2f, 18f)});
		super.SetValuesFromTowerLevel(0);
		super.SetInitialValue();
	}
}