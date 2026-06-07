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

public class WaveManager extends DrawableGameComponent implements
		IGameComponent {

	private static final class WaveSpec {
		private final int count;
		private final int baseHitPoints;
		private final float speed;
		private final double spread;
		private final int value;
		private final MonsterType monsterType;

		private WaveSpec(int count, int baseHitPoints, float speed,
				double spread, int value, MonsterType monsterType) {
			this.count = count;
			this.baseHitPoints = baseHitPoints;
			this.speed = speed;
			this.spread = spread;
			this.value = value;
			this.monsterType = monsterType;
		}
	}

	private static final WaveSpec[] WAVE_SPECS = {
			new WaveSpec(8, 20, 2f, 1500.0, 1, MonsterType.Peasant),
			new WaveSpec(8, 20, 2f, 1000.0, 1, MonsterType.Peasant),
			new WaveSpec(10, 30, 3.2f, 1000.0, 2, MonsterType.Peon),
			new WaveSpec(10, 30, 2f, 200.0, 2, MonsterType.Peasant),
			new WaveSpec(10, 30, 2f, 1000.0, 2, MonsterType.Chicken),
			new WaveSpec(10, 50, 2f, 1000.0, 2, MonsterType.Berserker),
			new WaveSpec(10, 70, 2f, 1000.0, 2, MonsterType.Chicken),
			new WaveSpec(10, 80, 2f, 200.0, 2, MonsterType.Berserker),
			new WaveSpec(1, 600, 2f, 1000.0, 25, MonsterType.Chieftain),
			new WaveSpec(10, 100, 3.2f, 300.0, 3, MonsterType.Peon),
			new WaveSpec(10, 130, 2f, 1000.0, 3, MonsterType.Berserker),
			new WaveSpec(13, 110, 2.4f, 800.0, 3, MonsterType.Chicken),
			new WaveSpec(2, 1000, 2f, 1000.0, 25, MonsterType.Doctor),
			new WaveSpec(10, 150, 2f, 200.0, 3, MonsterType.Berserker),
			new WaveSpec(10, 100, 3.2f, 1000.0, 3, MonsterType.Peon),
			new WaveSpec(18, 200, 2f, 1000.0, 2, MonsterType.Peasant),
			new WaveSpec(12, 260, 1.8f, 600.0, 2, MonsterType.Peasant),
			new WaveSpec(3, 500, 2f, 2000.0, 4, MonsterType.Doctor),
			new WaveSpec(8, 155, 2f, 1000.0, 3, MonsterType.Chicken),
			new WaveSpec(12, 220, 2f, 300.0, 2, MonsterType.Peasant),
			new WaveSpec(12, 260, 2f, 1000.0, 3, MonsterType.Berserker),
			new WaveSpec(10, 280, 2f, 1000.0, 3, MonsterType.Peon),
			new WaveSpec(10, 170, 2f, 600.0, 3, MonsterType.Chicken),
			new WaveSpec(10, 360, 1.8f, 200.0, 3, MonsterType.Peon),
			new WaveSpec(10, 500, 2f, 1000.0, 3, MonsterType.Berserker),
			new WaveSpec(1, 3500, 2f, 1000.0, 30, MonsterType.Chieftain),
			new WaveSpec(10, 310, 2f, 1000.0, 3, MonsterType.Chicken),
			new WaveSpec(10, 500, 2f, 1000.0, 3, MonsterType.Peasant),
			new WaveSpec(5, 900, 2f, 2000.0, 6, MonsterType.Doctor),
			new WaveSpec(20, 550, 2f, 1000.0, 2, MonsterType.Berserker),
			new WaveSpec(10, 500, 2f, 1000.0, 3, MonsterType.Chicken),
			new WaveSpec(10, 700, 1.8f, 400.0, 3, MonsterType.Peon),
			new WaveSpec(12, 800, 2f, 5000.0, 3, MonsterType.Peasant),
			new WaveSpec(10, 900, 2f, 1000.0, 3, MonsterType.Berserker),
			new WaveSpec(2, 4000, 2f, 1000.0, 30, MonsterType.Chieftain),
			new WaveSpec(10, 450, 2f, 1000.0, 3, MonsterType.Chicken),
			new WaveSpec(10, 1000, 2f, 1000.0, 3, MonsterType.Peasant),
			new WaveSpec(10, 1050, 2f, 1000.0, 3, MonsterType.Peon),
			new WaveSpec(10, 1200, 2f, 500.0, 3, MonsterType.Berserker),
			new WaveSpec(3, 4000, 2f, 4000.0, 30, MonsterType.Chieftain),
			new WaveSpec(10, 1300, 2f, 800.0, 4, MonsterType.Berserker),//41
			new WaveSpec(5, 1200, 2f, 2500.0, 22, MonsterType.Doctor),
			new WaveSpec(10, 650, 2f, 500.0, 4, MonsterType.Chicken),
			new WaveSpec(12, 1150, 3.2f, 350.0, 4, MonsterType.Peon),
			new WaveSpec(4, 4500, 2f, 2000.0, 35, MonsterType.Chieftain),//45
			new WaveSpec(12, 1500, 2f, 700.0, 5, MonsterType.Peasant),
			new WaveSpec(6, 1400, 2f, 3500.0, 25, MonsterType.Doctor),
			new WaveSpec(14, 1450, 2f, 250.0, 5, MonsterType.Berserker),
			new WaveSpec(12, 1700, 1.8f, 600.0, 5, MonsterType.Peon),
			new WaveSpec(5, 5000, 2f, 3500.0, 50, MonsterType.Chieftain),
	};

	private static final int TOTAL_WAVES = WAVE_SPECS.length;

	private static final MonsterType[] BOSS_SEQUENCE = { MonsterType.Peasant,
			MonsterType.Peon, MonsterType.Berserker, MonsterType.Chicken,
			MonsterType.Doctor, MonsterType.Chieftain };

	/**
	 * All 8 bosses — Easy HP of the boss model (same-type max before that wave),
	 * then × {@link #BOSS_HP_MULTIPLIER} and difficulty scaling at spawn.
	 * <pre>
	 *  #  Wave  Model      Easy ref  Easy boss HP
	 *  1     8  Peasant         30            60
	 *  2    16  Peon           360           720
	 *  3    24  Berserker      500          1000
	 *  4    32  Chicken        500          1000
	 *  5    40  Doctor        1400          2800
	 *  6    48  Chieftain     4500          9000
	 *  7    50  Chieftain     5000         10000  (finale front, purple)
	 *  8    50  Chieftain     5000         10000  (finale back, gold)
	 * </pre>
	 */
	private static final int[] BOSS_EASY_MODEL_HP = { 100, 360, 500, 500, 1400,
			4500, 5000, 5000 };

	private static final float BOSS_HP_MULTIPLIER = 2f;

	private static final float BOSS_SPEED = 2f;

	private static final double NEXT_WAVE_AFTER_BOSS_DELAY_MS = 5000.0;

	static boolean IsBossWave(int waveNumber) {
		return waveNumber % 8 == 0;
	}

	private static MonsterType GetBossType(int waveNumber) {
		return BOSS_SEQUENCE[(waveNumber / 8) - 1];
	}

	static int ComputeBossHitPoints(int easyModelHitPoints, Difficulty difficulty) {
		return Math.max(1, (int) (ScaleHitPoints(easyModelHitPoints, difficulty)
				* BOSS_HP_MULTIPLIER));
	}

	private static int GetBossEasyModelHp(int bossIndex) {
		return BOSS_EASY_MODEL_HP[bossIndex];
	}

	static int ScaleHitPoints(int baseHitPoints, Difficulty difficulty) {
		switch (difficulty) {
		case Easy:
			return baseHitPoints;
		case Medium:
			return (int) (baseHitPoints * 1.2);
		case Hard:
			return (int) (baseHitPoints * 1.5);
		default:
			throw new RuntimeException("Unknown difficulty in wavemanager!");
		}
	}

	private boolean holdNextWaveForBoss;
	private java.util.ArrayList<Wave> activeWaves;
	private Vector2f drawPosition;
	private LFont font;
	private MainGame game;
	private boolean isLastWave;
	private AnimatedSprite nextWaveMonsterType;
	private LTexture texture;
	private double timeUntilNextWave;
	private int waveNumber;
	private java.util.ArrayList<Wave> waves;

	public WaveManager(MainGame game, Difficulty difficulty) {
		super(game);
		this.waves = new java.util.ArrayList<Wave>(TOTAL_WAVES);
		this.activeWaves = new java.util.ArrayList<Wave>();
		this.drawPosition = new Vector2f(140f, -8f);
		this.game = game;
		for (int i = 0; i < WAVE_SPECS.length; i++) {
			WaveSpec spec = WAVE_SPECS[i];
			int waveNumber = i + 1;
			boolean isBossWave = IsBossWave(waveNumber);
			boolean isFinaleWave = waveNumber == TOTAL_WAVES;
			MonsterType bossType = isBossWave ? GetBossType(waveNumber) : null;
			int bossHitPoints = isBossWave ? ComputeBossHitPoints(
					GetBossEasyModelHp((waveNumber / 8) - 1), difficulty) : 0;
			int finaleBossHitPoints = isFinaleWave ? ComputeBossHitPoints(
					GetBossEasyModelHp(6), difficulty) : 0;
			this.waves.add(new Wave(game, spec.count,
					ScaleHitPoints(spec.baseHitPoints, difficulty), spec.speed,
					spec.spread, spec.value, spec.monsterType, isBossWave,
					bossType, BOSS_SPEED, spec.value, isFinaleWave,
					bossHitPoints, finaleBossHitPoints));
		}
		game.Components().add(this);
		this.timeUntilNextWave = -1.0;
	}

	public final void AddMonsterToCurrentWave(Monster monster) {
		this.waves.get(this.waveNumber - 1).AddMonster(monster);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		batch.draw(this.texture, this.drawPosition, LColor.white);
		batch.drawString(this.font, LanguageResources.getWave() + " "
				+ this.waveNumber + " " + LanguageResources.getof() + " "
				+ this.waves.size(), this.drawPosition.x + 34f,
				this.drawPosition.y + 4f, LColor.white);
		if (!this.isLastWave) {
			Utils.DrawStringAlignRight(batch, this.font,
					LanguageResources.getNext(), this.drawPosition.x + 260f,
					this.drawPosition.y + 24f, LColor.white);
			if (!this.holdNextWaveForBoss) {
				int num2 = ((int) Math.ceil(this.timeUntilNextWave)) / 0x3e8;
				batch.drawString(this.font, LanguageResources.getNextWave() + " "
						+ num2, this.drawPosition.x + 44f,
						this.drawPosition.y + 36f, LColor.white);
			}
		}
	}

	public final java.util.ArrayList<Monster> GetAllActiveMonsters() {
		java.util.ArrayList<Monster> list = new java.util.ArrayList<Monster>();
		for (Wave wave : this.game.getGameplayScreen().getWaveManager().activeWaves) {
			list.addAll(wave.getMonsters());
		}
		return list;
	}

	public final Monster GetSelectedMonster(RectBox touchRect) {
		for (Wave wave : this.activeWaves) {
			Monster selectedMonster = wave.GetSelectedMonster(touchRect);
			if (selectedMonster != null) {
				return selectedMonster;
			}
		}
		return null;
	}

	@Override
	protected void loadContent() {
		this.texture = LTextures.loadTexture("assets/wave_x_of_y.png");
		this.font = Constants.font(20);
		super.loadContent();
	}

	public final void ClearAllMonsters() {
		for (int i = 0; i < this.waves.size(); i++) {
			this.waves.get(i).RemoveAllMonsters();
		}
	}

	public final void Remove() {
		for (int i = 0; i < this.waves.size(); i++) {
			Wave wave = this.waves.get(i);
			wave.RemoveAllMonsters();
			this.game.Components().remove(wave);
		}
		this.waves.clear();
		this.activeWaves.clear();
		if (this.nextWaveMonsterType != null) {
			super.getGame().Components().remove(this.nextWaveMonsterType);
			this.nextWaveMonsterType = null;
		}
		super.getGame().Components().remove(this);
	}

	public final void OnBossSpawned() {
		if (this.holdNextWaveForBoss) {
			this.holdNextWaveForBoss = false;
			this.timeUntilNextWave = NEXT_WAVE_AFTER_BOSS_DELAY_MS;
		}
	}

	public final void RemoveActiveWave(Wave wave) {
		this.activeWaves.remove(wave);
	}

	private boolean anyActiveWaveHasPendingEncounters() {
		for (int i = 0; i < this.activeWaves.size(); i++) {
			if (this.activeWaves.get(i).hasPendingEncounters()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void update(GameTime gameTime) {
		if (GameplayScreen.getGameState() == GameState.Started) {
			if (!this.holdNextWaveForBoss) {
				this.timeUntilNextWave -= gameTime.getMilliseconds();
			}
			if (this.timeUntilNextWave < 0.0) {
				if (this.waveNumber < this.waves.size()) {
					this.activeWaves.add(this.waves.get(this.waveNumber));
					this.waves.get(this.waveNumber).setWaveState(
							WaveState.Started);
					if (this.nextWaveMonsterType != null) {
						this.game.Components().remove(this.nextWaveMonsterType);
					}
					this.waveNumber++;
					if (IsBossWave(this.waveNumber)) {
						this.holdNextWaveForBoss = true;
						this.timeUntilNextWave = Double.MAX_VALUE;
					} else {
						this.holdNextWaveForBoss = false;
						this.timeUntilNextWave = 20000.0;
					}
					if (this.waveNumber < this.waves.size()) {
						this.nextWaveMonsterType = AnimatedSpriteMonster
								.GetSmallAnimatedSpriteMonster(this.game,
										this.waves.get(this.waveNumber)
												.getMonsterType());
						this.nextWaveMonsterType.setAnimationSpeedRatio(3);
						this.nextWaveMonsterType.setObeyGameOpacity(false);
						this.nextWaveMonsterType.setDrawOrder(45);
						this.game.Components().add(this.nextWaveMonsterType);
					} else {
						this.isLastWave = true;
					}
				} else if (this.isLastWave
						&& !this.anyActiveWaveHasPendingEncounters()) {
					this.game.getGameplayScreen().Win();
				}
			}
		}
		super.update(gameTime);
	}

	public final int getRemainingWaves() {
		return this.waves.size();
	}
}
