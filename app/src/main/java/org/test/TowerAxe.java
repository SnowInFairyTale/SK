package org.test;

public class TowerAxe extends Tower
{
	public TowerAxe(MainGame game)
	{
		super(game, TowerType.Axe, Capability.AirGround, "assets/towers/bash_tower.png");
		super.setTowerLevels(new TowerLevel[] {new TowerLevel(5, 120f, 10, 1.5f, 0.6f, 0f), new TowerLevel(5, 120f, 20, 1.5f, 0.6f, 3f), new TowerLevel(10, 140f, 0x23, 1.5f, 0.6f, 3f), new TowerLevel(30, 140f, 60, 1.5f, 0.6f, 3f), new TowerLevel(70, 180f, 80, 1.5f, 0.6f, 3f)});
		super.SetValuesFromTowerLevel(0);
		super.SetInitialValue();
	}
}