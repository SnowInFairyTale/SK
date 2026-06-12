package com.loon.core.graphics.opengl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import com.loon.core.LSystem;

public final class LTextureDebugLog {

	private static final long MAX_BYTES = 512 * 1024;
	private static final long MIN_INTERVAL_MS = 250;
	private static final Object LOCK = new Object();

	private static File logFile;
	private static long lastWriteTime;
	private static String lastMessage;
	private static final HashMap<String, Long> RARE_WRITE_TIMES = new HashMap<String, Long>();

	private LTextureDebugLog() {
	}

	public static void write(String message) {
		message = sanitize(message);
		long now = System.currentTimeMillis();
		synchronized (LOCK) {
			if (message.equals(lastMessage)
					&& now - lastWriteTime < MIN_INTERVAL_MS) {
				return;
			}
			lastMessage = message;
			lastWriteTime = now;
			File file = getLogFile();
			if (file == null) {
				return;
			}
			trimIfNeeded(file);
			FileWriter writer = null;
			try {
				writer = new FileWriter(file, true);
				writer.write(now + " " + Thread.currentThread().getName() + " "
						+ message + "\n");
			} catch (IOException ignored) {
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException ignored) {
					}
				}
			}
		}
	}

	private static String sanitize(String message) {
		if (message == null) {
			return "null";
		}
		return message.replace('\n', ' ').replace('\r', ' ')
				.replace('\t', ' ');
	}

	public static void writeRare(String category, String message,
			long minIntervalMs) {
		long now = System.currentTimeMillis();
		synchronized (LOCK) {
			Long lastTime = RARE_WRITE_TIMES.get(category);
			if (lastTime != null && now - lastTime.longValue() < minIntervalMs) {
				return;
			}
			RARE_WRITE_TIMES.put(category, Long.valueOf(now));
		}
		write(message);
	}

	private static File getLogFile() {
		if (logFile != null) {
			return logFile;
		}
		if (LSystem.screenActivity == null) {
			return null;
		}
		File dir = LSystem.screenActivity.getExternalFilesDir("loon-trace");
		if (dir == null) {
			dir = new File(LSystem.screenActivity.getFilesDir(), "loon-trace");
		}
		if (!dir.exists() && !dir.mkdirs()) {
			return null;
		}
		logFile = new File(dir, "texture_trace.log");
		return logFile;
	}

	private static void trimIfNeeded(File file) {
		if (file.length() <= MAX_BYTES) {
			return;
		}
		FileWriter writer = null;
		try {
			writer = new FileWriter(file, false);
			writer.write(System.currentTimeMillis()
					+ " texture trace truncated\n");
		} catch (IOException ignored) {
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException ignored) {
				}
			}
		}
	}
}
