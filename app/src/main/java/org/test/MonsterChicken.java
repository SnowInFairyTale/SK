package org.test;

import loon.core.geom.Vector2f;

public class MonsterChicken extends Monster {
	private MainGame game;

	public MonsterChicken(MainGame game, Wave wave, float speed,
			int startHitPoints, int value) {
		super(game, wave, startHitPoints, speed, value, "assets/chicken.png",
				8, 8, 64, 64);
		this.game = game;
		super.setMonsterType(MonsterType.Chicken);
		super.setRadius(12f);
		super.setDrawOrder(30);
		super.layoutHealthBar();
	}

	private Vector2f result = new Vector2f();

	static void applyFlightPath(MainGame game, Vector2f gridPosition,
			Vector2f out, Monster monster) {
		if (game.getGameplayScreen().getLevel() >= 3) {
			if (gridPosition.x < 11) {
				monster.setRotation(Utils.GetAngle(new Vector2f(1f, -1f)));
				out.set(gridPosition.x + 1, gridPosition.y - 1);
				return;
			}
			monster.setRotation(0f);
		}
		out.set(gridPosition.x + 1, gridPosition.y);
	}

	@Override
	public Vector2f GetNextGridPoint(Vector2f gridPosition) {
		applyFlightPath(this.game, gridPosition, this.result, this);
		return this.result;
	}

	@Override
	public int GetVerticalTextureOffset() {
		return 0;
	}
}