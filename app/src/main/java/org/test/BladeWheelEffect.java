package org.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import com.loon.action.sprite.SpriteBatch;
import com.loon.action.sprite.painting.DrawableGameComponent;
import com.loon.core.geom.Vector2f;
import com.loon.core.graphics.LColor;
import com.loon.core.graphics.opengl.LTexture;
import com.loon.core.timer.GameTime;

/** 每次碎石攻击生成两个光球，从人物两侧飞向最近路径，再正反向沿路移动。 */
public final class BladeWheelEffect extends DrawableGameComponent {
	static final boolean ENABLED = true;
	/** 光球从塔上人物逻辑中心左右两侧出生，再飞向各自最近的路径位置。 */
	private static final float LAUNCH_SIDE_OFFSET = 32f;
	/** 飞出最外层攻击波后，再扩散此距离达到最大尺寸。 */
	private static final float WAVE_OUTSIDE_DISTANCE = 24f;
	/** 接上路径后计算的沿路行程，包含攻击范围内路段，不包含接入路径的飞行。 */
	private static final float TRAVEL_DISTANCE = 300f;
	private static final float TRAVEL_SPEED = 230f;
	/** 自旋角速度，两个光轮互为反向。 */
	private static final float SPIN_DEGREES_PER_SECOND = 810f;
	/** 光球、完全展开光轮的显示直径。 */
	private static final float ORB_SIZE = 22f;
	private static final float WHEEL_SIZE = 64f;
	/** 总不透明度，0～1；越小越透明。 */
	private static final float OPACITY_SCALE = 1f;
	private static final float GLOW_OPACITY_SCALE = 0.3f;
	/** 每个光轮对每只怪仅造成一次伤害，多个光轮独立结算。 */
	private static final float DAMAGE_RATIO = 0.25f;
	/** 固定按完全展开时的半径碰撞，不受视觉展开程度影响。 */
	private static final float CONTACT_RADIUS = 25f;
	private static final float FADE_SECONDS = 0.22f;

	private enum Phase { JOIN, FOLLOW, FADE, DONE }

	private static final class Wheel {
		final int direction;
		final Set<Monster> hitTargets = Collections.newSetFromMap(new IdentityHashMap<Monster, Boolean>());
		float x, y, size = ORB_SIZE, morph, spin, pathAt, travelled, startRadius;
		float fade = 1f;
		BladeWheelPath path;
		Phase phase = Phase.JOIN;
		Wheel(int direction) {
			this.direction = direction;
		}
	}

	private final MainGame owner;
	private final GameplayScreen scene;
	private final LurWeapon wave;
	private final float centerX, centerY, finalRadius;
	private final int damage;
	private final Wheel[] wheels;
	private final ArrayList<Monster> targets = new ArrayList<>();
	private final ArrayList<BloodSplash> bloodSplashes = new ArrayList<>();
	private int bloodSeed = (int) System.nanoTime();
	private final float[] point = new float[2];
	private final LColor tint = new LColor(LColor.white);
	private LTexture wheelTexture, orbTexture, bloodTexture;
	private float lastWaveRadius;
	private boolean removed;

	BladeWheelEffect(MainGame owner, Tower tower, LurWeapon wave) {
		super(owner);
		this.owner = owner;
		this.scene = owner.getGameplayScreen();
		this.wave = wave;
		centerX = tower.getPosition().x;
		centerY = tower.getPosition().y;
		finalRadius = Math.max(LAUNCH_SIDE_OFFSET + 1f,
				wave.getVisualMaxRadius() + Math.max(0f, WAVE_OUTSIDE_DISTANCE));
		damage = Math.max(0, Math.round(tower.getDamage() * Math.max(0f, DAMAGE_RATIO)));
		// 固定一逆一顺两个实体，不通过残影或展开过程额外生成光轮。
		wheels = new Wheel[] { new Wheel(-1), new Wheel(1) };
		Vector2f launchCenter = tower.getTowerManCenter();
		// 同次攻击只追踪三条入口路线一次；每个轮子仍独立选择最近路线。
		BladeWheelPath[] routes = new BladeWheelPath[3];
		for (int lane = -1; lane <= 1; lane++) {
			routes[lane + 1] = traceRoute(scene.getLevelSettings().getStartPoint().x(),
					scene.getLevelSettings().getStartPoint().y() + lane);
		}
		for (Wheel wheel : wheels) {
			float referenceX = launchCenter.x + wheel.direction * LAUNCH_SIDE_OFFSET;
			wheel.path = nearestRoute(routes, referenceX, launchCenter.y);
			if (wheel.path == null) {
				wheel.phase = Phase.DONE;
				continue;
			}
			wheel.pathAt = wheel.path.nearest(referenceX, launchCenter.y);
			wheel.x = referenceX;
			wheel.y = launchCenter.y;
			float dx = wheel.x - centerX, dy = wheel.y - centerY;
			wheel.startRadius = (float) Math.sqrt(dx * dx + dy * dy);
		}
		lastWaveRadius = wave.getVisualRadius();
		setDrawOrder(34); // 血溅覆盖分裂和闪电爆点；光球和光轮本体仍由场景底层绘制。
	}

	boolean isFinished() { return removed; }

	@Override
	protected void loadContent() {
		super.loadContent();
		wheelTexture = scene.getEffectTexture("assets/effects/blade_wheel_small_hole.png");
		orbTexture = scene.getEffectTexture("assets/effects/blade_orb.png");
		bloodTexture = scene.getEffectTexture("assets/effects/blood_splash.png");
		bloodTexture.loadTexture();
		scene.addGroundBladeWheel(this);
		SoundManager.PlaySound(SoundManager.BLADE);
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
		for (int i = bloodSplashes.size() - 1; i >= 0; i--) {
			if (bloodSplashes.get(i).update(dt)) bloodSplashes.remove(i);
		}
		// 光轮结束后只播放剩余血溅，不再查询怪物或计算移动。
		if (wheels[0].phase == Phase.DONE && wheels[1].phase == Phase.DONE) {
			if (bloodSplashes.isEmpty()) cancel();
			return;
		}
		float waveRadius = wave.getVisualRadius();
		float waveStep = Math.max(0f, waveRadius - lastWaveRadius);
		lastWaveRadius = waveRadius;
		// 接入和沿路阶段共用移动预算，随攻击波扩散；接上路径后严格沿折线转弯。
		float budget = wave.getHasHitTarget()
				? Math.max(waveStep, Math.max(1f, TRAVEL_SPEED) * dt) : waveStep;
		scene.getWaveManager().copyActiveMonstersTo(targets);
		for (Wheel wheel : wheels) {
			if (wheel.phase == Phase.DONE) continue;
			wheel.spin = (wheel.spin + SPIN_DEGREES_PER_SECOND * wheel.direction * dt) % 360f;
			switch (wheel.phase) {
			case JOIN:
				joinPath(wheel, budget, targets);
				break;
			case FOLLOW:
				follow(wheel, budget, targets);
				break;
			case FADE:
				wheel.fade = Math.max(0f, wheel.fade - dt / Math.max(0.01f, FADE_SECONDS));
				if (wheel.fade <= 0f) wheel.phase = Phase.DONE;
				break;
			default:
				break;
			}
		}
		if (wheels[0].phase == Phase.DONE && wheels[1].phase == Phase.DONE
				&& bloodSplashes.isEmpty()) cancel();
	}

	private void joinPath(Wheel wheel, float budget, List<Monster> targets) {
		wheel.path.sample(wheel.pathAt, point);
		float dx = point[0] - wheel.x, dy = point[1] - wheel.y;
		float distance = (float) Math.sqrt(dx * dx + dy * dy);
		if (distance <= budget) {
			move(wheel, point[0], point[1], targets);
			wheel.phase = Phase.FOLLOW;
			// 本帧剩余距离继续沿路径走，衔接处不瞬移、不额外停一帧。
			follow(wheel, budget - distance, targets);
		} else if (budget > 0f) {
			float fraction = budget / distance;
			move(wheel, wheel.x + dx * fraction, wheel.y + dy * fraction, targets);
		}
	}

	private void follow(Wheel wheel, float budget, List<Monster> targets) {
		budget = Math.min(budget, Math.max(0f, TRAVEL_DISTANCE - wheel.travelled));
		while (budget > 0.001f) {
			float corner = wheel.path.nextCorner(wheel.pathAt, wheel.direction);
			float step = Math.min(budget, Math.abs(corner - wheel.pathAt));
			if (step <= 0.001f) break;
			wheel.pathAt = Math.max(0f, Math.min(wheel.path.length, wheel.pathAt + wheel.direction * step));
			wheel.path.sample(wheel.pathAt, point);
			move(wheel, point[0], point[1], targets);
			wheel.travelled += step;
			budget -= step;
		}
		if (wheel.travelled >= Math.max(0f, TRAVEL_DISTANCE) - 0.001f
				|| (wheel.direction < 0 && wheel.pathAt <= 0.001f)
				|| (wheel.direction > 0 && wheel.pathAt >= wheel.path.length - 0.001f)) {
			wheel.phase = Phase.FADE;
		}
	}

	private void move(Wheel wheel, float x, float y, List<Monster> targets) {
		float dx = x - centerX, dy = y - centerY;
		float distance = (float) Math.sqrt(dx * dx + dy * dy);
		float progress = clamp01((distance - wheel.startRadius) / Math.max(1f, finalRadius - wheel.startRadius));
		// 在范围内沿路逐渐展开；真正走出攻击波外才达到最大，之后不再缩回。
		wheel.morph = Math.max(wheel.morph, progress * progress * (3f - 2f * progress));
		wheel.size = ORB_SIZE + (WHEEL_SIZE - ORB_SIZE) * wheel.morph;
		float radius = Math.max(0f, CONTACT_RADIUS);
		if (damage > 0) {
			for (Monster target : targets) {
				if (!TowerCombatRules.canDamage(TowerType.Lur, target.getMonsterType())
						|| target.getDead() || target.getHitPoints() <= 0 || wheel.hitTargets.contains(target)) continue;
				if (BladeWheelPath.touchesSegment(wheel.x, wheel.y, x, y,
						target.getPosition().x, target.getPosition().y, radius + Math.max(0f, target.getRadius()))) {
					wheel.hitTargets.add(target);
					bloodSplashes.add(new BloodSplash(target,
							(float) Math.atan2(target.getPosition().y - y, target.getPosition().x - x),
							bloodSeed++));
					target.Hit(damage);
				}
			}
		}
		wheel.x = x;
		wheel.y = y;
	}

	/** 从三个实际入口追踪当前寻路方向，选择最近的一条完整路线。 */
	private BladeWheelPath nearestRoute(BladeWheelPath[] routes, float x, float y) {
		BladeWheelPath best = null;
		float bestDistance = Float.MAX_VALUE;
		for (BladeWheelPath path : routes) {
			if (path == null) continue;
			path.sample(path.nearest(x, y), point);
			float dx = x - point[0], dy = y - point[1];
			if (dx * dx + dy * dy < bestDistance) {
				bestDistance = dx * dx + dy * dy;
				best = path;
			}
		}
		return best;
	}

	private BladeWheelPath traceRoute(int x, int y) {
		PathNode[][] dirs = scene.getDirs();
		int limit = Constants.GridWidth * Constants.GridHeight;
		float[] px = new float[limit], py = new float[limit];
		boolean[][] visited = new boolean[Constants.GridWidth][Constants.GridHeight];
		int count = 0;
		while (x >= 0 && x < dirs.length && y >= 0 && y < dirs[x].length && count < limit) {
			if (visited[x][y]) break;
			visited[x][y] = true;
			px[count] = x * Constants.GridSize + Constants.HorizontalGridOffset + Constants.GridSize * 0.5f;
			py[count++] = y * Constants.GridSize + Constants.VerticalGridOffset + Constants.GridSize * 0.5f;
			if (x >= scene.getLevelSettings().getEndPoint().x()) {
				return count >= 2 ? new BladeWheelPath(Arrays.copyOf(px, count), Arrays.copyOf(py, count)) : null;
			}
			PathNode node = dirs[x][y];
			if (node == null) break;
			x += node.x();
			y += node.y();
		}
		return null;
	}

	void drawGround(SpriteBatch batch) {
		if (removed || wheelTexture == null || orbTexture == null) return;
		LColor opacity = scene.getGameOpacity();
		// 起始光球及其柔光一起淡出，完全展开后只保留光轮图素。
		for (Wheel w : wheels) {
			if (w.phase == Phase.DONE) continue;
			float alpha = clamp01(OPACITY_SCALE) * w.fade * (1f - w.morph) * (1f - w.morph);
			drawSprite(batch, orbTexture, w.x, w.y, w.size * 1.3f, w.spin * 0.6f,
					opacity, alpha * clamp01(GLOW_OPACITY_SCALE), w.direction < 0);
			drawSprite(batch, orbTexture, w.x, w.y, w.size, w.spin, opacity,
					alpha, w.direction < 0);
		}
		for (Wheel w : wheels) {
			if (w.phase == Phase.DONE) continue;
			float alpha = clamp01(OPACITY_SCALE) * w.fade * w.morph;
			// 逆行轮同时镜像弯曲光刃，配合反向自旋呈现相反的旋向。
			drawSprite(batch, wheelTexture, w.x, w.y, w.size, w.spin, opacity, alpha,
					w.direction < 0);
		}
		batch.resetColor();
	}

	@Override
	public void draw(SpriteBatch batch, GameTime time) {
		if (removed || bloodTexture == null || bloodSplashes.isEmpty()) return;
		LColor opacity = scene.getGameOpacity();
		// 只隔离这一组血溅的提交状态，不让其他组件的乘色影响血溅淡出。
		// 保持进入前的状态提交已有内容，再以默认白色批次提交全部血溅顶点。
		batch.flush(batch.getBlendState());
		batch.resetColor();
		for (int i = 0; i < bloodSplashes.size(); i++) {
			bloodSplashes.get(i).draw(batch, bloodTexture, opacity);
		}
		batch.flush(batch.getBlendState());
	}

	private void drawSprite(SpriteBatch batch, LTexture texture, float x, float y,
			float size, float angle, LColor opacity, float alpha, boolean flipX) {
		if (alpha <= 0f || size <= 0f) return;
		tint.r = opacity.r;
		tint.g = opacity.g;
		tint.b = opacity.b;
		tint.a = opacity.a * alpha;
		batch.setColor(tint);
		batch.draw(texture, x, y, size * 0.5f, size * 0.5f, size, size, 1f, 1f, angle,
				0f, 0f, texture.getWidth(), texture.getHeight(), flipX, false, true);
	}

	private static float clamp01(float value) { return Math.max(0f, Math.min(1f, value)); }

	void cancel() {
		if (removed) return;
		removed = true;
		for (Wheel wheel : wheels) wheel.hitTargets.clear();
		bloodSplashes.clear();
		owner.Components().remove(this);
	}

	@Override
	protected void unloadContent() {
		removed = true;
		for (Wheel wheel : wheels) wheel.hitTargets.clear();
		targets.clear();
		bloodSplashes.clear();
		wheelTexture = null;
		orbTexture = null;
		bloodTexture = null;
		scene.removeGroundBladeWheel(this);
		super.unloadContent();
	}
}
