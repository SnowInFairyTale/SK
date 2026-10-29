package org.test;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.loon.media.Sound;
import com.loon.media.SoundImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EffectSoundPlaybackTest {
	private static final String[] EFFECTS = {
			SoundManager.BLADE, SoundManager.CLEAVE,
			SoundManager.LIGHTNING, SoundManager.PIERCING
	};
	private HashMap<String, Sound> sounds;
	private HashMap<String, Sound> originalSounds;

	@SuppressWarnings("unchecked")
	@Before
	public void replaceSounds() throws Exception {
		Field field = SoundManager.class.getDeclaredField("sounds");
		field.setAccessible(true);
		sounds = (HashMap<String, Sound>) field.get(null);
		originalSounds = new HashMap<>(sounds);
		sounds.clear();
	}

	@After
	public void restoreSounds() {
		sounds.clear();
		sounds.putAll(originalSounds);
	}

	@Test
	public void everyEffectRequestOverlapsWithoutCooldownOrStoppingPreviousAudio() {
		for (String name : EFFECTS) {
			RecordingSound sound = new RecordingSound();
			sound.onLoaded(1);
			sounds.put(name, sound);
			for (int i = 0; i < 50; i++) {
				SoundManager.PlaySound(name, Integer.MAX_VALUE);
			}
			assertEquals(name, 50, sound.priorities.size());
			assertEquals(0, sound.stops);
			for (int priority : sound.priorities) assertTrue(priority > 1);
			assertEquals(1f, sound.lastVolume, 0f);
			assertEquals(1f, sound.lastRate, 0f);
		}
	}

	@Test
	public void loadingPreservesEveryRequestAndDoesNotReplayTheLastOneTwice() {
		for (String name : EFFECTS) {
			RecordingSound sound = new RecordingSound();
			sounds.put(name, sound);
			for (int i = 0; i < 12; i++) SoundManager.PlaySound(name);
			assertTrue(sound.priorities.isEmpty());
			sound.onLoaded(1);
			assertEquals(name, 12, sound.priorities.size());
			SoundManager.PlaySound(name);
			assertEquals(13, sound.priorities.size());
			assertEquals(0, sound.stops);
		}
	}

	@Test
	public void ordinaryPlaybackRetainsDefaultPriorityAndDeferredStart() {
		RecordingSound sound = new RecordingSound();
		assertFalse(sound.play(0.5f, 1f));
		sound.onLoaded(1);
		assertEquals(1, sound.priorities.size());
		assertEquals(1, sound.priorities.get(0).intValue());
		assertEquals(0.5f, sound.lastVolume, 0f);
	}

	private static final class RecordingSound extends SoundImpl<Integer> {
		final List<Integer> priorities = new ArrayList<>();
		int stops;
		float lastVolume, lastRate;
		@Override protected boolean playImpl() {
			priorities.add(playPriority);
			lastVolume = playVolume;
			lastRate = playRate;
			return true;
		}
		@Override protected void stopImpl() { stops++; }
		@Override protected void setLoopingImpl(boolean looping) { }
		@Override protected void setVolumeImpl(float volume) { }
		@Override protected void releaseImpl() { }
	}
}
