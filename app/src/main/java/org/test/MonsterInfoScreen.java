package org.test;

import android.util.Log;

import loon.action.sprite.painting.IGameComponent;
import loon.core.geom.Vector2f;
import loon.core.input.LInput;
import loon.core.timer.GameTime;

public class MonsterInfoScreen extends MenuScreen {
	private static final String TAG = "InstructionsPerf";

	private java.util.ArrayList<AnimatedSprite> animatedSprites;
	private MainGame game;
	private boolean isFirstExit;
	private MonsterInfoScreenSpriteWithText monsterInfoScreenSpriteWithText;
	private final long screenCreatedAtNs;
	private ScreenState lastScreenState;

	public MonsterInfoScreen(MainGame game, ScreenType prevScreen) {
		super("", game, prevScreen);
		this.screenCreatedAtNs = System.nanoTime();
		this.isFirstExit = true;
		this.game = game;
		super.setScreenType(ScreenType.MonsterInfoScreen);
		super.setTransitionOnTime(0f);
		super.setTransitionOffTime(0.5f);

		MenuEntry item = new MenuEntry("");
		item.setuseButtonBackground(false);
		item.setPosition(new Vector2f(440f, 1688f));
		item.setnoButtonBackgroundSize(new Vector2f(480f, 152f));

		item.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				StartInstructionsMenuEntrySelected();
			}
		};
		super.getMenuEntries().add(item);
		this.monsterInfoScreenSpriteWithText = new MonsterInfoScreenSpriteWithText(
				game);
		long ctorMs = (System.nanoTime() - this.screenCreatedAtNs) / 1_000_000L;
		Log.d(TAG, "[Enemies] MonsterInfoScreen 构造完成 " + ctorMs + "ms");
	}

	private void Exit() {
		if (this.monsterInfoScreenSpriteWithText != null) {
			this.game.Components().remove(this.monsterInfoScreenSpriteWithText);
		}
	}

	@Override
	public void HandleInput(GameTime gameTime, LInput input) {
		super.HandleInput(gameTime, input);
	}

	@Override
	public void LoadContent() {
		long t0 = System.nanoTime();
		this.animatedSprites = AnimatedSpriteMonster
				.GetAllAnimatedSpriteMonsters(this.game);
		long createMs = (System.nanoTime() - t0) / 1_000_000L;
		long t1 = System.nanoTime();
		for (AnimatedSprite sprite : this.animatedSprites) {
			sprite.setOnlyAnimateIfGameStateStarted(false);
			sprite.setObeyGameOpacity(false);
			super.getScreenManager().getGame().Components().add(sprite);
		}
		long addMs = (System.nanoTime() - t1) / 1_000_000L;
		long sinceCreateMs = (System.nanoTime() - this.screenCreatedAtNs)
				/ 1_000_000L;
		Log.d(TAG, "[Enemies] LoadContent: 创建 " + this.animatedSprites.size()
				+ " 个动画 sprite " + createMs + "ms, 加入 Components "
				+ addMs + "ms, 距屏幕创建 " + sinceCreateMs + "ms");
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
		ScreenState state = super.getScreenState();
		if (this.lastScreenState != ScreenState.Active
				&& state == ScreenState.Active) {
			long sinceCreateMs = (System.nanoTime() - this.screenCreatedAtNs)
					/ 1_000_000L;
			Log.d(TAG, "[Enemies] 屏幕进入 Active: 距 MonsterInfoScreen 创建 "
					+ sinceCreateMs + "ms");
		}
		this.lastScreenState = state;
	}
}