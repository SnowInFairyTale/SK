package org.test;

import com.loon.action.sprite.SpriteBatch;
import com.loon.core.graphics.LColor;
import com.loon.core.graphics.opengl.LTexture;

/** 跟随受击怪物中心的血溅贴图，死亡后停在最后位置播完。 */
final class BloodSplash {
	private static final float DURATION_SECONDS = 0.32f;
	private static final float BASE_SIZE = 40f;
	private static final float SIZE_SCALE = 1f;
	private static final float OPACITY_SCALE = 1f;
	/** 前段保持强度，之后线性淡出。 */
	private static final float FADE_START_PROGRESS = 0.6f;

	private Monster target;
	private float x, y;
	private final float cos, sin, sizeVariation;
	private final float[] vertices = new float[20];
	private float age;

	BloodSplash(Monster target, float direction, int seed) {
		this.target = target;
		this.x = target.getPosition().x;
		this.y = target.getPosition().y;
		float angle = direction + noise(seed) * 0.35f;
		cos = (float) Math.cos(angle);
		sin = (float) Math.sin(angle);
		sizeVariation = 0.95f + 0.1f * noise(seed + 17);
	}

	boolean update(float seconds) {
		age += Math.max(0f, seconds);
		boolean finished = age >= Math.max(0.01f, DURATION_SECONDS);
		if (finished) target = null;
		return finished;
	}

	void draw(SpriteBatch batch, LTexture texture, LColor opacity) {
		// 绘制时读取本帧最终位置，避免组件更新顺序造成一帧跟随延迟。
		if (target != null) {
			x = target.getPosition().x;
			y = target.getPosition().y;
			if (target.getDead()) target = null;
		}
		float progress = Math.min(1f, age / Math.max(0.01f, DURATION_SECONDS));
		float fade = 1f - Math.max(0f, progress - FADE_START_PROGRESS)
				/ Math.max(0.01f, 1f - FADE_START_PROGRESS);
		float alpha = Math.max(0f, fade)
				* Math.max(0f, Math.min(1f, OPACITY_SCALE));
		if (alpha <= 0f || SIZE_SCALE <= 0f) return;
		float expansion = 1f - (1f - progress) * (1f - progress);
		float halfSize = BASE_SIZE * SIZE_SCALE * sizeVariation * (0.6f + 0.4f * expansion) * 0.5f;
		float color = LColor.toFloatBits(opacity.r, opacity.g, opacity.b, opacity.a * alpha);
		// 透明度写入顶点；保持批次默认白色状态，颜色完全来自红色贴图。
		vertex(0, -halfSize, -halfSize, color, texture.xOff, texture.yOff);
		vertex(5, -halfSize, halfSize, color, texture.xOff, texture.heightRatio);
		vertex(10, halfSize, halfSize, color, texture.widthRatio, texture.heightRatio);
		vertex(15, halfSize, -halfSize, color, texture.widthRatio, texture.yOff);
		batch.draw(texture, vertices, 0, vertices.length);
	}

	private void vertex(int offset, float localX, float localY, float color, float u, float v) {
		vertices[offset] = x + localX * cos - localY * sin;
		vertices[offset + 1] = y + localX * sin + localY * cos;
		vertices[offset + 2] = color;
		vertices[offset + 3] = u;
		vertices[offset + 4] = v;
	}

	private static float noise(int seed) {
		int n = seed * 374761393 + 668265263;
		n = (n ^ (n >>> 13)) * 1274126177;
		return ((n ^ (n >>> 16)) & 0xffff) / 32767.5f - 1f;
	}
}
