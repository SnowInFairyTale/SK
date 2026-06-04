package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.timer.GameTime;

public class MainMenuScreen extends MenuScreen {
	private boolean easyLocked;

	private LFont fontStd;
	private MainGame game;
	private boolean hardLocked;
	private boolean mediumLocked;
	private MenuEntry startHardGameMenuEntry;
	private MenuEntry startMediumGameMenuEntry;
	private LTexture texture;
	private LTexture textureSound;
	private LTexture textureSoundOff;
	private LTexture textureSoundOn;

	public MainMenuScreen(MainGame game, ScreenType prevScreen) {
		super("", game, prevScreen);
		this.game = game;
		super.setScreenType(ScreenType.MainMenuScreen);
		Vector2f rowHit = Constants.menuHitSize();
		MenuEntry item = new MenuEntry("");
		item.setuseButtonBackground(false);
		item.setPosition(Constants.menuHitAtCenterY(1620f));
		item.setnoButtonBackgroundSize(rowHit);
		MenuEntry entry2 = new MenuEntry("");
		entry2.setuseButtonBackground(false);
		entry2.setPosition(Constants.menuHitAtCenterY(876f));
		entry2.setnoButtonBackgroundSize(rowHit);
		this.startMediumGameMenuEntry = new MenuEntry("");
		this.startMediumGameMenuEntry.setuseButtonBackground(false);
		this.startMediumGameMenuEntry.setPosition(Constants.menuHitAtCenterY(1116f));
		this.startMediumGameMenuEntry.setnoButtonBackgroundSize(rowHit);
		this.startHardGameMenuEntry = new MenuEntry("");
		this.startHardGameMenuEntry.setuseButtonBackground(false);
		this.startHardGameMenuEntry.setPosition(Constants.menuHitAtCenterY(1356f));
		this.startHardGameMenuEntry.setnoButtonBackgroundSize(rowHit);
		MenuEntry entry3 = new MenuEntry("");
		entry3.setuseButtonBackground(false);
		entry3.setPosition(new Vector2f(80f, 1776f));
		entry3.setnoButtonBackgroundSize(new Vector2f(240f, 240f));
		MenuEntry entry4 = new MenuEntry("");
		entry4.setuseButtonBackground(false);
		entry4.setPosition(new Vector2f(1040f, 80f));
		entry4.setnoButtonBackgroundSize(new Vector2f(240f, 240f));
		this.UpdateLockedDifficulties();

		item.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				StartInstructionsMenuEntrySelected();
			}
		};

		entry3.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				ToggleSoundEnabledSelected();

			}
		};

		entry2.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				StartEasyGameMenuEntrySelected();

			}
		};

		this.startMediumGameMenuEntry.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				StartMediumGameMenuEntrySelected();

			}
		};

		this.startHardGameMenuEntry.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				StartHardGameMenuEntrySelected();
			}
		};

		super.getMenuEntries().add(item);
		super.getMenuEntries().add(entry2);
		super.getMenuEntries().add(this.startMediumGameMenuEntry);
		super.getMenuEntries().add(this.startHardGameMenuEntry);
		super.getMenuEntries().add(entry3);
		super.getMenuEntries().add(entry4);
	}

	Vector2f result = new Vector2f(Constants.ScreenCenterX, 876f);
	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		batch.draw(this.texture, 0f, 0f, LColor.white);
		batch.draw(this.textureSound, 80f, 1776f, LColor.white);

		result.set(Constants.ScreenCenterX, 876f);
		Utils.DrawLevelText(batch, this.fontStd, LanguageResources.getEasy()
				.toUpperCase(), this.easyLocked, result);

		result.set(Constants.ScreenCenterX, 1116f);
		Utils.DrawLevelText(batch, this.fontStd, LanguageResources.getMedium()
				.toUpperCase(), this.mediumLocked, result);

		result.set(Constants.ScreenCenterX, 1356f);
		Utils.DrawLevelText(batch, this.fontStd, LanguageResources.getHard()
				.toUpperCase(), this.hardLocked, result);

		Utils.DrawStringAlignCenter(batch, this.fontStd, LanguageResources
				.getInstructions().toUpperCase(), Constants.ScreenCenterX, 1620f, LColor.white);

		super.draw(batch, gameTime);
	}

	@Override
	public void LoadContent() {
		this.texture = LTextures
				.loadTexture("assets/backgrounds/main_menu.png");
		this.textureSoundOn = LTextures
				.loadTexture("assets/speaker_icon_on.png");
		this.textureSoundOff = LTextures
				.loadTexture("assets/speaker_icon_off.png");
		this.fontStd = Constants.uiFont(12);
		this.SetSoundTexture();
	}

	public final void PreloadAssets() {
		this.PreloadTextures();
		this.PreloadSound();
	}

	private void PreloadSound() {

	}

	private void PreloadTextures() {
	}

	private void SelectLevel(Difficulty difficulty) {
		super.getScreenManager().ExitAllScreens();
		if (((difficulty == Difficulty.Medium) || (difficulty == Difficulty.Hard))) {
			super.getScreenManager().AddScreen(
					new BuyToGetFeaturesScreen(this.game,
							ScreenType.MainMenuScreen, null));
		} else {
			super.getScreenManager().AddScreen(
					new SelectLevelScreen(this.game, ScreenType.MainMenuScreen,
							difficulty));
		}
	}

	private void SetSoundTexture() {
		if (this.game.getSoundEnabled()) {
			this.textureSound = this.textureSoundOn;
		} else {
			this.textureSound = this.textureSoundOff;
		}
	}

	private void StartEasyGameMenuEntrySelected() {
		this.SelectLevel(Difficulty.Easy);
	}

	private void StartHardGameMenuEntrySelected() {
		this.SelectLevel(Difficulty.Hard);
	}

	private void StartInstructionsMenuEntrySelected() {
		android.util.Log.d("InstructionsPerf",
				"从主菜单打开 Instructions");
		super.getScreenManager().ExitAllScreens();
		long t0 = System.nanoTime();
		super.getScreenManager().AddScreen(
				new InstructionScreen(this.game, ScreenType.MainMenuScreen));
		long addMs = (System.nanoTime() - t0) / 1_000_000L;
		android.util.Log.d("InstructionsPerf",
				"AddScreen(InstructionScreen) 返回 " + addMs + "ms");
	}

	private void StartMediumGameMenuEntrySelected() {
		this.SelectLevel(Difficulty.Medium);
	}

	private void ToggleSoundEnabledSelected() {
		this.SetSoundTexture();
	}

	@Override
	public void Update(GameTime gameTime, boolean otherScreenHasFocus,
			boolean coveredByOtherScreen) {
		super.Update(gameTime, otherScreenHasFocus, coveredByOtherScreen);
		this.UpdateLockedDifficulties();
	}

	private void UpdateLockedDifficulties() {
		this.easyLocked = false;
		this.mediumLocked = false;
		this.hardLocked = false;
	}
}