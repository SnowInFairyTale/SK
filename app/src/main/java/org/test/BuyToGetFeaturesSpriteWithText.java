package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.timer.GameTime;

public class BuyToGetFeaturesSpriteWithText extends Sprite {

	private AnimatedSprite animatedSpriteTowerLur;

	private AnimatedSprite animatedSpriteTowerSpear;

	private LFont font;

	private LFont fontHuge;

	private LFont fontMedium;

	private MainGame game;

	public BuyToGetFeaturesSpriteWithText(MainGame game) {
		super(game, "assets/buytogetfeatures.png", 0, new Vector2f(0f, 0f));
		this.game = game;
		this.animatedSpriteTowerSpear = AnimatedSpriteTower.GetPreviewTower(game,
				TowerType.Spear, 218f, 308f, 0.4f);
		this.animatedSpriteTowerSpear.setDrawOrder(0x149);
		this.animatedSpriteTowerSpear.setAnimationSpeedRatio(3);
		this.animatedSpriteTowerSpear.setObeyGameOpacity(false);
		this.animatedSpriteTowerSpear.setOnlyAnimateIfGameStateStarted(false);
		this.animatedSpriteTowerLur = AnimatedSpriteTower.GetPreviewTower(game,
				TowerType.Lur, 538f, 264f, 0.4f);
		this.animatedSpriteTowerLur.setDrawOrder(0x149);
		this.animatedSpriteTowerLur.setAnimationSpeedRatio(3);
		this.animatedSpriteTowerLur.setObeyGameOpacity(false);
		this.animatedSpriteTowerLur.setOnlyAnimateIfGameStateStarted(false);
		game.Components().add(this.animatedSpriteTowerSpear);
		game.Components().add(this.animatedSpriteTowerLur);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		super.draw(batch, gameTime);
		Utils.DrawStringAlignCenter(batch, this.fontHuge,
				LanguageResources.getBuyGame(), 320f, 6f, LColor.white);
		Utils.DrawStringAlignCenter(batch, this.fontMedium,
				LanguageResources.getPleaseUnlock(), new Vector2f(320f, 148f),
				LColor.white);
		int num = 16;
		int num2 = 0x15c;
		Utils.DrawStringAlignLeft(batch, this.font,
				"* " + LanguageResources.getMediumDifficulty(), num, 204f,
				LColor.white);
		Utils.DrawStringAlignLeft(batch, this.font,
				"* " + LanguageResources.getSpearTower(), num, 264f,
				LColor.white);
		Utils.DrawStringAlignLeft(batch, this.font,
				"* " + LanguageResources.getHardDifficulty(), num, 324f,
				LColor.white);
		Utils.DrawStringAlignLeft(batch, this.font,
				"* " + LanguageResources.getLurTower(), num2, 204f,
				LColor.white);
		Utils.DrawStringAlignLeft(batch, this.font,
				"* " + LanguageResources.getTwoExtraLevels(), num2, 264f,
				LColor.white);
		Utils.DrawStringAlignLeft(batch, this.font,
				"* " + LanguageResources.getSupportTheGame(), num2, 324f,
				LColor.white);
		Utils.DrawStringAlignCenter(batch, this.font, LanguageResources
				.getUnlockGame().toUpperCase(), 188f, 866f, LColor.white);
		Utils.DrawStringAlignCenter(batch, this.font,
				LanguageResources.getNotNow(), 484f, 866f, LColor.white);
	}

	public final void Exit() {
		if (this.animatedSpriteTowerSpear != null) {
			this.game.Components().remove(this.animatedSpriteTowerSpear);
		}
		if (this.animatedSpriteTowerLur != null) {
			this.game.Components().remove(this.animatedSpriteTowerLur);
		}
		this.game.Components().remove(this);
	}

	@Override
	protected void loadContent() {
		this.fontHuge = Constants.font(64);
		this.fontMedium = Constants.font(32);
		this.font = Constants.font(24);
		super.loadContent();
	}
}