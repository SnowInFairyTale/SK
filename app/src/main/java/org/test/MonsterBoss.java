package org.test;

import com.loon.action.sprite.SpriteBatch;
import com.loon.action.sprite.SpriteBatch.SpriteEffects;
import com.loon.core.geom.RectBox;
import com.loon.core.geom.Vector2f;
import com.loon.core.graphics.LColor;
import com.loon.core.graphics.opengl.LTexture;
import com.loon.core.timer.GameTime;

public class MonsterBoss extends Monster {

	private static final float SHIELD_FRAME_A_SCALE = 1f;
	private static final float SHIELD_FRAME_B_SCALE = 0.93f;
	private static final float SHIELD_FRAME_A_ROTATION = 0f;
	private static final float SHIELD_FRAME_B_ROTATION = 72f;
	private static final double SHIELD_FRAME_MS = 460.0;
	private static final float SHIELD_INNER_DIAMETER_RATIO = 0.44f;
	private static final float SHIELD_SIZE_MULTIPLIER = 0.59f;
	private static final String SHIELD_TEXTURE = "assets/boss_shield.png";

	private MainGame game;
	private final LColor shieldColor;
	private int shieldFrame;
	private final RectBox shieldRect = new RectBox();
	private final Vector2f shieldOrigin = new Vector2f();
	private double shieldFrameTime;
	private float shieldScale;
	private LTexture shieldTexture;
	private Vector2f chickenPathResult = new Vector2f();

	public static MonsterBoss Create(MainGame game, Wave wave, MonsterType type,
			float speed, int startHitPoints, int value) {
		return CreateWithShield(game, wave, type, speed, startHitPoints, value,
				BossShieldColors.GOLD, null);
	}

	public static MonsterBoss CreateWithShield(MainGame game, Wave wave,
			MonsterType type, float speed, int startHitPoints, int value,
			LColor shieldColor, Vector2f gridPosition) {
		switch (type) {
		case Peasant:
			return spawn(game, wave, speed, startHitPoints, value,
					"assets/peasant.png", 8, 8, 48, 48, 10f, type, shieldColor,
					gridPosition);
		case Peon:
			return spawn(game, wave, speed, startHitPoints, value,
					"assets/peon.png", 8, 8, 48, 48, 10f, type, shieldColor,
					gridPosition);
		case Berserker:
			return spawn(game, wave, speed, startHitPoints, value,
					"assets/berserker.png", 8, 8, 64, 64, 10f, type,
					shieldColor, gridPosition);
		case Chicken:
			return spawn(game, wave, speed, startHitPoints, value,
					"assets/chicken.png", 8, 8, 64, 64, 12f, type, shieldColor,
					gridPosition);
		case Doctor:
			return spawn(game, wave, speed, startHitPoints, value,
					"assets/doctor.png", 8, 8, 80, 80, 16f, type, shieldColor,
					gridPosition);
		case Chieftain:
			return spawn(game, wave, speed, startHitPoints, value,
					"assets/chieftain.png", 8, 13, 78, 78, 16f, type,
					shieldColor, gridPosition);
		default:
			throw new RuntimeException("Unsupported boss monster type: " + type);
		}
	}

	private static MonsterBoss spawn(MainGame game, Wave wave, float speed,
			int startHitPoints, int value, String textureFile, int columnCount,
			int spriteCount, int spriteHeight, int spriteWidth, float radius,
			MonsterType type, LColor shieldColor, Vector2f gridPosition) {
		if (gridPosition != null) {
			return new MonsterBoss(game, wave, speed, startHitPoints, value,
					textureFile, columnCount, spriteCount, spriteHeight,
					spriteWidth, radius, type, shieldColor, gridPosition);
		}
		return new MonsterBoss(game, wave, speed, startHitPoints, value,
				textureFile, columnCount, spriteCount, spriteHeight,
				spriteWidth, radius, type, shieldColor);
	}

	private MonsterBoss(MainGame game, Wave wave, float speed, int startHitPoints,
			int value, String textureFile, int columnCount, int spriteCount,
			int spriteHeight, int spriteWidth, float radius, MonsterType type,
			LColor shieldColor) {
		super(game, wave, startHitPoints, speed, value, textureFile, columnCount,
				spriteCount, spriteHeight, spriteWidth);
		this.game = game;
		this.shieldColor = shieldColor;
		this.initBoss(type, radius);
	}

	private MonsterBoss(MainGame game, Wave wave, float speed, int startHitPoints,
			int value, String textureFile, int columnCount, int spriteCount,
			int spriteHeight, int spriteWidth, float radius, MonsterType type,
			LColor shieldColor, Vector2f gridPosition) {
		super(game, wave, startHitPoints, speed, value, textureFile, columnCount,
				spriteCount, spriteHeight, spriteWidth, gridPosition);
		this.game = game;
		this.shieldColor = shieldColor;
		this.initBoss(type, radius);
	}

	private void initBoss(MonsterType type, float radius) {
		this.shieldFrameTime = SHIELD_FRAME_MS;
		super.setMonsterType(type);
		super.setRadius(radius);
		super.setAnimationSpeedRatio(3);
		if (type == MonsterType.Chicken) {
			super.setDrawOrder(30);
			super.layoutHealthBar();
		}
	}

	private void ensureShieldTexture() {
		if (this.shieldTexture != null || this.shieldColor == null) {
			return;
		}
		this.shieldTexture = TextureRecolor.recolorBlackTo(SHIELD_TEXTURE,
				this.shieldColor);
		this.shieldOrigin.set(this.shieldTexture.getWidth() / 2f,
				this.shieldTexture.getHeight() / 2f);
		this.shieldRect.setBounds(0f, 0f, this.shieldTexture.getWidth(),
				this.shieldTexture.getHeight());
	}

	@Override
	public Vector2f GetNextGridPoint(Vector2f gridPosition) {
		if (this.getMonsterType() == MonsterType.Chicken) {
			MonsterChicken.applyFlightPath(this.game, gridPosition,
					this.chickenPathResult, this);
			return this.chickenPathResult;
		}
		return super.GetNextGridPoint(gridPosition);
	}

	@Override
	public int GetVerticalTextureOffset() {
		if (this.getMonsterType() == MonsterType.Chicken) {
			return 0;
		}
		return super.GetVerticalTextureOffset();
	}

	private void updateShieldLayout() {
		if (this.shieldTexture == null) {
			return;
		}
		float texW = this.shieldTexture.getWidth();
		float monsterSize = Math.max(this.getSpriteWidth(),
				this.getSpriteHeight());
		float drawSize = monsterSize / SHIELD_INNER_DIAMETER_RATIO
				* SHIELD_SIZE_MULTIPLIER;
		this.shieldScale = drawSize / texW;
	}

	@Override
	protected void drawGlowLayer(SpriteBatch batch, GameTime gameTime) {
		this.ensureShieldTexture();
		if (this.shieldTexture == null) {
			return;
		}
		float frameScale = this.shieldFrame == 0 ? SHIELD_FRAME_A_SCALE
				: SHIELD_FRAME_B_SCALE;
		float frameRotation = this.shieldFrame == 0 ? SHIELD_FRAME_A_ROTATION
				: SHIELD_FRAME_B_ROTATION;
		LColor color = this.resolveTintColor(LColor.white);
		batch.setColor(color);
		batch.draw(this.shieldTexture, this.getPosition(), this.shieldRect,
				color, frameRotation, this.shieldOrigin,
				this.shieldScale * frameScale, SpriteEffects.None);
	}

	@Override
	public void update(GameTime gameTime) {
		super.update(gameTime);
		this.ensureShieldTexture();
		this.updateShieldLayout();
		if (GameplayScreen.getGameState() == GameState.Started) {
			this.shieldFrameTime -= gameTime.getMilliseconds();
			if (this.shieldFrameTime <= 0.0) {
				this.shieldFrame = 1 - this.shieldFrame;
				this.shieldFrameTime = SHIELD_FRAME_MS;
			}
		}
	}
}
