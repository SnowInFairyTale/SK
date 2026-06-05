package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.timer.GameTime;
import loon.utils.MathUtils;

public abstract class Missile extends AnimatedSprite {

	private static final float GLOW_ALPHA = 0.5f;
	private static final float GLOW_SCALE = 1.65f;

	private float dist;
	private MainGame game;
	private float speed;
	private float speedPrFrame;
	private Monster targetMonster;

	public Missile(MainGame game, MissileType missileType, String textureFile,
			Vector2f towerPosition, Monster targetMonster, int damage,
			int columnCount, int spriteCount, int spriteWidth, int spriteHeight) {
		super(game, textureFile, towerPosition.cpy().sub(18f, 18f), columnCount,
				spriteCount, spriteWidth, spriteHeight, 1f);
		this.speed = 12f;
		this.targetMonster = targetMonster;
		this.setHasHitTarget(false);
		this.game = game;
		targetMonster.addReservedHitPoints(this.getDamage());
		this.setDamage(damage);
		this.speedPrFrame = 30f;
		this.setDirection(Utils.GetDirection(towerPosition.cpy().sub(18f, 18f),
				targetMonster.getPosition()));
		super.setOrigin(new Vector2f(spriteWidth / 2f, spriteHeight / 2f));
		super.setDrawOrder(0x1f);
		if (missileType == MissileType.SPEAR) {
			super.setRotation(Utils.GetAngle(this.getDirection()) + 1.570796f);
		}
	}

	@Override
	protected void drawGlowLayer(SpriteBatch batch, GameTime gameTime) {
		LColor glowBase = this.getMissileGlowColor();
		if (glowBase == null) {
			return;
		}
		batch.flush();
		LColor glow = this.resolveTintColor(glowBase);
		batch.setColor(glow);
		this.drawMissileGlow(batch, glow, gameTime);
		batch.resetColor();
		batch.flush();
	}

	protected void drawMissileGlow(SpriteBatch batch, LColor glow,
			GameTime gameTime) {
		batch.draw(this.getAnimationTexture(), this.getDrawPosition(),
				this.getAnimationFrameRect(), glow,
				MathUtils.toDegrees(this.getRotation()), this.getOrigin(),
				GLOW_SCALE, SpriteEffects.None);
	}

	protected LColor getMissileGlowColor() {
		LColor base = this.getBlackRecolorTarget();
		if (base == null) {
			return null;
		}
		return new LColor(base.r, base.g, base.b, GLOW_ALPHA);
	}

	@Override
	public void update(GameTime gameTime) {
		super.update(gameTime);
		float distance = Utils.GetDistance(this.targetMonster.getPosition(),
				super.getDrawPosition());
		super.addDrawPosition(this.getDirection().mul(this.speed));
		if ((this.speedPrFrame >= distance)
				|| ((this.dist > 0f) && (distance > this.dist))) {
			this.setHasHitTarget(true);
			this.targetMonster.removeReservedHitPoints(this.getDamage());
			this.targetMonster.Hit(this.getDamage());
			this.game.Components().remove(this);
		}
		this.dist = distance;
	}

	private int privateDamage;

	public int getDamage() {
		return privateDamage;
	}

	public void setDamage(int value) {
		privateDamage = value;
	}

	private Vector2f privateDirection;

	public final Vector2f getDirection() {
		return privateDirection;
	}

	public final void setDirection(Vector2f value) {
		privateDirection = value;
	}

	private boolean privateHasHitTarget;

	public final boolean getHasHitTarget() {
		return privateHasHitTarget;
	}

	public final void setHasHitTarget(boolean value) {
		privateHasHitTarget = value;
	}

	private MissileType privateMissileType = MissileType.values()[0];

	public MissileType getMissileType() {
		return privateMissileType;
	}

	public void setMissileType(MissileType value) {
		privateMissileType = value;
	}

	private boolean privatePlayed_Sound;

	public final boolean getPlayed_Sound() {
		return privatePlayed_Sound;
	}

	public final void setPlayed_Sound(boolean value) {
		privatePlayed_Sound = value;
	}

	private int privatePos_X;

	public final int getPos_X() {
		return privatePos_X;
	}

	public final void setPos_X(int value) {
		privatePos_X = value;
	}

	private int privatePos_Y;

	public final int getPos_Y() {
		return privatePos_Y;
	}

	public final void setPos_Y(int value) {
		privatePos_Y = value;
	}
}
