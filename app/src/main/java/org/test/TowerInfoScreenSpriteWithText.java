package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.timer.GameTime;

public class TowerInfoScreenSpriteWithText extends Sprite {

	private static final float TEXT_X = 99f;

	private LFont font;

	public TowerInfoScreenSpriteWithText(MainGame game) {
		super(game, "assets/towers_2.png", 0, new Vector2f(0f, 0f));
		game.Components().add(this);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		super.draw(batch, gameTime);
		int num = Constants.infoScreenY(0x1a);
		int lineStep = Constants.s(0x10);
		float textX = Constants.s(TEXT_X);
		for (String str : LanguageResources.getTowerInfoPar1().split("[$]", -1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str, textX, num,
					LColor.white);
			num += lineStep;
		}
		int num3 = Constants.infoScreenY(0x80);
		for (String str2 : LanguageResources.getTowerInfoPar2()
				.split("[$]", -1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str2, textX, num3,
					LColor.white);
			num3 += lineStep;
		}
		int num4 = Constants.infoScreenY(0xe2);
		for (String str3 : LanguageResources.getTowerInfoPar3()
				.split("[$]", -1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str3, textX, num4,
					LColor.white);
			num4 += lineStep;
		}
		int num5 = Constants.infoScreenY(0x146);
		for (String str4 : LanguageResources.getTowerInfoPar4()
				.split("[$]", -1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str4, textX, num5,
					LColor.white);
			num5 += lineStep;
		}
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.font = Constants.uiFont(12);
	}
}
