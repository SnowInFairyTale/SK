package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.timer.GameTime;
import loon.utils.MathUtils;

public class MissileSpear extends Missile {

	private static final float SPEAR_GLOW_SCALE_X = 1.65f;
	private static final float SPEAR_GLOW_SCALE_Y = 1.15f;

	public MissileSpear(MainGame game, Monster targetMonster, Tower tower) {
		super(game, MissileType.SPEAR, "assets/spear.png", tower.getPosition(),
				targetMonster, tower.getDamage(), 2, 2, 48, 48,
				tower.getWeaponGemType());
	}

	@Override
	protected void drawMissileGlow(SpriteBatch batch, LColor glow,
			GameTime gameTime) {
		Vector2f pos = this.getDrawPosition();
		Vector2f origin = this.getOrigin();
		batch.draw(this.getAnimationTexture(), pos.x, pos.y,
				this.getAnimationFrameRect(), glow,
				MathUtils.toDegrees(this.getRotation()), origin.x, origin.y,
				SPEAR_GLOW_SCALE_X, SPEAR_GLOW_SCALE_Y, SpriteEffects.None);
	}

	private int privateDamage;

	@Override
	public int getDamage() {
		return privateDamage;
	}

	@Override
	public void setDamage(int value) {
		privateDamage = value;
	}
}
