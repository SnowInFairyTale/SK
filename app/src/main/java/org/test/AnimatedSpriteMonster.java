package org.test;

import loon.core.RefObject;
import loon.core.geom.Vector2f;

public class AnimatedSpriteMonster {

	/** Info screen: left column X for monster preview sprites. */
	private static final float INFO_COL_LEFT = 16f;
	/** Info screen: right column X for monster preview sprites. */
	private static final float INFO_COL_RIGHT = 464f;

	public static java.util.ArrayList<AnimatedSprite> GetAllAnimatedSpriteMonsters(
			MainGame game) {

		java.util.ArrayList<AnimatedSprite> list = new java.util.ArrayList<AnimatedSprite>();

		RefObject<Integer> numValue = new RefObject<Integer>(0);

		AnimatedSprite item = new AnimatedSprite(game, GetTextureFile(
				MonsterType.Peasant, "png/", numValue), new Vector2f(
				INFO_COL_LEFT, 16f), 6, numValue.argvalue,
				Constants.INFO_SPRITE_SIZE, Constants.INFO_SPRITE_SIZE, 1f);
		item.setAnimationSpeedRatio(3);
		list.add(item);
		numValue.argvalue = 0;

		AnimatedSprite sprite2 = new AnimatedSprite(game, GetTextureFile(
				MonsterType.Peon, "png/", numValue), new Vector2f(
				INFO_COL_RIGHT, 144f), 6, numValue.argvalue,
				Constants.INFO_SPRITE_SIZE, Constants.INFO_SPRITE_SIZE, 1f);
		sprite2.setAnimationSpeedRatio(3);
		list.add(sprite2);
		numValue.argvalue = 0;

		AnimatedSprite sprite3 = new AnimatedSprite(game, GetTextureFile(
				MonsterType.Berserker, "png/", numValue), new Vector2f(
				INFO_COL_LEFT, 264f), 6, numValue.argvalue,
				Constants.INFO_SPRITE_SIZE, Constants.INFO_SPRITE_SIZE, 1f);
		sprite3.setAnimationSpeedRatio(3);
		list.add(sprite3);
		numValue.argvalue = 0;

		AnimatedSprite sprite4 = new AnimatedSprite(game, GetTextureFile(
				MonsterType.Chicken, "png/", numValue), new Vector2f(
				INFO_COL_RIGHT, 400f), 6, numValue.argvalue,
				Constants.INFO_SPRITE_SIZE, Constants.INFO_SPRITE_SIZE, 1f);
		sprite4.setAnimationSpeedRatio(3);
		list.add(sprite4);
		numValue.argvalue = 0;

		AnimatedSprite sprite5 = new AnimatedSprite(game, GetTextureFile(
				MonsterType.Doctor, "png/", numValue), new Vector2f(
				INFO_COL_LEFT, 516f), 6, numValue.argvalue,
				Constants.INFO_SPRITE_SIZE, Constants.INFO_SPRITE_SIZE, 1f);
		sprite5.setAnimationSpeedRatio(3);
		list.add(sprite5);
		numValue.argvalue = 0;

		AnimatedSprite sprite6 = new AnimatedSprite(game, GetTextureFile(
				MonsterType.Chieftain, "png/", numValue), new Vector2f(
				INFO_COL_RIGHT, 644f), 6, numValue.argvalue,
				Constants.INFO_SPRITE_SIZE, Constants.INFO_SPRITE_SIZE, 1f);
		sprite6.setAnimationSpeedRatio(3);
		list.add(sprite6);
		numValue.argvalue = 0;

		return list;
	}

	public static AnimatedSprite GetAnimatedSpriteMonsterForMonsterToolbar(
			MainGame game, Vector2f monsterToolbarDrawPosition,
			MonsterType monsterType) {
		RefObject<Integer> tempRef_num = new RefObject<Integer>(0);
		return new AnimatedSprite(game, GetTextureFile(monsterType, "png/",
				tempRef_num), monsterToolbarDrawPosition.cpy().add(
				Constants.MONSTER_TOOLBAR_SPRITE_OFFSET_X,
				Constants.MONSTER_TOOLBAR_SPRITE_OFFSET_Y), 6,
				tempRef_num.argvalue, Constants.INFO_SPRITE_SIZE,
				Constants.INFO_SPRITE_SIZE, 1f);
	}

	public static AnimatedSprite GetSmallAnimatedSpriteMonster(MainGame game,
			MonsterType monsterType) {
		RefObject<Integer> tempRef_num = new RefObject<Integer>(0);
		return new AnimatedSprite(game, GetTextureFile(monsterType, "",
				tempRef_num), new Vector2f(400f, -8f), 12,
				tempRef_num.argvalue, Constants.INFO_SPRITE_SIZE,
				Constants.INFO_SPRITE_SIZE, 0.5f);
	}

	private static String GetTextureFile(MonsterType monsterType,
			String subDir, RefObject<Integer> spriteCount) {
		String str = "";
		spriteCount.argvalue = 0;
		switch (monsterType) {
		case Peasant:
			str = "monsterinfo1";
			spriteCount.argvalue = 0x10;
			break;

		case Peon:
			str = "monsterinfo2";
			spriteCount.argvalue = 15;
			break;

		case Berserker:
			str = "monsterinfo3";
			spriteCount.argvalue = 0x15;
			break;

		case Chicken:
			str = "monsterinfo4";
			spriteCount.argvalue = 0x22;
			break;

		case Doctor:
			str = "monsterinfo5";
			spriteCount.argvalue = 0x19;
			break;

		case Chieftain:
			str = "monsterinfo6";
			spriteCount.argvalue = 0x1b;
			break;
		}
		return ("assets/" + subDir + str + ".png");
	}
}
