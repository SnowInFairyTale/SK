package org.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ChainLightningSequenceTest {
	private static final class Target {
		float x, y;
		boolean alive = true;
		int hits;
		Target(float x, float y) { this.x = x; this.y = y; }
	}

	private final ChainLightningSequence.Targets<Target> access =
			new ChainLightningSequence.Targets<Target>() {
				public float x(Target t) { return t.x; }
				public float y(Target t) { return t.y; }
				public boolean alive(Target t) { return t.alive; }
				public void hit(Target t) { t.hits++; }
			};

	@Test
	public void hitsFourDistinctTargetsIncludingThePaidFirstImpact() {
		Target first = new Target(0, 0), a = new Target(100, 0),
				b = new Target(200, 0), c = new Target(300, 0), d = new Target(400, 0);
		ChainLightningSequence<Target> sequence = new ChainLightningSequence<>(first, access);
		List<Target> active = Arrays.asList(first, d, c, b, a);
		sequence.update(0.049f, active);
		assertEquals(1, sequence.getHits().size());
		sequence.update(0.001f, active);
		assertSame(a, sequence.getHits().get(1).target);
		sequence.update(0.10f, active);
		assertEquals(4, sequence.getHits().size());
		assertEquals(0, first.hits);
		assertEquals(1, a.hits);
		assertEquals(1, b.hits);
		assertEquals(1, c.hits);
		assertEquals(0, d.hits);
		sequence.update(2f, active);
		assertEquals(1, c.hits);
		assertTrue(sequence.isFinished());
	}

	@Test
	public void skipsDeadOrDepartedTargetsAndRechecksPositionsAtEachHop() {
		Target first = new Target(0, 0), dead = new Target(10, 0),
				departed = new Target(20, 0), moved = new Target(30, 0), next = new Target(60, 0);
		List<Target> active = new ArrayList<>(Arrays.asList(first, dead, departed, moved, next));
		ChainLightningSequence<Target> sequence = new ChainLightningSequence<>(first, access);
		dead.alive = false;
		active.remove(departed);
		moved.x = 500;
		sequence.update(0.05f, active);
		assertSame(next, sequence.getHits().get(1).target);
		assertEquals(0, dead.hits + departed.hits + moved.hits);
	}

	@Test
	public void aKilledFirstTargetStillConductsFromItsLastPosition() {
		Target first = new Target(40, 60), next = new Target(100, 60);
		ChainLightningSequence<Target> sequence = new ChainLightningSequence<>(first, access);
		first.alive = false;
		sequence.update(0.05f, Arrays.asList(next));
		assertEquals(1, next.hits);
		assertEquals(40f, sequence.getHits().get(0).x, 0f);
	}

	@Test
	public void rangeUsesThePreviousTargetAndNeverBridgesAnEmptyGap() {
		Target first = new Target(0, 0), edge = new Target(180, 0), far = new Target(361, 0);
		ChainLightningSequence<Target> sequence = new ChainLightningSequence<>(first, access);
		sequence.update(0.15f, Arrays.asList(first, edge, far));
		assertEquals(1, edge.hits);
		assertEquals(0, far.hits);
		far.x = 181; // A completed chain must not wake up when a new target enters.
		sequence.update(0.05f, Arrays.asList(first, edge, far));
		assertEquals(0, far.hits);
	}

	@Test
	public void overlappingTargetsAreHitOnlyOnceAndLinksFollowMovement() {
		Target first = new Target(0, 0), next = new Target(0, 0);
		ChainLightningSequence<Target> sequence = new ChainLightningSequence<>(first, access);
		sequence.update(0.05f, Arrays.asList(first, next));
		next.x = 25;
		sequence.update(0.05f, Arrays.asList(first, next));
		assertEquals(2, sequence.getHits().size());
		assertEquals(25f, sequence.getHits().get(1).x, 0f);
		assertEquals(1, next.hits);
	}

	@Test
	public void cancellationStopsPendingDamageAndFadeRetainsTheLastArc() {
		Target first = new Target(0, 0), next = new Target(100, 0);
		ChainLightningSequence<Target> sequence = new ChainLightningSequence<>(first, access);
		sequence.update(0.05f, Arrays.asList(first, next));
		sequence.update(0.10f, Arrays.asList(first, next));
		assertFalse(sequence.isFinished());
		sequence.update(0.09f, Arrays.asList(first, next));
		assertTrue(sequence.isFinished());
		ChainLightningSequence<Target> cancelled = new ChainLightningSequence<>(first, access);
		cancelled.cancel();
		cancelled.update(0.2f, Arrays.asList(first, next));
		assertEquals(1, next.hits);
		assertTrue(cancelled.isFinished());
	}

	@Test
	public void longFrameDoesNotLoseHopsAndShortFramesProduceTheSameDamage() {
		Target[] a = {new Target(0, 0), new Target(100, 0), new Target(200, 0), new Target(300, 0)};
		Target[] b = {new Target(0, 0), new Target(100, 0), new Target(200, 0), new Target(300, 0)};
		ChainLightningSequence<Target> one = new ChainLightningSequence<>(a[0], access);
		ChainLightningSequence<Target> many = new ChainLightningSequence<>(b[0], access);
		one.update(0.16f, Arrays.asList(a));
		for (int i = 0; i < 16; i++) many.update(0.01f, Arrays.asList(b));
		for (int i = 0; i < 4; i++) assertEquals(a[i].hits, b[i].hits);
	}

	@Test
	public void geometryHasFiniteCoordinatesAndExpiresWithoutDrawing() {
		int[] count = {0};
		LightningVisuals.Stroke stroke = (soft, x1, y1, x2, y2, width, r, g, b, alpha) -> {
			assertTrue(Float.isFinite(x1 + y1 + x2 + y2));
			assertTrue(width > 0f);
			assertTrue(alpha >= 0f && alpha <= 1f);
			count[0]++;
		};
		LightningVisuals.bolt(stroke, 10, 10, 10, 10, 0, 123);
		assertEquals(0, count[0]);
		LightningVisuals.bolt(stroke, 10, 10, 180, 90, 0.06f, 123);
		assertTrue(count[0] > 0);
		count[0] = 0;
		LightningVisuals.bolt(stroke, 10, 10, 180, 90, 0.18f, 123);
		LightningVisuals.impact((impact, x, y, w, h, angle, alpha) -> count[0]++,
				10, 10, 0.18f, 123);
		assertEquals(0, count[0]);
	}
}
