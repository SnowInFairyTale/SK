package org.test;

public class TowerLur extends Tower
{
	public TowerLur(MainGame game)
	{
		super(game, TowerType.Lur, Capability.Bash, "assets/towers/normal_tower.png");
		super.setTowerLevels(new TowerLevel[] {
				new TowerLevel(15, 90f, 15, 2f, 0.8f, 0f),
				new TowerLevel(15, 100f, 30, 2f, 0.8f, 3f),
				new TowerLevel(25, 110f, 50, 2f, 0.8f, 3f),
				new TowerLevel(40, 120f, 75, 2f, 0.8f, 3f),
				new TowerLevel(100, 120f, 150, 2f, 0.8f, 3f),
				new TowerLevel(25, 120f, 155, 2f, 0.8f, 3f) });
		super.SetValuesFromTowerLevel(0);
		super.SetInitialValue();
	}
}