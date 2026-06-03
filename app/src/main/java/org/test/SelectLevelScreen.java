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
	private boolean level1Locked;
	private boolean level2Locked;
	private boolean level3Locked;
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
		this.level2Locked = true;
		this.level3Locked = true;
		this.remainingLivesRecordLevel1 = -1;
		this.remainingLivesRecordLevel2 = -1;
		this.remainingLivesRecordLevel3 = -1;
		this.game = game;
		super.setScreenType(ScreenType.SelectLevelScreen);
		this.difficulty = difficulty;
		Vector2f rowHit = Constants.menuHitSize();
		MenuEntry item = new MenuEntry("");
		item.setuseButtonBackground(false);
		item.setPosition(Constants.menuHitAtCenterY(876f));
		item.setnoButtonBackgroundSize(rowHit);
		MenuEntry entry2 = new MenuEntry("");
		entry2.setuseButtonBackground(false);
		entry2.setPosition(Constants.menuHitAtCenterY(1116f));
		entry2.setnoButtonBackgroundSize(rowHit);
		MenuEntry entry3 = new MenuEntry("");
		entry3.setuseButtonBackground(false);
		entry3.setPosition(Constants.menuHitAtCenterY(1356f));
		entry3.setnoButtonBackgroundSize(rowHit);
		MenuEntry entry4 = new MenuEntry("");
		entry4.setuseButtonBackground(false);
		entry4.setPosition(Constants.menuHitAtCenterY(1608f));
		entry4.setnoButtonBackgroundSize(rowHit);
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

	Vector2f result = new Vector2f(Constants.ScreenCenterX, 876f);

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		batch.draw(this.texture, 0f, 0f, LColor.white);

		result.set(Constants.ScreenCenterX, 876f);
		Utils.DrawLevelText(batch, this.font,
				LanguageResources.getLevel1Title(), this.level1Locked, result);

		result.set(Constants.ScreenCenterX, 1116f);
		Utils.DrawLevelText(batch, this.font,
				LanguageResources.getLevel2Title(), this.level2Locked, result);

		result.set(Constants.ScreenCenterX, 1356f);
		Utils.DrawLevelText(batch, this.font,
				LanguageResources.getLevel3Title(), this.level3Locked, result);

		result.set(Constants.ScreenCenterX, 1608f);
		Utils.DrawStringAlignCenter(batch, this.font, LanguageResources
				.getMainMenu().toUpperCase(), result, LColor.white);

		this.DrawBestRemainingLives(batch, this.remainingLivesRecordLevel1,
				976f, 940f, this.font);
		this.DrawBestRemainingLives(batch, this.remainingLivesRecordLevel2,
				976f, 1180f, this.font);
		this.DrawBestRemainingLives(batch, this.remainingLivesRecordLevel3,
				976f, 1420f, this.font);
		super.draw(batch, gameTime);
	}

	private void DrawBestRemainingLives(SpriteBatch batch,
			int remainingLivesRecord, float x, float y, LFont font) {
		if (remainingLivesRecord >= 0) {
			batch.draw(this.textureFlagGreen, x, y, LColor.white);
			Utils.DrawStringAlignRight(batch, font, (new Integer(
					remainingLivesRecord)).toString() + "/" + 20, new Vector2f(
					332f, y), LColor.white);
			batch.draw(this.textureHeart, 336f, y + 12f, LColor.white);
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
		this.font = Constants.uiFont(12);
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

	@Override
	public void Update(GameTime gameTime, boolean otherScreenHasFocus,
			boolean coveredByOtherScreen) {
		super.Update(gameTime, otherScreenHasFocus, coveredByOtherScreen);

		this.level2Locked = false;
		this.level3Locked = false;

	}
}