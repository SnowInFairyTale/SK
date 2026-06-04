package org.test;

import android.util.Log;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.timer.GameTime;

public class MonsterInfoScreenSpriteWithText extends Sprite {
	private static final String TAG = "InstructionsPerf";

	private LFont font;
	private final long createdAtNs;
	private boolean loggedFirstDraw;

	public MonsterInfoScreenSpriteWithText(MainGame game) {
		super(game, "assets/screen_monsters.png", 0, new Vector2f(0f, 0f));
		this.createdAtNs = System.nanoTime();
		game.Components().add(this);
		Log.d(TAG, "[Enemies] 背景 sprite 已加入 Components");
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
				Log.d(TAG, "[Enemies] 背景首次 draw: 距 sprite 创建 "
						+ sinceCreateMs + "ms, 纹理图素 " + w + "x" + h + " ("
						+ (w * h) + " px)");
			} else {
				Log.d(TAG, "[Enemies] 背景首次 draw: 距 sprite 创建 "
						+ sinceCreateMs + "ms, 纹理尚未加载");
			}
		}
		super.draw(batch, gameTime);
		int num = 0x18;
		for (String str : LanguageResources.getMonsterInfoPar1().split("[$]",
				-1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str, 400f, num,
					LColor.white);
			num += 20;
		}
		int num2 = 0x60;
		for (String str2 : LanguageResources.getMonsterInfoPar2().split("[$]",
				-1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str2, 4f, num2,
					LColor.white);
			num2 += 20;
		}
		int num3 = 0x9a;
		for (String str3 : LanguageResources.getMonsterInfoPar3().split("[$]",
				-1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str3, 400f, num3,
					LColor.white);
			num3 += 20;
		}
		int num4 = 0xe0;
		for (String str4 : LanguageResources.getMonsterInfoPar4().split("[$]",
				-1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str4, 4f, num4,
					LColor.white);
			num4 += 20;
		}
		int num5 = 290;
		for (String str5 : LanguageResources.getMonsterInfoPar5().split("[$]",
				-1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str5, 400f, num5,
					LColor.white);
			num5 += 20;
		}
		int num6 = 0x162;
		for (String str6 : LanguageResources.getMonsterInfoPar6().split("[$]",
				-1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str6, 4f, num6,
					LColor.white);
			num6 += 20;
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
			Log.d(TAG, "[Enemies] 背景纹理 assets/screen_monsters.png: "
					+ textureLoadMs + "ms, 图素 " + w + "x" + h + " ("
					+ (w * h) + " px)");
		} else {
			Log.d(TAG, "[Enemies] 背景纹理加载 " + textureLoadMs + "ms, 纹理为 null");
		}
		long t1 = System.nanoTime();
		this.font = Constants.uiFont(16);
		long fontLoadMs = (System.nanoTime() - t1) / 1_000_000L;
		long sinceCreateMs = (System.nanoTime() - this.createdAtNs) / 1_000_000L;
		Log.d(TAG, "[Enemies] 背景字体: " + fontLoadMs + "ms, 距 sprite 创建 "
				+ sinceCreateMs + "ms");
	}
}