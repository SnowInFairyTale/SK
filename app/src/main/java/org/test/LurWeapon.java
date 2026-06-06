package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableGameComponent;
import loon.action.sprite.painting.IGameComponent;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.timer.GameTime;

public class LurWeapon extends DrawableGameComponent implements IGameComponent {

	private static final int INITIAL_RADIUS = 40;
	private static final int RADIUS_STEP = 4;
	private static final float BASH2_ALPHA = 0.55f;
	private static final float BASH2_SCALE = 1.1f;

	private int bashRadius;
	private LTexture bashTexture;
	private LTexture bash2Texture;
	private int damage;
	private final GemType weaponGem;
	private final boolean gemAttack;
	private MainGame game;

	private java.util.ArrayList<Monster> targetMonsters;
	private Tower tower;
	private boolean finished;
	private final Vector2f ringDrawPosition = new Vector2f();
	private final LColor ringTint = new LColor(LColor.white);
	private final LColor ring2Tint = new LColor(LColor.white);

	public LurWeapon(MainGame game, Tower tower,
			java.util.ArrayList<Monster> targetMonsters) {
		super(game);
		this.game = game;
		this.tower = tower;
		this.damage = tower.getDamage();
		this.weaponGem = tower.getWeaponGemType();
		this.gemAttack = this.weaponGem != GemType.None;
		this.setHasHitTarget(false);
		this.targetMonsters = targetMonsters;

		this.bashRadius = INITIAL_RADIUS;
		for (Monster monster : targetMonsters) {
			monster.addReservedHitPoints(this.damage);
		}
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		if (this.finished) {
			return;
		}
		batch.resetColor();
		this.applyGameOpacity(LColor.white, this.ringTint);
		this.drawRing(batch, this.bashTexture, this.bashRadius, this.ringTint,
				1f);
		if (this.gemAttack) {
			this.applyGameOpacity(LColor.white, this.ring2Tint);
			this.ring2Tint.a *= BASH2_ALPHA;
			this.drawRing(batch, this.bash2Texture, this.bashRadius,
					this.ring2Tint, BASH2_SCALE);
		}
		batch.resetColor();
	}

	private void drawRing(SpriteBatch batch, LTexture texture, int radius,
			LColor tint, float scale) {
		Vector2f towerPos = this.tower.getPosition();
		float halfSize = radius * scale;
		float size = halfSize * 2f;
		this.ringDrawPosition.set(towerPos.x - halfSize, towerPos.y - halfSize);
		batch.setColor(tint);
		batch.draw(texture, this.ringDrawPosition.x, this.ringDrawPosition.y,
				size, size);
	}

	private void applyGameOpacity(LColor base, LColor out) {
		out.r = base.r;
		out.g = base.g;
		out.b = base.b;
		out.a = base.a;
		if (this.game.getGameplayScreen() == null) {
			return;
		}
		LColor gameOpacity = this.game.getGameplayScreen().getGameOpacity();
		if (gameOpacity.equals(LColor.white)) {
			return;
		}
		out.r *= gameOpacity.r;
		out.g *= gameOpacity.g;
		out.b *= gameOpacity.b;
		out.a *= gameOpacity.a;
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.bashTexture = LTextures.loadTexture("assets/bash.png");
		if (this.gemAttack) {
			LColor recolor = GemWeaponColors.getRecolor(this.weaponGem);
			this.bash2Texture = TextureRecolor.recolorBlackTo(
					"assets/bash2.png", recolor);
		}
	}

	@Override
	protected void unloadContent() {
		if (!this.finished) {
			this.finished = true;
			this.setHasHitTarget(true);
			this.releaseReservedHitPoints();
		}
		super.unloadContent();
	}

	@Override
	public void update(GameTime gameTime) {
		if (this.finished) {
			return;
		}
		super.update(gameTime);
		int range = (int) this.tower.getRange();
		if (this.bashRadius >= range) {
			this.finishAttack();
		} else {
			this.bashRadius = Math.min(this.bashRadius + RADIUS_STEP, range);
		}
	}

	public final void cancel() {
		if (this.finished) {
			return;
		}
		this.finished = true;
		this.setHasHitTarget(true);
		this.releaseReservedHitPoints();
		this.game.Components().remove(this);
	}

	private void finishAttack() {
		if (this.finished) {
			return;
		}
		this.finished = true;
		this.setHasHitTarget(true);
		for (int i = 0; i < this.targetMonsters.size(); i++) {
			Monster monster = this.targetMonsters.get(i);
			monster.removeReservedHitPoints(this.damage);
			monster.Hit(this.damage);
		}
		this.game.Components().remove(this);
	}

	private void releaseReservedHitPoints() {
		for (int i = 0; i < this.targetMonsters.size(); i++) {
			this.targetMonsters.get(i).removeReservedHitPoints(this.damage);
		}
	}

	private boolean privateHasHitTarget;

	public final boolean getHasHitTarget() {
		return privateHasHitTarget;
	}

	public final void setHasHitTarget(boolean value) {
		privateHasHitTarget = value;
	}
}
