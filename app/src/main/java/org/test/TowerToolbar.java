package org.test;

import com.loon.action.sprite.SpriteBatch;
import com.loon.action.sprite.painting.DrawableGameComponent;
import com.loon.action.sprite.painting.IGameComponent;
import com.loon.core.geom.RectBox;
import com.loon.core.geom.Vector2f;
import com.loon.core.graphics.LColor;
import com.loon.core.graphics.LFont;
import com.loon.core.graphics.opengl.LTexture;
import com.loon.core.graphics.opengl.LTextures;
import com.loon.core.timer.GameTime;

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
	private final CachedText attackIntervalLabel = new CachedText();
	private final CachedText attackIntervalValueLabel = new CachedText();
	private final CachedText powerLabel = new CachedText();
	private final CachedText powerValueLabel = new CachedText();
	private final CachedText upgradeLabel = new CachedText();
	private final CachedText sellLabel = new CachedText();
	private final CachedText sellValueLabel = new CachedText();
	private final CachedText upgradeCostLabel = new CachedText();
	private final CachedText upgradeDamageLabel = new CachedText();
	private final CachedText upgradeRangeLabel = new CachedText();
	private final CachedText gemLabel = new CachedText();

	private static final class CachedText {
		private String text;
		private TextSprite sprite;

		void set(LFont font, String value) {
			if (value == null) {
				value = "";
			}
			if (value.equals(this.text)) {
				return;
			}
			this.dispose();
			this.text = value;
			this.sprite = TextSprite.create(font, value);
		}

		void drawLeft(SpriteBatch batch, float x, float y, LColor color) {
			if (this.sprite != null) {
				this.sprite.drawLeft(batch, x, y, color);
			}
		}

		void drawCentered(SpriteBatch batch, float centerX, float y,
				LColor color) {
			if (this.sprite != null) {
				this.sprite.drawCentered(batch, centerX, y, color);
			}
		}

		void dispose() {
			if (this.sprite != null) {
				this.sprite.dispose();
				this.sprite = null;
			}
			this.text = null;
		}
	}

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
		this.refreshDynamicTextSprites();
		this.drawToolbarLabel(batch, this.attackIntervalLabel,
				this.drawPosition.x + 128f, this.drawPosition.y + 66f,
				this.upgradeButtonWhiteColor);
		this.drawToolbarLabelCentered(batch, this.attackIntervalValueLabel,
				this.drawPosition.x + 240f, this.drawPosition.y + 66f,
				this.upgradeButtonGreenColor);
		this.drawToolbarLabel(batch, this.powerLabel,
				this.drawPosition.x + 128f, this.drawPosition.y + 30f,
				this.upgradeButtonWhiteColor);
		this.drawToolbarLabelCentered(batch, this.powerValueLabel,
				this.drawPosition.x + 240f, this.drawPosition.y + 30f,
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

	/** Flush batch before labels — avoids jar SpriteBatch / drawString state bleed. */
	private void drawToolbarLabel(SpriteBatch batch, CachedText text, float x,
			float y, LColor color) {
		batch.flush();
		batch.resetColor();
		text.drawLeft(batch, x, this.textTop(y), color);
	}

	private void drawToolbarLabelCentered(SpriteBatch batch, CachedText text,
			float centerX, float centerY, LColor color) {
		batch.flush();
		batch.resetColor();
		text.drawCentered(batch, centerX, this.textTop(centerY), color);
	}

	private static String formatAttackInterval(float seconds) {
		return String.format("%.1f", seconds);
	}

	private float textTop(float drawStringY) {
		return drawStringY + this.font.getHeight()
				- Constants.TEXT_LABEL_DRAWSTRING_HEIGHT_ADJUST
				+ this.font.getAscent();
	}

	private void buildStaticTextSprites() {
		this.attackIntervalLabel.set(this.font,
				LanguageResources.getAttackInterval());
		this.powerLabel.set(this.font, LanguageResources.getPower());
		this.upgradeLabel.set(this.font, LanguageResources.getUpgrade());
		this.sellLabel.set(this.font, LanguageResources.getSell());
		this.gemLabel.set(this.font, LanguageResources.getGem());
	}

	private void refreshDynamicTextSprites() {
		if (this.font == null) {
			return;
		}
		this.attackIntervalValueLabel.set(this.font,
				formatAttackInterval(this.tower.getReloadTime()));
		this.powerValueLabel.set(this.font, "" + this.tower.getDamage());
		this.sellValueLabel.set(this.font, "" + this.tower.GetSellValue());
		if (this.tower.IsMoreUpgradeLevelsAvailable()) {
			this.upgradeCostLabel.set(this.font, this.tower.GetUpgradeCost()
					.toString());
			this.upgradeDamageLabel.set(this.font, this.tower
					.GetUpgradeDamage().toString());
			this.upgradeRangeLabel.set(this.font, this.tower.GetUpgradeRange()
					.toString());
		}
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
		this.drawToolbarLabel(batch, this.upgradeLabel,
				this.drawPositionUpgradeButton.x + 46f,
				this.drawPositionUpgradeButton.y + 30f,
				this.upgradeButtonWhiteColor);
		float sellX = this.drawPosition.x + SELL_SLOT_X;
		float sellY = this.drawPosition.y;
		this.drawShellFrame(batch, sellX, sellY, LColor.white);
		this.drawToolbarLabel(batch, this.sellLabel,
				this.drawPosition.x + 548f, this.drawPosition.y + 30f,
				LColor.white);
		this.drawToolbarLabelCentered(batch, this.sellValueLabel,
				this.drawPosition.x + SELL_VALUE_CENTER_X,
				this.drawPosition.y + 66f, LColor.white);
		if (this.tower.IsMoreUpgradeLevelsAvailable()) {
			this.drawToolbarLabelCentered(batch, this.upgradeCostLabel,
					this.drawPositionUpgradeButton.x + 154f,
					this.drawPositionUpgradeButton.y + 30f,
					this.upgradeButtonWhiteColor);
			this.drawToolbarLabelCentered(batch, this.upgradeDamageLabel,
					this.drawPositionUpgradeButton.x + 88f,
					this.drawPositionUpgradeButton.y + 66f,
					this.upgradeButtonRedColor);
			this.drawToolbarLabelCentered(batch, this.upgradeRangeLabel,
					this.drawPositionUpgradeButton.x + 148f,
					this.drawPositionUpgradeButton.y + 66f,
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
		this.drawToolbarLabel(batch, this.gemLabel, panelX + 46f,
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
		this.buildStaticTextSprites();
		this.SetUpgradeButtonState();
	}

	public final void Remove() {
		this.disposeTextSprites();
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
		this.refreshDynamicTextSprites();
	}

	private void disposeTextSprites() {
		this.attackIntervalLabel.dispose();
		this.attackIntervalValueLabel.dispose();
		this.powerLabel.dispose();
		this.powerValueLabel.dispose();
		this.upgradeLabel.dispose();
		this.sellLabel.dispose();
		this.sellValueLabel.dispose();
		this.upgradeCostLabel.dispose();
		this.upgradeDamageLabel.dispose();
		this.upgradeRangeLabel.dispose();
		this.gemLabel.dispose();
	}

	@Override
	protected void dispose(boolean disposing) {
		this.disposeTextSprites();
		super.dispose(disposing);
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
