package org.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.loon.action.sprite.SpriteBatch;
import com.loon.core.geom.Vector2f;
import com.loon.core.graphics.LColor;
import com.loon.core.graphics.LFont;
import com.loon.core.timer.GameTime;

public class SpriteWithText extends Sprite {
	private LFont font;
	private MainGame game;
	private int showMilliseconds;
	private HashMap<Vector2f, String> textAndRelativePosition;
	private double timeLeft;

	public SpriteWithText(MainGame game, String textureFile, int showMilliseconds,
			Vector2f drawPosition,
			HashMap<Vector2f, String> textAndRelativePosition, LFont font) {
		super(game, textureFile, showMilliseconds, drawPosition);
		this.game = game;
		this.showMilliseconds = showMilliseconds;
		this.timeLeft = showMilliseconds;
		this.textAndRelativePosition = new HashMap<Vector2f, String>();
		for (Entry<Vector2f, String> entry : textAndRelativePosition.entrySet()) {
			this.textAndRelativePosition.put(entry.getKey().cpy(),
					entry.getValue());
		}
		this.font = font;
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		batch.draw(super.getTexture(), this.drawPosition, LColor.white);
		Set<Entry<Vector2f, String>> result = textAndRelativePosition.entrySet();
		for (Iterator<Entry<Vector2f, String>> it = result.iterator(); it
				.hasNext();) {
			Entry<Vector2f, String> pair = it.next();
			batch.drawString(this.font, pair.getValue(),
					this.drawPosition.add(pair.getKey()), LColor.white);
		}

	}

	@Override
	public void update(GameTime gameTime) {
		super.update(gameTime);
		if (this.showMilliseconds > 0) {
			this.timeLeft -= gameTime.getMilliseconds();
			if (this.timeLeft < 0.0) {
				this.game.Components().remove(this);
			}
		}
	}
}
