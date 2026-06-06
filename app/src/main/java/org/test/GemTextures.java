package org.test;

import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;

/** Shared bright/dim gem icons — one GPU copy for map towers and toolbar. */
public final class GemTextures {

	public static final float DIM_BRIGHTNESS = 0.55f;

	private static LTexture gold;
	private static LTexture goldDim;
	private static LTexture purple;
	private static LTexture purpleDim;
	private static LTexture red;
	private static LTexture redDim;

	private GemTextures() {
	}

	public static void ensureLoaded() {
		if (purple != null) {
			return;
		}
		purple = LTextures.loadTexture(GemType.Purple.getTextureFile());
		purpleDim = TextureRecolor.toGreyscaleDim(purple, DIM_BRIGHTNESS);
		red = LTextures.loadTexture(GemType.Red.getTextureFile());
		redDim = TextureRecolor.toGreyscaleDim(red, DIM_BRIGHTNESS);
		gold = LTextures.loadTexture(GemType.Gold.getTextureFile());
		goldDim = TextureRecolor.toGreyscaleDim(gold, DIM_BRIGHTNESS);
	}

	public static LTexture get(GemType type) {
		ensureLoaded();
		switch (type) {
		case Purple:
			return purple;
		case Red:
			return red;
		case Gold:
			return gold;
		default:
			return null;
		}
	}

	public static LTexture getDim(GemType type) {
		ensureLoaded();
		switch (type) {
		case Purple:
			return purpleDim;
		case Red:
			return redDim;
		case Gold:
			return goldDim;
		default:
			return null;
		}
	}
}
