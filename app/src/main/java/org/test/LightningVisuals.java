package org.test;

import java.util.List;

/** 连锁闪电的可调参数与绘制逻辑。 */
final class LightningVisuals {
	/** 怪物位置的柔光、爆点尺寸系数：1 = 原始大小，0.3 = 原始大小的 30%。 */
	private static final float SIZE_SCALE = 0.6f;

	/** 怪物位置的柔光、爆点不透明度系数：0～1，越小越透明。 */
	private static final float OPACITY_SCALE = 1f;
	/** 怪位置圆形柔光强度，不影响连接线柔光。 */
	private static final float HIT_GLOW_OPACITY_SCALE = 0.28f;
	/** 怪位置圆形柔光的独立尺寸系数，不影响连接线柔光。 */
	private static final float HIT_GLOW_SIZE_SCALE = 0.85f;
	/** 电击爆点的独立尺寸系数。 */
	private static final float IMPACT_SIZE_SCALE = 1.35f;
	/** 爆点淡出曲线指数：越小越能保留中后段亮度，持续时间不变。 */
	private static final float IMPACT_FADE_POWER = 0.65f;

	/** 连接线粗细系数（包括底部柔光和分叉）：1 = 基础线宽。
	 * 数值翻倍则线宽翻倍，不影响怪物位置的柔光和爆点。 */
	private static final float LINE_WIDTH_SCALE = 0.7f;

	/** 连接线不透明度系数：0～1，越小越透明，1 = 保留原始不透明度。
	 * 独立于怪物位置的柔光、爆点，保留原有闪烁和淡出。 */
	private static final float LINE_OPACITY_SCALE = 1f;

	/** 折线和分叉幅度系数：越大弯折越明显、分叉越长，与线宽独立。 */
	private static final float LINE_SHAPE_SCALE = 0.7f;

	interface Stroke {
		/** soft 为 true 时沿线铺原圆形柔光的中央截面，否则绘制实线。 */
		void line(boolean soft, float x1, float y1, float x2, float y2, float width,
				float r, float g, float b, float alpha);
	}

	interface Sprites {
		/** Coordinates are the center; angle is in degrees. */
		void draw(boolean impact, float x, float y, float width, float height,
				float angle, float alpha);
	}

	static <T> void drawBase(ChainLightningSequence<T> sequence, Stroke strokes,
			Sprites sprites, int seed) {
		List<ChainLightningSequence.Hit<T>> hits = sequence.getHits();
		float elapsed = sequence.getElapsed();
		// 连接线及其柔光、命中位置的圆形柔光保留原层级，爆点单独绘制。
		for (int i = 1; i < hits.size(); i++) {
			ChainLightningSequence.Hit<T> hit = hits.get(i);
			ChainLightningSequence.Hit<T> previous = hits.get(i - 1);
			bolt(strokes, previous.x, previous.y, hit.x, hit.y,
					elapsed - hit.time, seed + i * 101);
		}
		// 命中位置用完整圆形柔光；连接线仅取它的中央截面。
		for (int i = 0; i < hits.size(); i++) {
			ChainLightningSequence.Hit<T> hit = hits.get(i);
			float age = elapsed - hit.time;
			float alpha = fade(age);
			if (alpha <= 0f) continue;
			float size = scaledSize(58f + 22f * age / ChainLightningSequence.FADE_SECONDS)
					* Math.max(0f, HIT_GLOW_SIZE_SCALE);
			sprites.draw(false, hit.x, hit.y, size, size, 0f,
					scaledAlpha(alpha * Math.max(0f, Math.min(1f, HIT_GLOW_OPACITY_SCALE))));
		}
	}

	static <T> void drawImpacts(ChainLightningSequence<T> sequence, Sprites sprites, int seed) {
		List<ChainLightningSequence.Hit<T>> hits = sequence.getHits();
		float elapsed = sequence.getElapsed();
		for (int i = 0; i < hits.size(); i++) {
			ChainLightningSequence.Hit<T> hit = hits.get(i);
			impact(sprites, hit.x, hit.y, elapsed - hit.time, seed + i * 101);
		}
	}

	static void bolt(Stroke out, float x1, float y1, float x2, float y2,
			float age, int seed) {
		float alpha = fade(age);
		if (alpha <= 0f) return;
		float dx = x2 - x1, dy = y2 - y1;
		float length = (float) Math.sqrt(dx * dx + dy * dy);
		if (length < 1f) return;
		float nx = -dy / length, ny = dx / length;
		int segments = Math.max(4, Math.min(18, (int) (length / 14f)));
		int phase = (int) (age / 0.04f);
		float amplitude = scaledLineShape(Math.min(12f, length * 0.13f));
		float px = x1, py = y1;
		for (int i = 1; i <= segments; i++) {
			float t = i / (float) segments;
			float offset = i == segments ? 0f
					: noise(seed + phase * 97 + i * 31) * amplitude;
			float x = x1 + dx * t + nx * offset;
			float y = y1 + dy * t + ny * offset;
			glow(out, px, py, x, y, 1f, alpha);
			if (i == segments / 2) {
				float side = noise(seed + 13) < 0 ? -1f : 1f;
				float bx = x + scaledLineShape(dx * 0.1f + nx * 14f * side);
				float by = y + scaledLineShape(dy * 0.1f + ny * 14f * side);
				glow(out, x, y, bx, by, 0.5f, alpha * 0.65f);
				glow(out, bx, by, bx + scaledLineShape(dx * 0.08f - nx * 5f * side),
						by + scaledLineShape(dy * 0.08f - ny * 5f * side), 0.35f, alpha * 0.4f);
			}
			px = x;
			py = y;
		}
	}

	static void impact(Sprites out, float x, float y, float age, int seed) {
		if (age < 0f || age >= ChainLightningSequence.FADE_SECONDS) return;
		float t = age / ChainLightningSequence.FADE_SECONDS;
		float expansion = 1f - (1f - t) * (1f - t) * (1f - t);
		float size = scaledSize(46f + 30f * expansion) * Math.max(0f, IMPACT_SIZE_SCALE);
		float alpha = scaledAlpha((float) Math.pow(1f - t, Math.max(0.01f, IMPACT_FADE_POWER)));
		out.draw(true, x, y, size, size, noise(seed) * 180f + 18f * t, alpha);
	}

	private static float fade(float age) {
		if (age < 0 || age >= ChainLightningSequence.FADE_SECONDS) return 0f;
		float fade = 1f - age / ChainLightningSequence.FADE_SECONDS;
		float flicker = ((int) (age / 0.035f) & 1) == 0 ? 1f : 0.72f;
		return (float) Math.sqrt(fade) * flicker;
	}

	private static float noise(int seed) {
		int n = seed * 374761393 + 668265263;
		n = (n ^ (n >>> 13)) * 1274126177;
		return ((n ^ (n >>> 16)) & 0xffff) / 32767.5f - 1f;
	}

	private static float scaledSize(float value) {
		return value * Math.max(0f, SIZE_SCALE);
	}

	private static float scaledAlpha(float alpha) {
		return alpha * Math.max(0f, Math.min(1f, OPACITY_SCALE));
	}

	private static float scaledLineShape(float value) {
		return value * Math.max(0f, LINE_SHAPE_SCALE);
	}

	private static void glow(Stroke out, float x1, float y1, float x2,
			float y2, float scale, float alpha) {
		// 柔光与核心共用每一段的端点，随折线和分叉弯折。
		drawLineGlow(out, x1, y1, x2, y2, scale, alpha);
		scale *= Math.max(0f, LINE_WIDTH_SCALE);
		alpha *= Math.max(0f, Math.min(1f, LINE_OPACITY_SCALE));
		out.line(false, x1, y1, x2, y2, 6f * scale, 0.55f, 0.12f, 1f, alpha * 0.20f);
		out.line(false, x1, y1, x2, y2, 2.5f * scale, 0.70f, 0.25f, 1f, alpha * 0.85f);
		out.line(false, x1, y1, x2, y2, 1.1f * scale, 0.82f, 0.42f, 1f, alpha);
	}

	private static void drawLineGlow(Stroke out, float x1, float y1, float x2,
			float y2, float scale, float alpha) {
		float width = 13f * scale * Math.max(0f, LINE_WIDTH_SCALE);
		float opacity = alpha * 0.40f * Math.max(0f, Math.min(1f, LINE_OPACITY_SCALE));
		if (width <= 0f || opacity <= 0f) return;
		// 沿每一小段均匀铺中央截面；白色乘色保留原图颜色。
		out.line(true, x1, y1, x2, y2, width, 1f, 1f, 1f, opacity);
	}
}
