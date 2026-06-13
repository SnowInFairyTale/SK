package org.test;

import java.util.HashMap;

import com.loon.core.Assets;
import com.loon.media.Sound;

public class SoundManager {

	public static final String AXE_ATTACK = "axe_attack";
	public static final String SPEAR_ATTACK = "spear_attack";
	public static final String BUILDING = "building";
	public static final String BUTTON_CLICK = "button_click";
	public static final String CASH_DROP = "cach_drop";
	public static final String FAILED = "failed";
	public static final String SUCCESS = "success";
	public static final String UPGARD = "upgard";
	public static final String LUR_ATTACK = "lur_attack";
	public static final String GEM_DROP = "gem_drop";
	public static final String BATTLE_MUSIC = "bj";

	private static final HashMap<String, Sound> sounds = new HashMap<String, Sound>();
	private static final HashMap<String, Long> lastPlayedAt = new HashMap<String, Long>();

	private static MainGame game;
	private static Sound battleMusic;

	private static final Object[][] SOUND_FILES = {
			{ AXE_ATTACK, "sounds/axe_attack.mp3", Float.valueOf(0.9f) },
			{ SPEAR_ATTACK, "sounds/spear_attack.ogg", Float.valueOf(0.9f) },
			{ BUILDING, "sounds/building.ogg", Float.valueOf(0.9f) },
			{ BUTTON_CLICK, "sounds/button_click.wav", Float.valueOf(0.9f) },
			{ CASH_DROP, "sounds/cach_drop.mp3", Float.valueOf(0.85f) },
			{ FAILED, "sounds/failed.mp3", Float.valueOf(0.9f) },
			{ SUCCESS, "sounds/success.mp3", Float.valueOf(0.9f) },
			{ UPGARD, "sounds/upgard.mp3", Float.valueOf(0.9f) },
			{ LUR_ATTACK, "sounds/lur_attack.mp3", Float.valueOf(1f) },
			{ GEM_DROP, "sounds/gem_drop.mp3", Float.valueOf(0.9f) } };

	public static synchronized void Initialize(MainGame mainGame) {
		game = mainGame;
		Preload();
	}

	public static synchronized void Preload() {
		for (int i = 0; i < SOUND_FILES.length; i++) {
			getSound((String) SOUND_FILES[i][0]);
		}
	}

	public static void PlaySound(String name) {
		PlaySound(name, defaultMinDelay(name));
	}

	public static synchronized void PlaySound(String name, int minDelay) {
		if ((game != null) && !game.getSoundEnabled()) {
			return;
		}
		long now = System.currentTimeMillis();
		Long last = lastPlayedAt.get(name);
		if ((last != null) && ((now - last.longValue()) < minDelay)) {
			return;
		}
		Sound sound = getSound(name);
		if (sound == null) {
			return;
		}
		lastPlayedAt.put(name, Long.valueOf(now));
		sound.stop();
		sound.play();
	}

	public static void PlaySound() {
		PlaySound(BUTTON_CLICK);
	}

	public static void PlaySoundHighPriority() {
		PlaySound(BUTTON_CLICK, 50);
	}

	public static synchronized void PlayBattleMusic() {
		if ((game != null) && !game.getSoundEnabled()) {
			return;
		}
		if (battleMusic == null) {
			battleMusic = Assets.getMusic("sounds/bj.mp3");
			battleMusic.setLooping(true);
			battleMusic.setVolume(0.5f);
		}
		if (!battleMusic.isPlaying()) {
			battleMusic.play();
		}
	}

	public static synchronized void StopBattleMusic() {
		if (battleMusic != null) {
			battleMusic.stop();
		}
	}

	private static Sound getSound(String name) {
		Sound sound = sounds.get(name);
		if (sound == null) {
			String path = getPath(name);
			if (path == null) {
				return null;
			}
			sound = Assets.getSound(path);
			sound.setVolume(getVolume(name));
			sounds.put(name, sound);
		}
		return sound;
	}

	private static String getPath(String name) {
		for (int i = 0; i < SOUND_FILES.length; i++) {
			if (((String) SOUND_FILES[i][0]).equals(name)) {
				return (String) SOUND_FILES[i][1];
			}
		}
		return null;
	}

	private static float getVolume(String name) {
		for (int i = 0; i < SOUND_FILES.length; i++) {
			if (((String) SOUND_FILES[i][0]).equals(name)) {
				return ((Float) SOUND_FILES[i][2]).floatValue();
			}
		}
		return 0.9f;
	}

	private static int defaultMinDelay(String name) {
		if (BUTTON_CLICK.equals(name)) {
			return 60;
		}
		if (AXE_ATTACK.equals(name) || SPEAR_ATTACK.equals(name)
				|| LUR_ATTACK.equals(name)) {
			return 80;
		}
		if (CASH_DROP.equals(name) || GEM_DROP.equals(name)) {
			return 80;
		}
		return 0;
	}
}
