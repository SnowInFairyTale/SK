package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.timer.GameTime;

public class TowerInfoScreenSpriteWithText extends Sprite {

	private LFont font;

	public TowerInfoScreenSpriteWithText(MainGame game) {
		super(game, "assets/towers_2.png", 0, new Vector2f(0f, 0f));
		game.Components().add(this);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		super.draw(batch, gameTime);
		int num = 0x34;
		int num2 = 0x20;
		for (String str : LanguageResources.getTowerInfoPar1().split("[$]", -1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str, 198f, num,
					LColor.white);
			num += num2;
		}
		int num3 = 0x100;
		for (String str2 : LanguageResources.getTowerInfoPar2()
				.split("[$]", -1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str2, 198f, num3,
					LColor.white);
			num3 += num2;
		}
		int num4 = 0x1c4;
		for (String str3 : LanguageResources.getTowerInfoPar3()
				.split("[$]", -1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str3, 198f, num4,
					LColor.white);
			num4 += num2;
		}
		int num5 = 0x28c;
		for (String str4 : LanguageResources.getTowerInfoPar4()
				.split("[$]", -1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str4, 198f, num5,
					LColor.white);
			num5 += num2;
		}
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.font = Constants.font(24);
	}
}