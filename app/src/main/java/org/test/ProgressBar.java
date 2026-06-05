package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableGameComponent;
import loon.action.sprite.painting.IGameComponent;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.timer.GameTime;

public class ProgressBar extends DrawableGameComponent implements
		IGameComponent {
	/** healthBar.png is 40×12: rows 0–7 border, rows 8–11 fill. */
	private static final int FILL_SRC_Y = 8;
	private static final int FILL_SRC_H = 4;
	private static final int BORDER_SRC_H = 8;

	private LColor backColor;
	private LColor frontColor;
	private LColor frontColorLow;
	private MainGame game;
	private float lowColorLimit;
	private LTexture texture;
	private int width;

	public ProgressBar(MainGame game, int width, boolean isHealthBarMode) {
		super(game);
		this.game = game;
		this.setCurrentPercent(100);
		this.width = width;
		this.setHeight(8);
		this.setDrawBorder(false);
		super.setDrawOrder(30);
		if (isHealthBarMode) {
			this.frontColor = new LColor(0f, 1f, 0f, 1f);
			this.frontColorLow = LColor.red;
			this.backColor = LColor.black;
			this.lowColorLimit = 0.4f;
		} else {
			this.frontColor = LColor.white;
			this.frontColorLow = LColor.white;
			this.backColor = LColor.black;
			this.lowColorLimit = 0f;
		}
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		super.draw(batch, gameTime);
		batch.resetColor();
		float barX = this.getPosition().x;
		int barY = (int) this.getPosition().y;
		int barH = this.getHeight();
		int texW = this.texture.getWidth();
		if (this.getCurrentPercent() < 100) {
			batch.draw(this.texture, barX, barY, this.width, barH, 0,
					FILL_SRC_Y, texW, FILL_SRC_H, this.backColor);
		}
		float num = ((float) this.getCurrentPercent()) / 100f;
		if (num > 0f) {
			LColor color = (num < this.lowColorLimit) ? this.frontColorLow
					: this.frontColor;
			if (this.game
					.getGameplayScreen()
					.getGameOpacity()
					.equals(this.game.getGameplayScreen()
							.getGameOpacityWhenPaused())) {
				LColor col = new LColor(color);
				col.mul(0.3f);
				color = col;
			}
			int fillW = (int) Math.ceil(this.width * num);
			batch.draw(this.texture, barX, barY, fillW, barH, 0, FILL_SRC_Y,
					texW, FILL_SRC_H, color);
		}
		if (this.getDrawBorder()) {
			batch.draw(this.texture, barX, barY, this.width, barH, 0, 0, texW,
					BORDER_SRC_H, LColor.white);
		}
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.texture = LTextures.loadTexture("assets/healthBar.png");
	}

	private int privateCurrentPercent;

	public final int getCurrentPercent() {
		return privateCurrentPercent;
	}

	public final void setCurrentPercent(int value) {
		privateCurrentPercent = value;
	}

	private boolean privateDrawBorder;

	public final boolean getDrawBorder() {
		return privateDrawBorder;
	}

	public final void setDrawBorder(boolean value) {
		privateDrawBorder = value;
	}

	private int privateHeight;

	public final int getHeight() {
		return privateHeight;
	}

	public final void setHeight(int value) {
		privateHeight = value;
	}

	private boolean privateIsHealthBarMode;

	public final boolean getIsHealthBarMode() {
		return privateIsHealthBarMode;
	}

	public final void setIsHealthBarMode(boolean value) {
		privateIsHealthBarMode = value;
	}

	private Vector2f privatePosition;

	public final Vector2f getPosition() {
		return privatePosition;
	}

	public final void setPosition(Vector2f value) {
		privatePosition = value.cpy();
	}
}