package org.test;

import loon.core.RefObject;
import loon.core.geom.Vector2f;

public class AnimatedSpriteMonster {
	
	public static java.util.ArrayList<AnimatedSprite> GetAllAnimatedSpriteMonsters(
			MainGame game) {

		java.util.ArrayList<AnimatedSprite> list = new java.util.ArrayList<AnimatedSprite>();
		int num = Constants.s(8);
		int num2 = Constants.s(0xe8);
		int spriteWidth = Constants.s(80);
		int spriteHeight = Constants.s(80);

		RefObject<Integer> numValue = new RefObject<Integer>(0);

		AnimatedSprite item = new AnimatedSprite(game, GetTextureFile(
				MonsterType.Peasant, "png/", numValue), new Vector2f(
				(float) num, 32f), 6, numValue.argvalue, spriteWidth,
				spriteHeight, 1f);
		item.setAnimationSpeedRatio(3);
		list.add(item);
		numValue.argvalue = 0;

		AnimatedSprite sprite2 = new AnimatedSprite(game, GetTextureFile(
				MonsterType.Peon, "png/", numValue), new Vector2f((float) num2,
				288f), 6, numValue.argvalue, spriteWidth, spriteHeight, 1f);
		sprite2.setAnimationSpeedRatio(3);
		list.add(sprite2);
		numValue.argvalue = 0;

		AnimatedSprite sprite3 = new AnimatedSprite(game, GetTextureFile(
				MonsterType.Berserker, "png/", numValue), new Vector2f(
				(float) num, 528f), 6, numValue.argvalue, spriteWidth,
				spriteHeight, 1f);
		sprite3.setAnimationSpeedRatio(3);
		list.add(sprite3);
		numValue.argvalue = 0;

		AnimatedSprite sprite4 = new AnimatedSprite(game, GetTextureFile(
				MonsterType.Chicken, "png/", numValue), new Vector2f(
				(float) num2, 800f), 6, numValue.argvalue, spriteWidth,
				spriteHeight, 1f);
		sprite4.setAnimationSpeedRatio(3);
		list.add(sprite4);
		numValue.argvalue = 0;

		AnimatedSprite sprite5 = new AnimatedSprite(game, GetTextureFile(
				MonsterType.Doctor, "png/", numValue), new Vector2f(
				(float) num, 1032f), 6, numValue.argvalue, spriteWidth,
				spriteHeight, 1f);
		sprite5.setAnimationSpeedRatio(3);
		list.add(sprite5);
		numValue.argvalue = 0;

		AnimatedSprite sprite6 = new AnimatedSprite(game, GetTextureFile(
				MonsterType.Chieftain, "png/", numValue), new Vector2f(
				(float) num2, 1288f), 6, numValue.argvalue, spriteWidth,
				spriteHeight, 1f);
		sprite6.setAnimationSpeedRatio(3);
		list.add(sprite6);
		numValue.argvalue = 0;

		return list;
	}

	public static AnimatedSprite GetAnimatedSpriteMonsterForMonsterToolbar(
			MainGame game, Vector2f monsterToolbarDrawPosition,
			MonsterType monsterType) {
		RefObject<Integer> tempRef_num = new RefObject<Integer>(0);
		AnimatedSprite tempVar = new AnimatedSprite(game, GetTextureFile(
				monsterType, "png/", tempRef_num),
				monsterToolbarDrawPosition.add(Constants.s(-2f),
				Constants.s(-34f)), 6, tempRef_num.argvalue, Constants.s(80),
				Constants.s(80), 1f);
		return tempVar;
	}

	public static AnimatedSprite GetSmallAnimatedSpriteMonster(MainGame game,
			MonsterType monsterType) {
		RefObject<Integer> tempRef_num = new RefObject<Integer>(0);
		AnimatedSprite tempVar = new AnimatedSprite(game, GetTextureFile(
				monsterType, "", tempRef_num), new Vector2f(Constants.s(200f),
				Constants.s(-4f)), 12,
				tempRef_num.argvalue, Constants.s(80), Constants.s(80), 0.5f);
		return tempVar;
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