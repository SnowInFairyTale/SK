package org.test;

import com.loon.action.sprite.SpriteBatch;
import com.loon.action.sprite.painting.DrawableGameComponent;
import com.loon.action.sprite.painting.IGameComponent;
import com.loon.core.graphics.LColor;
import com.loon.core.graphics.LFont;
import com.loon.core.timer.GameTime;

/** Monster info overlay text — background is drawn by {@link MonsterInfoScreen}. */
public class MonsterInfoScreenSpriteWithText extends DrawableGameComponent
		implements IGameComponent {

	private static final String[] PAR_1 = LanguageResources
			.getMonsterInfoPar1().split("[$]", -1);
	private static final String[] PAR_2 = LanguageResources
			.getMonsterInfoPar2().split("[$]", -1);
	private static final String[] PAR_3 = LanguageResources
			.getMonsterInfoPar3().split("[$]", -1);
	private static final String[] PAR_4 = LanguageResources
			.getMonsterInfoPar4().split("[$]", -1);
	private static final String[] PAR_5 = LanguageResources
			.getMonsterInfoPar5().split("[$]", -1);
	private static final String[] PAR_6 = LanguageResources
			.getMonsterInfoPar6().split("[$]", -1);

	private LFont backFont;
	private LFont font;

	public MonsterInfoScreenSpriteWithText(MainGame game) {
		super(game);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		int num = 0x30;
		for (String str : PAR_1) {
			Utils.DrawStringAlignLeft(batch, this.font, str, 200f, num,
					LColor.white);
			num += 40;
		}
		int num2 = 0xc0;
		for (String str2 : PAR_2) {
			Utils.DrawStringAlignLeft(batch, this.font, str2, 8f, num2,
					LColor.white);
			num2 += 40;
		}
		int num3 = 0x134;
		for (String str3 : PAR_3) {
			Utils.DrawStringAlignLeft(batch, this.font, str3, 200f, num3,
					LColor.white);
			num3 += 40;
		}
		int num4 = 0x1c0;
		for (String str4 : PAR_4) {
			Utils.DrawStringAlignLeft(batch, this.font, str4, 8f, num4,
					LColor.white);
			num4 += 40;
		}
		int num5 = 580;
		for (String str5 : PAR_5) {
			Utils.DrawStringAlignLeft(batch, this.font, str5, 200f, num5,
					LColor.white);
			num5 += 40;
		}
		int num6 = 0x2c4;
		for (String str6 : PAR_6) {
			Utils.DrawStringAlignLeft(batch, this.font, str6, 8f, num6,
					LColor.white);
			num6 += 40;
		}
		Utils.drawInfoBackButtonLabel(batch, this.backFont);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.font = Constants.font(32);
		this.backFont = Constants.font(Constants.INFO_BACK_FONT_SIZE);
	}
}
