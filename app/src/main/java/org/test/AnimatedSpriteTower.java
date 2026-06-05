package org.test;

import loon.core.RefObject;
import loon.core.geom.Vector2f;

public class AnimatedSpriteTower extends AnimatedSprite {

	public AnimatedSpriteTower(MainGame game, String textureFile,
			Vector2f position, int spriteCount) {
		super(game, textureFile, position, 6, spriteCount,
				Constants.INFO_SPRITE_SIZE, Constants.INFO_SPRITE_SIZE, 1f);
		super.setAnimationSpeedRatio(3);
	}

	public static java.util.ArrayList<AnimatedSpriteTower> GetAllAnimatedSpriteTowers(
			MainGame game) {

		java.util.ArrayList<AnimatedSpriteTower> list = new java.util.ArrayList<AnimatedSpriteTower>();

		RefObject<Integer> num2 = new RefObject<Integer>(0);
		list.add(new AnimatedSpriteTower(game, GetTextureFile(TowerType.Axe,
				"png/", num2), new Vector2f(20f, 36f), num2.argvalue));
		num2.argvalue = 0;

		list.add(new AnimatedSpriteTower(game, GetTextureFile(TowerType.Spear,
				"png/", num2), new Vector2f(20f, 236f), num2.argvalue));
		num2.argvalue = 0;

		list.add(new AnimatedSpriteTower(game, GetTextureFile(
				TowerType.AirDefence, "png/", num2), new Vector2f(20f, 436f),
				num2.argvalue));
		num2.argvalue = 0;

		list.add(new AnimatedSpriteTower(game, GetTextureFile(TowerType.Lur,
				"png/", num2), new Vector2f(20f, 636f), num2.argvalue));
		num2.argvalue = 0;

		return list;
	}

	/** Fixed layout on info / promo screens (screen pixels). */
	public static AnimatedSprite GetPreviewTower(MainGame game,
			TowerType towerType, float x, float y, float scale) {
		RefObject<Integer> num2 = new RefObject<Integer>(0);
		AnimatedSprite sprite = new AnimatedSprite(game, GetTextureFile(
				towerType, "png/", num2), new Vector2f(x, y), 6,
				num2.argvalue, Constants.INFO_SPRITE_SIZE,
				Constants.INFO_SPRITE_SIZE, scale);
		sprite.setAnimationSpeedRatio(3);
		return sprite;
	}

	/** Runtime toolbar — position is toolbar anchor in screen pixels. */
	public static AnimatedSprite GetAnimatedSpriteTowerForTowerToolbar(
			MainGame game, Vector2f towerToolbarDrawPosition,
			TowerType towerType, float scale) {

		RefObject<Integer> num2 = new RefObject<Integer>(0);
		return new AnimatedSprite(game, GetTextureFile(towerType, "png/", num2),
				towerToolbarDrawPosition.cpy().add(
						Constants.TOWER_TOOLBAR_SPRITE_OFFSET_X,
						Constants.TOWER_TOOLBAR_SPRITE_OFFSET_Y), 6,
				num2.argvalue, Constants.INFO_SPRITE_SIZE,
				Constants.INFO_SPRITE_SIZE, scale);
	}

	private static String GetTextureFile(TowerType towerType, String subDir,
			RefObject<Integer> spriteCount) {
		String str = "";
		spriteCount.argvalue = 0x24;
		switch (towerType) {
		case Axe:
			str = "towerinfo1";
			break;

		case Spear:
			str = "towerinfo2";
			break;

		case AirDefence:
			str = "towerinfo3";
			break;

		case Lur:
			str = "towerinfo4";
			break;
		}
		return ("assets/" + subDir + str + ".png");
	}
}
