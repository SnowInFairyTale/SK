package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableGameComponent;
import loon.action.sprite.painting.IGameComponent;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.timer.GameTime;

public class TowerToolbar extends DrawableGameComponent implements
		IGameComponent {

	private static final float TOOLBAR_H = 120f;
	private static final float POWER_SRC_W = 478f;
	private static final float SHELL_SRC_X = 518f;
	private static final float SHELL_SRC_W = 122f;

	private static final float UPGRADE_PANEL_X = 300f;
	private static final float SELL_SLOT_X = 518f;
	private static final float SELL_VALUE_CENTER_X = 562f;
	private static final int UPGRADE_FRAME_W = 220;
	private static final int UPGRADE_FRAME_H = 120;
	private static final float BUILD_FRAME_SRC_W = 160f;
	private static final float BUILD_FRAME_SRC_H = 120f;
	/** Scaled width when picking a gem — keeps three slots evenly spaced. */
	private static final float GEM_FRAME_DST_W = 100f;
	private static final float GEM_FRAME_DST_H = 120f;
	private static final float GEM_SLOT_START_X = 300f;
	private static final float GEM_SLOT_GAP = 5f;
	private static final float GEM_SLOT_ICON_SIZE = 44f;
	private static final float GEM_INFO_ICON_SIZE = 56f;
	private static final float GEM_INFO_ICON_OFFSET_X = -24f;
	private static final float GEM_INFO_ICON_OFFSET_Y = 0f;
	/** Extra width for equipped-gem display panel only (not selection slots). */
	private static final float GEM_INFO_PANEL_EXTRA_W = 32f;
	/** Equipped-gem panel spans upgrade + sell area (power frame style). */
	private static final float GEM_INFO_PANEL_W = SELL_SLOT_X + SHELL_SRC_W
			- UPGRADE_PANEL_X + GEM_INFO_PANEL_EXTRA_W;

	private AnimatedSprite animatedSpriteTower;
	private boolean canUpgrade;
	private Vector2f drawPosition;
	private Vector2f drawPositionUpgradeButton;
	private LFont font;
	private Gems gems;
	private MainGame game;
	private RectBox sellButtonArea;
	private LTexture textureBuildFrame;
	private LTexture textureToolbarAtlas;
	private LTexture textureUpgradeButton;
	private LTexture textureUpgradeButtonActive;
	private LTexture textureUpgradeButtonInactive;
	private Tower tower;
	private LColor upgradeButtonGreenColor;
	private LColor upgradeButtonRedColor;
	private LColor upgradeButtonWhiteColor;

	public TowerToolbar(MainGame game, Tower tower) {
		super(game);
		this.game = game;
		this.tower = tower;
		this.gems = game.getGameplayScreen().getGems();
		this.drawPosition = new Vector2f(Constants.TOWER_TOOLBAR_X,
				Constants.TOWER_TOOLBAR_Y);
		this.drawPositionUpgradeButton = this.drawPosition
				.add(UPGRADE_PANEL_X, 0f);
		this.animatedSpriteTower = AnimatedSpriteTower
				.GetAnimatedSpriteTowerForTowerToolbar(game, this.drawPosition,
						tower.getTowerType(), 1f);
		this.animatedSpriteTower.setDrawOrder(0x1d);
		this.animatedSpriteTower.setAnimationSpeedRatio(3);
		this.animatedSpriteTower.setObeyGameOpacity(false);
		this.animatedSpriteTower.setOnlyAnimateIfGameStateStarted(false);
		super.setDrawOrder(1);
		this.sellButtonArea = new RectBox(
				(int) (this.drawPosition.x + SELL_SLOT_X),
				(int) this.drawPosition.y, (int) SHELL_SRC_W, (int) TOOLBAR_H);
	}

	public final RectBox CentralCollisionAreaSellButton() {
		return this.sellButtonArea;
	}

	private final RectBox[] gemSlotAreas = new RectBox[GemType.equippable().length];

	public final GemType GetGemSlotAt(RectBox point) {
		if (!this.isGemSelectionMode()) {
			return GemType.None;
		}
		GemType[] types = GemType.equippable();
		for (int i = 0; i < types.length; i++) {
			if (this.gemSlotAreas[i] != null
					&& this.gemSlotAreas[i].intersects(point)) {
				return types[i];
			}
		}
		return GemType.None;
	}

	public final boolean isGemSelectionMode() {
		return this.tower.isAtMaxLevel() && !this.tower.hasGem();
	}

	public final boolean isGemInfoMode() {
		return this.tower.hasGem();
	}

	private RectBox rect = new RectBox();

	public final RectBox CentralCollisionAreaUpgradeButton() {
		rect.setBounds(this.drawPositionUpgradeButton.x,
				this.drawPositionUpgradeButton.y, UPGRADE_FRAME_W,
				UPGRADE_FRAME_H);
		return rect;
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		batch.resetColor();
		this.drawPowerPanel(batch, this.drawPosition.x, this.drawPosition.y,
				POWER_SRC_W, TOOLBAR_H);
		// Draw tower preview before gem slots so SpriteBatch never binds gem
		// textures first and then reuses that state for the tower sprite.
		this.animatedSpriteTower.initialize();
		this.animatedSpriteTower.draw(batch, gameTime);
		batch.drawString(this.font, LanguageResources.getAttackInterval(),
				this.drawPosition.x + 128f, this.drawPosition.y + 66f,
				this.upgradeButtonWhiteColor);
		Utils.DrawStringAlignCenter(batch, this.font,
				formatAttackInterval(this.tower.getReloadTime()),
				this.drawPosition.add(240f, 66f),
				this.upgradeButtonGreenColor);
		batch.drawString(this.font, LanguageResources.getPower(),
				this.drawPosition.add(128f, 30f), this.upgradeButtonWhiteColor);
		Utils.DrawStringAlignCenter(batch, this.font,
				"" + this.tower.getDamage(), this.drawPosition.add(240f, 30f),
				this.upgradeButtonRedColor);

		if (this.isGemSelectionMode()) {
			this.drawGemSelection(batch);
		} else if (this.isGemInfoMode()) {
			this.drawGemInfo(batch);
		} else {
			this.drawUpgradeAndSell(batch);
		}
		batch.resetColor();
		super.draw(batch, gameTime);
	}

	private static String formatAttackInterval(float seconds) {
		return String.format("%.1f", seconds);
	}

	private void drawPowerPanel(SpriteBatch batch, float x, float y,
			float dstW, float dstH) {
		batch.draw(this.textureToolbarAtlas, x, y, dstW, dstH, 0f, 0f,
				POWER_SRC_W, TOOLBAR_H, LColor.white);
	}

	private void drawShellFrame(SpriteBatch batch, float x, float y, LColor tint) {
		batch.draw(this.textureToolbarAtlas, x, y, SHELL_SRC_W, TOOLBAR_H,
				SHELL_SRC_X, 0f, SHELL_SRC_W, TOOLBAR_H, tint);
	}

	private void drawBuildFrame(SpriteBatch batch, float x, float y, float dstW,
			float dstH, LColor tint) {
		this.drawTintedTexture(batch, this.textureBuildFrame, x, y, dstW, dstH,
				0f, 0f, BUILD_FRAME_SRC_W, BUILD_FRAME_SRC_H, tint);
	}

	/** Always sets batch color — avoids SpriteBatch white-skip leaking between slots. */
	private void drawTintedTexture(SpriteBatch batch, LTexture texture, float x,
			float y, float dstW, float dstH, float srcX, float srcY,
			float srcW, float srcH, LColor tint) {
		batch.setColor(tint);
		batch.draw(texture, x, y, dstW, dstH, srcX, srcY, srcW, srcH);
		batch.resetColor();
	}

	private float gemSlotX(int index) {
		return this.drawPosition.x + GEM_SLOT_START_X
				+ index * (GEM_FRAME_DST_W + GEM_SLOT_GAP);
	}

	private void drawUpgradeAndSell(SpriteBatch batch) {
		batch.draw(this.textureUpgradeButton, this.drawPositionUpgradeButton,
				LColor.white);
		batch.drawString(this.font, LanguageResources.getUpgrade(),
				this.drawPositionUpgradeButton.add(46f, 30f),
				this.upgradeButtonWhiteColor);
		float sellX = this.drawPosition.x + SELL_SLOT_X;
		float sellY = this.drawPosition.y;
		this.drawShellFrame(batch, sellX, sellY, LColor.white);
		batch.drawString(this.font, LanguageResources.getSell(),
				this.drawPosition.add(548f, 30f), LColor.white);
		Utils.DrawStringAlignCenter(batch, this.font,
				"" + this.tower.GetSellValue(),
				this.drawPosition.add(SELL_VALUE_CENTER_X, 66f), LColor.white);
		if (this.tower.IsMoreUpgradeLevelsAvailable()) {
			Utils.DrawStringAlignCenter(batch, this.font, this.tower
					.GetUpgradeCost().toString(),
					this.drawPositionUpgradeButton.add(154f, 30f),
					this.upgradeButtonWhiteColor);
			Utils.DrawStringAlignCenter(batch, this.font, this.tower
					.GetUpgradeDamage().toString(),
					this.drawPositionUpgradeButton.add(88f, 66f),
					this.upgradeButtonRedColor);
			Utils.DrawStringAlignCenter(batch, this.font, this.tower
					.GetUpgradeRange().toString(),
					this.drawPositionUpgradeButton.add(148f, 66f),
					this.upgradeButtonGreenColor);
		}
	}

	private void drawGemSelection(SpriteBatch batch) {
		batch.resetColor();
		GemType[] types = GemType.equippable();
		for (int i = 0; i < types.length; i++) {
			this.drawGemSlot(batch, i, types[i]);
		}
	}

	private void drawGemInfo(SpriteBatch batch) {
		float panelX = this.drawPositionUpgradeButton.x;
		float panelY = this.drawPositionUpgradeButton.y;
		this.drawPowerPanel(batch, panelX, panelY, GEM_INFO_PANEL_W,
				UPGRADE_FRAME_H);
		batch.drawString(this.font, LanguageResources.getGem(), panelX + 46f,
				panelY + 30f, LColor.white);
		float iconX = panelX + (GEM_INFO_PANEL_W - GEM_INFO_ICON_SIZE) / 2f
				+ GEM_INFO_ICON_OFFSET_X;
		float iconY = panelY + (UPGRADE_FRAME_H - GEM_INFO_ICON_SIZE) / 2f
				+ GEM_INFO_ICON_OFFSET_Y;
		this.drawGemIcon(batch, this.tower.getGemType(), iconX, iconY,
				GEM_INFO_ICON_SIZE, LColor.white);
	}

	private LColor gemSlotTint(boolean available) {
		if (available) {
			return LColor.white;
		}
		return this.game.getGameplayScreen().getGameOpacity();
	}

	private void drawGemSlot(SpriteBatch batch, int index, GemType type) {
		float x = this.gemSlotX(index);
		float y = this.drawPosition.y;
		boolean available = this.gems.getCount(type) > 0;
		LColor tint = this.gemSlotTint(available);
		this.drawBuildFrame(batch, x, y, GEM_FRAME_DST_W, GEM_FRAME_DST_H, tint);
		float iconX = x + (GEM_FRAME_DST_W - GEM_SLOT_ICON_SIZE) / 2f;
		float iconY = y + (GEM_FRAME_DST_H - GEM_SLOT_ICON_SIZE) / 2f;
		this.drawGemIcon(batch, type, iconX, iconY, GEM_SLOT_ICON_SIZE, available);
		this.gemSlotAreas[index] = new RectBox((int) x, (int) y,
				(int) GEM_FRAME_DST_W, (int) GEM_FRAME_DST_H);
	}

	private void drawGemIcon(SpriteBatch batch, GemType type, float x, float y,
			float size, boolean available) {
		LTexture gemTexture = this.getGemTexture(type, available);
		if (gemTexture == null) {
			return;
		}
		this.drawTintedTexture(batch, gemTexture, x, y, size, size, 0f, 0f,
				gemTexture.getWidth(), gemTexture.getHeight(), LColor.white);
	}

	private void drawGemIcon(SpriteBatch batch, GemType type, float x, float y,
			float size, LColor tint) {
		this.drawGemIcon(batch, type, x, y, size, true);
	}

	private LTexture getGemTexture(GemType type, boolean available) {
		return available ? GemTextures.get(type) : GemTextures.getDim(type);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.animatedSpriteTower.initialize();
		this.textureToolbarAtlas = LTextures
				.loadTexture("assets/tower_toolbar.png");
		this.textureBuildFrame = LTextures
				.loadTexture("assets/build_toolbar.png");
		this.textureUpgradeButtonActive = LTextures
				.loadTexture("assets/button_upgrade.png");
		this.textureUpgradeButtonInactive = LTextures
				.loadTexture("assets/button_upgrade_greyed.png");
		GemTextures.ensureLoaded();
		this.font = Constants.font(18);
		this.SetUpgradeButtonState();
	}

	public final void Remove() {
		this.game.Components().remove(this);
	}

	public final void SetUpgradeButtonState() {
		this.gems = this.game.getGameplayScreen().getGems();
		this.canUpgrade = this.tower.CanUpgrade();
		this.textureUpgradeButton = this.canUpgrade ? this.textureUpgradeButtonActive
				: this.textureUpgradeButtonInactive;
		this.upgradeButtonWhiteColor = this.canUpgrade ? LColor.white
				: this.game.getGameplayScreen().getGameOpacity();
		this.upgradeButtonGreenColor = this.canUpgrade ? new LColor(0f, 1f, 0f,
				1f) : new LColor(0f, 0.7f, 0f, 1f);
		this.upgradeButtonRedColor = this.canUpgrade ? LColor.red
				: LColor.darkGray;
	}

	@Override
	public void update(GameTime gameTime) {
		this.animatedSpriteTower.update(gameTime);
		super.update(gameTime);
	}

	private boolean privateShowActiveUpgradeButton;

	public final boolean getShowActiveUpgradeButton() {
		return privateShowActiveUpgradeButton;
	}

	public final void setShowActiveUpgradeButton(boolean value) {
		privateShowActiveUpgradeButton = value;
	}
}
