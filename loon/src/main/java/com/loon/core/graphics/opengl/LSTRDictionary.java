package com.loon.core.graphics.opengl;

import java.util.HashMap;
import java.util.LinkedHashSet;

import com.loon.core.LRelease;
import com.loon.core.LSystem;
import com.loon.core.event.Updateable;
import com.loon.core.graphics.LColor;
import com.loon.core.graphics.LFont;

/**
 * Copyright 2008 - 2011
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
public final class LSTRDictionary {

	private static final Object LOCK = new Object();

	private final static HashMap<LFont, Dict> fontList = new HashMap<LFont, Dict>(
			20);

	public final static String added = "0123456789";

	public final static char split = '$';

	private static String globalChars = "";

	static class Dict implements LRelease {

		private final LinkedHashSet<Character> chars = new LinkedHashSet<Character>(
				512);

		LSTRFont font;

		static Dict newDict() {
			return new Dict();
		}

		@Override
		public void dispose() {
			if (font != null) {
				font.dispose();
				font = null;
			}
			chars.clear();
		}

		boolean addChars(String text) {
			boolean changed = false;
			if (text == null) {
				return false;
			}
			for (int i = 0; i < text.length(); i++) {
				if (chars.add(Character.valueOf(text.charAt(i)))) {
					changed = true;
				}
			}
			return changed;
		}

		String charsAsString() {
			StringBuilder builder = new StringBuilder(chars.size());
			for (Character ch : chars) {
				builder.append(ch.charValue());
			}
			return builder.toString();
		}

		void rebuildFont(LFont sourceFont) {
			if (font != null) {
				font.dispose();
				font = null;
			}
			font = new LSTRFont(sourceFont, charsAsString());
		}

	}

	public static void clearStringLazy() {
		synchronized (LOCK) {
			for (Dict d : fontList.values()) {
				if (d != null) {
					d.dispose();
				}
			}
			fontList.clear();
		}
	}

	public final static Dict bind(final LFont font, final String mes) {
		synchronized (LOCK) {
			return bindLocked(font, mes);
		}
	}

	public static void addGlobalChars(String chars) {
		if (chars == null || chars.length() == 0) {
			return;
		}
		final String preloadChars = chars;
		if (!LSystem.isThreadDrawing()) {
			LSystem.load(new Updateable() {
				@Override
				public void action() {
					addGlobalChars(preloadChars);
				}
			});
			return;
		}
		synchronized (LOCK) {
			Dict globalDict = Dict.newDict();
			globalDict.addChars(globalChars);
			if (!globalDict.addChars(chars)) {
				return;
			}
			globalChars = globalDict.charsAsString();
			for (java.util.Map.Entry<LFont, Dict> entry : fontList.entrySet()) {
				Dict dict = entry.getValue();
				if (dict != null && dict.addChars(globalChars)) {
					dict.rebuildFont(entry.getKey());
				}
			}
		}
	}

	public final static void drawString(LFont font, String message, float x,
			float y, float angle, LColor c) {
		synchronized (LOCK) {
			Dict pDict = bindLocked(font, message);
			if (pDict.font != null) {
				synchronized (pDict.font) {
					pDict.font.drawString(message, x, y, angle, c);
				}
			}
		}
	}

	public final static void drawString(LFont font, String message, float x,
			float y, float sx, float sy, float ax, float ay, float angle,
			LColor c) {
		synchronized (LOCK) {
			Dict pDict = bindLocked(font, message);
			if (pDict.font != null) {
				synchronized (pDict.font) {
					pDict.font.drawString(message, x, y, sx, sy, ax, ay, angle,
							c);
				}
			}
		}
	}

	private static Dict bindLocked(final LFont font, final String mes) {
		Dict dict = fontList.get(font);
		if (dict == null) {
			dict = Dict.newDict();
			fontList.put(font, dict);
		}
		String message = (mes == null ? "" : mes) + added + globalChars;
		if (dict.addChars(message)) {
			dict.rebuildFont(font);
		}
		return dict;
	}

	/**
	 * 生成特定字符串的缓存用ID
	 * 
	 * @param font
	 * @param text
	 * @return
	 */
	public static String makeStringLazyKey(final LFont font, final String text) {
		int hashCode = 0;
		hashCode = LSystem.unite(hashCode, font.getSize());
		hashCode = LSystem.unite(hashCode, font.getStyle());
		StringBuilder key = new StringBuilder();
		key.append(font.getFontName().toLowerCase());
		key.append(hashCode);
		key.append(split);
		key.append(text);
		return key.toString();
	}

	public final static void dispose() {
		clearStringLazy();
	}

}
