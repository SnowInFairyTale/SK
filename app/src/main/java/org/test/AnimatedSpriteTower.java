package org.test;

import loon.core.RefObject;
import loon.core.geom.Vector2f;

public class AnimatedSpriteTower extends AnimatedSprite {
	
	public AnimatedSpriteTower(MainGame game, String textureFile,
			Vector2f position, int spriteCount) {
		super(game, textureFile, position, 6, spriteCount, Constants.s(80),
				Constants.s(80), 1f);
		super.setAnimationSpeedRatio(3);
	}

	public static java.util.ArrayList<AnimatedSpriteTower> GetAllAnimatedSpriteTowers(
			MainGame game) {

		int num = 40;

		java.util.ArrayList<AnimatedSpriteTower> list = new java.util.ArrayList<AnimatedSpriteTower>();

		RefObject<Integer> num2 = new RefObject<Integer>(0);
		list.add(new AnimatedSpriteTower(game, GetTextureFile(TowerType.Axe,
				"png/", num2), new Vector2f((float) num, 72f), num2.argvalue));
		num2.argvalue = 0;

		list.add(new AnimatedSpriteTower(game, GetTextureFile(TowerType.Spear,
				"png/", num2), new Vector2f((float) num, 472f), num2.argvalue));
		num2.argvalue = 0;

		list.add(new AnimatedSpriteTower(game, GetTextureFile(
				TowerType.AirDefence, "png/", num2), new Vector2f((float) num,
				872f), num2.argvalue));
		num2.argvalue = 0;

		list.add(new AnimatedSpriteTower(game, GetTextureFile(TowerType.Lur,
				"png/", num2), new Vector2f((float) num, 1272f), num2.argvalue));
		num2.argvalue = 0;

		return list;
	}

	public static AnimatedSprite GetAnimatedSpriteTowerForTowerToolbar(
			MainGame game, Vector2f towerToolbarDrawPosition, TowerType towerType,
			float scale) {

		RefObject<Integer> num2 = new RefObject<Integer>(0);
		AnimatedSprite tempVar = new AnimatedSprite(game, GetTextureFile(
				towerType, "png/", num2), towerToolbarDrawPosition.add(
				Constants.s(-2f), Constants.s(-34f)), 6, num2.argvalue,
				Constants.s(80), Constants.s(80), scale);

		return tempVar;
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