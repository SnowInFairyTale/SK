package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.timer.GameTime;

public class InstructionsScreenSpriteWithText extends Sprite {
	private LFont font;
	private LFont fontBig;

	public InstructionsScreenSpriteWithText(MainGame game) {
		super(game, "assets/screen_introduction.png", 0, new Vector2f(0f, 0f));
		game.Components().add(this);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		super.draw(batch, gameTime);

		int num = 12;
		int num2 = 12;
		Utils.DrawStringAlignLeft(batch, this.fontBig,
				LanguageResources.getInstructionsHeader1(), num, num2,
				LColor.white);
		num2 += 60;
		for (String str : LanguageResources.getInstructionsPar1().split("[$]",
				-1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str, num, num2,
					LColor.white);
			num2 += 0x22;
		}
		num2 += 12;
		Utils.DrawStringAlignLeft(batch, this.fontBig,
				LanguageResources.getInstructionsHeader2(), num, num2,
				LColor.white);
		num2 += 60;
		for (String str2 : LanguageResources.getInstructionsPar2().split("[$]",
				-1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str2, num, num2,
					LColor.white);
			num2 += 0x22;
		}
		num2 += 12;
		Utils.DrawStringAlignLeft(batch, this.fontBig,
				LanguageResources.getInstructionsHeader3(), num, num2,
				LColor.white);
		num2 += 60;
		for (String str3 : LanguageResources.getInstructionsPar3().split("[$]",
				-1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str3, num, num2,
					LColor.white);
			num2 += 0x22;
		}
		Utils.DrawButtonLabel(batch, this.font, LanguageResources.getTowers()
				.toUpperCase(), 124f, 850f, 76f, LColor.white);
		Utils.DrawButtonLabel(batch, this.font, LanguageResources.getEnemies()
				.toUpperCase(), 358f, 850f, 76f, LColor.white);
		Utils.DrawButtonLabel(batch, this.font, LanguageResources.getMenu()
				.toUpperCase(), 556f, 850f, 76f, LColor.white);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.font = Constants.font(32);
		this.fontBig = Constants.font(40);
	}
}