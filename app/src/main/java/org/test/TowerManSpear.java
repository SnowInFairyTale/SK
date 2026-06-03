package org.test;

public class TowerManSpear extends TowerMan
{
	public TowerManSpear(MainGame game, Tower tower)
	{
		super(game, "assets/tower_spearman.png", tower, Constants.s(40),
				Constants.s(40), 8, 0x10);
	}
}