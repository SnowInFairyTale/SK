package org.test;

/** 塔的技能解锁、单次普攻触发和空地目标规则。 */
final class TowerCombatRules {
	private TowerCombatRules() { }

	/** roll 为本次普攻的一次随机值，范围 [0, 1)。 */
	static boolean triggersSkill(TowerType tower, GemType gem, double roll) {
		switch (tower) {
		case Axe:
			return gem == GemType.Red;
		case Lur:
			return gem == GemType.Gold;
		case Spear:
			return gem == GemType.Purple && roll >= 0.0 && roll < 0.40;
		case AirDefence:
			return gem == GemType.Purple && roll >= 0.0 && roll < 1.0;
		default:
			return false;
		}
	}

	static boolean canDamage(TowerType tower, MonsterType target) {
		if (tower == TowerType.AirDefence) return target == MonsterType.Chicken;
		if (tower == TowerType.Lur) return target != MonsterType.Chicken;
		return true;
	}
}
