package org.test;

import loon.action.sprite.painting.DrawableGameComponent;
import loon.action.sprite.painting.IGameComponent;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.timer.GameTime;

public class Wave extends DrawableGameComponent implements IGameComponent {

	private static final double BOSS_SPAWN_DELAY_MS = 5000.0;
	private static final double FINALE_BOSS_SPAWN_DELAY_MS = 30000.0;
	private static final int CHIEFTAIN_SPRITE_SIZE = 78;

	private MainGame game;
	private int bossHitPoints;
	private boolean bossSpawned;
	private MonsterType bossType;
	private float bossSpeed;
	private int bossValue;
	private boolean finaleBossesSpawned;
	private int finaleBossHitPoints;
	private boolean isBossWave;
	private boolean isFinaleWave;
	private int numberOfMonstersAdded;
	private int numMonsters;
	private float speed;
	private double spread;
	private int startHitPoints;
	private double timeUntilBossSpawn;
	private double timeUntilFinaleSpawn;
	private double timeUntilNextMonsterAdd;
	private int value;

	public Wave(MainGame game, int num_monsters, int startHitPoints,
			float speed, double spread, int value, MonsterType monsterType,
			boolean isBossWave, MonsterType bossType, float bossSpeed,
			int bossValue, boolean isFinaleWave, int bossHitPoints,
			int finaleBossHitPoints) {
		super(game);
		this.game = game;
		this.privateMonsters = new java.util.ArrayList<Monster>(10);
		this.numMonsters = num_monsters;
		this.startHitPoints = startHitPoints;
		this.speed = speed;
		this.spread = spread;
		this.value = value;
		this.isBossWave = isBossWave;
		this.bossType = bossType;
		this.bossSpeed = bossSpeed;
		this.bossValue = bossValue;
		this.isFinaleWave = isFinaleWave;
		this.bossHitPoints = bossHitPoints;
		this.finaleBossHitPoints = finaleBossHitPoints;
		this.setMonsterType(monsterType);
		this.setWaveState(WaveState.NotStarted);
		this.timeUntilNextMonsterAdd = 0.0;
		this.timeUntilBossSpawn = BOSS_SPAWN_DELAY_MS;
		this.timeUntilFinaleSpawn = FINALE_BOSS_SPAWN_DELAY_MS;
		game.Components().add(this);
	}

	public final void AddMonster(Monster monster) {
		this.getMonsters().add(monster);
	}

	public final Monster GetSelectedMonster(RectBox touchRect) {
		for (Monster monster : this.getMonsters()) {
			if (monster.CentralCollisionArea().intersects(touchRect)) {
				return monster;
			}
		}
		return null;
	}

	public final void Remove() {
		this.RemoveAllMonsters();
	}

	private void RemoveAllMonsters() {
		for (int i = 0; i < this.getMonsters().size(); i++) {
			this.getMonsters().get(i).Remove();
		}
	}

	public final void RemoveMonster(Monster monster) {
		this.getMonsters().remove(monster);
		if (this.CanRemoveActiveWave()) {
			this.game.getGameplayScreen().getWaveManager()
					.RemoveActiveWave(this);
		}
	}

	private boolean CanRemoveActiveWave() {
		if (this.getMonsters().size() != 0) {
			return false;
		}
		if (this.numberOfMonstersAdded != this.numMonsters) {
			return false;
		}
		if (this.isBossWave && !this.bossSpawned) {
			return false;
		}
		return !this.isFinaleWave || this.finaleBossesSpawned;
	}

	private void SpawnBoss() {
		int reward = Math.max(this.bossValue, this.value * 2);
		Monster boss = MonsterBoss.Create(this.game, this, this.bossType,
				this.bossSpeed, this.bossHitPoints, reward);
		this.AddMonster(boss);
		this.bossSpawned = true;
		this.game.getGameplayScreen().getWaveManager().OnBossSpawned();
	}

	private void SpawnFinaleBosses() {
		Vector2f startGrid = this.game.getGameplayScreen().getLevelSettings()
				.getStartPoint().cpy();
		int reward = Math.max(this.bossValue, this.value * 2);
		MonsterBoss front = MonsterBoss.CreateWithShield(this.game, this,
				MonsterType.Chieftain, this.speed, this.finaleBossHitPoints,
				reward, BossShieldColors.ELECTRIC_PURPLE, startGrid);
		Vector2f nextGrid = front.GetNextGridPoint(startGrid);
		Vector2f nextCenter = Utils.ConvertToPositionCoordinates(nextGrid).add(
				Constants.GridSize / 2f, Constants.GridSize / 2f);
		Vector2f marchDirection = Utils.GetDirection(front.getPosition(),
				nextCenter);
		MonsterBoss back = MonsterBoss.CreateWithShield(this.game, this,
				MonsterType.Chieftain, this.speed, this.finaleBossHitPoints,
				reward, BossShieldColors.GOLD, startGrid);
		back.setPosition(front.getPosition().sub(
				marchDirection.mul(CHIEFTAIN_SPRITE_SIZE)));
		this.AddMonster(front);
		this.AddMonster(back);
		this.finaleBossesSpawned = true;
	}

	private boolean MinionsFinishedSpawning() {
		return this.numberOfMonstersAdded == this.numMonsters;
	}

	@Override
	public void update(GameTime gameTime) {
		if ((GameplayScreen.getGameState() == GameState.Started)
				&& (this.getWaveState() == WaveState.Started)) {
			this.timeUntilNextMonsterAdd -= gameTime.getMilliseconds();
			if ((this.timeUntilNextMonsterAdd < 0.0)
					&& (this.numberOfMonstersAdded < this.numMonsters)) {
				Monster monster;
				switch (this.getMonsterType()) {
				case Peasant:
					monster = new MonsterPeasant(this.game, this, this.speed,
							this.startHitPoints, this.value);
					break;

				case Peon:
					monster = new MonsterPeon(this.game, this, this.speed,
							this.startHitPoints, this.value);
					break;

				case Berserker:
					monster = new MonsterBerserker(this.game, this, this.speed,
							this.startHitPoints, this.value);
					break;

				case Chicken:
					monster = new MonsterChicken(this.game, this, this.speed,
							this.startHitPoints, this.value);
					break;

				case Doctor:
					monster = new MonsterDoctor(this.game, this, this.speed,
							this.startHitPoints, this.value);
					break;

				case Chieftain:
					monster = new MonsterChieftain(this.game, this, this.speed,
							this.startHitPoints, this.value);
					break;

				default:
					monster = null;
					break;
				}
				this.AddMonster(monster);
				this.timeUntilNextMonsterAdd = this.spread;
				this.numberOfMonstersAdded++;
			}
			if (this.MinionsFinishedSpawning()) {
				if (this.isBossWave && !this.bossSpawned) {
					this.timeUntilBossSpawn -= gameTime.getMilliseconds();
					if (this.timeUntilBossSpawn < 0.0) {
						this.SpawnBoss();
					}
				}
				if (this.isFinaleWave && !this.finaleBossesSpawned) {
					this.timeUntilFinaleSpawn -= gameTime.getMilliseconds();
					if (this.timeUntilFinaleSpawn < 0.0) {
						this.SpawnFinaleBosses();
					}
				}
			}
		}
		super.update(gameTime);
	}

	private java.util.ArrayList<Monster> privateMonsters;

	public final java.util.ArrayList<Monster> getMonsters() {
		return privateMonsters;
	}

	public final void setMonsters(java.util.ArrayList<Monster> value) {
		privateMonsters = value;
	}

	private MonsterType privateMonsterType;

	public final MonsterType getMonsterType() {
		return privateMonsterType;
	}

	public final void setMonsterType(MonsterType value) {
		privateMonsterType = value;
	}

	private WaveState privateWaveState;

	public final WaveState getWaveState() {
		return privateWaveState;
	}

	public final void setWaveState(WaveState value) {
		privateWaveState = value;
	}
}
