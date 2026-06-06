package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.timer.GameTime;

public class WinScreen extends MenuScreen {
	private static final String MENU_LABEL = LanguageResources.getMenu()
			.toUpperCase();

	private java.util.ArrayList<TextSprite> bodyLines;
	private TextSprite headerLabel;
	private MainGame game;
	private TextSprite menuLabel;
	private LTexture texture;

	public WinScreen(MainGame game, ScreenType prevScreen) {
		super("", game, prevScreen);
		this.game = game;
		super.setTransitionOnTime(2f);
		super.setTransitionOffTime(0.5f);

		MenuEntry item = new MenuEntry("");
		item.setuseButtonBackground(false);
		item.setPosition(new Vector2f(192f, 792f));
		item.setnoButtonBackgroundSize(new Vector2f(280f, 100f));
		item.Selected = new GameEvent() {
			@Override
			public void invoke(MenuEntry comp) {
				HandleButtonSelected();
			}
		};
		super.getMenuEntries().add(item);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		if (this.texture == null) {
			this.LoadContent();
		}
		if (this.texture != null) {
			batch.draw(this.texture, new Vector2f(0f, 0f), LColor.white);
			this.drawLabels(batch);
		}
		super.draw(batch, gameTime);
	}

	private void drawLabels(SpriteBatch batch) {
		if (this.headerLabel != null) {
			this.headerLabel.drawCentered(batch, 328f, 102f);
		}
		float y = 0xa6;
		for (int i = 0; i < this.bodyLines.size(); i++) {
			this.bodyLines.get(i).drawLeft(batch, 68f, y);
			y += 40f;
		}
		if (this.menuLabel != null) {
			this.menuLabel.drawCenteredInButton(batch,
					Constants.RESULT_MENU_BTN_CENTER_X,
					Constants.RESULT_MENU_BTN_TOP_Y,
					Constants.RESULT_MENU_BTN_HEIGHT,
					Constants.RESULT_MENU_BTN_LABEL_OFFSET_Y);
		}
	}

	private void buildLabels() {
		this.disposeLabels();
		LFont bodyFont = Constants.font(32);
		LFont headerFont = Constants.font(52);
		this.headerLabel = TextSprite.create(headerFont,
				LanguageResources.getWinHeader());
		this.bodyLines = splitLines(bodyFont, LanguageResources.getWinPar1());
		this.menuLabel = TextSprite.create(bodyFont, MENU_LABEL);
	}

	private static java.util.ArrayList<TextSprite> splitLines(LFont font,
			String text) {
		java.util.ArrayList<TextSprite> lines = new java.util.ArrayList<TextSprite>();
		for (String line : text.split("[$]", -1)) {
			TextSprite sprite = TextSprite.create(font, line);
			if (sprite != null) {
				lines.add(sprite);
			}
		}
		return lines;
	}

	private void disposeLabels() {
		if (this.headerLabel != null) {
			this.headerLabel.dispose();
			this.headerLabel = null;
		}
		if (this.bodyLines != null) {
			for (int i = 0; i < this.bodyLines.size(); i++) {
				this.bodyLines.get(i).dispose();
			}
			this.bodyLines = null;
		}
		if (this.menuLabel != null) {
			this.menuLabel.dispose();
			this.menuLabel = null;
		}
	}

	private void HandleButtonSelected() {
		super.getScreenManager().ExitAllScreens();
		super.getScreenManager().AddScreen(
				new MainMenuScreen(this.game, ScreenType.WinScreen));
	}

	@Override
	public void LoadContent() {
		if (this.texture != null) {
			return;
		}
		this.texture = LTextures.loadTexture("assets/win.png");
		this.buildLabels();
		super.LoadContent();
	}

	@Override
	public void UnloadContent() {
		this.disposeLabels();
		this.texture = null;
		super.UnloadContent();
	}

	@Override
	protected void OnCancel() {
		this.HandleButtonSelected();
	}
}
