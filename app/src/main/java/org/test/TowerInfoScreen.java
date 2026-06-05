package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.IGameComponent;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.input.LInput;
import loon.core.timer.GameTime;

public class TowerInfoScreen extends MenuScreen {
	private java.util.ArrayList<AnimatedSpriteTower> animatedSprites;
	private boolean componentsDetached;
	private MainGame game;
	private boolean screenContentLoaded;
	private LTexture texture;
	private TowerInfoScreenSpriteWithText towerInfoScreenSpriteWithText;

	public TowerInfoScreen(MainGame game, ScreenType prevScreen) {
		super("", game, prevScreen);
		this.game = game;
		super.setScreenType(ScreenType.TowerInfoScreen);
		super.setTransitionOnTime(0f);
		super.setTransitionOffTime(0.5f);
		MenuEntry item = new MenuEntry("");
		item.setuseButtonBackground(false);
		item.setPosition(new Vector2f(Constants.INFO_BACK_HIT_LEFT,
				Constants.INFO_BACK_BTN_TOP_Y));
		item.setnoButtonBackgroundSize(new Vector2f(
				Constants.INFO_BACK_HIT_WIDTH, Constants.INFO_BACK_HIT_HEIGHT));

		item.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				StartInstructionsMenuEntrySelected();
			}
		};
		super.getMenuEntries().add(item);
		this.towerInfoScreenSpriteWithText = new TowerInfoScreenSpriteWithText(
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
		if (this.animatedSprites != null) {
			for (IGameComponent component : this.animatedSprites) {
				this.game.Components().remove(component);
			}
			this.animatedSprites = null;
		}
		if (this.towerInfoScreenSpriteWithText != null) {
			this.game.Components().remove(this.towerInfoScreenSpriteWithText);
		}
	}

	@Override
	public void HandleInput(GameTime gameTime, LInput input) {
		super.HandleInput(gameTime, input);
	}

	@Override
	public void LoadContent() {
		if (this.screenContentLoaded) {
			return;
		}
		this.screenContentLoaded = true;
		this.componentsDetached = false;
		this.texture = LTextures.loadTexture("assets/towers_2.png");
		if (this.towerInfoScreenSpriteWithText != null) {
			this.towerInfoScreenSpriteWithText
					.setDrawOrder(Constants.INFO_OVERLAY_DRAW_ORDER);
			this.game.Components().add(this.towerInfoScreenSpriteWithText);
		}
		this.animatedSprites = AnimatedSpriteTower
				.GetAllAnimatedSpriteTowers(this.game);
		for (AnimatedSpriteTower tower : this.animatedSprites) {
			tower.setOnlyAnimateIfGameStateStarted(false);
			tower.setObeyGameOpacity(false);
			tower.setDrawOrder(50);
			this.game.Components().add(tower);
		}
	}

	@Override
	public void UnloadContent() {
		this.detachScreenComponents();
		this.texture = null;
		this.screenContentLoaded = false;
	}

	@Override
	protected void OnCancel() {
		this.detachScreenComponents();
		super.OnCancel();
	}

	private void StartInstructionsMenuEntrySelected() {
		this.detachScreenComponents();
		super.getScreenManager().ExitAllScreens();
		super.getScreenManager().AddScreen(
				new InstructionScreen(this.game, ScreenType.TowerInfoScreen));
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
