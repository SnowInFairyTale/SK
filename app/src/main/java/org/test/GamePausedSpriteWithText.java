package org.test;

import com.loon.action.sprite.SpriteBatch;
import com.loon.core.geom.Vector2f;
import com.loon.core.graphics.LColor;
import com.loon.core.graphics.LFont;
import com.loon.core.timer.GameTime;

public class GamePausedSpriteWithText extends Sprite
{
	private LFont font;

	public GamePausedSpriteWithText(MainGame game)
	{
		super(game, "assets/ingame_menu.png", 0, new Vector2f(0f, 0f));
	}

	@Override
	public void draw(SpriteBatch batch,GameTime gameTime)
	{
		super.draw(batch,gameTime);
		Utils.DrawStringAlignCenter(batch, this.font, LanguageResources.getResume().toUpperCase(), 320f, 520f, LColor.white);
		Utils.DrawStringAlignCenter(batch, this.font, LanguageResources.getRestart().toUpperCase(), 320f, 640f, LColor.white);
		Utils.DrawStringAlignCenter(batch, this.font, LanguageResources.getMainMenu().toUpperCase(), 320f, 760f, LColor.white);
	}

	@Override
	protected void loadContent()
	{
		super.loadContent();
		this.font = Constants.font(24);
	}
}