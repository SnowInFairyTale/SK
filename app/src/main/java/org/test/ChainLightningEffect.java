package org.test;

import java.util.ArrayList;

import com.loon.action.sprite.SpriteBatch;
import com.loon.action.sprite.painting.DrawableGameComponent;
import com.loon.core.graphics.LColor;
import com.loon.core.graphics.opengl.LTexture;
import com.loon.core.timer.GameTime;

/** One impact plus three subsequent hops, with no recursive projectile spawning. */
public final class ChainLightningEffect extends DrawableGameComponent {
	private static final int IMPACT_DRAW_ORDER = 33;
	private final MainGame owner;
	private final GameplayScreen scene;
	private final ChainLightningSequence<Monster> sequence;
	private final int seed = (int) System.nanoTime();
	private final LColor tint = new LColor(LColor.white);
	private final ArrayList<Monster> targets = new ArrayList<>();
	private final DrawableGameComponent impactLayer;
	private LTexture glowTexture;
	private LTexture impactTexture;
	private boolean removed;

	public ChainLightningEffect(MainGame owner, Monster first, final int damage) {
		super(owner);
		this.owner = owner;
		this.scene = owner.getGameplayScreen();
		setDrawOrder(32); // Above projectiles, below health bars and overlays.
		sequence = new ChainLightningSequence<>(first,
				new ChainLightningSequence.Targets<Monster>() {
					public float x(Monster target) { return target.getPosition().x; }
					public float y(Monster target) { return target.getPosition().y; }
					public boolean alive(Monster target) {
						return !target.getDead() && target.getHitPoints() > 0;
					}
					public void hit(Monster target) { target.Hit(damage); }
				});
		// 只拆出爆点绘制，共用原序列和寿命，不重复更新或结算伤害。
		impactLayer = new DrawableGameComponent(owner) {
			@Override
			public void draw(SpriteBatch batch, GameTime time) {
				if (removed) return;
				LightningVisuals.drawImpacts(sequence, sprites(batch, scene.getGameOpacity()), seed);
				batch.resetColor();
			}
		};
		impactLayer.setDrawOrder(IMPACT_DRAW_ORDER);
	}

	@Override
	public void update(GameTime time) {
		if (removed) return;
		if (owner.getGameplayScreen() != scene || scene.getIsExiting()
				|| GameplayScreen.getGameState() == GameState.Ended) {
			cancel();
			return;
		}
		if (GameplayScreen.getGameState() != GameState.Started) return;
		scene.getWaveManager().copyActiveMonstersTo(targets);
		sequence.update(time.getElapsedGameTime(), targets);
		if (sequence.isFinished()) cancel();
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		// 借用场景持有的原图，场景退出时统一释放。
		glowTexture = scene.getEffectTexture("assets/effects/lightning_glow.png");
		impactTexture = scene.getEffectTexture("assets/effects/lightning_impact.png");
		owner.Components().add(impactLayer);
		SoundManager.PlaySound(SoundManager.LIGHTNING);
	}

	@Override
	public void draw(final SpriteBatch batch, GameTime time) {
		if (removed) return;
		final LColor opacity = scene.getGameOpacity();
		LightningVisuals.Stroke renderer = (soft, x1, y1, x2, y2, width, r, g, b, alpha) -> {
			tint.r = r * opacity.r;
			tint.g = g * opacity.g;
			tint.b = b * opacity.b;
			tint.a = alpha * opacity.a;
			batch.setColor(tint);
			if (soft) {
				float dx = x2 - x1, dy = y2 - y1;
				float length = (float) Math.sqrt(dx * dx + dy * dy);
				if (length <= 0f || width <= 0f) return;
				// 沿线的所有像素取原圆形柔光的同一列，保留原色彩和横向衰减。
				// srcWidth = 0 使两端 U 坐标相同；绘制长度仍为 length。
				float centerColumn = glowTexture.getWidth() / 2 + 0.5f;
				batch.draw(glowTexture, (x1 + x2) * 0.5f, (y1 + y2) * 0.5f,
						length * 0.5f, width * 0.5f, length, width, 1f, 1f,
						(float) Math.toDegrees(Math.atan2(dy, dx)), centerColumn, 0f,
						0f, glowTexture.getHeight(),
						false, false, true);
			} else {
				batch.drawLine(x1, y1, x2, y2, width);
			}
		};
		LightningVisuals.drawBase(sequence, renderer, sprites(batch, opacity), seed);
		batch.resetColor();
	}

	private LightningVisuals.Sprites sprites(final SpriteBatch batch, final LColor opacity) {
		return (impact, x, y, width, height, angle, alpha) -> {
			LTexture texture = impact ? impactTexture : glowTexture;
			tint.r = opacity.r;
			tint.g = opacity.g;
			tint.b = opacity.b;
			tint.a = opacity.a * alpha;
			batch.setColor(tint);
			batch.draw(texture, x, y, width / 2f, height / 2f, width, height,
					1f, 1f, angle, 0f, 0f, texture.getWidth(), texture.getHeight(),
					false, false, true);
		};
	}

	private void cancel() {
		removed = true;
		sequence.cancel();
		owner.Components().remove(this);
	}

	@Override
	protected void unloadContent() {
		removed = true;
		owner.Components().remove(impactLayer);
		sequence.cancel();
		targets.clear();
		glowTexture = null;
		impactTexture = null;
		super.unloadContent();
	}
}
