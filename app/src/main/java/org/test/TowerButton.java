package org.test;

import com.loon.action.sprite.SpriteBatch;
import com.loon.action.sprite.painting.DrawableGameComponent;
import com.loon.action.sprite.painting.IGameComponent;
import com.loon.core.geom.RectBox;
import com.loon.core.geom.Vector2f;
import com.loon.core.graphics.LColor;
import com.loon.core.graphics.opengl.LTexture;
import com.loon.core.graphics.opengl.LTextures;
import com.loon.core.timer.GameTime;

public abstract class TowerButton extends DrawableGameComponent implements
		IGameComponent {

	private static final int BUTTON_SIZE = 120;
	private static final float BUTTON_Y = 840f;

	private Vector2f drawPosition = new Vector2f();

	private MainGame game;

	private LTexture texture;

	private int textureOffsetX;
	private int textureOffsetY;

	public TowerButton(MainGame game, TowerType towerType) {
		super(game);
		this.game = game;
		this.setIsActive(true);
		this.setTowerType(towerType);
		switch (this.getTowerType()) {
		case Axe:
			this.textureOffsetX = 0;
			break;

		case Spear:
			this.textureOffsetX = 240;
			break;

		case AirDefence:
			this.textureOffsetX = 120;
			break;

		case Lur:
			this.textureOffsetX = 360;
			break;
		}
		this.drawPosition.x = this.textureOffsetX;
		this.Show();
	}

	private RectBox rect = new RectBox();

	public final RectBox CentralCollisionArea() {
		rect.setBounds(this.drawPosition.x, this.drawPosition.y, BUTTON_SIZE,
				BUTTON_SIZE);
		return rect;
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		batch.draw(this.texture, this.drawPosition.x, this.drawPosition.y,
				this.textureOffsetX, this.textureOffsetY, BUTTON_SIZE,
				BUTTON_SIZE, this.game.getGameplayScreen().getGameOpacity());
		super.draw(batch, gameTime);
	}

	public final void Hide() {
		this.drawPosition.y = -600f;
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.texture = LTextures.loadTexture("assets/factory_buttons.png");
	}

	public final void Show() {
		this.drawPosition.y = BUTTON_Y;
	}

	@Override
	public void update(GameTime gameTime) {
		super.update(gameTime);
	}

	public final void UpdateStatus(int currentCash) {
		if (currentCash >= this.getTowerPrice()) {
			this.setIsActive(true);
			this.textureOffsetY = 0;
		} else {
			this.setIsActive(false);
			this.textureOffsetY = 0x82;
		}
	}

	private boolean privateIsActive;

	public final boolean getIsActive() {
		return privateIsActive;
	}

	public final void setIsActive(boolean value) {
		privateIsActive = value;
	}

	private int privateTowerPrice;

	public final int getTowerPrice() {
		return privateTowerPrice;
	}

	public final void setTowerPrice(int value) {
		privateTowerPrice = value;
	}

	private TowerType privateTowerType;

	public final TowerType getTowerType() {
		return privateTowerType;
	}

	public final void setTowerType(TowerType value) {
		privateTowerType = value;
	}
}
