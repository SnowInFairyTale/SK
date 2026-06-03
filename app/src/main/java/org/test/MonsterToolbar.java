package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableGameComponent;
import loon.action.sprite.painting.IGameComponent;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.timer.GameTime;

public class MonsterToolbar extends DrawableGameComponent implements
		IGameComponent {
	private AnimatedSprite animatedSpriteMonster;
	private Vector2f drawPosition;
	private Vector2f drawPositionFont;
	private LFont font;
	private MainGame game;
	private ProgressBar healthBar;
	private Monster monster;
	private LTexture texture;

	public MonsterToolbar(MainGame game, Monster monster) {
		super(game);
		this.game = game;
		this.monster = monster;
		this.drawPosition = new Vector2f(Constants.s(10f), Constants.s(420f));
		this.drawPositionFont = this.drawPosition.add(Constants.s(78f),
				Constants.s(12f));
		this.animatedSpriteMonster = AnimatedSpriteMonster
				.GetAnimatedSpriteMonsterForMonsterToolbar(game,
						this.drawPosition, monster.getMonsterType());
		this.animatedSpriteMonster.setDrawOrder(0x1d);
		this.animatedSpriteMonster.setAnimationSpeedRatio(3);
		this.animatedSpriteMonster.setObeyGameOpacity(false);
		this.animatedSpriteMonster.setOnlyAnimateIfGameStateStarted(false);
		super.setDrawOrder(1);
		game.Components().add(this.animatedSpriteMonster);
		this.healthBar = new ProgressBar(game, Constants.s(50), true);
		this.healthBar.setPosition(this.drawPosition.add(Constants.s(75f),
				Constants.s(30f)));
		this.healthBar.setHeight(Constants.s(8));
		game.Components().add(this.healthBar);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		batch.draw(this.texture, this.drawPosition, LColor.white);
		batch.drawString(this.font, LanguageResources.getRemainingHealth()
				+ " " + this.monster.getHitPoints(), this.drawPositionFont,
				LColor.white);
		super.draw(batch, gameTime);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.texture = LTextures.loadTexture("assets/monster_toolbar.png");
		this.font = Constants.uiFont(12);
	}

	public final void Remove() {
		this.game.Components().remove(this.animatedSpriteMonster);
		this.game.Components().remove(this.healthBar);
		this.game.Components().remove(this);
	}

	@Override
	public void update(GameTime gameTime) {
		super.update(gameTime);
		this.healthBar.setCurrentPercent((100 * this.monster.getHitPoints())
				/ this.monster.getStartHitPoints());
	}
}