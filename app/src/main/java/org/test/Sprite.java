package org.test;

import com.loon.action.sprite.SpriteBatch;
import com.loon.action.sprite.painting.DrawableGameComponent;
import com.loon.action.sprite.painting.IGameComponent;
import com.loon.core.geom.Vector2f;
import com.loon.core.graphics.LColor;
import com.loon.core.graphics.opengl.LTexture;
import com.loon.core.graphics.opengl.LTextures;
import com.loon.core.timer.GameTime;

public class Sprite extends DrawableGameComponent implements IGameComponent {

	protected Vector2f drawPosition = new Vector2f();
	private MainGame game;
	private int showMilliseconds;
	private String textureFile;
	private double timeLeft;

	public Sprite(MainGame game, String textureFile, int showMilliseconds,
			Vector2f drawPosition) {
		super(game);
		this.game = game;
		this.showMilliseconds = showMilliseconds;
		this.timeLeft = showMilliseconds;
		this.textureFile = textureFile;
		this.drawPosition = drawPosition.cpy();
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		batch.draw(this.getTexture(), this.drawPosition, LColor.white);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.setTexture(LTextures.loadTexture(this.textureFile));
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

	private LTexture privateTexture;

	public final LTexture getTexture() {
		return privateTexture;
	}

	public final void setTexture(LTexture value) {
		privateTexture = value;
	}
}