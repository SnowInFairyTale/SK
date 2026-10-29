package org.test;

import static org.junit.Assert.*;
import org.junit.Test;

public class CleaveAreaTest {
	private final CleaveArea area = new CleaveArea(40f, 60f, 120f);

	@Test
	public void hitsAllDirectionsIncludingBehindThePrimaryTarget() {
		for (int degrees = 0; degrees < 360; degrees++) {
			double angle = Math.toRadians(degrees);
			assertTrue("direction " + degrees, area.contains(
					40f + 119f * (float) Math.cos(angle),
					60f + 119f * (float) Math.sin(angle)));
		}
	}

	@Test
	public void visualGapsStillDealDamage() {
		// 三道弧以 0、120、240 度为中心，间隙位于 60、180、300 度。
		for (int degrees : new int[] { 60, 180, 300 }) {
			double angle = Math.toRadians(degrees);
			assertTrue(area.contains(40f + 100f * (float) Math.cos(angle),
					60f + 100f * (float) Math.sin(angle)));
		}
	}

	@Test
	public void includesCenterAndExactRadiusButExcludesOutside() {
		assertTrue(area.contains(40f, 60f));
		assertTrue(area.contains(160f, 60f));
		assertTrue(area.contains(-80f, 60f));
		assertTrue(area.contains(40f, 180f));
		assertTrue(area.contains(40f, -60f));
		for (int degrees = 0; degrees < 360; degrees++) {
			double angle = Math.toRadians(degrees);
			assertFalse(area.contains(40f + 120.1f * (float) Math.cos(angle),
					60f + 120.1f * (float) Math.sin(angle)));
		}
		assertFalse(area.contains(140f, 160f));
	}

	@Test
	public void nonPositiveRadiusDisablesDamage() {
		assertFalse(new CleaveArea(40f, 60f, 0f).contains(40f, 60f));
		assertFalse(new CleaveArea(40f, 60f, -120f).contains(40f, 60f));
	}
}
