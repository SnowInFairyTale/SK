package org.test;

import com.loon.action.sprite.SpriteBatch;
import com.loon.core.geom.Vector2f;
import com.loon.core.graphics.LColor;
import com.loon.core.graphics.LFont;
import com.loon.core.timer.GameTime;

public class LoseScreenSpriteWithText extends Sprite {
	private LFont font;
	private LFont fontHeader;

	public LoseScreenSpriteWithText(MainGame game) {
		super(game, "assets/lose.png", 0, new Vector2f(0f, 0f));
		game.Components().add(this);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		super.draw(batch, gameTime);
		Utils.DrawStringAlignCenter(batch, this.fontHeader, LanguageResources
				.getLoseHeader().toUpperCase(), 328f, 102f,
				LColor.white);
		int num = 0x46;
		int num2 = 0xca;
		for (String str : LanguageResources.getLosePar1().split("[$]", -1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str, 
					 num, num2, LColor.white);
			num2 += 40;
		}
		String text = LanguageResources.getMenu().toUpperCase();
		Utils.DrawStringAlignCenter(batch, this.font, text,320f,
				800f, LColor.white);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.font = Constants.font(32);
		this.fontHeader = Constants.font(76);
	}
}