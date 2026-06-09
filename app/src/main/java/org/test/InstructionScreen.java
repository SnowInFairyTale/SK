package org.test;

import com.loon.action.sprite.SpriteBatch;
import com.loon.core.geom.Vector2f;
import com.loon.core.graphics.LColor;
import com.loon.core.graphics.opengl.LTexture;
import com.loon.core.graphics.opengl.LTextures;
import com.loon.core.input.LInput;
import com.loon.core.timer.GameTime;

public class InstructionScreen extends MenuScreen {
	private boolean componentsDetached;
	private MainGame game;
	private InstructionsScreenSpriteWithText instructionsScreenSpriteWithText;
	private boolean screenContentLoaded;
	private LTexture texture;

	public InstructionScreen(MainGame game, ScreenType prevScreen) {
		super("", game, prevScreen);
		this.game = game;
		super.setScreenType(ScreenType.InstructionsScreen);
		super.setTransitionOnTime(0f);
		super.setTransitionOffTime(0.5f);
		MenuEntry item = new MenuEntry("");
		item.setuseButtonBackground(false);
		item.setPosition(new Vector2f(14f, 850f));
		item.setnoButtonBackgroundSize(new Vector2f(188f, 76f));
		MenuEntry entry2 = new MenuEntry("");
		entry2.setuseButtonBackground(false);
		entry2.setPosition(new Vector2f(256f, 850f));
		entry2.setnoButtonBackgroundSize(new Vector2f(188f, 76f));
		MenuEntry entry3 = new MenuEntry("");
		entry3.setuseButtonBackground(false);
		entry3.setPosition(new Vector2f(500f, 850f));
		entry3.setnoButtonBackgroundSize(new Vector2f(116f, 76f));

		entry2.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				StartMonsterInfoSelected();
			}
		};
		item.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				StartTowerInfoSelected();
			}
		};

		entry3.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				StartMainMenuSelected();

			}
		};
		super.getMenuEntries().add(item);
		super.getMenuEntries().add(entry2);
		super.getMenuEntries().add(entry3);
		this.instructionsScreenSpriteWithText = new InstructionsScreenSpriteWithText(
				game);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		if (this.texture != null) {
			batch.draw(this.texture, 0f, 0f, LColor.white);
		}
		super.draw(batch, gameTime);
	}

	private void detachScreenComponents() {
		if (this.componentsDetached) {
			return;
		}
		this.componentsDetached = true;
		if (this.instructionsScreenSpriteWithText != null) {
			this.game.Components().remove(this.instructionsScreenSpriteWithText);
		}
	}

	@Override
	public void LoadContent() {
		if (this.screenContentLoaded) {
			return;
		}
		this.screenContentLoaded = true;
		this.componentsDetached = false;
		this.texture = LTextures
				.loadTexture("assets/screen_introduction.png");
		if (this.instructionsScreenSpriteWithText != null) {
			this.instructionsScreenSpriteWithText
					.setDrawOrder(Constants.INFO_OVERLAY_DRAW_ORDER);
			this.game.Components().add(this.instructionsScreenSpriteWithText);
		}
	}

	@Override
	public void UnloadContent() {
		this.detachScreenComponents();
		this.texture = null;
		this.screenContentLoaded = false;
	}

	@Override
	public void HandleInput(GameTime gameTime, LInput input) {
		super.HandleInput(gameTime, input);
	}

	@Override
	protected void OnCancel() {
		this.detachScreenComponents();
		super.OnCancel();
	}

	private void StartMainMenuSelected() {
		this.detachScreenComponents();
		super.getScreenManager().ExitAllScreens();
		super.getScreenManager().AddScreen(
				new MainMenuScreen(this.game, ScreenType.InstructionsScreen));
	}

	private void StartMonsterInfoSelected() {
		this.detachScreenComponents();
		super.getScreenManager().ExitAllScreens();
		super.getScreenManager().AddScreen(
				new MonsterInfoScreen(this.game, ScreenType.InstructionsScreen));
	}

	private void StartTowerInfoSelected() {
		this.detachScreenComponents();
		super.getScreenManager().ExitAllScreens();
		super.getScreenManager().AddScreen(
				new TowerInfoScreen(this.game, ScreenType.InstructionsScreen));
	}

	@Override
	public void Update(GameTime gameTime, boolean otherScreenHasFocus,
			boolean coveredByOtherScreen) {
		if (super.getIsExiting()) {
			this.detachScreenComponents();
		}
		super.Update(gameTime, otherScreenHasFocus, coveredByOtherScreen);
	}
}
