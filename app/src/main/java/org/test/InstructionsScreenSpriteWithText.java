package org.test;

import android.util.Log;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.timer.GameTime;

public class InstructionsScreenSpriteWithText extends Sprite {
	private static final String TAG = "InstructionsPerf";

	private LFont font;
	private LFont fontBig;
	private final long createdAtNs;
	private boolean loggedFirstDraw;

	public InstructionsScreenSpriteWithText(MainGame game) {
		super(game, "assets/screen_introduction.png", 0, new Vector2f(0f, 0f));
		this.createdAtNs = System.nanoTime();
		game.Components().add(this);
		Log.d(TAG, "sprite 已加入 Components");
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		if (!this.loggedFirstDraw) {
			this.loggedFirstDraw = true;
			long sinceCreateMs = (System.nanoTime() - this.createdAtNs) / 1_000_000L;
			LTexture tex = this.getTexture();
			if (tex != null) {
				int w = tex.getWidth();
				int h = tex.getHeight();
				Log.d(TAG, "首次 draw: 距 sprite 创建 " + sinceCreateMs
						+ "ms, 纹理图素 " + w + "x" + h + " (" + (w * h) + " px)");
			} else {
				Log.d(TAG, "首次 draw: 距 sprite 创建 " + sinceCreateMs
						+ "ms, 纹理尚未加载");
			}
		}
		super.draw(batch, gameTime);

		int num = Constants.s(6);
		int num2 = Constants.s(6);
		Utils.DrawStringAlignLeft(batch, this.fontBig,
				LanguageResources.getInstructionsHeader1(), num, num2,
				LColor.white);
		num2 += Constants.s(30);
		for (String str : LanguageResources.getInstructionsPar1().split("[$]",
				-1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str, num, num2,
					LColor.white);
			num2 += Constants.s(0x11);
		}
		num2 += Constants.s(6);
		Utils.DrawStringAlignLeft(batch, this.fontBig,
				LanguageResources.getInstructionsHeader2(), num, num2,
				LColor.white);
		num2 += Constants.s(30);
		for (String str2 : LanguageResources.getInstructionsPar2().split("[$]",
				-1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str2, num, num2,
					LColor.white);
			num2 += Constants.s(0x11);
		}
		num2 += Constants.s(6);
		Utils.DrawStringAlignLeft(batch, this.fontBig,
				LanguageResources.getInstructionsHeader3(), num, num2,
				LColor.white);
		num2 += Constants.s(30);
		for (String str3 : LanguageResources.getInstructionsPar3().split("[$]",
				-1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str3, num, num2,
					LColor.white);
			num2 += Constants.s(0x11);
		}
		Utils.DrawStringAlignCenter(batch, this.font, LanguageResources
				.getTowers().toUpperCase(), Constants.s(62f),
				Constants.s(439f), LColor.white);
		Utils.DrawStringAlignCenter(batch, this.font, LanguageResources
				.getEnemies().toUpperCase(), Constants.s(179f),
				Constants.s(439f), LColor.white);
		Utils.DrawStringAlignCenter(batch, this.font, LanguageResources
				.getMenu().toUpperCase(), Constants.s(278f), Constants.s(439f),
				LColor.white);
	}

	@Override
	protected void loadContent() {
		long t0 = System.nanoTime();
		super.loadContent();
		long textureLoadMs = (System.nanoTime() - t0) / 1_000_000L;
		LTexture tex = this.getTexture();
		if (tex != null) {
			int w = tex.getWidth();
			int h = tex.getHeight();
			Log.d(TAG, "loadContent 纹理 assets/screen_introduction.png: "
					+ textureLoadMs + "ms, 图素 " + w + "x" + h + " ("
					+ (w * h) + " px)");
		} else {
			Log.d(TAG, "loadContent 纹理加载 " + textureLoadMs + "ms, 纹理为 null");
		}
		long t1 = System.nanoTime();
        this.font = Constants.uiFont(16);
        this.fontBig = Constants.uiFont(20);
		long fontLoadMs = (System.nanoTime() - t1) / 1_000_000L;
		long sinceCreateMs = (System.nanoTime() - this.createdAtNs) / 1_000_000L;
		Log.d(TAG, "loadContent 字体: " + fontLoadMs + "ms, 距 sprite 创建 "
				+ sinceCreateMs + "ms");
	}
}
