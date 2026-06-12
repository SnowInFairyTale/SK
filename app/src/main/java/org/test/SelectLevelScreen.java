package org.test;

import com.loon.action.sprite.SpriteBatch;
import com.loon.core.geom.Vector2f;
import com.loon.core.graphics.LColor;
import com.loon.core.graphics.LFont;
import com.loon.core.graphics.opengl.LTexture;
import com.loon.core.graphics.opengl.LTextures;
import com.loon.core.timer.GameTime;

public class SelectLevelScreen extends MenuScreen {
	private static final String TEXTURE_BACKGROUND = "assets/backgrounds/select_level_menu.png";
	private static final String TEXTURE_FLAG_GREEN = "assets/icon_flag_green.png";
	private static final String TEXTURE_FLAG_RED = "assets/icon_flag_red.png";
	private static final String TEXTURE_ICON_HEART = "assets/icon_heart.png";
	private static final float LEVEL_BUTTON_HIT_X = 80f;
	private static final float LEVEL_BUTTON_HIT_WIDTH = 460f;
	private static final float BACK_BUTTON_HIT_X = 172f;
	private static final float BACK_BUTTON_HIT_WIDTH = 296f;

	private Difficulty difficulty;
	private LFont font;
	private MainGame game;
	private int remainingLivesRecordLevel1;
	private int remainingLivesRecordLevel2;
	private int remainingLivesRecordLevel3;
	private LTexture texture;
	private LTexture textureFlagGreen;
	private LTexture textureFlagRed;
	private LTexture textureHeart;

	public SelectLevelScreen(MainGame game, ScreenType prevScreen,
			Difficulty difficulty) {
		super("", game, prevScreen);
		this.remainingLivesRecordLevel1 = -1;
		this.remainingLivesRecordLevel2 = -1;
		this.remainingLivesRecordLevel3 = -1;
		this.game = game;
		super.setScreenType(ScreenType.SelectLevelScreen);
		this.difficulty = difficulty;
		MenuEntry item = this.createHitEntry(LEVEL_BUTTON_HIT_X,
				Constants.MENU_BTN_ROW_1_Y, LEVEL_BUTTON_HIT_WIDTH);
		MenuEntry entry2 = this.createHitEntry(LEVEL_BUTTON_HIT_X,
				Constants.MENU_BTN_ROW_2_Y, LEVEL_BUTTON_HIT_WIDTH);
		MenuEntry entry3 = this.createHitEntry(LEVEL_BUTTON_HIT_X,
				Constants.MENU_BTN_ROW_3_Y, LEVEL_BUTTON_HIT_WIDTH);
		MenuEntry entry4 = this.createHitEntry(BACK_BUTTON_HIT_X,
				Constants.MENU_BTN_ROW_4_Y, BACK_BUTTON_HIT_WIDTH);
		super.getMenuEntries().add(item);
		super.getMenuEntries().add(entry2);
		super.getMenuEntries().add(entry3);
		super.getMenuEntries().add(entry4);
		for (CompletedLevel level : game.getCompletedLevels()) {
			if (level.getDifficulty() != difficulty.getValue()) {
				continue;
			}
			switch (level.getLevel()) {
			case 1:
				this.remainingLivesRecordLevel1 = level.getRemainingLives();
				break;
			case 2:
				this.remainingLivesRecordLevel2 = level.getRemainingLives();
				break;
			case 3:
				this.remainingLivesRecordLevel3 = level.getRemainingLives();
				break;
			default:
				break;
			}
		}

		item.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				StartLevel1MenuEntrySelected();

			}
		};

		entry2.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				StartLevel2MenuEntrySelected();

			}
		};

		entry3.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				StartLevel3MenuEntrySelected();

			}
		};

		entry4.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				StartMainMenuEntrySelected();

			}
		};
	}

	private MenuEntry createHitEntry(float x, float y, float width) {
		MenuEntry entry = new MenuEntry("");
		entry.setuseButtonBackground(false);
		entry.setPosition(new Vector2f(x, y));
		entry.setnoButtonBackgroundSize(new Vector2f(width,
				Constants.MENU_BTN_HEIGHT));
		return entry;
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		if (this.font == null) {
			this.LoadContent();
		}
		this.texture = this.resolveTexture(this.texture, TEXTURE_BACKGROUND);
		batch.flush();
		batch.draw(this.texture, 0f, 0f, LColor.white);

		Utils.drawMenuButtonLabel(batch, this.font,
				LanguageResources.getLevelTitle(this.difficulty, 1),
				Constants.MENU_BTN_ROW_1_Y);
		Utils.drawMenuButtonLabel(batch, this.font,
				LanguageResources.getLevelTitle(this.difficulty, 2),
				Constants.MENU_BTN_ROW_2_Y);
		Utils.drawMenuButtonLabel(batch, this.font,
				LanguageResources.getLevelTitle(this.difficulty, 3),
				Constants.MENU_BTN_ROW_3_Y);
		Utils.drawMenuButtonLabel(batch, this.font, LanguageResources
				.getMainMenu().toUpperCase(), Constants.MENU_BTN_ROW_4_Y);

		float flagY = Constants.MENU_BTN_HEIGHT / 2f - 15f;
		this.DrawBestRemainingLives(batch, this.remainingLivesRecordLevel1,
				488f, Constants.MENU_BTN_ROW_1_Y + flagY, this.font);
		this.DrawBestRemainingLives(batch, this.remainingLivesRecordLevel2,
				488f, Constants.MENU_BTN_ROW_2_Y + flagY, this.font);
		this.DrawBestRemainingLives(batch, this.remainingLivesRecordLevel3,
				488f, Constants.MENU_BTN_ROW_3_Y + flagY, this.font);
		super.draw(batch, gameTime);
	}

	private void DrawBestRemainingLives(SpriteBatch batch,
			int remainingLivesRecord, float x, float y, LFont font) {
		if (remainingLivesRecord >= 0) {
			this.textureFlagGreen = this.drawLevelIcon(batch,
					this.textureFlagGreen, TEXTURE_FLAG_GREEN, x, y);
			Utils.DrawStringAlignRight(batch, font, (remainingLivesRecord) + "/"
							+ Constants.InitialRemainingLives, new Vector2f(
					166f, y), LColor.white);
			this.textureHeart = this.drawLevelIcon(batch, this.textureHeart,
					TEXTURE_ICON_HEART, 168f, y + 6f);
		} else {
			this.textureFlagRed = this.drawLevelIcon(batch, this.textureFlagRed,
					TEXTURE_FLAG_RED, x, y);
		}
	}

	private LTexture resolveTexture(LTexture cached, String path) {
		if (cached == null || cached.isClose()) {
			return LTextures.loadTexture(path);
		}
		if (!cached.isLoaded()) {
			cached.loadTexture();
		}
		return cached;
	}

	private LTexture drawLevelIcon(SpriteBatch batch, LTexture cached,
			String path, float x, float y) {
		LTexture texture = this.resolveTexture(cached, path);
		batch.flush();
		batch.draw(texture, x, y, LColor.white);
		return texture;
	}

	@Override
	public void LoadContent() {
		if (this.font != null) {
			return;
		}
		this.texture = LTextures.loadTexture(TEXTURE_BACKGROUND);
		this.textureHeart = LTextures.loadTexture(TEXTURE_ICON_HEART);
		this.textureFlagGreen = LTextures.loadTexture(TEXTURE_FLAG_GREEN);
		this.textureFlagRed = LTextures.loadTexture(TEXTURE_FLAG_RED);
		this.font = Constants.font(24);
	}

	@Override
	public void UnloadContent() {
		this.font = null;
		this.texture = null;
		this.textureHeart = null;
		this.textureFlagGreen = null;
		this.textureFlagRed = null;
		super.UnloadContent();
	}

	private void StartGame(int level) {
		super.getScreenManager().ExitAllScreens();
		super.getScreenManager().AddScreen(
				new GameplayScreen(this.game, this.difficulty, level));
	}

	private void StartLevel1MenuEntrySelected() {
		this.StartGame(1);
	}

	private void StartLevel2MenuEntrySelected() {

		this.StartGame(2);

	}

	private void StartLevel3MenuEntrySelected() {

		this.StartGame(3);

	}

	private void StartMainMenuEntrySelected() {
		super.getScreenManager().ExitAllScreens();
		super.getScreenManager().AddScreen(
				new MainMenuScreen(this.game, ScreenType.SelectLevelScreen));
	}

}
