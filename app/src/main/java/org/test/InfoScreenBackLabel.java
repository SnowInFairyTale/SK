package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableGameComponent;
import loon.action.sprite.painting.IGameComponent;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.timer.GameTime;

/** Back button caption drawn above info-screen animated sprites. */
public class InfoScreenBackLabel extends DrawableGameComponent implements IGameComponent {

	private LFont font;

	public InfoScreenBackLabel(MainGame game) {
		super(game);
		setDrawOrder(100);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		Utils.DrawStringAlignCenter(batch, this.font, LanguageResources
				.getBack().toUpperCase(), Constants.s(169f),
				Constants.infoScreenBackY(), LColor.white);
		super.draw(batch, gameTime);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.font = Constants.uiFont(16);
	}
}
