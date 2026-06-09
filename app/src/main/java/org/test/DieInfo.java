package org.test;

import com.loon.action.sprite.SpriteBatch;
import com.loon.action.sprite.painting.DrawableGameComponent;
import com.loon.action.sprite.painting.IGameComponent;
import com.loon.core.geom.Vector2f;
import com.loon.core.graphics.LColor;
import com.loon.core.graphics.LFont;
import com.loon.core.graphics.opengl.LTexture;
import com.loon.core.graphics.opengl.LTextures;
import com.loon.core.timer.GameTime;

public class DieInfo extends DrawableGameComponent implements IGameComponent {

	private int energy;
	private LFont font;
	private MainGame game;

	private Vector2f position;
	private LTexture texture;
	private int value;

	public DieInfo(MainGame game, Vector2f position, int value) {
		super(game);
		this.game = game;
		this.position = position;
		this.value = value;
		this.energy = 20;
		super.setDrawOrder(40);
	}

	private LColor color = new LColor();

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		float r = ((float) this.energy) / 20f;
		int num2 = 20 - this.energy;
		Vector2f position = this.position.cpy();
		position.y -= num2;
		if (r < 255 && r > 0) {
			color.setColor(r, r, r, r);
			batch.draw(this.texture, position, color);
			batch.drawString(this.font, "" + this.value, position.x + 16f,
					position.y + 8f, color);
		}
		super.draw(batch, gameTime);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.texture = LTextures.loadTexture("assets/icon_coin_tiny.png");
		this.font = Constants.font(24);
	}

	@Override
	public void update(GameTime gameTime) {
		super.update(gameTime);
		if (this.energy-- < 0) {
			this.game.Components().remove(this);
		}
	}
}