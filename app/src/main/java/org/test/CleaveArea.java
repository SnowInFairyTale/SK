package org.test;

/** 以主目标为中心的完整圆形伤害范围，使用目标中心判断是否命中。 */
final class CleaveArea {
	final float x, y;
	final float radius;
	CleaveArea(float x, float y, float radius) {
		this.x = x;
		this.y = y;
		this.radius = Math.max(0f, radius);
	}

	boolean contains(float targetX, float targetY) {
		if (radius <= 0f) return false;
		float dx = targetX - x, dy = targetY - y;
		float distanceSquared = dx * dx + dy * dy;
		return distanceSquared <= radius * radius;
	}
}
