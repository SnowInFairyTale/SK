package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableGameComponent;
import loon.action.sprite.painting.IGameComponent;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.timer.GameTime;

public class Tower extends DrawableGameComponent implements IGameComponent {

	/** On-screen size for assets/towers/*.png (source atlas may be larger). */
	private static final int TOWER_SPRITE_SIZE = 128;

	/** Tap target — slightly larger than the 72×76 sprite footprint. */
	private static final int HIT_BOX_WIDTH = 80;
	private static final int HIT_BOX_HEIGHT = 84;

	/** Above missiles (drawOrder 31) so flying projectiles do not cover the bar. */
	private static final int UPGRADE_BAR_DRAW_ORDER = 50;
	private static final int UPGRADE_BAR_WIDTH = 60;
	private static final int UPGRADE_BAR_HEIGHT = 4;
	/** Below the top of the 128px tower sprite. */
	private static final float UPGRADE_BAR_OFFSET_BELOW_TOP = 26f;

	/** Left-aligned stars; 5 fit where the old 4×24px row used to sit. */
	private static final float LEVEL_STAR_START_X = 10f;
	private static final float LEVEL_STAR_Y = 70f;
	private static final int LEVEL_STAR_SIZE = 18;
	private static final float LEVEL_STAR_STEP = 15f;
	/** Full 5-star row width — equipped gem centers in this band. */
	private static final float LEVEL_STAR_ROW_WIDTH = 4f * LEVEL_STAR_STEP
			+ LEVEL_STAR_SIZE;
	/** Fine-tune equipped gem X; negative = left, positive = right. */
	private static final float EQUIPPED_GEM_OFFSET_X = 4f;

	private LTexture bashTexture;
	private int currentUpgradeLevel;
	private GemType gemType = GemType.None;
	private float elapsedTime;
	private MainGame game;
	private boolean isSelected;
	private boolean isUpgrading;
	private LTexture levelTexture;
	private LurWeapon lurWeapon;
	private Missile missile;
	private boolean obeyGameOpacity;
	private LTexture occupiedTexture;
	private LTexture occupiedTextureGreen;
	private Vector2f occupiedTexturePosition;
	private LTexture occupiedTextureRed;
	private Vector2f position;

	private LTexture texture;
	private String textureFile;
	private TowerMan towerMan;
	private ProgressBar upgradeProgressBar;
	private double upgradeTimeLeft;

	public Tower(MainGame game, TowerType towerType, Capability capability,
			String textureFile) {
		super(game);
		this.obeyGameOpacity = true;
		this.setTowerType(towerType);
		this.setCapability(capability);
		this.game = game;
		this.textureFile = textureFile;
		this.setPlaced(false);
		this.elapsedTime = 9999f;
		this.currentUpgradeLevel = 0;
		this.isSelected = false;
		this.isUpgrading = false;
		this.setPosition(game.getGameplayScreen().getLastTouchPosition());
		this.SetDrawOrder();
	}

	public final boolean CanPlace() {
		if (((this.getGridX() < 2) || (this.getGridX() >= 15))
				|| ((this.getGridY() < 0) || (this.getGridY() >= 0x12))) {
			return false;
		}
		if (this.game.getGameplayScreen().IsOccupied(this.getGridX(),
				this.getGridY(), 2)) {
			return false;
		}
		for (Vector2f point : this.game.getGameplayScreen().getLevelSettings()
				.getTowerBlockingGridCells()) {
			if ((((this.getGridX() != point.x) || (this.getGridY() != point.y)) && (((this
					.getGridX() + 1) != point.x) || (this.getGridY() != point.y)))
					&& (((this.getGridX() != point.x) || ((this.getGridY() + 1) != point.y)) && (((this
							.getGridX() + 1) != point.x) || ((this.getGridY() + 1) != point.y)))) {
				continue;
			}
			return false;
		}
		return true;
	}

	public final boolean CanUpgrade() {
		if (this.hasGem() || this.isAtMaxLevel() || this.isUpgrading
				|| !this.IsMoreUpgradeLevelsAvailable()) {
			return false;
		}
		return this.game.getGameplayScreen().getCash().getCurrentCash() >= this
				.GetUpgradeCost();
	}

	public final boolean CanSell() {
		return !this.isAtMaxLevel() && !this.hasGem();
	}

	public final boolean isAtMaxLevel() {
		return this.currentUpgradeLevel >= this.getTowerLevels().length - 1;
	}

	public final boolean hasGem() {
		return this.gemType != GemType.None;
	}

	public final GemType getGemType() {
		return this.gemType;
	}

	/** Gem type for weapon tinting; {@link GemType#None} when no gem equipped. */
	public final GemType getWeaponGemType() {
		return this.gemType;
	}

	public final boolean TryApplyGem(GemType type) {
		if (type == GemType.None || this.hasGem() || !this.isAtMaxLevel()
				|| this.isUpgrading) {
			return false;
		}
		if (!this.game.getGameplayScreen().getGems().tryConsume(type)) {
			return false;
		}
		this.gemType = type;
		this.applyGemBonuses();
		this.game.getGameplayScreen().RefreshTowerToolbar();
		return true;
	}

	private void applyGemBonuses() {
		this.SetValuesFromTowerLevel(this.currentUpgradeLevel);
		this.setDamage(this.getDamage() + this.gemType.getAttackBonus());
		if (this.gemType.appliesSpeedBonus()) {
			this.setReloadTime(this.getReloadTime() * 0.7f);
		}
	}

	public final RectBox CentralCollisionArea() {
		return new RectBox(
				(int) (this.getPosition().x - HIT_BOX_WIDTH / 2f),
				(int) (this.getPosition().y - HIT_BOX_HEIGHT / 2f),
				HIT_BOX_WIDTH, HIT_BOX_HEIGHT);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		batch.resetColor();
		if (this.occupiedTexture != null) {
			batch.draw(this.occupiedTexture, this.occupiedTexturePosition,
					this.game.getGameplayScreen().getGameOpacity());
		}
		LColor gameOpacity = this.game.getGameplayScreen().getGameOpacity();
		if (!this.obeyGameOpacity) {
			gameOpacity = LColor.white;
		}
		batch.draw(this.texture, this.getDrawPosition().x,
				this.getDrawPosition().y, TOWER_SPRITE_SIZE, TOWER_SPRITE_SIZE,
				0f, 0f, this.texture.getWidth(), this.texture.getHeight(),
				gameOpacity);
		if (this.hasGem()) {
			this.drawEquippedGem(batch, this.game.getGameplayScreen()
					.getGameOpacity());
		} else {
			int num = this.isUpgrading ? (this.currentUpgradeLevel - 1)
					: this.currentUpgradeLevel;
			this.drawLevelStars(batch, num, this.game.getGameplayScreen()
					.getGameOpacity());
		}
		if (this.isSelected) {
			int range = (int) this.getRange();
			batch.draw(this.bashTexture, position.x - range,
					position.y - range, range * 2, range * 2, 0f, 0f,
					this.bashTexture.getWidth(), this.bashTexture.getHeight(),
					gameOpacity);
		}
		super.draw(batch, gameTime);
	}

	public final int GetSellValue() {
		if (GameplayScreen.getGameState() != GameState.Started) {
			return this.getValue();
		}
		return (this.getValue() / 2);
	}

	public final Monster GetTargetMonster() {
		for (Monster monster : this.game.getGameplayScreen().getWaveManager()
				.GetAllActiveMonsters()) {
			float num = monster.getPosition().x - this.getPosition().x;
			float num2 = monster.getPosition().y - this.getPosition().y;
			float num3 = this.getRange() + monster.getRadius();
			if ((((((num * num) + (num2 * num2)) <= (num3 * num3)) && ((this
					.getCapability() != Capability.Bash) || (monster
					.getMonsterType() != MonsterType.Chicken))) && ((this
					.getCapability() != Capability.Air) || (monster
					.getMonsterType() == MonsterType.Chicken)))
					&& ((monster.getHitPoints() - monster
							.getReservedHitPoints()) > 0)) {
				return monster;
			}
		}
		return null;
	}

	public final java.util.ArrayList<Monster> GetTargetMonstersForLurWeapon() {
		java.util.ArrayList<Monster> list = new java.util.ArrayList<Monster>();
		for (Monster monster : this.game.getGameplayScreen().getWaveManager()
				.GetAllActiveMonsters()) {
			if ((monster.getMonsterType() != MonsterType.Chicken)
					&& (Utils.GetDistance(this.getPosition(),
							monster.getPosition()) < this.getRange())) {
				list.add(monster);
			}
		}
		if (list.size() <= 0) {
			return null;
		}
		return list;
	}

	public final Integer GetUpgradeCost() {
		if (this.IsMoreUpgradeLevelsAvailable()) {
			return new Integer(
					this.getTowerLevels()[this.currentUpgradeLevel + 1]
							.getCost());
		}
		return null;
	}

	public final Integer GetUpgradeDamage() {
		if (this.IsMoreUpgradeLevelsAvailable()) {
			return new Integer(
					this.getTowerLevels()[this.currentUpgradeLevel + 1]
							.getDamage() - this.getDamage());
		}
		return null;
	}

	public final Integer GetUpgradeRange() {
		if (this.IsMoreUpgradeLevelsAvailable()) {
			return new Integer(
					((int) this.getTowerLevels()[this.currentUpgradeLevel + 1]
							.getRange()) - ((int) this.getRange()));
		}
		return null;
	}

	public final boolean IsMoreUpgradeLevelsAvailable() {
		return ((this.currentUpgradeLevel + 1) < this.getTowerLevels().length);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.texture = LTextures.loadTexture(this.textureFile);
		this.bashTexture = LTextures.loadTexture("assets/range.png");
		this.occupiedTextureGreen = LTextures
				.loadTexture("assets/green_square.png");
		this.occupiedTextureRed = LTextures
				.loadTexture("assets/red_square.png");
		this.levelTexture = LTextures.loadTexture("assets/star.png");
	}

	public final void Place() {
		this.setPlaced(true);
		this.occupiedTexture = null;
		this.game.getGameplayScreen().getCash().Decrease(this.getValue());
		this.SetDrawOrder();
		switch (this.getTowerType()) {
		case Axe:
			this.towerMan = new TowerManAxe(this.game, this);
			break;

		case Spear:
			this.towerMan = new TowerManSpear(this.game, this);
			break;

		case AirDefence:
			this.towerMan = new TowerManSpear(this.game, this);
			break;

		case Lur:
			this.towerMan = new TowerManLur(this.game, this);
			break;
		}
		this.game.Components().add(this.towerMan);
	}

	public final void remove() {
		if (this.towerMan != null) {
			this.game.Components().remove(this.towerMan);
		}
		if (this.upgradeProgressBar != null) {
			this.game.Components().remove(this.upgradeProgressBar);
		}
		this.game.Components().remove(this);
	}

	public final void Sell() {
		this.game.getGameplayScreen().getCash().Increase(this.GetSellValue());
		this.remove();
	}

	private void SetDrawOrder() {
		super.setDrawOrder(8 + this.getGridY());
	}

	public final void SetInitialValue() {
		this.setValue(this.getTowerLevels()[0].getCost());
	}

	public final void SetValuesFromTowerLevel(int level) {
		this.setRange(this.getTowerLevels()[level].getRange());
		this.setReleaseTime(this.getTowerLevels()[level].getReleaseTime());
		this.setReloadTime(this.getTowerLevels()[level].getReloadTime());
		this.setDamage(this.getTowerLevels()[level].getDamage());
		this.setUpgradeCost(this.getTowerLevels()[level].getCost());
		this.setUpgradeTime(this.getTowerLevels()[level].getUpgradeTime());
	}

	private void drawEquippedGem(SpriteBatch batch, LColor opacity) {
		LTexture gemTexture = GemTextures.get(this.gemType);
		if (gemTexture == null) {
			return;
		}
		float size = LEVEL_STAR_SIZE;
		float x = this.getDrawPosition().x + LEVEL_STAR_START_X
				+ (LEVEL_STAR_ROW_WIDTH - size) / 2f + EQUIPPED_GEM_OFFSET_X;
		float y = this.getDrawPosition().y + LEVEL_STAR_Y;
		float texW = gemTexture.getWidth();
		float texH = gemTexture.getHeight();
		batch.setColor(opacity);
		batch.draw(gemTexture, x, y, size, size, 0f, 0f, texW, texH);
		batch.resetColor();
	}

	private void drawLevelStars(SpriteBatch batch, int count, LColor opacity) {
		if (count <= 0) {
			return;
		}
		float x0 = this.getDrawPosition().x + LEVEL_STAR_START_X;
		float y = this.getDrawPosition().y + LEVEL_STAR_Y;
		float texW = this.levelTexture.getWidth();
		float texH = this.levelTexture.getHeight();
		for (int i = 0; i < count; i++) {
			batch.draw(this.levelTexture, x0 + (i * LEVEL_STAR_STEP), y,
					LEVEL_STAR_SIZE, LEVEL_STAR_SIZE, 0f, 0f, texW, texH,
					opacity);
		}
	}

	public final void StartedSelection() {
		this.isSelected = true;
		this.obeyGameOpacity = false;
		this.syncUpgradeProgressBarOpacity();
	}

	private void layoutUpgradeProgressBar() {
		if (this.upgradeProgressBar == null) {
			return;
		}
		this.upgradeProgressBar.setHeight(UPGRADE_BAR_HEIGHT);
		float barX = this.getPosition().x - UPGRADE_BAR_WIDTH / 2f;
		float barY = this.getDrawPosition().y + UPGRADE_BAR_OFFSET_BELOW_TOP;
		this.upgradeProgressBar.setPosition(new Vector2f(barX, barY));
	}

	private void StartUpgrade() {
		this.currentUpgradeLevel++;
		this.SetValuesFromTowerLevel(this.currentUpgradeLevel);
		this.game.getGameplayScreen().getCash().Decrease(this.getUpgradeCost());
		this.setValue(this.getValue() + this.getUpgradeCost());
		if (GameplayScreen.getGameState() == GameState.Started) {
			this.upgradeProgressBar = new ProgressBar(this.game,
					UPGRADE_BAR_WIDTH, false);
			this.layoutUpgradeProgressBar();
			this.upgradeProgressBar.setCurrentPercent(0);
			this.syncUpgradeProgressBarOpacity();
			this.upgradeProgressBar.setDrawOrder(UPGRADE_BAR_DRAW_ORDER);
			this.game.Components().add(this.upgradeProgressBar);
			this.isUpgrading = true;
			this.upgradeTimeLeft = this.getUpgradeTime() * 1000f;
		} else {
			this.UpgradeCompleted();
		}
	}

	public final void StoppedSelection() {
		this.isSelected = false;
		this.obeyGameOpacity = true;
		this.syncUpgradeProgressBarOpacity();
	}

	private void syncUpgradeProgressBarOpacity() {
		if (this.upgradeProgressBar != null) {
			this.upgradeProgressBar.setObeyGameOpacity(!this.isSelected);
		}
	}

	@Override
	public void update(GameTime gameTime) {
		super.update(gameTime);
		this.elapsedTime += 0.03333334f;
		if (!this.getPlaced()) {
			Vector2f vector = new Vector2f(50f, -80f);
			Vector2f point = Utils.ConvertToGridPoint(this.game
					.getGameplayScreen().getLastTouchPosition().add(vector));
			this.setGridX(point.x());
			this.setGridY(point.y());
			this.setPosition(Utils.ConvertToPositionCoordinates(
					new Vector2f(this.getGridX(), this.getGridY())).add(40f,
					40f));
			this.occupiedTexturePosition = new Vector2f(
					this.getPosition().x - 40f, this.getPosition().y - 40f);
			this.occupiedTexture = this.CanPlace() ? this.occupiedTextureGreen
					: this.occupiedTextureRed;
			this.SetDrawOrder();
		} else if (GameplayScreen.getGameState() == GameState.Started) {
			if ((this.missile != null) && this.missile.getHasHitTarget()) {
				this.missile = null;
			}
			if ((this.lurWeapon != null) && this.lurWeapon.getHasHitTarget()) {
				this.lurWeapon = null;
			}
			if (this.isUpgrading) {
				this.upgradeTimeLeft -= gameTime.getMilliseconds();
				float duration = this.getUpgradeTime();
				if (duration > 0f) {
					int percent = (int) Math.min(100, Math.max(0, Math.round(
							(1.0 - this.upgradeTimeLeft / (duration * 1000.0)) * 100.0)));
					this.upgradeProgressBar.setCurrentPercent(percent);
				}
				if (this.upgradeTimeLeft <= 0.0) {
					this.UpgradeCompleted();
				}
			}
			if ((this.elapsedTime > this.getReloadTime()) && !this.isUpgrading) {
				if (this.getTowerType() == TowerType.Lur) {
					java.util.ArrayList<Monster> targetMonstersForLurWeapon = this
							.GetTargetMonstersForLurWeapon();
					if ((targetMonstersForLurWeapon != null)
							&& (this.lurWeapon == null)) {
						this.lurWeapon = new LurWeapon(this.game, this,
								targetMonstersForLurWeapon);
						super.getGame().Components().add(this.lurWeapon);
						this.towerMan.PlayAnimation();
						this.elapsedTime = 0f;
					}
				} else if (this.missile == null) {
					Monster targetMonster = this.GetTargetMonster();
					if (targetMonster != null) {
						switch (this.getTowerType()) {
						case Axe:
							this.missile = new MissileAxe(this.game,
									targetMonster, this);
							break;

						case Spear:
							this.missile = new MissileSpear(this.game,
									targetMonster, this);
							break;

						case AirDefence:
							this.missile = new MissileSpear(this.game,
									targetMonster, this);
							break;
						default:
							break;
						}
						super.getGame().Components().add(this.missile);
						this.towerMan.UpdateThrowDirection(this.missile
								.getDirection());
						this.towerMan.PlayAnimation();
						this.elapsedTime = 0f;
					}
				}
			}
		}
	}

	public final void Upgrade() {
		if (this.CanUpgrade()) {
			this.StartUpgrade();
		}
	}

	private void UpgradeCompleted() {
		this.game.Components().remove(this.upgradeProgressBar);
		this.upgradeProgressBar = null;
		this.game.getGameplayScreen().UpdateUpgradeButtonState();
		this.isUpgrading = false;
	}

	private Capability privateCapability;

	public final Capability getCapability() {
		return privateCapability;
	}

	public final void setCapability(Capability value) {
		privateCapability = value;
	}

	private int privateDamage;

	public final int getDamage() {
		return privateDamage;
	}

	public final void setDamage(int value) {
		privateDamage = value;
	}

	private Vector2f privateDrawPosition;

	public final Vector2f getDrawPosition() {
		return privateDrawPosition;
	}

	public final void setDrawPosition(Vector2f value) {
		privateDrawPosition = value;
	}

	private int privateGridX;

	public final int getGridX() {
		return privateGridX;
	}

	public final void setGridX(int value) {
		privateGridX = value;
	}

	private int privateGridY;

	public final int getGridY() {
		return privateGridY;
	}

	public final void setGridY(int value) {
		privateGridY = value;
	}

	private boolean privatePlaced;

	public final boolean getPlaced() {
		return privatePlaced;
	}

	public final void setPlaced(boolean value) {
		privatePlaced = value;
	}

	public final Vector2f getPosition() {
		return this.position;
	}

	public final void setPosition(Vector2f value) {
		this.position = value;
		this.setDrawPosition(new Vector2f(value.x - 52f, value.y - 76f));
	}

	private float privateRange;

	public final float getRange() {
		return privateRange;
	}

	public final void setRange(float value) {
		privateRange = value;
	}

	private float privateReleaseTime;

	public final float getReleaseTime() {
		return privateReleaseTime;
	}

	public final void setReleaseTime(float value) {
		privateReleaseTime = value;
	}

	private float privateReloadTime;

	public final float getReloadTime() {
		return privateReloadTime;
	}

	public final void setReloadTime(float value) {
		privateReloadTime = value;
	}

	private TowerLevel[] privateTowerLevels;

	protected final TowerLevel[] getTowerLevels() {
		return privateTowerLevels;
	}

	protected final void setTowerLevels(TowerLevel[] value) {
		privateTowerLevels = value;
	}

	private TowerType privateTowerType;

	public final TowerType getTowerType() {
		return privateTowerType;
	}

	public final void setTowerType(TowerType value) {
		privateTowerType = value;
	}

	private int privateUpgradeCost;

	public final int getUpgradeCost() {
		return privateUpgradeCost;
	}

	public final void setUpgradeCost(int value) {
		privateUpgradeCost = value;
	}

	private float privateUpgradeTime;

	public final float getUpgradeTime() {
		return privateUpgradeTime;
	}

	public final void setUpgradeTime(float value) {
		privateUpgradeTime = value;
	}

	private int privateValue;

	public final int getValue() {
		return privateValue;
	}

	public final void setValue(int value) {
		privateValue = value;
	}
}