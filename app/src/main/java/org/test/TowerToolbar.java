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

	private AnimatedSprite animatedSpriteTower;
	private boolean canUpgrade;
	private Vector2f drawPosition;
	private Vector2f drawPositionUpgradeButton;
	private LFont font;
	private MainGame game;
	private RectBox sellButtonArea;
	private LTexture texture;
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
		this.drawPosition = new Vector2f(Constants.TOWER_TOOLBAR_X,
				Constants.TOWER_TOOLBAR_Y);
		this.drawPositionUpgradeButton = this.drawPosition.add(300f, 0f);
		this.animatedSpriteTower = AnimatedSpriteTower
				.GetAnimatedSpriteTowerForTowerToolbar(game, this.drawPosition,
						tower.getTowerType(), 1f);
		this.animatedSpriteTower.setDrawOrder(0x1d);
		this.animatedSpriteTower.setAnimationSpeedRatio(3);
		this.animatedSpriteTower.setObeyGameOpacity(false);
		this.animatedSpriteTower.setOnlyAnimateIfGameStateStarted(false);
		super.setDrawOrder(1);
		this.sellButtonArea = new RectBox(((int) this.drawPosition.x) + 0x212,
				((int) this.drawPosition.y) + 10, 0x5a, 0x5a);
		game.Components().add(this.animatedSpriteTower);
	}

	public final RectBox CentralCollisionAreaSellButton() {
		return this.sellButtonArea;
	}

	private RectBox rect = new RectBox();

	public final RectBox CentralCollisionAreaUpgradeButton() {
		rect.setBounds(this.drawPositionUpgradeButton.x,
				this.drawPositionUpgradeButton.y,
				this.textureUpgradeButton.getWidth(),
				this.textureUpgradeButton.getHeight());
		return rect;
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		batch.draw(this.texture, this.drawPosition, LColor.white);
		batch.draw(this.textureUpgradeButton, this.drawPositionUpgradeButton,
				LColor.white);
		batch.drawString(this.font, LanguageResources.getRange(),
				this.drawPosition.x + 128f, this.drawPosition.y + 66f,
				this.upgradeButtonWhiteColor);
		Utils.DrawStringAlignCenter(batch, this.font,
				"" + this.tower.getRange(), this.drawPosition.add(240f, 66f),
				this.upgradeButtonGreenColor);
		batch.drawString(this.font, LanguageResources.getPower(),
				this.drawPosition.add(128f, 30f), this.upgradeButtonWhiteColor);
		Utils.DrawStringAlignCenter(batch, this.font,
				"" + this.tower.getDamage(), this.drawPosition.add(240f, 30f),
				this.upgradeButtonRedColor);
		batch.drawString(this.font, LanguageResources.getUpgrade(),
				this.drawPositionUpgradeButton.add(46f, 30f),
				this.upgradeButtonWhiteColor);
		batch.drawString(this.font, LanguageResources.getSell(),
				this.drawPosition.add(548f, 30f), LColor.white);
		Utils.DrawStringAlignCenter(batch, this.font,
				"" + this.tower.GetSellValue(),
				this.drawPosition.add(574f, 66f), LColor.white);
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
		super.draw(batch, gameTime);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.texture = LTextures.loadTexture("assets/tower_toolbar.png");
		this.textureUpgradeButtonActive = LTextures
				.loadTexture("assets/button_upgrade.png");
		this.textureUpgradeButtonInactive = LTextures
				.loadTexture("assets/button_upgrade_greyed.png");
		this.font = Constants.font(18);
		this.SetUpgradeButtonState();
	}

	public final void Remove() {
		this.game.Components().remove(this.animatedSpriteTower);
		this.game.Components().remove(this);
	}

	public final void SetUpgradeButtonState() {
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