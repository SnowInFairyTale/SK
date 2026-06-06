package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableGameComponent;
import loon.action.sprite.painting.IGameComponent;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.timer.GameTime;

public class GemDropInfo extends DrawableGameComponent implements IGameComponent {

	private int energy;
	private final GemType gemType;
	private final MainGame game;
	private final Vector2f position;
	private LTexture texture;

	public GemDropInfo(MainGame game, Vector2f position, GemType gemType) {
		super(game);
		this.game = game;
		this.position = position;
		this.gemType = gemType;
		this.energy = 20;
		super.setDrawOrder(40);
	}

	private final LColor color = new LColor();

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		float r = ((float) this.energy) / 20f;
		int rise = 20 - this.energy;
		Vector2f drawPos = this.position.cpy();
		drawPos.y -= rise;
		if (r > 0f) {
			color.setColor(r, r, r, r);
			batch.draw(this.texture, drawPos, color);
		}
		super.draw(batch, gameTime);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.texture = GemTextures.get(this.gemType);
	}

	@Override
	public void update(GameTime gameTime) {
		super.update(gameTime);
		if (this.energy-- < 0) {
			this.game.Components().remove(this);
		}
	}
}
