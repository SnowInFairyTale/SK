package org.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import com.loon.action.sprite.SpriteBatch;
import com.loon.action.sprite.painting.DrawableGameComponent;
import com.loon.core.geom.Vector2f;
import com.loon.core.graphics.LColor;
import com.loon.core.graphics.opengl.LTexture;
import com.loon.core.timer.GameTime;

/** 防空塔普攻命中后发出三根光线，向前扫过扇形区域，只伤后续飞行怪。 */
public final class PiercingArrowEffect extends DrawableGameComponent {
	static final boolean ENABLED = true;
	private static final float TRAVEL_DISTANCE = 250f;
	/** 总夹角，绘制与伤害共用；三根光线分别位于两侧和中央。 */
	private static final float ANGLE_DEGREES = 30f;
	private static final float TRAVEL_SPEED = 600f;
	private static final float DAMAGE_RATIO = 1f;
	/** 向外飞行的光线段长度，仅影响外观。 */
	private static final float LINE_LENGTH = 112f;
	/** 整张短光束贴图的宽度，包含亮芯和半透明光晕。 */
	private static final float VISUAL_WIDTH = 28f;
	/** 外围光晕单独加宽、叠加，亮芯仍按原图比例绘制。 */
	private static final float GLOW_WIDTH_SCALE = 1.6f;
	private static final float GLOW_OPACITY_SCALE = 0.65f;
	private static final float OPACITY_SCALE = 0.85f;
	/** 最后这段路程边移动边淡出，到达终点后直接结束。 */
	private static final float FADE_DISTANCE = 60f;

	private final MainGame owner;
	private final GameplayScreen scene;
	private final PiercingArrowArea area;
	private final int damage;
	private final ArrayList<Monster> targets = new ArrayList<>();
	private final Set<Monster> hitTargets = Collections.newSetFromMap(new IdentityHashMap<Monster, Boolean>());
	private final float[] vertices = new float[20];
	private final float[] rayX = new float[3];
	private final float[] rayY = new float[3];
	private LTexture texture;
	private float travelled;
	private boolean removed;

	PiercingArrowEffect(MainGame owner, Monster primary, Vector2f direction, int attackDamage) {
		super(owner);
		this.owner = owner;
		this.scene = owner.getGameplayScreen();
		Vector2f start = primary.getPosition();
		area = new PiercingArrowArea(start.x, start.y, start.x + direction.x,
				start.y + direction.y, TRAVEL_DISTANCE, ANGLE_DEGREES);
		for (int i = 0; i < rayX.length; i++) {
			float angle = (i - 1) * area.halfAngle;
			float cos = (float) Math.cos(angle), sin = (float) Math.sin(angle);
			rayX[i] = area.dx * cos - area.dy * sin;
			rayY[i] = area.dy * cos + area.dx * sin;
		}
		damage = Math.max(0, Math.round(attackDamage * Math.max(0f, DAMAGE_RATIO)));
		// 主目标的伤害仍由原普攻结算，三根光线共用命中集合。
		hitTargets.add(primary);
		setDrawOrder(32);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		texture = scene.getEffectTexture("assets/effects/piercing_ray_blue.png");
		texture.loadTexture();
		SoundManager.PlaySound(SoundManager.PIERCING);
		owner.Components().add(new PiercingImpactEffect(owner, area.x, area.y));
	}

	@Override
	public void update(GameTime time) {
		if (removed) return;
		if (!ENABLED || owner.getGameplayScreen() != scene || scene.getIsExiting()
				|| GameplayScreen.getGameState() == GameState.Ended) {
			cancel();
			return;
		}
		if (GameplayScreen.getGameState() != GameState.Started) return;
		float dt = Math.max(0f, time.getElapsedGameTime());
		if (dt <= 0f) return;
		float before = travelled;
		travelled = Math.min(area.range, before + Math.max(1f, TRAVEL_SPEED) * dt);
		if (damage > 0 && travelled > before) {
			scene.getWaveManager().copyActiveMonstersTo(targets);
			for (int i = 0; i < targets.size(); i++) {
				Monster target = targets.get(i);
				if (!TowerCombatRules.canDamage(TowerType.AirDefence, target.getMonsterType()) || target.getDead()
						|| target.getHitPoints() <= 0 || hitTargets.contains(target)) continue;
				if (area.touches(before, travelled, target.getPosition().x,
						target.getPosition().y, target.getRadius())) {
					hitTargets.add(target);
					owner.Components().add(new PiercingImpactEffect(owner,
							target.getPosition().x, target.getPosition().y));
					target.Hit(damage);
				}
			}
			targets.clear();
		}
		// 先完整结算本帧扫过的区域，再移除；大帧步长也不会漏掉末段。
		if (travelled >= area.range) cancel();
	}

	@Override
	public void draw(SpriteBatch batch, GameTime time) {
		if (removed || texture == null || travelled <= 0f) return;
		float fadeDistance = Math.max(0.01f, Math.min(area.range, FADE_DISTANCE));
		float fade = Math.max(0f, Math.min(1f, (area.range - travelled) / fadeDistance));
		// 平滑过渡，避免淡出开始和结束时亮度突变。
		float alpha = fade * fade * (3f - 2f * fade)
				* Math.max(0f, Math.min(1f, OPACITY_SCALE));
		if (alpha <= 0f || LINE_LENGTH <= 0f || VISUAL_WIDTH <= 0f) return;
		float tail = Math.max(0f, travelled - LINE_LENGTH);
		LColor opacity = scene.getGameOpacity();
		// 先铺较宽的半透明光晕，再叠完整贴图，保留清晰的亮芯和流光。
		// 白色批次状态保留原图色彩，并保证顶点透明度生效。
		batch.flush(batch.getBlendState());
		batch.resetColor();
		drawRays(batch, tail, VISUAL_WIDTH * Math.max(0f, GLOW_WIDTH_SCALE), opacity,
				alpha * Math.max(0f, Math.min(1f, GLOW_OPACITY_SCALE)));
		drawRays(batch, tail, VISUAL_WIDTH, opacity, alpha);
		batch.flush(batch.getBlendState());
	}

	private void drawRays(SpriteBatch batch, float tail, float width, LColor opacity, float alpha) {
		if (width <= 0f || alpha <= 0f) return;
		float halfWidth = width * 0.5f;
		float color = LColor.toFloatBits(opacity.r, opacity.g, opacity.b, opacity.a * alpha);
		for (int ray = 0; ray < rayX.length; ray++) {
			vertex(0, ray, tail, -halfWidth, color, texture.xOff, texture.yOff);
			vertex(5, ray, tail, halfWidth, color, texture.xOff, texture.heightRatio);
			vertex(10, ray, travelled, halfWidth, color, texture.widthRatio, texture.heightRatio);
			vertex(15, ray, travelled, -halfWidth, color, texture.widthRatio, texture.yOff);
			batch.draw(texture, vertices, 0, vertices.length);
		}
	}

	private void vertex(int offset, int ray, float along, float across, float color, float u, float v) {
		vertices[offset] = area.x + rayX[ray] * along - rayY[ray] * across;
		vertices[offset + 1] = area.y + rayY[ray] * along + rayX[ray] * across;
		vertices[offset + 2] = color;
		vertices[offset + 3] = u;
		vertices[offset + 4] = v;
	}

	private void cancel() {
		if (removed) return;
		removed = true;
		owner.Components().remove(this);
	}

	@Override
	protected void unloadContent() {
		removed = true;
		targets.clear();
		hitTargets.clear();
		texture = null;
		super.unloadContent();
	}
}
