package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.LFont;
import loon.core.timer.GameTime;

public class InstructionsScreenSpriteWithText extends Sprite {
	private LFont font;

	public InstructionsScreenSpriteWithText(MainGame game) {
		super(game, "assets/screen_introduction.png", 0, new Vector2f(0f, 0f));
		super.setDrawOrder(Constants.INFO_OVERLAY_DRAW_ORDER);
		game.Components().add(this);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		super.draw(batch, gameTime);
		batch.flush();
		Utils.drawIntroButtonLabel(batch, this.font, LanguageResources
				.getTowers().toUpperCase(), Constants.INTRO_BTN_TOWERS_CENTER_X);
		Utils.drawIntroButtonLabel(batch, this.font, LanguageResources
				.getEnemies().toUpperCase(),
				Constants.INTRO_BTN_ENEMIES_CENTER_X);
		Utils.drawIntroButtonLabel(batch, this.font, LanguageResources
				.getMenu().toUpperCase(), Constants.INTRO_BTN_MENU_CENTER_X);
		batch.resetColor();
		batch.flush();
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.font = Constants.font(32);
	}
}
