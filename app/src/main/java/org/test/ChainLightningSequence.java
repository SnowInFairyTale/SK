package org.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Timed hops; the projectile has already paid for the first hit. */
final class ChainLightningSequence<T> {

	static final int MAX_TARGETS = 4;
	static final float HOP_SECONDS = 0.05f;
	static final float HOP_RANGE = 180f;
	static final float FADE_SECONDS = 0.18f;

	interface Targets<T> {
		float x(T target);
		float y(T target);
		boolean alive(T target);
		void hit(T target);
	}

	static final class Hit<T> {
		final T target;
		final float time;
		float x, y;

		Hit(T target, float x, float y, float time) {
			this.target = target;
			this.x = x;
			this.y = y;
			this.time = time;
		}
	}

	private final Targets<T> targets;
	private final ArrayList<Hit<T>> hits = new ArrayList<>(MAX_TARGETS);
	private final List<Hit<T>> readOnlyHits = Collections.unmodifiableList(hits);
	private float elapsed;
	private boolean hopping = true;
	private boolean cancelled;

	ChainLightningSequence(T first, Targets<T> targets) {
		this.targets = targets;
		hits.add(new Hit<>(first, targets.x(first), targets.y(first), 0f));
	}

	void update(float seconds, List<T> active) {
		if (isFinished()) return;
		elapsed += Math.max(0f, seconds);
		for (Hit<T> hit : hits) {
			if (active.contains(hit.target) && targets.alive(hit.target)) {
				hit.x = targets.x(hit.target);
				hit.y = targets.y(hit.target);
			}
		}
		while (hopping && hits.size() < MAX_TARGETS
				&& elapsed + 0.000001f >= hits.size() * HOP_SECONDS) {
			Hit<T> previous = hits.get(hits.size() - 1);
			T next = null;
			float nearest = HOP_RANGE * HOP_RANGE;
			for (T candidate : active) {
				if (!targets.alive(candidate) || wasHit(candidate)) continue;
				float dx = targets.x(candidate) - previous.x;
				float dy = targets.y(candidate) - previous.y;
				float distance = dx * dx + dy * dy;
				if (distance <= nearest && (next == null || distance < nearest)) {
					next = candidate;
					nearest = distance;
				}
			}
			if (next == null) {
				hopping = false;
				break;
			}
			hits.add(new Hit<>(next, targets.x(next), targets.y(next),
					hits.size() * HOP_SECONDS));
			targets.hit(next);
		}
		if (hits.size() == MAX_TARGETS) hopping = false;
	}

	private boolean wasHit(T target) {
		for (Hit<T> hit : hits) if (hit.target == target) return true;
		return false;
	}

	List<Hit<T>> getHits() { return readOnlyHits; }
	float getElapsed() { return elapsed; }
	void cancel() { cancelled = true; }
	boolean isFinished() {
		return cancelled || (!hopping
				&& elapsed >= hits.get(hits.size() - 1).time + FADE_SECONDS);
	}
}
