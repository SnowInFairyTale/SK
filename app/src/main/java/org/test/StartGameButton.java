package org.test;

import com.loon.action.sprite.SpriteBatch;
import com.loon.action.sprite.painting.DrawableGameComponent;
import com.loon.action.sprite.painting.IGameComponent;
import com.loon.core.geom.RectBox;
import com.loon.core.geom.Vector2f;
import com.loon.core.graphics.LColor;
import com.loon.core.graphics.LFont;
import com.loon.core.graphics.opengl.LTexture;
import com.loon.core.graphics.opengl.LTextures;
import com.loon.core.timer.GameTime;

public class StartGameButton extends DrawableGameComponent implements
		IGameComponent {
	private Vector2f drawPosition;
	private LFont font;
	private MainGame game;
	private LTexture texture;
	private String textureFile;

	public StartGameButton(MainGame game) {
		super(game);
		this.drawPosition = new Vector2f(200f, 4f);
		this.textureFile = "assets/start.png";
		this.game = game;
		super.setDrawOrder(40);
	}

	private RectBox rect = new RectBox();

	public final RectBox CentralCollisionArea() {
		rect.setBounds(this.drawPosition.x, this.drawPosition.y,
				this.texture.getWidth(), this.texture.getHeight());
		return rect;
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		batch.draw(this.texture, this.drawPosition, this.game
				.getGameplayScreen().getGameOpacity());
		Utils.DrawStringAlignCenter(batch, this.font,
				"" + LanguageResources.getStart(), this.drawPosition.x + 100f,
				this.drawPosition.y + 26f, LColor.white);
		super.draw(batch, gameTime);
	}

	public final void Hide() {
		this.drawPosition.y = -600f;
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.texture = LTextures.loadTexture(this.textureFile);
		this.font = Constants.font(24);
	}

	public final void Show() {
		this.drawPosition.y = 4f;
	}

	@Override
	public void update(GameTime gameTime) {
		super.update(gameTime);
	}
}