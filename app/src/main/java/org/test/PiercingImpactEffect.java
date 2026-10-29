package org.test;

import com.loon.action.sprite.SpriteBatch;
import com.loon.action.sprite.painting.DrawableGameComponent;
import com.loon.core.graphics.LColor;
import com.loon.core.graphics.opengl.LTexture;
import com.loon.core.timer.GameTime;

/** 穿透命中位置的柔光爆点，独立于光束寿命播放。 */
final class PiercingImpactEffect extends DrawableGameComponent {
	private static final float DURATION = 0.20f;
	private static final float START_SIZE = 26.6f;
	private static final float END_SIZE = 43.4f;
	private static final float OPACITY = 0.9f;
	private final MainGame owner;
	private final GameplayScreen scene;
	private final float x, y;
	private final LColor tint = new LColor(LColor.white);
	private LTexture texture;
	private float age;
	private boolean removed;

	PiercingImpactEffect(MainGame owner, float x, float y) {
		super(owner);
		this.owner = owner;
		this.scene = owner.getGameplayScreen();
		this.x = x;
		this.y = y;
		setDrawOrder(33);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		// 借用场景缓存的贴图，退出场景时统一释放。
		texture = scene.getEffectTexture("assets/effects/lightning_glow.png");
		texture.loadTexture();
	}

	@Override
	public void update(GameTime time) {
		if (removed) return;
		if (!PiercingArrowEffect.ENABLED || owner.getGameplayScreen() != scene
				|| scene.getIsExiting() || GameplayScreen.getGameState() == GameState.Ended) {
			cancel();
			return;
		}
		if (GameplayScreen.getGameState() != GameState.Started) return;
		age += Math.max(0f, time.getElapsedGameTime());
		if (age >= DURATION) cancel();
	}

	@Override
	public void draw(SpriteBatch batch, GameTime time) {
		if (removed || texture == null) return;
		float progress = Math.min(1f, age / DURATION);
		float remaining = 1f - progress;
		float size = START_SIZE + (END_SIZE - START_SIZE)
				* (1f - remaining * remaining * remaining);
		LColor opacity = scene.getGameOpacity();
		tint.r = opacity.r;
		tint.g = opacity.g;
		tint.b = opacity.b;
		tint.a = opacity.a * OPACITY * remaining;
		batch.setColor(tint);
		batch.draw(texture, x, y, size / 2f, size / 2f, size, size,
				1f, 1f, 0f, 0f, 0f, texture.getWidth(), texture.getHeight(),
				false, false, true);
		batch.resetColor();
	}

	private void cancel() {
		removed = true;
		owner.Components().remove(this);
	}

	@Override
	protected void unloadContent() {
		removed = true;
		texture = null;
		super.unloadContent();
	}
}
