package org.test;

public enum GemType {
	None,
	Purple,
	Red,
	Gold;

	public int getAttackBonus() {
		switch (this) {
		case Purple:
			return 10;
		case Red:
			return 15;
		case Gold:
			return 20;
		default:
			return 0;
		}
	}

	public String getTextureFile() {
		switch (this) {
		case Purple:
			return "assets/purple_gem.png";
		case Red:
			return "assets/red_gem.png";
		case Gold:
			return "assets/gold_gem.png";
		default:
			return null;
		}
	}

	public boolean appliesSpeedBonus() {
		return this == Gold;
	}

	public static GemType[] equippable() {
		return new GemType[] { Purple, Red, Gold };
	}
}
