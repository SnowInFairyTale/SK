package org.test;

import com.loon.action.sprite.SpriteBatch;
import com.loon.core.geom.Vector2f;
import com.loon.core.graphics.LColor;
import com.loon.core.graphics.LFont;
import com.loon.core.timer.GameTime;

public class WinScreenSpriteWithText extends Sprite {

	private LFont font;
	private LFont fontHeader;

	public WinScreenSpriteWithText(MainGame game) {
		super(game, "assets/win.png", 0, new Vector2f(0f, 0f));
		game.Components().add(this);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		super.draw(batch, gameTime);
		Utils.DrawStringAlignCenter(batch, this.fontHeader,
				LanguageResources.getWinHeader(), 328f, 102f, LColor.white);
		int num = 0xa6;
		for (String str : LanguageResources.getWinPar1().split("[$]")) {
			Utils.DrawStringAlignLeft(batch, this.font, str, 68f,
					(float) num, LColor.white);
			num += 40;
		}
		String text = LanguageResources.getMenu().toUpperCase();
		Utils.DrawStringAlignCenter(batch, this.font, text, 320f, 800f,
				LColor.white);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.font = Constants.font(32);
		this.fontHeader = Constants.font(52);
	}
}