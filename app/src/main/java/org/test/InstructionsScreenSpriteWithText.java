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

	private TextSprite enemiesLabel;
	private TextSprite menuLabel;
	private TextSprite towersLabel;

	public InstructionsScreenSpriteWithText(MainGame game) {
		super(game);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		if (this.towersLabel != null) {
			this.towersLabel.drawCenteredInButton(batch,
					Constants.INTRO_BTN_TOWERS_CENTER_X,
					Constants.INTRO_BTN_TOP_Y,
					Constants.INTRO_BTN_TEXT_HEIGHT);
		}
		if (this.enemiesLabel != null) {
			this.enemiesLabel.drawCenteredInButton(batch,
					Constants.INTRO_BTN_ENEMIES_CENTER_X,
					Constants.INTRO_BTN_TOP_Y,
					Constants.INTRO_BTN_TEXT_HEIGHT);
		}
		if (this.menuLabel != null) {
			this.menuLabel.drawCenteredInButton(batch,
					Constants.INTRO_BTN_MENU_CENTER_X,
					Constants.INTRO_BTN_TOP_Y,
					Constants.INTRO_BTN_TEXT_HEIGHT);
		}
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.buildLabels();
	}

	@Override
	protected void unloadContent() {
		this.disposeLabels();
		super.unloadContent();
	}

	private void buildLabels() {
		this.disposeLabels();
		LFont font = Constants.font(32);
		this.towersLabel = TextSprite.create(font, LABEL_TOWERS);
		this.enemiesLabel = TextSprite.create(font, LABEL_ENEMIES);
		this.menuLabel = TextSprite.create(font, LABEL_MENU);
	}

	private void disposeLabels() {
		if (this.towersLabel != null) {
			this.towersLabel.dispose();
			this.towersLabel = null;
		}
		if (this.enemiesLabel != null) {
			this.enemiesLabel.dispose();
			this.enemiesLabel = null;
		}
		if (this.menuLabel != null) {
			this.menuLabel.dispose();
			this.menuLabel = null;
		}
	}
}
