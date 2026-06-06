package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.IGameComponent;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.input.LInput;
import loon.core.timer.GameTime;

public class MonsterInfoScreen extends MenuScreen {
	private static final java.util.ArrayList<AnimatedSprite> registeredPreviews = new java.util.ArrayList<AnimatedSprite>();

	private java.util.ArrayList<AnimatedSprite> animatedSprites;
	private boolean componentsDetached;
	private MainGame game;
	private MonsterInfoScreenSpriteWithText monsterInfoScreenSpriteWithText;
	private boolean screenContentLoaded;
	private LTexture texture;

	public MonsterInfoScreen(MainGame game, ScreenType prevScreen) {
		super("", game, prevScreen);
		this.game = game;
		super.setScreenType(ScreenType.MonsterInfoScreen);
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
		this.monsterInfoScreenSpriteWithText = new MonsterInfoScreenSpriteWithText(
				game);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		if (this.texture != null) {
			batch.draw(this.texture, 0f, 0f, LColor.white);
		}
		super.draw(batch, gameTime);
	}

	public static void purgeLeakedPreviews(MainGame game) {
		for (AnimatedSprite sprite : new java.util.ArrayList<AnimatedSprite>(
				registeredPreviews)) {
			game.Components().remove(sprite);
		}
		registeredPreviews.clear();
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
			registeredPreviews.removeAll(this.animatedSprites);
			this.animatedSprites = null;
		}
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
		if (this.screenContentLoaded) {
			return;
		}
		this.screenContentLoaded = true;
		this.componentsDetached = false;
		this.texture = LTextures.loadTexture("assets/screen_monsters.png");
		if (this.monsterInfoScreenSpriteWithText != null) {
			this.monsterInfoScreenSpriteWithText
					.setDrawOrder(Constants.INFO_OVERLAY_DRAW_ORDER);
			this.game.Components().add(this.monsterInfoScreenSpriteWithText);
		}
		this.animatedSprites = AnimatedSpriteMonster
				.GetAllAnimatedSpriteMonsters(this.game);
		for (AnimatedSprite sprite : this.animatedSprites) {
			sprite.setOnlyAnimateIfGameStateStarted(false);
			sprite.setObeyGameOpacity(false);
			sprite.setDrawOrder(50);
			this.game.Components().add(sprite);
			registeredPreviews.add(sprite);
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
				new InstructionScreen(this.game, ScreenType.MonsterInfoScreen));
	}

	@Override
	public void Update(GameTime gameTime, boolean otherScreenHasFocus,
			boolean coveredByOtherScreen) {
		if (super.getIsExiting() || coveredByOtherScreen) {
			this.detachScreenComponents();
		}
		super.Update(gameTime, otherScreenHasFocus, coveredByOtherScreen);
	}
}
