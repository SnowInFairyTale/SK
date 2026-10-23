package org.test;

import static org.junit.Assert.*;
import org.junit.Test;

public class PiercingArrowAreaTest {
	private final PiercingArrowArea area = new PiercingArrowArea(0f, 0f, 100f, 0f, 300f, 30f);

	@Test
	public void thirtyDegreeSectorIncludesSpaceBetweenRaysAndStopsAtThreeHundred() {
		assertTrue(touchesAt(0f, 1000f, 300f, 0f));
		assertTrue(touchesAt(0f, 1000f, 300f, 7f));
		assertTrue(touchesAt(0f, 1000f, 300f, -7f));
		assertTrue(touchesAt(0f, 1000f, 300f, 15f));
		assertTrue(touchesAt(0f, 1000f, 300f, -15f));
		assertFalse(touchesAt(0f, 1000f, 300f, 15.1f));
		assertFalse(touchesAt(0f, 1000f, 300f, -15.1f));
		assertFalse(touchesAt(0f, 1000f, 300.1f, 0f));
		assertFalse(area.touches(0f, 400f, -1f, 0f, 0f));
	}

	@Test
	public void fastStepSweepsTheWholeArcWithoutHittingPastOrFutureBands() {
		assertTrue(touchesAt(100f, 300f, 200f, 10f));
		assertFalse(area.touches(100f, 300f, 50f, 0f, 5f));
		assertFalse(area.touches(100f, 300f, 350f, 0f, 5f));
		assertTrue(area.touches(0f, 1000f, 299f, 0f, 0f));
	}

	@Test
	public void monsterRadiusTouchesInnerAndOuterArcs() {
		assertTrue(area.touches(100f, 300f, 88f, 0f, 12f));
		assertFalse(area.touches(100f, 300f, 87.9f, 0f, 12f));
		assertTrue(area.touches(100f, 300f, 312f, 0f, 12f));
		assertFalse(area.touches(100f, 300f, 312.1f, 0f, 12f));
	}

	@Test
	public void monsterRadiusTouchesSidesAndRoundedArcEndpoints() {
		float cos = (float) Math.cos(Math.toRadians(15f));
		float sin = (float) Math.sin(Math.toRadians(15f));
		assertTrue(area.touches(100f, 300f, 200f * cos - 12f * sin,
				200f * sin + 12f * cos, 12f));
		assertFalse(area.touches(100f, 300f, 200f * cos - 12.1f * sin,
				200f * sin + 12.1f * cos, 12f));
		assertTrue(area.touches(100f, 300f, 304f * cos - 3f * sin,
				304f * sin + 3f * cos, 5.01f));
		assertFalse(area.touches(100f, 300f, 304f * cos - 4f * sin,
				304f * sin + 4f * cos, 5f));
	}

	@Test
	public void diagonalAndReverseShotsRotateTheSectorAroundTheImpactPoint() {
		PiercingArrowArea diagonal = new PiercingArrowArea(10f, 20f, 13f, 24f, 400f, 30f);
		assertTrue(diagonal.touches(0f, 300f, 160f, 220f, 0f));
		assertFalse(diagonal.touches(0f, 300f, 260f, 20f, 0f));
		PiercingArrowArea reverse = new PiercingArrowArea(100f, 100f, 0f, 100f, 400f, 30f);
		assertTrue(reverse.touches(0f, 400f, -200f, 150f, 0f));
		assertFalse(reverse.touches(0f, 400f, 110f, 100f, 0f));
	}

	@Test
	public void coincidentAimIsFiniteAndEmptySectorDoesNotHit() {
		PiercingArrowArea coincident = new PiercingArrowArea(10f, 20f, 10f, 20f, 400f, 30f);
		assertEquals(1f, coincident.dx, 0f);
		assertEquals(0f, coincident.dy, 0f);
		assertTrue(coincident.touches(0f, 100f, 60f, 20f, 0f));
		assertFalse(new PiercingArrowArea(0f, 0f, 1f, 0f, 400f, 0f).touches(0f, 400f, 50f, 0f, 10f));
		assertFalse(new PiercingArrowArea(0f, 0f, 1f, 0f, 0f, 30f).touches(0f, 400f, 0f, 0f, 10f));
	}

	private boolean touchesAt(float from, float to, float distance, float degrees) {
		double angle = Math.toRadians(degrees);
		return area.touches(from, to, distance * (float) Math.cos(angle),
				distance * (float) Math.sin(angle), 0f);
	}
}
