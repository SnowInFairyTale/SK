package org.test;

import loon.core.geom.Vector2f;


public class TowerManLur extends TowerMan
{
	public TowerManLur(MainGame game, Tower tower)
	{
		super(game, "assets/tower_lurman.png", tower, 160, 160, 0x40, 0x40);
		super.setVerticalTextureOffset( 0);
	}

	@Override
	public void UpdateThrowDirection(Vector2f direction)
	{
	}
}