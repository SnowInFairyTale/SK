package org.test;

import android.util.Log;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.timer.GameTime;

public class TowerInfoScreenSpriteWithText extends Sprite {
	private static final String TAG = "InstructionsPerf";

	private LFont font;
	private final long createdAtNs;
	private boolean loggedFirstDraw;

	public TowerInfoScreenSpriteWithText(MainGame game) {
		super(game, "assets/towers_2.png", 0, new Vector2f(0f, 0f));
		this.createdAtNs = System.nanoTime();
		game.Components().add(this);
		Log.d(TAG, "[Towers] 背景 sprite 已加入 Components");
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		if (!this.loggedFirstDraw) {
			this.loggedFirstDraw = true;
			long sinceCreateMs = (System.nanoTime() - this.createdAtNs)
					/ 1_000_000L;
			LTexture tex = this.getTexture();
			if (tex != null) {
				int w = tex.getWidth();
				int h = tex.getHeight();
				Log.d(TAG, "[Towers] 背景首次 draw: 距 sprite 创建 "
						+ sinceCreateMs + "ms, 纹理图素 " + w + "x" + h + " ("
						+ (w * h) + " px)");
			} else {
				Log.d(TAG, "[Towers] 背景首次 draw: 距 sprite 创建 "
						+ sinceCreateMs + "ms, 纹理尚未加载");
			}
		}
		super.draw(batch, gameTime);
		int num = 0x1a;
		int num2 = 0x10;
		for (String str : LanguageResources.getTowerInfoPar1().split("[$]", -1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str, 99f, num,
					LColor.white);
			num += num2;
		}
		int num3 = 0x80;
		for (String str2 : LanguageResources.getTowerInfoPar2()
				.split("[$]", -1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str2, 99f, num3,
					LColor.white);
			num3 += num2;
		}
		int num4 = 0xe2;
		for (String str3 : LanguageResources.getTowerInfoPar3()
				.split("[$]", -1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str3, 99f, num4,
					LColor.white);
			num4 += num2;
		}
		int num5 = 0x146;
		for (String str4 : LanguageResources.getTowerInfoPar4()
				.split("[$]", -1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str4, 99f, num5,
					LColor.white);
			num5 += num2;
		}
		Utils.DrawStringAlignCenter(batch, this.font, LanguageResources
				.getBack().toUpperCase(), 169f, 1740f, LColor.white);
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
			Log.d(TAG, "[Towers] 背景纹理 assets/towers_2.png: "
					+ textureLoadMs + "ms, 图素 " + w + "x" + h + " ("
					+ (w * h) + " px)");
		} else {
			Log.d(TAG, "[Towers] 背景纹理加载 " + textureLoadMs + "ms, 纹理为 null");
		}
		long t1 = System.nanoTime();
		this.font = Constants.uiFont(12);
		long fontLoadMs = (System.nanoTime() - t1) / 1_000_000L;
		long sinceCreateMs = (System.nanoTime() - this.createdAtNs) / 1_000_000L;
		Log.d(TAG, "[Towers] 背景字体: " + fontLoadMs + "ms, 距 sprite 创建 "
				+ sinceCreateMs + "ms");
	}
}