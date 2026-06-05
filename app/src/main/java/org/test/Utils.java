package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.utils.MathUtils;

public class Utils {
	public static Vector2f ConvertToGridPoint(Vector2f positionCoordinates) {
		return new Vector2f(
				((int) ((positionCoordinates.x - Constants.HorizontalGridOffset) / Constants.GridSize)) - 1,
				((int) ((positionCoordinates.y - Constants.VerticalGridOffset) / Constants.GridSize)) - 1);
	}

	public static Vector2f ConvertToPositionCoordinates(Vector2f gridPoint) {
		return new Vector2f(
				(float) ((gridPoint.x * Constants.GridSize) + Constants.HorizontalGridOffset),
				(float) ((gridPoint.y * Constants.GridSize) + Constants.VerticalGridOffset));
	}

	public static void DrawLevelText(SpriteBatch spriteBatch, LFont font,
			String text, Vector2f position) {
		DrawStringAlignCenter(spriteBatch, font, text, position, LColor.white);
	}

	private static Vector2f pos = new Vector2f();

	public static void DrawStringAlignCenter(SpriteBatch spriteBatch,
			LFont font, String text, float x, float y, LColor color) {
		pos.set(x - (font.stringWidth(text) / 2f), y);
		spriteBatch.drawString(font, text, pos, color);
	}

	/** Horizontally centered label vertically aligned within a button rect (screen pixels). */
	public static void DrawButtonLabel(SpriteBatch spriteBatch, LFont font,
			String text, float centerX, float buttonY, float buttonHeight,
			LColor color) {
		float centerY = buttonY + buttonHeight / 2f;
		float textY = centerY - font.getHeight() / 2f;
		DrawStringAlignCenter(spriteBatch, font, text, centerX, textY, color);
	}

	public static void drawMenuButtonLabel(SpriteBatch batch, LFont font,
			String text, float buttonTopY) {
		DrawButtonLabel(batch, font, text, Constants.MENU_BTN_CENTER_X,
				buttonTopY, Constants.MENU_BTN_HEIGHT, LColor.white);
	}

	public static void drawIntroButtonLabel(SpriteBatch batch, LFont font,
			String text, float centerX) {
		DrawButtonLabel(batch, font, text, centerX, Constants.INTRO_BTN_TOP_Y,
				Constants.INTRO_BTN_TEXT_HEIGHT, LColor.white);
	}

	/** Isolated text draw — avoids SpriteBatch state mixing with preview sprites. */
	public static void drawInfoBackButtonLabel(SpriteBatch batch, LFont font) {
		batch.flush();
		DrawButtonLabel(batch, font, LanguageResources.getBack().toUpperCase(),
				Constants.INFO_BACK_BTN_CENTER_X, Constants.INFO_BACK_BTN_TOP_Y,
				Constants.INFO_BACK_BTN_TEXT_HEIGHT, LColor.white);
		batch.resetColor();
		batch.flush();
	}

	public static void DrawStringAlignCenter(SpriteBatch spriteBatch,
			LFont font, String text, Vector2f position, LColor color) {
		spriteBatch.drawString(font, text,
				new Vector2f(position.x - (font.stringWidth(text) / 2f),
						position.y), color);
	}

	public static void DrawStringAlignCenter(SpriteBatch spriteBatch,
			LFont font, String text, Vector2f position, LColor color,
			float scale) {
		spriteBatch.drawString(font, text,
				new Vector2f(position.x - (font.stringWidth(text) / 2f),
						position.y), color, 0f, new Vector2f(0f, 0f), scale);
	}

	public static void DrawStringAlignLeft(SpriteBatch spriteBatch, LFont font,
			String text, float x, float y, LColor color) {
		pos.set(x, y);
		spriteBatch.drawString(font, text, pos, color);
	}

	public static void DrawStringAlignLeft(SpriteBatch spriteBatch, LFont font,
			String text, Vector2f position, LColor color) {
		spriteBatch.drawString(font, text,
				new Vector2f(position.x, position.y), color);
	}

	public static void DrawStringAlignRight(SpriteBatch spriteBatch,
			LFont font, String text, Vector2f position, LColor color) {
		spriteBatch.drawString(font, text,
				new Vector2f(position.x - font.stringWidth(text), position.y),
				color);
	}

	public static void DrawStringAlignRight(SpriteBatch spriteBatch,
			LFont font, String text, float x, float y, LColor color) {
		pos.set(x - font.stringWidth(text), y);
		spriteBatch.drawString(font, text, pos, color);
	}

	public static float GetAngle(Vector2f v1) {
		v1.nor();
		return (float) Math.atan2((double) v1.y, (double) v1.x);
	}

	public static Vector2f GetDirection(Vector2f v1, Vector2f v2) {
		Vector2f vector = v2.sub(v1);
		vector.normalize();
		return vector;
	}

	public static float GetDistance(Vector2f v1, Vector2f v2) {
		float num = v1.x - v2.x;
		float num2 = v1.y - v2.y;
		return (float) Math.sqrt((double) ((num * num) + (num2 * num2)));
	}

	public static int GetTextureOffsetY(float angleInRadians, int spriteHeight) {
		float num = MathUtils.wrapAngle(angleInRadians + 1.570796f);
		if ((num >= -Constants.PiOver8) && (num <= Constants.PiOver8)) {
			return 0;
		}
		if ((num >= -1.963495f) && (num <= -1.178097f)) {
			return (6 * spriteHeight);
		}
		if ((num <= -2.748894f) || (num >= 2.748894f)) {
			return (4 * spriteHeight);
		}
		if ((num >= 1.178097f) && (num <= 1.963495f)) {
			return (2 * spriteHeight);
		}
		if ((num >= -2.748894f) && (num <= -1.963495f)) {
			return (5 * spriteHeight);
		}
		if ((num >= Constants.PiOver8) && (num <= 1.178097f)) {
			return spriteHeight;
		}
		if ((num >= 1.963495f) && (num <= 2.748894f)) {
			return (3 * spriteHeight);
		}
		return (7 * spriteHeight);
	}

}
