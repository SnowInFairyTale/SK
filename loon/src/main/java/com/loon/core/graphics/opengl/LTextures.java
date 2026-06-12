package com.loon.core.graphics.opengl;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.loon.core.LSystem;
import com.loon.core.event.Updateable;
import com.loon.core.graphics.opengl.LTexture.Format;


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

public class LTextures {

	private static final ConcurrentHashMap<String, LTexture> lazyTextures = new ConcurrentHashMap<String, LTexture>(
			100);

	private static final AtomicLong generatedTextureSerial = new AtomicLong();

	public static int count() {
		return lazyTextures.size();
	}

	public static LTexture loadTexture(String path) {
		return loadTexture(path, Format.DEFAULT);
	}

	public static boolean containsValue(LTexture texture) {
		return lazyTextures.containsValue(texture);
	}

	public static int getRefCount(LTexture texture) {
		return getRefCount(texture.lazyName);
	}

	public static int getRefCount(String fileName) {
		if (fileName == null) {
			return 0;
		}
		String key = normalizeKey(fileName);
		LTexture texture = lazyTextures.get(key);
		if (texture != null) {
			synchronized (texture) {
				return texture.refCount;
			}
		}
		return 0;
	}

	public static LTexture loadTexture(String fileName, Format format) {
		if (fileName == null) {
			return null;
		}
		String key = normalizeKey(fileName);
		while (true) {
			LTexture texture = lazyTextures.get(key);
			if (texture != null) {
				synchronized (texture) {
					if (!texture.isClose) {
						texture.refCount++;
						return texture;
					}
				}
				lazyTextures.remove(key, texture);
				continue;
			}
			LTexture created = new LTexture(fileName, format);
			created.lazyName = key;
			LTexture raced = lazyTextures.putIfAbsent(key, created);
			if (raced == null) {
				return created;
			}
		}
	}

	public static LTexture loadTexture(LTexture texture) {
		return loadTexture(0, texture);
	}

	public static LTexture loadTexture(long id, LTexture tex2d) {
		if (tex2d == null) {
			return null;
		}
		if (tex2d.lazyName == null) {
			synchronized (tex2d) {
				if (tex2d.lazyName == null) {
					tex2d.lazyName = "generated:"
							+ generatedTextureSerial.incrementAndGet();
				}
			}
		}
		String key = normalizeKey(tex2d.lazyName);
		tex2d.lazyName = key;
		while (true) {
			LTexture texture = lazyTextures.get(key);
			if (texture != null) {
				synchronized (texture) {
					if (!texture.isClose) {
						texture.refCount++;
						return texture;
					}
				}
				lazyTextures.remove(key, texture);
				continue;
			}
			LTexture raced = lazyTextures.putIfAbsent(key, tex2d);
			if (raced == null) {
				return tex2d;
			}
		}
	}

	public static int removeTexture(String name, final boolean remove) {
		if (name == null) {
			return -1;
		}
		final String key = normalizeKey(name);
		final LTexture texture = lazyTextures.get(key);
		if (texture != null) {
			int refCount;
			synchronized (texture) {
				if (texture.refCount > 0) {
					texture.refCount--;
					return texture.refCount;
				}
				if (remove) {
					lazyTextures.remove(key, texture);
				}
				if (texture.isClose) {
					return texture.refCount;
				}
				texture.isLoaded = false;
				texture.isClose = true;
				texture.reload = false;
				refCount = texture.refCount;
			}
			closeTexture(texture);
			return refCount;
		}
		return -1;
	}

	public static int removeTexture(LTexture texture, final boolean remove) {
		if (texture == null) {
			return -1;
		}
		return removeTexture(texture.lazyName, remove);
	}

	public static void reload() {
		if (lazyTextures.size() > 0) {
			LTexture[] textures = lazyTextures.values().toArray(
					new LTexture[0]);
			for (final LTexture texture : textures) {
				if (texture != null && !texture.isClose) {
					synchronized (texture) {
						texture.isLoaded = false;
						texture.reload = true;
						texture._hashCode = 1;
					}
					if (texture.childs != null) {
						Updateable u = new Updateable() {
							@Override
							public void action() {
								synchronized (texture) {
									texture.loadTexture();
									if (texture.childs != null) {
										for (int i = 0; i < texture.childs.size(); i++) {
											LTexture child = texture.childs
													.get(i);
											if (child != null) {
												synchronized (child) {
													child.textureID = texture.textureID;
													child.isLoaded = texture.isLoaded;
													child.reload = texture.reload;
												}
												if (GLEx.isVbo()) {
													child.bufferID = GLEx
															.createBufferID();
													GLEx.bufferDataARR(
															child.bufferID,
															texture.data,
															GL11.GL_STATIC_DRAW);
												}
											}
										}
									}
								}
								LTextureBatch.isBatchCacheDitry = true;
							}
						};
						LSystem.load(u);
					}
				}
			}
		}
		GLUtils.reload();
	}

	public static void disposeAll() {
		if (lazyTextures.size() > 0) {
			LTexture[] textures = lazyTextures.values().toArray(
					new LTexture[0]);
			for (LTexture tex2d : textures) {
				if (tex2d != null && !tex2d.isClose) {
					removeTextureMapping(tex2d);
					boolean close = false;
					synchronized (tex2d) {
						tex2d.refCount = 0;
						if (!tex2d.isClose) {
							tex2d.isLoaded = false;
							tex2d.isClose = true;
							tex2d.reload = false;
							close = true;
						}
					}
					if (close) {
						closeTexture(tex2d);
					}
					tex2d = null;
				}
			}
		}
		LSTRDictionary.dispose();
	}

	public static void destroyAll() {
		if (lazyTextures.size() > 0) {
			LTexture[] textures = lazyTextures.values().toArray(
					new LTexture[0]);
			for (LTexture tex2d : textures) {
				if (tex2d != null && !tex2d.isClose) {
					removeTextureMapping(tex2d);
					boolean close = false;
					synchronized (tex2d) {
						tex2d.refCount = 0;
						if (!tex2d.isClose) {
							tex2d.isLoaded = false;
							tex2d.isClose = true;
							tex2d.reload = false;
							close = true;
						}
					}
					if (close) {
						closeTexture(tex2d);
					}
					tex2d.freeCache();
					tex2d.freeBatch();
					tex2d = null;
				}
			}
		}
		LSTRDictionary.dispose();
	}

	private static String normalizeKey(String name) {
		return name.trim().toLowerCase(Locale.ROOT);
	}

	private static void removeTextureMapping(LTexture texture) {
		if (texture != null && texture.lazyName != null) {
			lazyTextures.remove(normalizeKey(texture.lazyName), texture);
		}
	}

	private static void closeTexture(final LTexture texture) {
		Updateable u = new Updateable() {
			@Override
			public void action() {
				synchronized (texture) {
					if (texture.textureID > 0) {
						if (texture.parent == null) {
							GLEx.deleteTexture(texture.textureID);
						}
						texture.textureID = -1;
						GLEx.deleteBuffer(texture.bufferID);
						texture.bufferID = -1;
					}
					texture.isLoaded = false;
					texture.isClose = true;
					LTextureBatch.isBatchCacheDitry = true;
				}
			}
		};
		LSystem.load(u);
		if (texture.imageData != null && texture.parent == null) {
			if (texture.imageData.fileName == null) {
				texture.imageData.source = null;
				texture.imageData = null;
			}
		}
		if (texture.childs != null) {
			texture.childs.clear();
			texture.childs = null;
		}
	}
}
