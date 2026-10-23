package org.test;

/** 从主目标向攻击方向展开的扇形，使用目标中心判断是否命中。 */
final class CleaveArea {
	final float x, y;
	final float radius;
	final float directionAngle;
	final float halfAngle;
	private final float forwardX, forwardY;
	private final float halfAngleCos;

	CleaveArea(float x, float y, float directionX, float directionY,
			float radius, float angleDegrees) {
		this.x = x;
		this.y = y;
		this.radius = Math.max(0f, radius);
		float length = (float) Math.sqrt(directionX * directionX + directionY * directionY);
		forwardX = length > 0.0001f ? directionX / length : 1f;
		forwardY = length > 0.0001f ? directionY / length : 0f;
		directionAngle = (float) Math.atan2(forwardY, forwardX);
		halfAngle = (float) Math.toRadians(Math.max(0f, Math.min(180f, angleDegrees)) * 0.5f);
		halfAngleCos = Math.max(0f, (float) Math.cos(halfAngle));
	}

	boolean contains(float targetX, float targetY) {
		if (radius <= 0f || halfAngle <= 0f) return false;
		float dx = targetX - x, dy = targetY - y;
		float distanceSquared = dx * dx + dy * dy;
		if (distanceSquared > radius * radius) return false;
		float forward = dx * forwardX + dy * forwardY;
		return forward >= 0f
				&& forward * forward + 0.0001f >= distanceSquared * halfAngleCos * halfAngleCos;
	}
}
