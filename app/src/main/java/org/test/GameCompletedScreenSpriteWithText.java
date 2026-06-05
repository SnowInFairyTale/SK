package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.timer.GameTime;

public class GameCompletedScreenSpriteWithText extends Sprite {
	
	private LFont font;
	
	private LFont fontHeader;


	public GameCompletedScreenSpriteWithText(MainGame game) {
		super(game, "assets/win_screen_game_completed.png", 0, new Vector2f(0f, 0f));
		game.Components().add(this);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		super.draw(batch, gameTime);
		Utils.DrawStringAlignCenter(batch, this.fontHeader,
				LanguageResources.getGameCompletedHeader(), new Vector2f(328f, 40f),
				LColor.white);
		int num = 16;
		int num2 = 0x6e;
		for (String str : LanguageResources.getGameCompletedPar1().split("[$]", -1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str,
					num, num2, LColor.white);
			num2 += 40;
		}
		String text = LanguageResources.getMenu().toUpperCase();
		Utils.DrawStringAlignCenter(batch, this.font, text,
				320f, 870f, LColor.white);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.font = Constants.font(24);
		this.fontHeader = Constants.font(52);
	}
}