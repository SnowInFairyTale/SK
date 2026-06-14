package org.test;

import java.util.HashMap;

import com.loon.core.Assets;
import com.loon.media.BigClip;
import com.loon.media.Sound;
import com.loon.utils.MathUtils;

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
	private static final HashMap<String, Long> lastActualPlayedAt = new HashMap<String, Long>();

	private static final int BUTTON_CLICK_MIN_DELAY_MS = 300;
	private static final int ATTACK_MIN_DELAY_MS = 300;
	private static final int DROP_MIN_DELAY_MS = 300;
	private static final int GEM_DROP_MIN_DELAY_MS = 0;
	private static final int NO_MIN_DELAY_MS = 300;
	private static final int ATTACK_FORCE_PLAY_AFTER_MS = 900;
	private static final float ATTACK_PLAY_PROBABILITY = 0.7f;
	private static final float ATTACK_VOLUME_MIN = 0.8f;
	private static final float ATTACK_VOLUME_MAX = 1f;
	private static final float ATTACK_RATE_MIN = 0.94f;
	private static final float ATTACK_RATE_MAX = 1.06f;
	private static final float CASH_VOLUME_MIN = 0.8f;
	private static final float CASH_VOLUME_MAX = 1f;
	private static final float CASH_RATE_MIN = 0.97f;
	private static final float CASH_RATE_MAX = 1.03f;
	private static final float DEFAULT_RATE = 1f;

	private static MainGame game;
	private static Sound battleMusic;

	private static final Object[][] SOUND_FILES = {
			{ AXE_ATTACK, "sounds/axe_attack.mp3", Float.valueOf(0.85f) },
			{ SPEAR_ATTACK, "sounds/spear_attack.ogg", Float.valueOf(0.5f) },
			{ BUILDING, "sounds/building.ogg", Float.valueOf(1f) },
			{ BUTTON_CLICK, "sounds/button_click.wav", Float.valueOf(0.85f) },
			{ CASH_DROP, "sounds/cach_drop.mp3", Float.valueOf(0.5f) },
			{ FAILED, "sounds/failed.mp3", Float.valueOf(1f) },
			{ SUCCESS, "sounds/success.mp3", Float.valueOf(1f) },
			{ UPGARD, "sounds/upgard.mp3", Float.valueOf(1f) },
			{ LUR_ATTACK, "sounds/lur_attack.mp3", Float.valueOf(1f) },
			{ GEM_DROP, "sounds/gem_drop.mp3", Float.valueOf(1f) } };

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
		if (!shouldPlayByProbability(name, now)) {
			lastPlayedAt.put(name, Long.valueOf(now));
			return;
		}
		Sound sound = getSound(name);
		if (sound == null) {
			return;
		}
		lastPlayedAt.put(name, Long.valueOf(now));
		lastActualPlayedAt.put(name, Long.valueOf(now));
		if (shouldRestart(name)) {
			sound.stop();
		}
		sound.play(resolvePlayVolume(name), resolvePlayRate(name));
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
			battleMusic.setVolume(0.1f);
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

	public static synchronized void PauseBattleMusic() {
		if (battleMusic instanceof BigClip) {
			((BigClip) battleMusic).pausePlayback();
		} else if (battleMusic != null) {
			battleMusic.stop();
		}
	}

	public static synchronized void ResumeBattleMusic() {
		if ((game != null) && !game.getSoundEnabled()) {
			return;
		}
		if (battleMusic instanceof BigClip) {
			((BigClip) battleMusic).resumePlayback();
		} else {
			PlayBattleMusic();
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
		return 1f;
	}

	private static boolean shouldRestart(String name) {
		return BUTTON_CLICK.equals(name) || UPGARD.equals(name)
				|| BUILDING.equals(name);
	}

	private static boolean isAttackSound(String name) {
		return AXE_ATTACK.equals(name) || SPEAR_ATTACK.equals(name)
				|| LUR_ATTACK.equals(name);
	}

	private static boolean shouldPlayByProbability(String name, long now) {
		if (isAttackSound(name)) {
			Long lastActual = lastActualPlayedAt.get(name);
			if ((lastActual == null) || ((now - lastActual.longValue()) >= ATTACK_FORCE_PLAY_AFTER_MS)) {
				return true;
			}
			return MathUtils.random() <= ATTACK_PLAY_PROBABILITY;
		}
		return true;
	}

	private static float resolvePlayVolume(String name) {
		float baseVolume = getVolume(name);
		if (isAttackSound(name)) {
			return baseVolume
					* MathUtils.random(ATTACK_VOLUME_MIN, ATTACK_VOLUME_MAX);
		}
		if (CASH_DROP.equals(name)) {
			return baseVolume
					* MathUtils.random(CASH_VOLUME_MIN, CASH_VOLUME_MAX);
		}
		return baseVolume;
	}

	private static float resolvePlayRate(String name) {
		if (isAttackSound(name)) {
			return MathUtils.random(ATTACK_RATE_MIN, ATTACK_RATE_MAX);
		}
		if (CASH_DROP.equals(name)) {
			return MathUtils.random(CASH_RATE_MIN, CASH_RATE_MAX);
		}
		return DEFAULT_RATE;
	}

	private static int defaultMinDelay(String name) {
		if (BUTTON_CLICK.equals(name)) {
			return BUTTON_CLICK_MIN_DELAY_MS;
		}
		if (isAttackSound(name)) {
			return ATTACK_MIN_DELAY_MS;
		}
		if (CASH_DROP.equals(name)) {
			return DROP_MIN_DELAY_MS;
		}
		if (GEM_DROP.equals(name)) {
			return GEM_DROP_MIN_DELAY_MS;
		}
		return NO_MIN_DELAY_MS;
	}
}
