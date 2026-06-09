package org.test;

import com.loon.core.graphics.LColor;
import com.loon.core.graphics.LImage;
import com.loon.core.graphics.opengl.LTexture;
import com.loon.core.graphics.opengl.LTextures;

/**
 * Inverted recolor: non-black pixels → target color; semi-transparent black halo
 * stays black. Opaque black frame padding is cleared (spear.png uses solid black
 * where alpha should be empty).
 */
public final class TextureRecolor {

	/** RGB channels at or below this count as black. */
	private static final int BLACK_CHANNEL_MAX = 32;
	/** Below this alpha (0–255) the pixel is forced transparent. */
	private static final int ALPHA_TRANSPARENT_MAX = 8;
	/** Opaque black in spear art is padding — clear, not a visible halo. */
	private static final int OPAQUE_BLACK_ALPHA_MIN = 250;

	private static final java.util.HashMap<String, LTexture> cache = new java.util.HashMap<String, LTexture>();

	private TextureRecolor() {
	}

	/**
	 * Dim greyscale variant for disabled UI icons. Clears true transparency and
	 * opaque black sheet padding (RGB≈0, alpha high) so backgrounds stay empty.
	 */
	public static LTexture toGreyscaleDim(LTexture source, float brightness) {
		LImage image = source.getImage();
		int width = image.getWidth();
		int height = image.getHeight();
		int[] pixels = new int[width * height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				LColor pixel = image.getColorAt(x, y);
				int a = (int) (pixel.a * 255f + 0.5f);

				if (a <= ALPHA_TRANSPARENT_MAX) {
					pixels[(y * width) + x] = 0;
				} else if (isBlackRgb(pixel)) {
					if (a >= OPAQUE_BLACK_ALPHA_MIN) {
						pixels[(y * width) + x] = 0;
					} else {
						pixels[(y * width) + x] = toArgb(pixel.r, pixel.g,
								pixel.b, pixel.a);
					}
				} else {
					float lum = (0.299f * pixel.r) + (0.587f * pixel.g)
							+ (0.114f * pixel.b);
					float v = lum * brightness;
					pixels[(y * width) + x] = toArgb(v, v, v, pixel.a);
				}
			}
		}

		return new LTexture(LImage.createRGBImage(pixels, width, height, true));
	}

	public static LTexture toGreyscaleDim(String textureFile, float brightness) {
		String key = "gs2:" + textureFile + "#" + brightness;
		LTexture cached = cache.get(key);
		if (cached != null) {
			return cached;
		}
		LTexture dimmed = toGreyscaleDim(LTextures.loadTexture(textureFile),
				brightness);
		cache.put(key, dimmed);
		return dimmed;
	}

	public static LTexture recolorBlackTo(String textureFile, LColor target) {
		String key = "v5:" + textureFile + "#"
				+ toArgb(target.r, target.g, target.b, 1f);
		LTexture cached = cache.get(key);
		if (cached != null) {
			return cached;
		}
		LTexture recolored = recolorBlackTo(LTextures.loadTexture(textureFile),
				target);
		cache.put(key, recolored);
		return recolored;
	}

	private static boolean isBlackRgb(LColor color) {
		int r = (int) (color.r * 255f);
		int g = (int) (color.g * 255f);
		int b = (int) (color.b * 255f);
		return r <= BLACK_CHANNEL_MAX && g <= BLACK_CHANNEL_MAX
				&& b <= BLACK_CHANNEL_MAX;
	}

	public static LTexture recolorBlackTo(LTexture source, LColor target) {
		LImage image = source.getImage();
		int width = image.getWidth();
		int height = image.getHeight();
		int[] pixels = new int[width * height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				LColor pixel = image.getColorAt(x, y);
				int a = (int) (pixel.a * 255f + 0.5f);

				if (a <= ALPHA_TRANSPARENT_MAX) {
					pixels[(y * width) + x] = 0;
				} else if (isBlackRgb(pixel)) {
					if (a >= OPAQUE_BLACK_ALPHA_MIN) {
						pixels[(y * width) + x] = 0;
					} else {
						pixels[(y * width) + x] = toArgb(pixel.r, pixel.g,
								pixel.b, pixel.a);
					}
				} else {
					pixels[(y * width) + x] = toArgb(target.r, target.g,
							target.b, pixel.a);
				}
			}
		}

		return new LTexture(LImage.createRGBImage(pixels, width, height, true));
	}

	private static int toArgb(float r, float g, float b, float a) {
		int ai = ((int) (a * 255f)) & 0xFF;
		int ri = ((int) (r * 255f)) & 0xFF;
		int gi = ((int) (g * 255f)) & 0xFF;
		int bi = ((int) (b * 255f)) & 0xFF;
		return (ai << 24) | (ri << 16) | (gi << 8) | bi;
	}
}
