package org.test;

/** 按路径累计距离定位；正反方向共用同一条折线，不把拐角连成捷径。 */
final class BladeWheelPath {
	final float[] x, y, distance;
	final float length;

	BladeWheelPath(float[] x, float[] y) {
		if (x.length < 2 || x.length != y.length) throw new IllegalArgumentException("Invalid path");
		this.x = x.clone();
		this.y = y.clone();
		distance = new float[x.length];
		for (int i = 1; i < x.length; i++) {
			float dx = x[i] - x[i - 1], dy = y[i] - y[i - 1];
			distance[i] = distance[i - 1] + (float) Math.sqrt(dx * dx + dy * dy);
		}
		length = distance[distance.length - 1];
	}

	float nearest(float px, float py) {
		float best = Float.MAX_VALUE, result = 0f;
		for (int i = 1; i < x.length; i++) {
			float dx = x[i] - x[i - 1], dy = y[i] - y[i - 1];
			float t = projection(px, py, x[i - 1], y[i - 1], dx, dy);
			float ex = px - x[i - 1] - dx * t, ey = py - y[i - 1] - dy * t;
			float squared = ex * ex + ey * ey;
			if (squared < best) {
				best = squared;
				result = distance[i - 1] + (distance[i] - distance[i - 1]) * t;
			}
		}
		return result;
	}

	void sample(float at, float[] out) {
		at = Math.max(0f, Math.min(length, at));
		for (int i = 1; i < x.length; i++) {
			if (at <= distance[i]) {
				float segment = distance[i] - distance[i - 1];
				float t = segment > 0f ? (at - distance[i - 1]) / segment : 0f;
				out[0] = x[i - 1] + (x[i] - x[i - 1]) * t;
				out[1] = y[i - 1] + (y[i] - y[i - 1]) * t;
				return;
			}
		}
		out[0] = x[x.length - 1];
		out[1] = y[y.length - 1];
	}

	float nextCorner(float at, int direction) {
		if (direction > 0) {
			for (float d : distance) if (d > at + 0.001f) return d;
			return length;
		}
		for (int i = distance.length - 1; i >= 0; i--) {
			if (distance[i] < at - 0.001f) return distance[i];
		}
		return 0f;
	}

	static boolean touchesSegment(float x0, float y0, float x1, float y1,
			float px, float py, float radius) {
		float dx = x1 - x0, dy = y1 - y0;
		float t = projection(px, py, x0, y0, dx, dy);
		float ex = px - x0 - dx * t, ey = py - y0 - dy * t;
		return ex * ex + ey * ey <= radius * radius;
	}

	private static float projection(float px, float py, float x, float y, float dx, float dy) {
		float squared = dx * dx + dy * dy;
		return squared > 0f ? Math.max(0f, Math.min(1f, ((px - x) * dx + (py - y) * dy) / squared)) : 0f;
	}
}
