package org.test;

import com.loon.action.sprite.SpriteBatch;
import com.loon.core.geom.Vector2f;
import com.loon.core.graphics.LColor;
import com.loon.core.graphics.opengl.LTexture;
import com.loon.core.graphics.opengl.LTextures;
import com.loon.core.timer.GameTime;

public class LoseScreen extends MenuScreen {
	private static final String MENU_LABEL = LanguageResources.getMenu()
			.toUpperCase();

	private java.util.ArrayList<TextSprite> bodyLines;
	private TextSprite headerLabel;
	private MainGame game;
	private TextSprite menuLabel;
	private LTexture texture;

	public LoseScreen(MainGame game, ScreenType prevScreen) {
		super("", game, prevScreen);
		this.game = game;
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
			batch.draw(this.texture, 0f, 0f, LColor.white);
			this.drawLabels(batch);
		}
		super.draw(batch, gameTime);
	}

	private void drawLabels(SpriteBatch batch) {
		if (this.headerLabel != null) {
			this.headerLabel.drawCentered(batch, 328f, 102f);
		}
		float y = 0xca;
		for (int i = 0; i < this.bodyLines.size(); i++) {
			this.bodyLines.get(i).drawLeft(batch, 0x46, y);
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
		this.headerLabel = TextSprite.create(Constants.font(76), LanguageResources
				.getLoseHeader().toUpperCase());
		this.bodyLines = new java.util.ArrayList<TextSprite>();
		for (String line : LanguageResources.getLosePar1().split("[$]", -1)) {
			TextSprite sprite = TextSprite.create(Constants.font(32), line);
			if (sprite != null) {
				this.bodyLines.add(sprite);
			}
		}
		this.menuLabel = TextSprite.create(Constants.font(32), MENU_LABEL);
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
				new MainMenuScreen(this.game, ScreenType.LoseScreen));
	}

	@Override
	public void LoadContent() {
		if (this.texture != null) {
			return;
		}
		this.texture = LTextures.loadTexture("assets/lose.png");
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
