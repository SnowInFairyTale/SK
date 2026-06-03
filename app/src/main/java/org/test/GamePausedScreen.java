package org.test;

import loon.core.geom.Vector2f;
import loon.core.timer.GameTime;

public class GamePausedScreen extends MenuScreen {
	private MainGame game;
	private GamePausedSpriteWithText gamePausedSpriteWithText;

	public GamePausedScreen(MainGame game, ScreenType prevScreen) {
		super("", game, prevScreen);
		this.game = game;
		super.setTransitionOnTime(0f);
		super.setTransitionOffTime(0f);
		super.setIsPopup(true);
		super.setScreenType(ScreenType.GamePausedScreen);
		Vector2f rowHit = Constants.menuHitSize();
		MenuEntry item = new MenuEntry("");
		item.setuseButtonBackground(false);
		item.setPosition(Constants.menuHitAtCenterY(1520f));
		item.setnoButtonBackgroundSize(rowHit);
		MenuEntry entry2 = new MenuEntry("");
		entry2.setuseButtonBackground(false);
		entry2.setPosition(Constants.menuHitAtCenterY(1040f));
		entry2.setnoButtonBackgroundSize(rowHit);
		MenuEntry entry3 = new MenuEntry("");
		entry3.setuseButtonBackground(false);
		entry3.setPosition(Constants.menuHitAtCenterY(1280f));
		entry3.setnoButtonBackgroundSize(rowHit);

		item.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				ShowMainMenuSelected();

			}
		};

		entry2.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				ResumeSelected();
			}
		};

		entry3.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				RestartSelected();
			}
		};
		super.getMenuEntries().add(item);
		super.getMenuEntries().add(entry2);
		super.getMenuEntries().add(entry3);
		this.gamePausedSpriteWithText = new GamePausedSpriteWithText(game);
		this.gamePausedSpriteWithText.setDrawOrder(100);
		game.Components().add(this.gamePausedSpriteWithText);
	}

	private void Exit() {
		if (this.gamePausedSpriteWithText != null) {
			this.game.Components().remove(this.gamePausedSpriteWithText);
		}
	}

	@Override
	protected void OnCancel() {
		this.Resume();
	}

	private void RestartSelected() {
		this.Exit();
		super.getScreenManager().AddScreen(
				new ConfirmScreen(this.game, ScreenType.GamePausedScreen,
						ConfirmType.RestartGame));
	}

	private void Resume() {
		this.Exit();
		super.ExitScreen();
		this.game.getGameplayScreen().GameResumed();
	}

	private void ResumeSelected() {
		this.Resume();
	}

	private void ShowMainMenuSelected() {
		this.Exit();
		super.getScreenManager().AddScreen(
				new ConfirmScreen(this.game, ScreenType.GamePausedScreen,
						ConfirmType.ExitToMainMenu));
	}

	@Override
	public void Update(GameTime gameTime, boolean otherScreenHasFocus,
			boolean coveredByOtherScreen) {
		super.Update(gameTime, otherScreenHasFocus, coveredByOtherScreen);
	}
}