package org.test;

import loon.core.geom.Vector2f;
import loon.core.timer.GameTime;
import loon.utils.MathUtils;

public abstract class Monster extends AnimatedSprite {

	private static final int HEALTH_BAR_WIDTH = 40;
	private static final int HEALTH_BAR_HEIGHT = 4;
	private static final int HEALTH_BAR_GAP = 3;
	/** Above tower upgrade bars (drawOrder 50) so they are not covered. */
	private static final int HEALTH_BAR_DRAW_ORDER = 55;

	static int HealthPercent(int hitPoints, int startHitPoints) {
		if (hitPoints <= 0 || startHitPoints <= 0) {
			return 0;
		}
		int percent = (100 * hitPoints) / startHitPoints;
		return Math.max(1, Math.min(100, percent));
	}

	private Vector2f destinationPosition;
	private Vector2f direction;
	private MainGame game;
	private boolean isCurrentlyInMud;
	private Vector2f position;
	private Wave wave;

	public Monster(MainGame game, Wave wave, int startHitPoints, float speed,
			int value, String textureFile, int columnCount, int spriteCount,
			int spriteHeight, int spriteWidth) {
		super(game, textureFile, new Vector2f(2f, 400f), columnCount,
				spriteCount, spriteWidth, spriteHeight, 1f);
		Vector2f startPoint = game.getGameplayScreen().getLevelSettings()
				.getStartPoint().cpy();

		this.setGridPosition(new Vector2f(startPoint.x, MathUtils
				.nextInt(-1, 2) + startPoint.y));
		this.Init(game, wave, value, startHitPoints, speed);
	}

	public Monster(MainGame game, Wave wave, int startHitPoints, float speed,
			int value, String textureFile, int columnCount, int spriteCount,
			int spriteHeight, int spriteWidth, Vector2f gridPosition) {
		super(game, textureFile, new Vector2f(2f, 400f), columnCount,
				spriteCount, spriteWidth, spriteHeight, 1f);
		if ((gridPosition.x < 0) || (gridPosition.x > 0x10)
				|| (gridPosition.y < 0) || (gridPosition.y > 0x12)) {
			throw new RuntimeException("gridPosition is out of bounds.");
		}
		if (game.getGameplayScreen().getDirs()[gridPosition.x()][gridPosition
				.y()] == null) {
			throw new RuntimeException("gridPosition is not valid.");
		}
		this.setGridPosition(gridPosition);
		this.Init(game, wave, value, startHitPoints, speed);
	}

	private static final float[][] DOCTOR_PEON_SAME_CELL_OFFSETS = { { -10f, -10f },
			{ 10f, -10f }, { -10f, 10f }, { 10f, 10f } };

	private boolean IsValidSpawnGrid(Vector2f gridPosition) {
		if ((gridPosition.x < 0) || (gridPosition.x > 0x10)
				|| (gridPosition.y < 0) || (gridPosition.y > 0x12)) {
			return false;
		}
		return this.game.getGameplayScreen().getDirs()[gridPosition.x()][gridPosition
				.y()] != null;
	}

	private Vector2f GetPreviousGridPoint(Vector2f gridPosition) {
		PathNode node = this.game.getGameplayScreen().getDirs()[gridPosition
				.x()][gridPosition.y()];
		if (node == null) {
			return null;
		}
		return new Vector2f(gridPosition.x - node.x(), gridPosition.y
				- node.y());
	}

	private void AddDoctorPeonSpawnCandidate(
			java.util.ArrayList<Vector2f> candidates,
			java.util.HashSet<String> seen, Vector2f gridPosition) {
		if (!this.IsValidSpawnGrid(gridPosition)) {
			return;
		}
		String key = ((int) gridPosition.x) + "," + ((int) gridPosition.y);
		if (seen.add(key)) {
			candidates.add(gridPosition.cpy());
		}
	}

	private void AddDoctorPeonSpawnCandidatesAlongPath(
			java.util.ArrayList<Vector2f> candidates,
			java.util.HashSet<String> seen, Vector2f start,
			java.util.function.Function<Vector2f, Vector2f> step, int maxSteps) {
		Vector2f current = start.cpy();
		for (int i = 0; i < maxSteps; i++) {
			current = step.apply(current);
			if (current == null || !this.IsValidSpawnGrid(current)) {
				break;
			}
			this.AddDoctorPeonSpawnCandidate(candidates, seen, current);
		}
	}

	private boolean TrySpawnDoctorPeon(Vector2f gridPosition,
			Vector2f pixelOffset) {
		try {
			MonsterPeon monster = new MonsterPeon(this.game, this.wave,
					this.getSpeed(), this.getStartHitPoints() / 4,
					this.getValue() / 2, gridPosition.cpy());
			if (pixelOffset != null) {
				monster.setPosition(monster.getPosition().add(pixelOffset));
			}
			this.wave.AddMonster(monster);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private void SpawnDoctorPeons() {
		Vector2f doctorGrid = this.getGridPosition();
		java.util.ArrayList<Vector2f> candidates = new java.util.ArrayList<Vector2f>();
		java.util.HashSet<String> seen = new java.util.HashSet<String>();

		this.AddDoctorPeonSpawnCandidatesAlongPath(candidates, seen,
				doctorGrid, this::GetNextGridPoint, 2);
		this.AddDoctorPeonSpawnCandidatesAlongPath(candidates, seen,
				doctorGrid, this::GetPreviousGridPoint, 2);

		int[][] neighborOffsets = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 },
				{ 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };
		for (int[] offset : neighborOffsets) {
			this.AddDoctorPeonSpawnCandidate(candidates, seen, new Vector2f(
					doctorGrid.x + offset[0], doctorGrid.y + offset[1]));
		}
		this.AddDoctorPeonSpawnCandidate(candidates, seen, doctorGrid);

		int spawned = 0;
		int sameCellOffsetIndex = 0;
		for (Vector2f candidate : candidates) {
			if (spawned >= 4) {
				break;
			}
			Vector2f pixelOffset = null;
			if (candidate.x == doctorGrid.x && candidate.y == doctorGrid.y
					&& sameCellOffsetIndex < DOCTOR_PEON_SAME_CELL_OFFSETS.length) {
				float[] offset = DOCTOR_PEON_SAME_CELL_OFFSETS[sameCellOffsetIndex++];
				pixelOffset = new Vector2f(offset[0], offset[1]);
			}
			if (this.TrySpawnDoctorPeon(candidate, pixelOffset)) {
				spawned++;
			}
		}
	}

	public Vector2f GetNextGridPoint(Vector2f gridPosition) {
		return this.game.getGameplayScreen().GetNextGridPoint(gridPosition);
	}

	public int GetVerticalTextureOffset() {
		return Utils.GetTextureOffsetY(Utils.GetAngle(this.direction),
				super.getSpriteHeight());
	}

	public final void Hit(int damage) {
		if (!this.getDead()) {
			this.setHitPoints(this.getHitPoints() - damage);
			this.getHealthBar().setCurrentPercent(
					HealthPercent(this.getHitPoints(), this.getStartHitPoints()));
			if (this.getHitPoints() <= 0) {
				this.setDead(true);
				this.game.getGameplayScreen().getCash()
						.Increase(this.getValue());
				if (this instanceof MonsterBoss) {
					GemType gemDrop = BossGemDrops.roll();
					if (gemDrop != GemType.None) {
						this.game.getGameplayScreen().getGems().add(gemDrop);
						this.game.Components().add(new GemDropInfo(this.game,
								super.getDrawPosition(), gemDrop));
					}
				}
				switch (this.getMonsterType()) {
				case Peasant:

					break;

				case Peon:

					break;

				case Berserker:

					break;

				case Chicken:

					break;

				case Doctor:
					this.SpawnDoctorPeons();
					break;
				case Chieftain:

					break;
				}
				this.Remove();
				this.game.Components().add(
						new DieInfo(this.game, super.getDrawPosition(), this
								.getValue()));
				this.game.getGameplayScreen().MonsterDied(this);
			}
		}
	}

	private void Init(MainGame game, Wave wave, int value, int startHitPoints,
			float speed) {
		this.setStartHitPoints(startHitPoints);
		this.setHitPoints(startHitPoints);
		this.setSpeed(speed);
		this.setHealthBar(new ProgressBar(game, HEALTH_BAR_WIDTH, true));
		this.getHealthBar().setObeyGameOpacity(true);
		this.setPosition(Utils.ConvertToPositionCoordinates(
				this.getGridPosition()).add(Constants.GridSize / 2f,
				Constants.GridSize / 2f));
		this.destinationPosition = this.getPosition();
		this.game = game;
		this.wave = wave;
		this.setValue(value);
		game.Components().add(this.getHealthBar());
		game.Components().add(this);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
	}

	public final void Remove() {
		this.game.Components().remove(this.getHealthBar());
		this.wave.RemoveMonster(this);
		this.game.Components().remove(this);
	}

	public final void StartedSelection() {
		super.setObeyGameOpacity(false);
		this.getHealthBar().setObeyGameOpacity(false);
	}

	public final void StoppedSelection() {
		super.setObeyGameOpacity(true);
		this.getHealthBar().setObeyGameOpacity(true);
	}

	public final void Survived() {
		if (this.getHealthBar() != null) {
			this.game.Components().remove(this.getHealthBar());
		}
		this.wave.RemoveMonster(this);

		this.game.getGameplayScreen().MonsterSurvived(this);
		this.game.Components().remove(this);
		if (this.game.getGameplayScreen().getRemainingLives().Decrease() < 0) {
			this.game.getGameplayScreen().Lose();
		}
	}

	@Override
	public void update(GameTime gameTime) {
		super.update(gameTime);
		if (GameplayScreen.getGameState() == GameState.Started) {
			if (Utils.GetDistance(this.getPosition(), this.destinationPosition) < 2f) {
				if (this.game.getGameplayScreen().getLevelSettings()
						.getTowerBlockingGridCells()
						.contains(this.getGridPosition())
						&& (this.getMonsterType() != MonsterType.Chicken)) {
					this.isCurrentlyInMud = true;
				} else {
					this.isCurrentlyInMud = false;
				}
				this.setGridPosition(this.GetNextGridPoint(this
						.getGridPosition()));
				this.destinationPosition = Utils.ConvertToPositionCoordinates(
						this.getGridPosition()).add(Constants.GridSize / 2f,
						Constants.GridSize / 2f);
				if (this.getGridPosition().x >= this.game.getGameplayScreen()
						.getLevelSettings().getEndPoint().x) {
					this.Survived();
					return;
				}
			}
			this.direction = Utils.GetDirection(this.getPosition(),
					this.destinationPosition);
			if (this.isCurrentlyInMud) {
				this.setPosition(this.getPosition().add(
						(this.direction.mul(this.getSpeed())).mul(0.55f)));
			} else {
				this.setPosition(this.getPosition().add(
						this.direction.mul(this.getSpeed())));
			}
			super.setVerticalTextureOffset(this.GetVerticalTextureOffset());
		}
	}

	private boolean privateDead;

	public final boolean getDead() {
		return privateDead;
	}

	public final void setDead(boolean value) {
		privateDead = value;
	}

	private Vector2f privateGridPosition;

	public final Vector2f getGridPosition() {
		return privateGridPosition;
	}

	public final void setGridPosition(Vector2f value) {
		privateGridPosition = value;
	}

	private ProgressBar privateHealthBar;

	public final ProgressBar getHealthBar() {
		return privateHealthBar;
	}

	public final void setHealthBar(ProgressBar value) {
		privateHealthBar = value;
	}

	private int privateHitPoints;

	public final int getHitPoints() {
		return privateHitPoints;
	}

	public final void setHitPoints(int value) {
		privateHitPoints = value;
	}

	private int privateLivingTime;

	public final int getLivingTime() {
		return privateLivingTime;
	}

	public final void setLivingTime(int value) {
		privateLivingTime = value;
	}

	private MonsterType privateMonsterType;

	public final MonsterType getMonsterType() {
		return privateMonsterType;
	}

	public final void setMonsterType(MonsterType value) {
		privateMonsterType = value;
	}

	public final Vector2f getPosition() {
		return this.position;
	}

	protected void layoutHealthBar() {
		if (this.getHealthBar() == null) {
			return;
		}
		this.getHealthBar().setHeight(HEALTH_BAR_HEIGHT);
		this.getHealthBar().setDrawOrder(
				Math.max(HEALTH_BAR_DRAW_ORDER, super.getDrawOrder() + 1));
		this.getHealthBar().setPosition(new Vector2f(this.getPosition().x
				- HEALTH_BAR_WIDTH / 2f, super.getDrawPosition().y
				- HEALTH_BAR_HEIGHT - HEALTH_BAR_GAP));
	}

	public final void setPosition(Vector2f value) {
		this.position = value;
		super.setDrawPosition(new Vector2f(value.x
				- (super.getSpriteWidth() / 2), value.y
				- (super.getSpriteHeight() / 2)));
		this.layoutHealthBar();
	}

	private float privateRadius;

	public final float getRadius() {
		return privateRadius;
	}

	public final void setRadius(float value) {
		privateRadius = value;
	}

	private int privateReservedHitPoints;

	public final int getReservedHitPoints() {
		return privateReservedHitPoints;
	}

	public final void setReservedHitPoints(int value) {
		privateReservedHitPoints = value;
	}

	public final void addReservedHitPoints(int value) {
		privateReservedHitPoints += value;
	}

	public final void removeReservedHitPoints(int value) {
		privateReservedHitPoints -= value;
	}

	private float privateSpeed;

	public final float getSpeed() {
		return privateSpeed;
	}

	public final void setSpeed(float value) {
		privateSpeed = value;
	}

	private int privateStartHitPoints;

	public final int getStartHitPoints() {
		return privateStartHitPoints;
	}

	public final void setStartHitPoints(int value) {
		privateStartHitPoints = value;
	}

	private int privateValue;

	public final int getValue() {
		return privateValue;
	}

	public final void setValue(int value) {
		privateValue = value;
	}
}