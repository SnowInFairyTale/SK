package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.timer.GameTime;

public class MonsterInfoScreenSpriteWithText extends Sprite {

	private static final float TEXT_X_RIGHT = 100f;
	private static final float TEXT_X_LEFT = 4f;
	private static final int LINE_STEP = 20;

	private LFont font;

	public MonsterInfoScreenSpriteWithText(MainGame game) {
		super(game, "assets/screen_monsters.png", 0, new Vector2f(0f, 0f));
		game.Components().add(this);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		super.draw(batch, gameTime);
		int lineStep = Constants.s(LINE_STEP);
		int num = Constants.infoScreenY(0x18);
		for (String str : LanguageResources.getMonsterInfoPar1().split("[$]",
				-1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str,
					Constants.s(TEXT_X_RIGHT), num, LColor.white);
			num += lineStep;
		}
		int num2 = Constants.infoScreenY(0x60);
		for (String str2 : LanguageResources.getMonsterInfoPar2().split("[$]",
				-1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str2,
					Constants.s(TEXT_X_LEFT), num2, LColor.white);
			num2 += lineStep;
		}
		int num3 = Constants.infoScreenY(0x9a);
		for (String str3 : LanguageResources.getMonsterInfoPar3().split("[$]",
				-1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str3,
					Constants.s(TEXT_X_RIGHT), num3, LColor.white);
			num3 += lineStep;
		}
		int num4 = Constants.infoScreenY(0xe0);
		for (String str4 : LanguageResources.getMonsterInfoPar4().split("[$]",
				-1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str4,
					Constants.s(TEXT_X_LEFT), num4, LColor.white);
			num4 += lineStep;
		}
		int num5 = Constants.infoScreenY(290);
		for (String str5 : LanguageResources.getMonsterInfoPar5().split("[$]",
				-1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str5,
					Constants.s(TEXT_X_RIGHT), num5, LColor.white);
			num5 += lineStep;
		}
		int num6 = Constants.infoScreenY(0x162);
		for (String str6 : LanguageResources.getMonsterInfoPar6().split("[$]",
				-1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str6,
					Constants.s(TEXT_X_LEFT), num6, LColor.white);
			num6 += lineStep;
		}
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.font = Constants.uiFont(16);
	}
}
