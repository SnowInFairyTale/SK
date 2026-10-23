package org.test;

import static org.junit.Assert.*;
import org.junit.Test;

public class BladeWheelPathTest {
	private final BladeWheelPath route = new BladeWheelPath(
			new float[] {0, 100, 100, 400}, new float[] {0, 0, 200, 200});
	private final float[] point = new float[2];

	@Test
	public void measuresTravelAlongCornersInsteadOfStraightLine() {
		assertEquals(600f, route.length, 0.001f);
		route.sample(500f, point);
		assertArrayEquals(new float[] {300, 200}, point, 0.001f);
	}

	@Test
	public void forwardAndReverseChooseDifferentNextCorners() {
		assertEquals(300f, route.nextCorner(200f, 1), 0.001f);
		assertEquals(100f, route.nextCorner(200f, -1), 0.001f);
		assertEquals(600f, route.nextCorner(300f, 1), 0.001f);
		assertEquals(100f, route.nextCorner(300f, -1), 0.001f);
	}

	@Test
	public void joinsNearestPointOnSegmentWithoutSnappingToCorner() {
		float at = route.nearest(130f, 70f);
		assertEquals(170f, at, 0.001f);
		route.sample(at, point);
		assertArrayEquals(new float[] {100, 70}, point, 0.001f);
	}

	@Test
	public void bothLaunchReferencesChooseNearestJoinPointsOnTheRoute() {
		// 人物中心 (60, 40)，左右出生点各偏移 32；投影得到飞行接入点，而非出生点。
		route.sample(route.nearest(28f, 40f), point);
		assertArrayEquals(new float[] {28f, 0f}, point, 0.001f);
		route.sample(route.nearest(92f, 40f), point);
		assertArrayEquals(new float[] {100f, 40f}, point, 0.001f);
	}

	@Test
	public void clampsAtBothEndsInsteadOfLeavingTheRoute() {
		route.sample(-50f, point);
		assertArrayEquals(new float[] {0, 0}, point, 0.001f);
		route.sample(700f, point);
		assertArrayEquals(new float[] {400, 200}, point, 0.001f);
		assertEquals(0f, route.nextCorner(0f, -1), 0f);
		assertEquals(600f, route.nextCorner(600f, 1), 0f);
	}

	@Test
	public void sweptCollisionCatchesFastMotionButDoesNotCutCorner() {
		assertTrue(BladeWheelPath.touchesSegment(0, 0, 100, 0, 50, 5, 6));
		assertFalse(BladeWheelPath.touchesSegment(0, 0, 100, 0, 50, 50, 6));
		assertFalse(BladeWheelPath.touchesSegment(100, 0, 100, 200, 50, 50, 6));
		assertTrue(BladeWheelPath.touchesSegment(100, 200, 100, 200, 102, 200, 3));
	}

	@Test
	public void skipsDuplicatePointsWithoutDivisionByZero() {
		BladeWheelPath repeated = new BladeWheelPath(new float[] {0, 0, 100}, new float[] {0, 0, 0});
		repeated.sample(0f, point);
		assertArrayEquals(new float[] {0, 0}, point, 0f);
		assertEquals(100f, repeated.nextCorner(0f, 1), 0f);
	}
}
