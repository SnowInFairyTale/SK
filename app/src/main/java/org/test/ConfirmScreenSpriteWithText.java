package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.timer.GameTime;

public class ConfirmScreenSpriteWithText extends Sprite {

	private LFont font;

	private LFont fontStdHuge;

	public ConfirmScreenSpriteWithText(MainGame game) {
		super(game, "assets/shield.png", 0, new Vector2f(0f, 0f));
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		super.draw(batch, gameTime);
		batch.drawString(this.fontStdHuge, LanguageResources.getAreYouSure(),
				160f, 320f, LColor.white);
		Utils.DrawStringAlignCenter(batch, this.font, LanguageResources
				.getYes().toUpperCase(), 160f, 480f, LColor.white);
		Utils.DrawStringAlignCenter(batch, this.font, LanguageResources.getNo()
				.toUpperCase(), 480f, 480f, LColor.white);
	}

	@Override
	protected void loadContent() {
		this.fontStdHuge = Constants.font(52);
		this.font = Constants.font(24);
		super.loadContent();
	}
}