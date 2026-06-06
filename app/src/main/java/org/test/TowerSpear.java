package org.test;

public class TowerSpear extends Tower
{
	public TowerSpear(MainGame game)
	{
		super(game, TowerType.Spear, Capability.AirGround, "assets/towers/turbo_tower.png");
		super.setTowerLevels(new TowerLevel[] {
				new TowerLevel(15, 140f, 8, 0.5f, 0.2f, 0f),
				new TowerLevel(12, 140f, 15, 0.5f, 0.2f, 3f),
				new TowerLevel(23, 140f, 25, 0.5f, 0.2f, 3f),
				new TowerLevel(35, 140f, 45, 0.5f, 0.2f, 3f),
				new TowerLevel(75, 140f, 65, 0.5f, 0.2f, 3f),
				new TowerLevel(25, 140f, 70, 0.5f, 0.2f, 3f) });
		super.SetValuesFromTowerLevel(0);
		super.SetInitialValue();
	}
}