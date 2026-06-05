package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableGameComponent;
import loon.action.sprite.painting.IGameComponent;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.timer.GameTime;

/** Menu button caption drawn above animated screen overlays. */
public class ScreenButtonLabel extends DrawableGameComponent implements
		IGameComponent {

	private final float buttonHeight;
	private final float buttonY;
	private LFont font;
	private final int fontSize;
	private String text;
	private final float x;

	public ScreenButtonLabel(MainGame game, String text, float centerX,
			float buttonY, float buttonHeight, int fontSize) {
		super(game);
		this.text = text;
		this.x = centerX;
		this.buttonY = buttonY;
		this.buttonHeight = buttonHeight;
		this.fontSize = fontSize;
		super.setDrawOrder(200);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		Utils.DrawButtonLabel(batch, this.font, this.text, this.x, this.buttonY,
				this.buttonHeight, LColor.white);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.font = Constants.font(this.fontSize);
	}
}
