package org.test;

import java.util.HashMap;

import com.loon.action.sprite.SpriteBatch;
import com.loon.core.graphics.LColor;
import com.loon.core.graphics.LFont;
import com.loon.core.graphics.LImage;
import com.loon.core.graphics.opengl.LSTRFont;
import com.loon.core.graphics.opengl.LTexture;

/** Rasterized label; avoids jar {@code drawString} / LSTRDictionary cache misses. */
final class TextSprite {

	private static final int MAX_CACHE_ENTRIES = 256;
	private static final HashMap<String, SharedTextSprite> CACHE = new HashMap<String, SharedTextSprite>();
	private static int cacheUseSerial;

	private final SharedTextSprite shared;
	private boolean disposed;

	private static final class SharedTextSprite {
		final String key;
		final float buttonLabelTopOffset;
		final LTexture texture;
		final float width;
		final float height;
		int refs;
		int lastUsed;

		SharedTextSprite(String key, LTexture texture, float width, float height,
				float buttonLabelTopOffset) {
			this.key = key;
			this.texture = texture;
			this.width = width;
			this.height = height;
			this.buttonLabelTopOffset = buttonLabelTopOffset;
		}
	}

	private TextSprite(SharedTextSprite shared) {
		this.shared = shared;
	}

	static TextSprite create(LFont font, String text) {
		if ((font == null) || (text == null) || (text.length() == 0)) {
			return null;
		}
		String key = cacheKey(font, text);
		synchronized (CACHE) {
			SharedTextSprite cached = CACHE.get(key);
			if (cached != null) {
				cached.refs++;
				cached.lastUsed = ++cacheUseSerial;
				return new TextSprite(cached);
			}
		}
		LImage image = LSTRFont.createFontImage(font, LColor.white, text);
		float imageHeight = image.getHeight();
		float buttonLabelTopOffset = Constants.textLabelButtonTopOffset(font,
				imageHeight);
		SharedTextSprite shared = new SharedTextSprite(key, image.getTexture(),
				image.getWidth(), imageHeight, buttonLabelTopOffset);
		synchronized (CACHE) {
			SharedTextSprite cached = CACHE.get(key);
			if (cached != null) {
				cached.refs++;
				cached.lastUsed = ++cacheUseSerial;
				shared.texture.dispose();
				return new TextSprite(cached);
			}
			shared.refs = 1;
			shared.lastUsed = ++cacheUseSerial;
			CACHE.put(key, shared);
			trimCacheLocked();
		}
		return new TextSprite(shared);
	}

	private static String cacheKey(LFont font, String text) {
		return font.getFontName().toLowerCase() + "|" + font.getStyle() + "|"
				+ font.getSize() + "|" + text;
	}

	private static void trimCacheLocked() {
		while (CACHE.size() > MAX_CACHE_ENTRIES) {
			SharedTextSprite oldest = null;
			for (SharedTextSprite sprite : CACHE.values()) {
				if (sprite.refs == 0
						&& (oldest == null || sprite.lastUsed < oldest.lastUsed)) {
					oldest = sprite;
				}
			}
			if (oldest == null) {
				return;
			}
			CACHE.remove(oldest.key);
			oldest.texture.dispose();
		}
	}

	void drawCentered(SpriteBatch batch, float centerX, float topY) {
		batch.draw(this.shared.texture, centerX - (this.shared.width / 2f),
				topY, this.shared.width, this.shared.height);
	}

	void drawCentered(SpriteBatch batch, float centerX, float topY, LColor color) {
		batch.draw(this.shared.texture, centerX - (this.shared.width / 2f),
				topY, this.shared.width, this.shared.height, 0f, color);
	}

	void drawCenteredInButton(SpriteBatch batch, float centerX,
			float buttonTopY, float buttonHeight) {
		this.drawCenteredInButton(batch, centerX, buttonTopY, buttonHeight, 0f);
	}

	void drawCenteredInButton(SpriteBatch batch, float centerX,
			float buttonTopY, float buttonHeight, float extraTopOffsetY) {
		this.drawCentered(batch, centerX,
				buttonTopY + ((buttonHeight - this.shared.height) / 2f)
						+ this.shared.buttonLabelTopOffset + extraTopOffsetY);
	}

	void drawLeft(SpriteBatch batch, float x, float topY) {
		batch.draw(this.shared.texture, x, topY, this.shared.width,
				this.shared.height);
	}

	void drawLeft(SpriteBatch batch, float x, float topY, LColor color) {
		batch.draw(this.shared.texture, x, topY, this.shared.width,
				this.shared.height, 0f, color);
	}

	void dispose() {
		if (this.disposed) {
			return;
		}
		this.disposed = true;
		synchronized (CACHE) {
			if (this.shared.refs > 0) {
				this.shared.refs--;
			}
			this.shared.lastUsed = ++cacheUseSerial;
			trimCacheLocked();
		}
	}
}
