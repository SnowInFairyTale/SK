package org.test;

import com.loon.action.sprite.SpriteBatch;
import com.loon.action.sprite.painting.DrawableGameComponent;
import com.loon.action.sprite.painting.IGameComponent;
import com.loon.core.geom.Vector2f;
import com.loon.core.graphics.LColor;
import com.loon.core.graphics.LFont;
import com.loon.core.graphics.opengl.LTexture;
import com.loon.core.graphics.opengl.LTextures;
import com.loon.core.timer.GameTime;

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
		this.drawPosition = new Vector2f(Constants.MONSTER_TOOLBAR_X,
				Constants.MONSTER_TOOLBAR_Y);
		this.drawPositionFont = this.drawPosition.add(156f, 24f);
		this.animatedSpriteMonster = AnimatedSpriteMonster
				.GetAnimatedSpriteMonsterForMonsterToolbar(game,
						this.drawPosition, monster.getMonsterType());
		this.animatedSpriteMonster.setDrawOrder(0x1d);
		this.animatedSpriteMonster.setAnimationSpeedRatio(3);
		this.animatedSpriteMonster.setObeyGameOpacity(false);
		this.animatedSpriteMonster.setOnlyAnimateIfGameStateStarted(false);
		super.setDrawOrder(1);
		game.Components().add(this.animatedSpriteMonster);
		this.healthBar = new ProgressBar(game, 400, true);
		this.healthBar.setObeyGameOpacity(false);
		this.healthBar.setPosition(this.drawPosition.add(150f, 60f));
		this.healthBar.setHeight(16);
		game.Components().add(this.healthBar);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		batch.draw(this.texture, this.drawPosition, LColor.white);
		batch.drawString(this.font, LanguageResources.getMaxHealth()
				+ " " + this.monster.getStartHitPoints(), this.drawPositionFont,
				LColor.white);
		super.draw(batch, gameTime);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.texture = LTextures.loadTexture("assets/monster_toolbar.png");
		this.font = Constants.font(24);
	}

	public final void Remove() {
		if (this.animatedSpriteMonster != null) {
			this.game.Components().remove(this.animatedSpriteMonster);
			this.animatedSpriteMonster = null;
		}
		if (this.healthBar != null) {
			this.game.Components().remove(this.healthBar);
			this.healthBar = null;
		}
		this.game.Components().remove(this);
	}

	@Override
	public void update(GameTime gameTime) {
		super.update(gameTime);
		this.healthBar.setCurrentPercent(Monster.HealthPercent(
				this.monster.getHitPoints(), this.monster.getStartHitPoints()));
	}
}