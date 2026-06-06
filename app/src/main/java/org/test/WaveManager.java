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
			new WaveSpec(5, 4000, 2f, 4000.0, 30, MonsterType.Chieftain),
			new WaveSpec(12, 1300, 2f, 800.0, 4, MonsterType.Berserker),
			new WaveSpec(5, 1200, 2f, 2500.0, 22, MonsterType.Doctor),
			new WaveSpec(14, 1150, 3.2f, 350.0, 4, MonsterType.Peon),
			new WaveSpec(15, 1250, 2f, 500.0, 4, MonsterType.Chicken),
			new WaveSpec(2, 4500, 2f, 2000.0, 35, MonsterType.Chieftain),
			new WaveSpec(12, 1500, 2f, 700.0, 5, MonsterType.Peasant),
			new WaveSpec(6, 1400, 2f, 3500.0, 25, MonsterType.Doctor),
			new WaveSpec(18, 1450, 2f, 250.0, 5, MonsterType.Berserker),
			new WaveSpec(12, 1700, 1.8f, 600.0, 5, MonsterType.Peon),
			new WaveSpec(3, 5000, 2f, 3500.0, 50, MonsterType.Chieftain),
	};

	private static final int TOTAL_WAVES = WAVE_SPECS.length;

	static int ScaleHitPoints(int baseHitPoints, Difficulty difficulty) {
		switch (difficulty) {
		case Easy:
			return baseHitPoints;
		case Medium:
			return baseHitPoints * 2;
		case Hard:
			return baseHitPoints * 3;
		default:
			throw new RuntimeException("Unknown difficulty in wavemanager!");
		}
	}

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
		for (WaveSpec spec : WAVE_SPECS) {
			this.waves.add(new Wave(game, spec.count,
					ScaleHitPoints(spec.baseHitPoints, difficulty), spec.speed,
					spec.spread, spec.value, spec.monsterType));
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
			int num2 = ((int) Math.ceil(this.timeUntilNextWave)) / 0x3e8;
			batch.drawString(this.font, LanguageResources.getNextWave() + " "
					+ num2, this.drawPosition.x + 44f,
					this.drawPosition.y + 36f, LColor.white);
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

	public final void Remove() {
		for (int i = 0; i < this.activeWaves.size(); i++) {
			this.activeWaves.get(i).Remove();
		}
		if (this.nextWaveMonsterType != null) {
			super.getGame().Components().remove(this.nextWaveMonsterType);
		}
		super.getGame().Components().remove(this);
	}

	public final void RemoveActiveWave(Wave wave) {
		this.activeWaves.remove(wave);
	}

	@Override
	public void update(GameTime gameTime) {
		if (GameplayScreen.getGameState() == GameState.Started) {
			this.timeUntilNextWave -= gameTime.getMilliseconds();
			if (this.timeUntilNextWave < 0.0) {
				if (this.waveNumber < this.waves.size()) {
					this.activeWaves.add(this.waves.get(this.waveNumber));
					this.waves.get(this.waveNumber).setWaveState(
							WaveState.Started);
					if (this.nextWaveMonsterType != null) {
						this.game.Components().remove(this.nextWaveMonsterType);
					}
					this.timeUntilNextWave = 20000.0;
					if ((this.waveNumber + 1) < this.waves.size()) {
						this.nextWaveMonsterType = AnimatedSpriteMonster
								.GetSmallAnimatedSpriteMonster(this.game,
										this.waves.get(this.waveNumber + 1)
												.getMonsterType());
						this.nextWaveMonsterType.setAnimationSpeedRatio(3);
						this.nextWaveMonsterType.setObeyGameOpacity(false);
						this.nextWaveMonsterType.setDrawOrder(45);
						this.game.Components().add(this.nextWaveMonsterType);
					} else {
						this.isLastWave = true;
					}
					this.waveNumber++;
				} else {
					boolean flag = true;
					for (Wave wave : this.activeWaves) {
						if (wave.getMonsters().size() > 0) {
							flag = false;
							break;
						}
					}
					if (flag) {
						this.game.getGameplayScreen().Win();
					}
				}
			}
		}
		super.update(gameTime);
	}

	public final int getRemainingWaves() {
		return this.waves.size();
	}
}
