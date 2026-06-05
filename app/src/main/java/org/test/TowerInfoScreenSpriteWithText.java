package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableGameComponent;
import loon.action.sprite.painting.IGameComponent;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.timer.GameTime;

/** Tower info overlay text — background is drawn by {@link TowerInfoScreen}. */
public class TowerInfoScreenSpriteWithText extends DrawableGameComponent
		implements IGameComponent {

	private static final String[] PAR_1 = LanguageResources.getTowerInfoPar1()
			.split("[$]", -1);
	private static final String[] PAR_2 = LanguageResources.getTowerInfoPar2()
			.split("[$]", -1);
	private static final String[] PAR_3 = LanguageResources.getTowerInfoPar3()
			.split("[$]", -1);
	private static final String[] PAR_4 = LanguageResources.getTowerInfoPar4()
			.split("[$]", -1);

	private LFont backFont;
	private LFont font;

	public TowerInfoScreenSpriteWithText(MainGame game) {
		super(game);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		int num = 0x34;
		int num2 = 0x20;
		for (String str : PAR_1) {
			Utils.DrawStringAlignLeft(batch, this.font, str, 198f, num,
					LColor.white);
			num += num2;
		}
		int num3 = 0x100;
		for (String str2 : PAR_2) {
			Utils.DrawStringAlignLeft(batch, this.font, str2, 198f, num3,
					LColor.white);
			num3 += num2;
		}
		int num4 = 0x1c4;
		for (String str3 : PAR_3) {
			Utils.DrawStringAlignLeft(batch, this.font, str3, 198f, num4,
					LColor.white);
			num4 += num2;
		}
		int num5 = 0x28c;
		for (String str4 : PAR_4) {
			Utils.DrawStringAlignLeft(batch, this.font, str4, 198f, num5,
					LColor.white);
			num5 += num2;
		}
		Utils.drawInfoBackButtonLabel(batch, this.backFont);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.font = Constants.font(24);
		this.backFont = Constants.font(Constants.INFO_BACK_FONT_SIZE);
	}
}
