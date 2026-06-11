package org.test;

import com.loon.action.sprite.SpriteBatch;
import com.loon.core.graphics.LColor;
import com.loon.core.graphics.LFont;
import com.loon.core.graphics.LImage;
import com.loon.core.graphics.opengl.LSTRFont;
import com.loon.core.graphics.opengl.LTexture;

/** Rasterized label; avoids jar {@code drawString} / LSTRDictionary cache misses. */
final class TextSprite {

	private final float buttonLabelTopOffset;
	private final LTexture texture;
	private final float width;
	private final float height;

	private TextSprite(LTexture texture, float width, float height,
			float buttonLabelTopOffset) {
		this.texture = texture;
		this.width = width;
		this.height = height;
		this.buttonLabelTopOffset = buttonLabelTopOffset;
	}

	static TextSprite create(LFont font, String text) {
		if ((font == null) || (text == null) || (text.length() == 0)) {
			return null;
		}
		LImage image = LSTRFont.createFontImage(font, LColor.white, text);
		float imageHeight = image.getHeight();
		float buttonLabelTopOffset = Constants.textLabelButtonTopOffset(font,
				imageHeight);
		return new TextSprite(image.getTexture(), image.getWidth(), imageHeight,
				buttonLabelTopOffset);
	}

	void drawCentered(SpriteBatch batch, float centerX, float topY) {
		batch.draw(this.texture, centerX - (this.width / 2f), topY, this.width,
				this.height);
	}

	void drawCentered(SpriteBatch batch, float centerX, float topY, LColor color) {
		batch.draw(this.texture, centerX - (this.width / 2f), topY, this.width,
				this.height, 0f, color);
	}

	void drawCenteredInButton(SpriteBatch batch, float centerX,
			float buttonTopY, float buttonHeight) {
		this.drawCenteredInButton(batch, centerX, buttonTopY, buttonHeight, 0f);
	}

	void drawCenteredInButton(SpriteBatch batch, float centerX,
			float buttonTopY, float buttonHeight, float extraTopOffsetY) {
		this.drawCentered(batch, centerX, buttonTopY
				+ ((buttonHeight - this.height) / 2f) + this.buttonLabelTopOffset
				+ extraTopOffsetY);
	}

	void drawLeft(SpriteBatch batch, float x, float topY) {
		batch.draw(this.texture, x, topY, this.width, this.height);
	}

	void drawLeft(SpriteBatch batch, float x, float topY, LColor color) {
		batch.draw(this.texture, x, topY, this.width, this.height, 0f, color);
	}

	void dispose() {
		if (this.texture != null) {
			this.texture.dispose();
		}
	}
}
