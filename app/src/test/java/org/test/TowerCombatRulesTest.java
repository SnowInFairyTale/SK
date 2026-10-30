package org.test;

import static org.junit.Assert.*;
import org.junit.Test;

public class TowerCombatRulesTest {
	@Test
	public void eachTowerRequiresItsMatchingGem() {
		assertGemMapping(TowerType.Axe, GemType.Red);
		assertGemMapping(TowerType.Spear, GemType.Purple);
		assertGemMapping(TowerType.AirDefence, GemType.Purple);
		assertGemMapping(TowerType.Lur, GemType.Gold);
	}

	private void assertGemMapping(TowerType tower, GemType required) {
		for (GemType gem : GemType.values()) {
			assertEquals(tower + ": " + gem, gem == required,
					TowerCombatRules.triggersSkill(tower, gem, 0.0));
			if (gem != required) {
				assertFalse(TowerCombatRules.triggersSkill(tower, gem, 0.99));
			}
		}
	}

	@Test
	public void unlockedAxeAndLurTriggerEveryAttack() {
		for (int i = 0; i < 100; i++) {
			double roll = (i + 0.5) / 100.0;
			assertTrue(TowerCombatRules.triggersSkill(TowerType.Axe, GemType.Red, roll));
			assertTrue(TowerCombatRules.triggersSkill(TowerType.Lur, GemType.Gold, roll));
		}
	}

	@Test
	public void purpleSpearAndAirDefenceUseTheirConfiguredTriggerRates() {
		for (TowerType tower : new TowerType[] { TowerType.Spear, TowerType.AirDefence }) {
			int expectedPercent = tower == TowerType.AirDefence ? 100 : 40;
			double threshold = expectedPercent / 100.0;
			int triggered = 0;
			for (int i = 0; i < 100; i++) {
				if (TowerCombatRules.triggersSkill(tower, GemType.Purple, (i + 0.5) / 100.0)) triggered++;
			}
			assertEquals(expectedPercent, triggered);
			assertTrue(TowerCombatRules.triggersSkill(tower, GemType.Purple, threshold - 0.000001));
			assertFalse(TowerCombatRules.triggersSkill(tower, GemType.Purple, threshold));
			assertEquals(tower == TowerType.AirDefence,
					TowerCombatRules.triggersSkill(tower, GemType.Purple, 0.999999));
		}
	}

	@Test
	public void airDefenceAndLurHaveOppositeTargetRestrictions() {
		for (MonsterType target : MonsterType.values()) {
			boolean flying = target == MonsterType.Chicken;
			assertEquals(flying, TowerCombatRules.canDamage(TowerType.AirDefence, target));
			assertEquals(!flying, TowerCombatRules.canDamage(TowerType.Lur, target));
			assertTrue(TowerCombatRules.canDamage(TowerType.Axe, target));
			assertTrue(TowerCombatRules.canDamage(TowerType.Spear, target));
		}
	}
}
