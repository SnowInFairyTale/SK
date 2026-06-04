package org.test;

import android.util.Log;

import loon.core.geom.Vector2f;
import loon.core.input.LInput;
import loon.core.timer.GameTime;

public class InstructionScreen extends MenuScreen
{
	private static final String TAG = "InstructionsPerf";

	private MainGame game;
	private InstructionsScreenSpriteWithText instructionsScreenSpriteWithText;
	private final long screenCreatedAtNs;
	private ScreenState lastScreenState;

	public InstructionScreen(MainGame game, ScreenType prevScreen)
	{
		super("", game, prevScreen);
		this.screenCreatedAtNs = System.nanoTime();
		this.game = game;
		super.setScreenType( ScreenType.InstructionsScreen);
		super.setTransitionOnTime(0f);
		super.setTransitionOffTime(0.5f);
		MenuEntry item = new MenuEntry("");
		item.setuseButtonBackground(false);
		item.setPosition(new Vector2f(28f, 1700f));
		item.setnoButtonBackgroundSize(new Vector2f(376f, 152f));
		MenuEntry entry2 = new MenuEntry("");
		entry2.setuseButtonBackground(false);
		entry2.setPosition(new Vector2f(512f, 1700f));
		entry2.setnoButtonBackgroundSize(new Vector2f(376f, 152f));
		MenuEntry entry3 = new MenuEntry("");
		entry3.setuseButtonBackground(false);
		entry3.setPosition(new Vector2f(1000f, 1700f));
		entry3.setnoButtonBackgroundSize(new Vector2f(232f, 152f));

		entry2.Selected =new GameEvent() {
			
			@Override
			public void invoke(MenuEntry comp) {
				StartMonsterInfoSelected();
			}
		};
		item.Selected =new GameEvent() {
			
			@Override
			public void invoke(MenuEntry comp) {
				StartTowerInfoSelected();
			}
		};

		entry3.Selected =new GameEvent() {
			
			@Override
			public void invoke(MenuEntry comp) {
				StartMainMenuSelected();
				
			}
		};
		super.getMenuEntries().add(item);
		super.getMenuEntries().add(entry2);
		super.getMenuEntries().add(entry3);
		this.instructionsScreenSpriteWithText = new InstructionsScreenSpriteWithText(game);
		long ctorMs = (System.nanoTime() - this.screenCreatedAtNs) / 1_000_000L;
		Log.d(TAG, "InstructionScreen 构造完成 " + ctorMs + "ms, transitionOn="
				+ super.getTransitionOnTime() + "s transitionOff="
				+ super.getTransitionOffTime() + "s, prevScreen=" + prevScreen);
	}

	private void Exit()
	{
		if (this.instructionsScreenSpriteWithText != null)
		{
			this.game.Components().remove(this.instructionsScreenSpriteWithText);
		}
	}

	@Override
	public void HandleInput(GameTime gameTime, LInput input)
	{
		super.HandleInput(gameTime, input);
	}

	@Override
	protected void OnCancel( )
	{
		this.Exit();
		super.OnCancel();
	}

	private void StartMainMenuSelected( )  
	{
		this.Exit();
		super.getScreenManager().ExitAllScreens();
		super.getScreenManager().AddScreen(new MainMenuScreen(this.game, ScreenType.InstructionsScreen));
	}

	private void StartMonsterInfoSelected()
	{
		Log.d(TAG, "[Enemies] 从 Instructions 点击打开");
		long t0 = System.nanoTime();
		this.Exit();
		super.getScreenManager().ExitAllScreens();
		super.getScreenManager().AddScreen(new MonsterInfoScreen(this.game, ScreenType.InstructionsScreen));
		Log.d(TAG, "[Enemies] AddScreen 返回 "
				+ ((System.nanoTime() - t0) / 1_000_000L) + "ms");
	}

	private void StartTowerInfoSelected()
	{
		Log.d(TAG, "[Towers] 从 Instructions 点击打开");
		long t0 = System.nanoTime();
		this.Exit();
		super.getScreenManager().ExitAllScreens();
		super.getScreenManager().AddScreen(new TowerInfoScreen(this.game, ScreenType.InstructionsScreen));
		Log.d(TAG, "[Towers] AddScreen 返回 "
				+ ((System.nanoTime() - t0) / 1_000_000L) + "ms");
	}

	@Override
	public void Update(GameTime gameTime, boolean otherScreenHasFocus, boolean coveredByOtherScreen)
	{
		super.Update(gameTime, otherScreenHasFocus, coveredByOtherScreen);
		ScreenState state = super.getScreenState();
		if (this.lastScreenState != ScreenState.Active
				&& state == ScreenState.Active) {
			long sinceCreateMs = (System.nanoTime() - this.screenCreatedAtNs)
					/ 1_000_000L;
			Log.d(TAG, "屏幕进入 Active: 距 InstructionScreen 创建 "
					+ sinceCreateMs + "ms");
		}
		this.lastScreenState = state;
	}
}
