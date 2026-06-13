package org.test;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.loon.core.LSystem;

public final class VibrationManager {

	private static boolean enabled = true;
	private static final long TOWER_BUTTON_DURATION_MS = 20L;

	private VibrationManager() {
	}

	public static void VibrateTowerButton(MainGame game) {
		if (!enabled) {
			return;
		}
		if ((game != null) && !game.getVibrationEnabled()) {
			return;
		}
		Vibrate(TOWER_BUTTON_DURATION_MS);
	}

	public static void SetEnabled(boolean value) {
		enabled = value;
	}

	public static boolean IsEnabled() {
		return enabled;
	}

	private static void Vibrate(long durationMs) {
		if (!enabled) {
			return;
		}
		Context context = LSystem.getActivity();
		if (context == null) {
			return;
		}
		Vibrator vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		if ((vibrator == null) || !vibrator.hasVibrator()) {
			return;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			vibrator.vibrate(VibrationEffect.createOneShot(durationMs,
					VibrationEffect.DEFAULT_AMPLITUDE));
		} else {
			vibrator.vibrate(durationMs);
		}
	}
}
