package org.test;

public final class Gems {
	private final MainGame game;
	private int goldCount;
	private int purpleCount;
	private int redCount;

	public Gems(MainGame game, int purpleCount, int redCount, int goldCount) {
		this.game = game;
		this.purpleCount = purpleCount;
		this.redCount = redCount;
		this.goldCount = goldCount;
	}

	public final int getCount(GemType type) {
		switch (type) {
		case Purple:
			return this.purpleCount;
		case Red:
			return this.redCount;
		case Gold:
			return this.goldCount;
		default:
			return 0;
		}
	}

	public final void add(GemType type) {
		if (type == GemType.None) {
			return;
		}
		switch (type) {
		case Purple:
			this.purpleCount++;
			break;
		case Red:
			this.redCount++;
			break;
		case Gold:
			this.goldCount++;
			break;
		default:
			return;
		}
		this.game.getGameplayScreen().GemsChanged();
	}

	public final boolean tryConsume(GemType type) {
		if (this.getCount(type) <= 0) {
			return false;
		}
		switch (type) {
		case Purple:
			this.purpleCount--;
			break;
		case Red:
			this.redCount--;
			break;
		case Gold:
			this.goldCount--;
			break;
		default:
			return false;
		}
		this.game.getGameplayScreen().GemsChanged();
		return true;
	}
}
