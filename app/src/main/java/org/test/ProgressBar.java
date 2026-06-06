package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableGameComponent;
import loon.action.sprite.painting.IGameComponent;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.timer.GameTime;

public class ProgressBar extends DrawableGameComponent implements
		IGameComponent {

	private LColor backColor;
	private LColor frontColor;
	private LColor frontColorLow;
	private MainGame game;
	private float lowColorLimit;
	private boolean obeyGameOpacity = true;
	private int width;

	public ProgressBar(MainGame game, int width, boolean isHealthBarMode) {
		super(game);
		this.game = game;
		this.setIsHealthBarMode(isHealthBarMode);
		this.setCurrentPercent(100);
		this.width = width;
		this.setHeight(8);
		this.setDrawBorder(isHealthBarMode);
		super.setDrawOrder(30);
		if (isHealthBarMode) {
			this.frontColor = new LColor(0f, 1f, 0f, 1f);
			this.frontColorLow = LColor.red;
			this.backColor = LColor.black;
			this.lowColorLimit = 0.4f;
		} else {
			this.frontColor = new LColor(1f, 0.82f, 0.15f, 1f);
			this.frontColorLow = this.frontColor;
			this.backColor = LColor.black;
			this.lowColorLimit = 0f;
		}
	}

	private LColor resolveTintColor(LColor base) {
		LColor color = new LColor(base);
		if (!this.obeyGameOpacity || this.game.getGameplayScreen() == null) {
			return color;
		}
		LColor gameOpacity = this.game.getGameplayScreen().getGameOpacity();
		if (!gameOpacity.equals(LColor.white)) {
			float dim = (gameOpacity.r + gameOpacity.g + gameOpacity.b) / 3f;
			color.r *= dim;
			color.g *= dim;
			color.b *= dim;
		}
		if (gameOpacity.equals(this.game.getGameplayScreen()
				.getGameOpacityWhenPaused())) {
			color.mul(0.3f);
		}
		return color;
	}

	/** Slight rounding (1–2px), not a full pill. */
	private int cornerRadius(float h) {
		return Math.max(1, Math.min(2, Math.round(h / 4f)));
	}

	private void fillBar(SpriteBatch batch, float x, float y, float w,
			float h, LColor baseColor) {
		if (w <= 0f || h <= 0f) {
			return;
		}
		batch.setColor(this.resolveTintColor(baseColor));
		batch.fillRoundRect(x, y, w, h, this.cornerRadius(h));
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		super.draw(batch, gameTime);
		float barX = this.getPosition().x;
		float barY = this.getPosition().y;
		float barH = this.getHeight();
		if (this.getDrawBorder()) {
			float border = this.getIsHealthBarMode() ? 2f : 1f;
			LColor borderColor = this.getIsHealthBarMode() ? LColor.black
					: new LColor(0.2f, 0.2f, 0.2f, 1f);
			this.fillBar(batch, barX - border, barY - border, this.width
					+ border * 2f, barH + border * 2f, borderColor);
		}
		this.fillBar(batch, barX, barY, this.width, barH, this.backColor);
		float num = ((float) this.getCurrentPercent()) / 100f;
		if (num > 0f) {
			LColor color = (num < this.lowColorLimit) ? this.frontColorLow
					: this.frontColor;
			float fillW = (float) Math.ceil(this.width * num);
			float minFillW = this.cornerRadius(barH) * 2f;
			fillW = Math.max(minFillW, fillW);
			fillW = Math.min(this.width, fillW);
			this.fillBar(batch, barX, barY, fillW, barH, color);
		}
		batch.resetColor();
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

	public final boolean getObeyGameOpacity() {
		return this.obeyGameOpacity;
	}

	public final void setObeyGameOpacity(boolean value) {
		this.obeyGameOpacity = value;
	}

	private Vector2f privatePosition;

	public final Vector2f getPosition() {
		return privatePosition;
	}

	public final void setPosition(Vector2f value) {
		privatePosition = value.cpy();
	}
}
