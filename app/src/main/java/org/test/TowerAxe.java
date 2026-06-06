package org.test;

public class TowerAxe extends Tower
{
	public TowerAxe(MainGame game)
	{
		super(game, TowerType.Axe, Capability.AirGround, "assets/towers/bash_tower.png");
		super.setTowerLevels(new TowerLevel[] {
				new TowerLevel(5, 120f, 8, 1f, 0.6f, 0f),
				new TowerLevel(5, 120f, 15, 1f, 0.6f, 3f),
				new TowerLevel(10, 140f, 25, 1f, 0.6f, 3f),
				new TowerLevel(30, 140f, 40, 1f, 0.6f, 3f),
				new TowerLevel(70, 180f, 50, 1f, 0.6f, 3f),
				new TowerLevel(25, 180f, 55, 1f, 0.6f, 3f) });
		super.SetValuesFromTowerLevel(0);
		super.SetInitialValue();
	}
}