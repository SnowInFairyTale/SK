package org.test;

/** 光线前沿扫过的环形扇区；目标按圆形碰撞体判断。 */
final class PiercingArrowArea {
	final float x, y, dx, dy, range;
	final float halfAngle;
	private final float edgeCos, edgeSin;

	PiercingArrowArea(float x, float y, float targetX, float targetY, float range, float angleDegrees) {
		this.x = x;
		this.y = y;
		float vx = targetX - x, vy = targetY - y;
		float length = (float) Math.sqrt(vx * vx + vy * vy);
		dx = length > 0.0001f ? vx / length : 1f;
		dy = length > 0.0001f ? vy / length : 0f;
		this.range = Math.max(0f, range);
		halfAngle = (float) Math.toRadians(Math.max(0f, Math.min(180f, angleDegrees)) * 0.5f);
		edgeCos = (float) Math.cos(halfAngle);
		edgeSin = (float) Math.sin(halfAngle);
	}

	boolean touches(float from, float to, float targetX, float targetY, float radius) {
		if (range <= 0f || halfAngle <= 0f) return false;
		float start = Math.max(0f, Math.min(range, from));
		float end = Math.max(start, Math.min(range, to));
		float vx = targetX - x, vy = targetY - y;
		float along = vx * dx + vy * dy;
		float across = Math.abs(-vx * dy + vy * dx);
		float distanceSquared;
		if (along >= 0f && across * edgeCos <= along * edgeSin) {
			// 夹角内：距离最近的内弧或外弧，而不是整块已扫过区域。
			float distance = (float) Math.sqrt(along * along + across * across);
			float gap = distance - Math.max(start, Math.min(end, distance));
			distanceSquared = gap * gap;
		} else {
			// 夹角外：求到最近径向边线段的距离，兼顾弧端点。
			float projection = along * edgeCos + across * edgeSin;
			float nearest = Math.max(start, Math.min(end, projection));
			float gapX = along - nearest * edgeCos;
			float gapY = across - nearest * edgeSin;
			distanceSquared = gapX * gapX + gapY * gapY;
		}
		float hitRadius = Math.max(0f, radius);
		return distanceSquared <= hitRadius * hitRadius + 0.0001f;
	}
}
