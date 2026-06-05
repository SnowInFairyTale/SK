package org.test;

import loon.action.sprite.painting.IGameComponent;
import loon.core.geom.Vector2f;
import loon.core.input.LInput;
import loon.core.timer.GameTime;

public class MonsterInfoScreen extends MenuScreen {
	private java.util.ArrayList<AnimatedSprite> animatedSprites;
	private MainGame game;
	private boolean isFirstExit;
	private ScreenButtonLabel backButtonLabel;
	private MonsterInfoScreenSpriteWithText monsterInfoScreenSpriteWithText;

	public MonsterInfoScreen(MainGame game, ScreenType prevScreen) {
		super("", game, prevScreen);
		this.isFirstExit = true;
		this.game = game;
		super.setScreenType(ScreenType.MonsterInfoScreen);
		super.setTransitionOnTime(0f);
		super.setTransitionOffTime(0.5f);

		MenuEntry item = new MenuEntry("");
		item.setuseButtonBackground(false);
		item.setPosition(new Vector2f(220f, 844f));
		item.setnoButtonBackgroundSize(new Vector2f(240f, 76f));

		item.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				StartInstructionsMenuEntrySelected();
			}
		};
		super.getMenuEntries().add(item);
		this.monsterInfoScreenSpriteWithText = new MonsterInfoScreenSpriteWithText(
				game);
		this.backButtonLabel = new ScreenButtonLabel(game, LanguageResources
				.getBack().toUpperCase(), 338f, 844f, 76f, 32);
		game.Components().add(this.backButtonLabel);
	}

	private void Exit() {
		if (this.monsterInfoScreenSpriteWithText != null) {
			this.game.Components().remove(this.monsterInfoScreenSpriteWithText);
		}
		if (this.backButtonLabel != null) {
			this.game.Components().remove(this.backButtonLabel);
		}
	}

	@Override
	public void HandleInput(GameTime gameTime, LInput input) {
		super.HandleInput(gameTime, input);
	}

	@Override
	public void LoadContent() {
		this.animatedSprites = AnimatedSpriteMonster
				.GetAllAnimatedSpriteMonsters(this.game);
		for (AnimatedSprite sprite : this.animatedSprites) {
			sprite.setOnlyAnimateIfGameStateStarted(false);
			sprite.setObeyGameOpacity(false);
			sprite.setDrawOrder(50);
			super.getScreenManager().getGame().Components().add(sprite);
		}
	}

	@Override
	protected void OnCancel() {
		this.Exit();
		super.getScreenManager().ExitAllScreens();
		super.OnCancel();
	}

	private void StartInstructionsMenuEntrySelected() {
		this.Exit();
		super.getScreenManager().ExitAllScreens();
		super.getScreenManager().AddScreen(
				new InstructionScreen(this.game, ScreenType.MonsterInfoScreen));
	}

	@Override
	public void Update(GameTime gameTime, boolean otherScreenHasFocus,
			boolean coveredByOtherScreen) {
		if (super.getIsExiting() && this.isFirstExit) {
			for (IGameComponent component : this.animatedSprites) {
				super.getScreenManager().getGame().Components()
						.remove(component);
			}
			this.isFirstExit = false;
		}
		super.Update(gameTime, otherScreenHasFocus, coveredByOtherScreen);
	}
}