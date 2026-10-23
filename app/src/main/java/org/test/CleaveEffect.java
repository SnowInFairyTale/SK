package org.test;

import com.loon.action.sprite.SpriteBatch;
import com.loon.action.sprite.painting.DrawableGameComponent;
import com.loon.core.geom.Vector2f;
import com.loon.core.graphics.LColor;
import com.loon.core.graphics.opengl.LTexture;
import com.loon.core.timer.GameTime;

/** 飞斧命中的扇形分裂：一次结算范围伤害，斩痕独立淡出。 */
public final class CleaveEffect extends DrawableGameComponent {
	/** 总开关。 */
	private static final boolean ENABLED = true;
	/** 分裂伤害 / 飞斧单次伤害；主目标仍由飞斧正常结算，不重复扣血。 */
	private static final float DAMAGE_RATIO = 0.6f;
	/** 从主目标中心向飞斧来向的前方延伸，作用于扇形内的其他怪物。 */
	private static final float RANGE = 120f;
	/** 扇形总夹角，建议 1～180 度。 */
	private static final float ANGLE_DEGREES = 130f;
	/** 斩痕持续时间，单位秒。 */
	private static final float DURATION_SECONDS = 0.22f;
	/** 仅改变视觉尺寸，不改变伤害范围；1 表示斩痕延伸到伤害半径。 */
	private static final float SIZE_SCALE = 1f;
	/** 斩痕粗细系数，与尺寸独立。 */
	private static final float WIDTH_SCALE = 1f;
	/** 不透明度系数，0～1，越小越透明。 */
	private static final float OPACITY_SCALE = 1f;
	/** 拖尾强度，0 关闭。 */
	private static final float TRAIL_OPACITY_SCALE = 0.7f;
	/** 飞散火花数量；每次命中固定生成，不随帧数增加。 */
	private static final int SPARK_COUNT = 10;
	/** 火花长度和粗细系数，0 关闭。 */
	private static final float SPARK_SIZE_SCALE = 1f;
	/** 命中闪光尺寸系数，0 关闭。 */
	private static final float FLASH_SIZE_SCALE = 1.2f;
	/** 爆点先保持亮度，再独立淡出至斩痕寿命结束，不叠乘波的衰减。 */
	private static final float FLASH_FADE_START_PROGRESS = 0.25f;
	/** 刀光贴图的柔光宽度；独立于原有细刀锋。 */
	private static final float BLADE_GLOW_WIDTH_SCALE = 1f;
	/** 刀光贴图不透明度，0 关闭；同时受总透明度控制。 */
	private static final float BLADE_GLOW_OPACITY_SCALE = 0.9f;
	/** 命中爆点不透明度，0 关闭；尺寸由 FLASH_SIZE_SCALE 调整。 */
	private static final float FLASH_OPACITY_SCALE = 0.85f;

	private static final int ARC_SEGMENTS = 24;
	private final MainGame owner;
	private final GameplayScreen scene;
	private final CleaveArea area;
	private final float[] arcX = new float[ARC_SEGMENTS + 1];
	private final float[] arcY = new float[ARC_SEGMENTS + 1];
	private final float[] sparkX = new float[Math.max(0, SPARK_COUNT)];
	private final float[] sparkY = new float[sparkX.length];
	private final float[] sparkSpeed = new float[sparkX.length];
	private final float[] sparkDelay = new float[sparkX.length];
	private final LColor tint = new LColor(LColor.white);
	private final float[] ribbonQuad = new float[20];
	private LTexture ribbonTexture;
	private LTexture impactTexture;
	private float elapsed;
	private boolean removed;

	private CleaveEffect(MainGame owner, Monster primary, Vector2f direction) {
		super(owner);
		this.owner = owner;
		this.scene = owner.getGameplayScreen();
		area = new CleaveArea(primary.getPosition().x, primary.getPosition().y,
				direction.x, direction.y, RANGE, ANGLE_DEGREES);
		for (int i = 0; i <= ARC_SEGMENTS; i++) {
			float angle = area.directionAngle - area.halfAngle
					+ 2f * area.halfAngle * i / ARC_SEGMENTS;
			arcX[i] = (float) Math.cos(angle);
			arcY[i] = (float) Math.sin(angle);
		}
		java.util.Random random = new java.util.Random();
		for (int i = 0; i < sparkX.length; i++) {
			float across = (i + random.nextFloat()) / sparkX.length;
			float angle = area.directionAngle + (across * 2f - 1f) * area.halfAngle;
			sparkX[i] = (float) Math.cos(angle);
			sparkY[i] = (float) Math.sin(angle);
			sparkSpeed[i] = 0.65f + random.nextFloat() * 0.55f;
			sparkDelay[i] = across * 0.20f;
		}
		setDrawOrder(32);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		// 借用场景持有的原图，场景退出时统一释放。
		ribbonTexture = scene.getEffectTexture("assets/effects/cleave_ribbon.png");
		impactTexture = scene.getEffectTexture("assets/effects/cleave_impact.png");
		ribbonTexture.loadTexture();
	}

	static void trigger(MainGame owner, Monster primary, Vector2f direction, int attackDamage) {
		if (!ENABLED || GameplayScreen.getGameState() != GameState.Started) return;
		CleaveEffect effect = new CleaveEffect(owner, primary, direction);
		owner.Components().add(effect);
		int damage = Math.max(0, Math.round(attackDamage * Math.max(0f, DAMAGE_RATIO)));
		if (damage == 0) return;
		// 命中时取一次快照，避免扣血移除目标时影响遍历，也不会触发递归分裂。
		for (Monster target : effect.scene.getWaveManager().GetAllActiveMonsters()) {
			if (target == primary || target.getDead() || target.getHitPoints() <= 0) continue;
			if (effect.area.contains(target.getPosition().x, target.getPosition().y)) {
				target.Hit(damage);
			}
		}
	}

	@Override
	public void update(GameTime time) {
		if (removed) return;
		if (owner.getGameplayScreen() != scene || scene.getIsExiting()
				|| GameplayScreen.getGameState() == GameState.Ended) {
			remove();
			return;
		}
		if (GameplayScreen.getGameState() != GameState.Started) return;
		elapsed += Math.max(0f, time.getElapsedGameTime());
		if (elapsed >= Math.max(0.01f, DURATION_SECONDS)) remove();
	}

	@Override
	public void draw(SpriteBatch batch, GameTime time) {
		if (removed) return;
		float progress = Math.min(1f, elapsed / Math.max(0.01f, DURATION_SECONDS));
		float size = Math.max(0f, SIZE_SCALE);
		float width = Math.max(0f, WIDTH_SCALE);
		float alpha = (1f - progress) * (1f - progress)
				* Math.max(0f, Math.min(1f, OPACITY_SCALE));
		if (size <= 0f || width <= 0f || alpha <= 0f) return;
		LColor opacity = scene.getGameOpacity();
		// 前沿快速向外扩张，同时从扇形左侧扫到右侧；末段只淡出。
		float expansion = 1f - (1f - progress) * (1f - progress);
		float radius = area.radius * size * (0.22f + 0.78f * expansion);
		float sweep = Math.min(1f, 0.18f + progress * 2.4f);
		// 先画拖尾和柔光，再叠加细刀锋与命中反馈。
		float trailAlpha = alpha * Math.max(0f, TRAIL_OPACITY_SCALE);
		for (int trail = 2; trail >= 1; trail--) {
			float trailingSweep = Math.max(0f, sweep - trail * 0.09f);
			setTint(opacity, 1f, 0.85f, 0.65f,
					trailAlpha * (trail == 1 ? 0.30f : 0.16f));
			drawRibbon(batch, radius * (1f - trail * 0.13f), trailingSweep,
					(22f - trail * 4f) * width);
		}
		// 同一张刀光纹理集中绘制。
		setTint(opacity, 1f, 0.8f, 0.6f, alpha * 0.22f);
		drawRibbon(batch, radius, sweep, 48f * width);
		setTint(opacity, 1f, 1f, 1f, alpha);
		drawRibbon(batch, radius, sweep, 30f * width);
		setTint(opacity, 1f, 0.78f, 0.38f, alpha * 0.55f);
		batch.setColor(tint);
		drawArc(batch, radius, sweep, 1.1f * width);
		drawSparks(batch, opacity, progress, size, width, alpha);
		drawImpact(batch, opacity, progress, size);
		batch.resetColor();
	}

	private void drawRibbon(SpriteBatch batch, float radius, float sweep, float width) {
		width *= Math.max(0f, BLADE_GLOW_WIDTH_SCALE);
		if (ribbonTexture == null || sweep <= 0f || width <= 0f) return;
		float opacity = Math.max(0f, Math.min(1f, BLADE_GLOW_OPACITY_SCALE));
		if (opacity <= 0f) return;
		float color = LColor.toFloatBits(tint.r, tint.g, tint.b, tint.a * opacity);
		float end = Math.min(1f, sweep) * ARC_SEGMENTS;
		float halfWidth = Math.min(width * 0.5f, radius * 0.8f);
		float uRange = ribbonTexture.widthRatio - ribbonTexture.xOff;
		for (int i = 0; i < (int) Math.ceil(end); i++) {
			float part = Math.min(1f, end - i);
			float dx = arcX[i] + (arcX[i + 1] - arcX[i]) * part;
			float dy = arcY[i] + (arcY[i + 1] - arcY[i]) * part;
			float u0 = ribbonTexture.xOff + uRange * i / end;
			float u1 = ribbonTexture.xOff + uRange * (i + part) / end;
			// 相邻四边形共享内外缘顶点，整张纹理连续弯曲，不在每段重复拉伸。
			ribbonVertex(0, arcX[i], arcY[i], radius + halfWidth, color, u0, ribbonTexture.yOff);
			ribbonVertex(5, arcX[i], arcY[i], radius - halfWidth, color, u0, ribbonTexture.heightRatio);
			ribbonVertex(10, dx, dy, radius - halfWidth, color, u1, ribbonTexture.heightRatio);
			ribbonVertex(15, dx, dy, radius + halfWidth, color, u1, ribbonTexture.yOff);
			batch.draw(ribbonTexture, ribbonQuad, 0, ribbonQuad.length);
		}
	}

	private void ribbonVertex(int offset, float dx, float dy, float radius,
			float color, float u, float v) {
		ribbonQuad[offset] = area.x + dx * radius;
		ribbonQuad[offset + 1] = area.y + dy * radius;
		ribbonQuad[offset + 2] = color;
		ribbonQuad[offset + 3] = u;
		ribbonQuad[offset + 4] = v;
	}

	private void drawArc(SpriteBatch batch, float radius, float sweep, float width) {
		if (sweep <= 0f || width <= 0f) return;
		float end = Math.min(1f, sweep) * ARC_SEGMENTS;
		for (int i = 0; i < (int) Math.ceil(end); i++) {
			// 末段插值，让刀锋连续扫动，不按整段跳变。
			float part = Math.min(1f, end - i);
			float along = (i + part * 0.5f) / end;
			float taper = 0.15f + 0.85f * 4f * along * (1f - along);
			float x = arcX[i] + (arcX[i + 1] - arcX[i]) * part;
			float y = arcY[i] + (arcY[i + 1] - arcY[i]) * part;
			batch.drawLine(area.x + arcX[i] * radius, area.y + arcY[i] * radius,
					area.x + x * radius, area.y + y * radius, width * taper);
		}
	}

	private void drawSparks(SpriteBatch batch, LColor opacity, float progress,
			float size, float width, float alpha) {
		float sparkSize = Math.max(0f, SPARK_SIZE_SCALE);
		if (sparkSize <= 0f) return;
		for (int i = 0; i < sparkX.length; i++) {
			float age = (progress - sparkDelay[i]) / (1f - sparkDelay[i]);
			if (age <= 0f) continue;
			float travel = 1f - (1f - age) * (1f - age);
			float distance = area.radius * size * (0.18f + travel * sparkSpeed[i]);
			float length = (5f + 12f * (1f - age)) * size * sparkSize;
			float x = area.x + sparkX[i] * distance;
			float y = area.y + sparkY[i] * distance;
			// 橙色外缘与金色内芯，共用同一条轨迹。
			for (int layer = 0; layer < 2; layer++) {
				setTint(opacity, 1f, layer == 0 ? 0.35f : 0.83f,
						layer == 0 ? 0.06f : 0.40f, alpha * (layer == 0 ? 0.25f : 0.85f));
				batch.setColor(tint);
				batch.drawLine(x - sparkX[i] * length, y - sparkY[i] * length,
						x, y, (layer == 0 ? 3.2f : 1.1f) * width * sparkSize * (1f - age * 0.7f));
			}
		}
	}

	private void drawImpact(SpriteBatch batch, LColor opacity, float progress, float size) {
		float flashSize = Math.max(0f, FLASH_SIZE_SCALE);
		float fadeStart = Math.max(0f, Math.min(0.99f, FLASH_FADE_START_PROGRESS));
		float fadeProgress = Math.max(0f, Math.min(1f, (progress - fadeStart) / (1f - fadeStart)));
		float flash = 1f - fadeProgress * fadeProgress * (3f - 2f * fadeProgress);
		float flashOpacity = Math.max(0f, Math.min(1f, FLASH_OPACITY_SCALE));
		if (impactTexture == null || flashSize <= 0f || flash <= 0f || flashOpacity <= 0f) return;
		float expansion = 1f - (1f - progress) * (1f - progress);
		float extent = (38f + 24f * expansion) * size * flashSize;
		float alpha = flash * flashOpacity * Math.max(0f, Math.min(1f, OPACITY_SCALE));
		setTint(opacity, 1f, 1f, 1f, alpha);
		batch.setColor(tint);
		batch.draw(impactTexture, area.x, area.y, extent * 0.5f, extent * 0.5f,
				extent, extent, 1f, 1f, (float) Math.toDegrees(area.directionAngle) + 45f,
				0f, 0f, impactTexture.getWidth(), impactTexture.getHeight(), false, false, true);
	}

	private void setTint(LColor opacity, float r, float g, float b, float alpha) {
		tint.r = r * opacity.r;
		tint.g = g * opacity.g;
		tint.b = b * opacity.b;
		tint.a = alpha * opacity.a;
	}

	private void remove() {
		removed = true;
		owner.Components().remove(this);
	}

	@Override
	protected void unloadContent() {
		removed = true;
		ribbonTexture = null;
		impactTexture = null;
		super.unloadContent();
	}
}
