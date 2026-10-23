package org.test;

import com.loon.action.sprite.SpriteBatch;
import com.loon.action.sprite.SpriteBatch.SpriteEffects;
import com.loon.core.geom.Vector2f;
import com.loon.core.graphics.LColor;
import com.loon.core.timer.GameTime;
import com.loon.utils.MathUtils;

public class MissileSpear extends Missile {

	private static final float SPEAR_GLOW_SCALE_X = 1.65f;
	private static final float SPEAR_GLOW_SCALE_Y = 1.15f;
	private final MainGame owner;
	private final TowerType sourceTowerType;
	private final boolean chainLightning;
	private final boolean piercingArrow;

	public MissileSpear(MainGame game, Monster targetMonster, Tower tower) {
		super(game, MissileType.SPEAR, "assets/spear.png", tower.getPosition(),
				targetMonster, tower.getDamage(), 2, 2, 48, 48,
				tower.getWeaponGemType());
		this.owner = game;
		this.sourceTowerType = tower.getTowerType();
		boolean skillTriggered = tower.rollSkillTrigger();
		this.chainLightning = skillTriggered && this.sourceTowerType == TowerType.Spear;
		this.piercingArrow = skillTriggered && this.sourceTowerType == TowerType.AirDefence;
	}

	@Override
	protected boolean canHitTarget(Monster target) {
		return TowerCombatRules.canDamage(this.sourceTowerType, target.getMonsterType());
	}

	@Override
	protected void onImpact(Monster target) {
		if (this.chainLightning) {
			this.owner.Components().add(new ChainLightningEffect(this.owner, target,
					this.getDamage()));
		}
		if (this.piercingArrow && PiercingArrowEffect.ENABLED) {
			this.owner.Components().add(new PiercingArrowEffect(this.owner, target,
					this.getDirection(), this.getDamage()));
		}
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
