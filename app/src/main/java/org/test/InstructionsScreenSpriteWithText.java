package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableGameComponent;
import loon.action.sprite.painting.IGameComponent;
import loon.core.graphics.LFont;
import loon.core.timer.GameTime;

/** Introduction screen button labels — background is drawn by {@link InstructionScreen}. */
public class InstructionsScreenSpriteWithText extends DrawableGameComponent
		implements IGameComponent {

	private static final String LABEL_ENEMIES = LanguageResources.getEnemies()
			.toUpperCase();
	private static final String LABEL_MENU = LanguageResources.getMenu()
			.toUpperCase();
	private static final String LABEL_TOWERS = LanguageResources.getTowers()
			.toUpperCase();

	private LFont font;

	public InstructionsScreenSpriteWithText(MainGame game) {
		super(game);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		batch.flush();
		Utils.drawIntroButtonLabel(batch, this.font, LABEL_TOWERS,
				Constants.INTRO_BTN_TOWERS_CENTER_X);
		Utils.drawIntroButtonLabel(batch, this.font, LABEL_ENEMIES,
				Constants.INTRO_BTN_ENEMIES_CENTER_X);
		Utils.drawIntroButtonLabel(batch, this.font, LABEL_MENU,
				Constants.INTRO_BTN_MENU_CENTER_X);
		batch.resetColor();
		batch.flush();
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.font = Constants.font(32);
	}
}
