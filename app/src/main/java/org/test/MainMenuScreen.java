package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.timer.GameTime;

public class MainMenuScreen extends MenuScreen {
	private LFont fontStd;
	private MainGame game;
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
		Vector2f vector = new Vector2f(220f, 82f);
		MenuEntry item = new MenuEntry("");
		item.setuseButtonBackground(false);
		item.setPosition(new Vector2f(172f, 780f));
		item.setnoButtonBackgroundSize(vector);
		MenuEntry entry2 = new MenuEntry("");
		entry2.setuseButtonBackground(false);
		entry2.setPosition(new Vector2f(172f, 420f));
		entry2.setnoButtonBackgroundSize(vector);
		this.startMediumGameMenuEntry = new MenuEntry("");
		this.startMediumGameMenuEntry.setuseButtonBackground(false);
		this.startMediumGameMenuEntry.setPosition(new Vector2f(172f, 540f));
		this.startMediumGameMenuEntry.setnoButtonBackgroundSize(vector);
		this.startHardGameMenuEntry = new MenuEntry("");
		this.startHardGameMenuEntry.setuseButtonBackground(false);
		this.startHardGameMenuEntry.setPosition(new Vector2f(172f, 660f));
		this.startHardGameMenuEntry.setnoButtonBackgroundSize(vector);
		MenuEntry entry3 = new MenuEntry("");
		entry3.setuseButtonBackground(false);
		entry3.setPosition(new Vector2f(10f, 820f));
		entry3.setnoButtonBackgroundSize(new Vector2f(120f, 120f));
		MenuEntry entry4 = new MenuEntry("");
		entry4.setuseButtonBackground(false);
		entry4.setPosition(new Vector2f(460f, 40f));
		entry4.setnoButtonBackgroundSize(new Vector2f(120f, 120f));
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

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		batch.draw(this.texture, 0f, 0f, LColor.white);
		batch.draw(this.textureSound, 40f, 888f, LColor.white);

		Utils.drawMenuButtonLabel(batch, this.fontStd,
				LanguageResources.getEasy(), Constants.MENU_BTN_ROW_1_Y);
		Utils.drawMenuButtonLabel(batch, this.fontStd,
				LanguageResources.getMedium(), Constants.MENU_BTN_ROW_2_Y);
		Utils.drawMenuButtonLabel(batch, this.fontStd,
				LanguageResources.getHard(), Constants.MENU_BTN_ROW_3_Y);
		Utils.drawMenuButtonLabel(batch, this.fontStd, LanguageResources
				.getInstructions().toUpperCase(), Constants.MENU_BTN_ROW_4_Y);

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
		this.fontStd = Constants.font(24);
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
		super.getScreenManager().AddScreen(
				new SelectLevelScreen(this.game, ScreenType.MainMenuScreen,
						difficulty));
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
		super.getScreenManager().ExitAllScreens();
		super.getScreenManager().AddScreen(
				new InstructionScreen(this.game, ScreenType.MainMenuScreen));
	}

	private void StartMediumGameMenuEntrySelected() {
		this.SelectLevel(Difficulty.Medium);
	}

	private void ToggleSoundEnabledSelected() {
		this.SetSoundTexture();
	}

}
