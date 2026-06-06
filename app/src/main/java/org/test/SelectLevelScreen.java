package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.timer.GameTime;

public class SelectLevelScreen extends MenuScreen {
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
		Vector2f vector = new Vector2f(220f, 82f);
		MenuEntry item = new MenuEntry("");
		item.setuseButtonBackground(false);
		item.setPosition(new Vector2f(172f, 420f));
		item.setnoButtonBackgroundSize(vector);
		MenuEntry entry2 = new MenuEntry("");
		entry2.setuseButtonBackground(false);
		entry2.setPosition(new Vector2f(172f, 540f));
		entry2.setnoButtonBackgroundSize(vector);
		MenuEntry entry3 = new MenuEntry("");
		entry3.setuseButtonBackground(false);
		entry3.setPosition(new Vector2f(172f, 660f));
		entry3.setnoButtonBackgroundSize(vector);
		MenuEntry entry4 = new MenuEntry("");
		entry4.setuseButtonBackground(false);
		entry4.setPosition(new Vector2f(172f, 780f));
		entry4.setnoButtonBackgroundSize(vector);
		super.getMenuEntries().add(item);
		super.getMenuEntries().add(entry2);
		super.getMenuEntries().add(entry3);
		super.getMenuEntries().add(entry4);
		for (CompletedLevel level : game.getCompletedLevels()) {
			if (level.getDifficulty() == difficulty.getValue()) {
				if (level.getLevel() == 1) {
					this.remainingLivesRecordLevel1 = level.getRemainingLives();
				} else {
					if (level.getLevel() == 2) {
						this.remainingLivesRecordLevel2 = level
								.getRemainingLives();
						continue;
					}
					if (level.getLevel() == 3) {
						this.remainingLivesRecordLevel3 = level
								.getRemainingLives();
					}
				}
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

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
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
			batch.draw(this.textureFlagGreen, x, y, LColor.white);
			Utils.DrawStringAlignRight(batch, font, (remainingLivesRecord) + "/"
							+ Constants.InitialRemainingLives, new Vector2f(
					166f, y), LColor.white);
			batch.draw(this.textureHeart, 168f, y + 6f, LColor.white);
		} else {
			batch.draw(this.textureFlagRed, x, y, LColor.white);
		}
	}

	@Override
	public void LoadContent() {
		this.texture = LTextures
				.loadTexture("assets/backgrounds/select_level_menu.png");
		this.textureHeart = LTextures.loadTexture("assets/icon_heart.png");
		this.textureFlagGreen = LTextures
				.loadTexture("assets/icon_flag_green.png");
		this.textureFlagRed = LTextures.loadTexture("assets/icon_flag_red.png");
		this.font = Constants.font(24);
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
