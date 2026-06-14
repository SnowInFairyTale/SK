/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package com.loon.media;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.SoundPool;

import com.loon.core.LSystem;
import com.loon.core.event.Updateable;

public class Audio {

	protected static <I> void dispatchLoaded(final SoundImpl<I> sound,
			final I impl) {
		Updateable update = new Updateable() {
			@Override
			public void action() {
				sound.onLoaded(impl);
			}
		};
		LSystem.unload(update);
	}

	protected static <I> void dispatchLoadError(final SoundImpl<I> sound,
			final Throwable error) {
		Updateable update = new Updateable() {
			@Override
			public void action() {
				sound.onLoadError(error);
			}
		};
		LSystem.unload(update);
	}

	interface Resolver<I> {
		void resolve(AndroidSound<I> sound);
	}

	private final HashSet<AndroidSound<?>> playing = new HashSet<AndroidSound<?>>();

	private final HashMap<Integer, PooledSound> loadingSounds = new HashMap<Integer, PooledSound>();
	private final HashSet<AndroidSound<MediaPlayer>> loadingMusic = new HashSet<AndroidSound<MediaPlayer>>();
	private final SoundPool pool;
	private volatile boolean destroyed;

	final static boolean notSupport() {
		return (LSystem.isDevice("GT-S5830B") || LSystem.isDevice("GT-I9100"));
	}

	private class PooledSound extends SoundImpl<Integer> {
		public final int soundId;
		private int streamId;

		public PooledSound(int soundId) {
			this.soundId = soundId;
		}

		@Override
		public String toString() {
			return "pooled:" + soundId;
		}

		@Override
		protected boolean playingImpl() {
			return streamId != 0 && playing;
		}

		@Override
		protected boolean playImpl() {
			if (destroyed || notSupport() || soundId == 0) {
				playing = false;
				return false;
			}
			streamId = pool.play(soundId, playVolume, playVolume, 1,
					looping ? -1 : 0, playRate);
			playing = streamId != 0;
			return playing;
		}

		@Override
		protected void stopImpl() {
			if (destroyed || notSupport()) {
				return;
			}
			if (streamId != 0) {
				pool.stop(streamId);
				streamId = 0;
			}
		}

		@Override
		protected void setLoopingImpl(boolean looping) {
			if (destroyed || notSupport()) {
				return;
			}
			if (streamId != 0) {
				pool.setLoop(streamId, looping ? -1 : 0);
			}
		}

		@Override
		protected void setVolumeImpl(float volume) {
			if (destroyed || notSupport()) {
				return;
			}
			if (streamId != 0) {
				pool.setVolume(streamId, volume, volume);
			}
		}

		@Override
		protected void releaseImpl() {
			synchronized (loadingSounds) {
				loadingSounds.remove(Integer.valueOf(soundId));
			}
			if (destroyed || notSupport() || soundId == 0) {
				return;
			}
			stopImpl();
			pool.unload(soundId);
		}

		@Override
		public void release() {
			boolean pending = impl == null && soundId != 0;
			super.release();
			if (pending && !destroyed && !notSupport()) {
				synchronized (loadingSounds) {
					loadingSounds.remove(Integer.valueOf(soundId));
				}
				pool.unload(soundId);
			}
		}
	};

	public Audio() {
		this.pool = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
		this.pool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				loading(sampleId, status);
			}
		});
	}

	private void loading(int soundId, int status) {
		PooledSound sound;
		synchronized (loadingSounds) {
			sound = loadingSounds.remove(Integer.valueOf(soundId));
		}
		if (sound != null) {
			if (status == 0) {
				dispatchLoaded(sound, Integer.valueOf(soundId));
			} else {
				dispatchLoadError(sound, new Exception("Sound load failed [id="
						+ soundId + ", status=" + status + "]"));
			}
		} else {
			// The sound may have been released while SoundPool was still loading it.
			if (!destroyed && soundId != 0) {
				pool.unload(soundId);
			}
		}
	}

	private boolean addLoadingMusic(AndroidSound<MediaPlayer> sound) {
		synchronized (loadingMusic) {
			if (destroyed || sound.released) {
				return false;
			}
			loadingMusic.add(sound);
			return true;
		}
	}

	private boolean removeLoadingMusic(AndroidSound<MediaPlayer> sound) {
		synchronized (loadingMusic) {
			loadingMusic.remove(sound);
			return !destroyed && !sound.released;
		}
	}

	void onReleased(AndroidSound<MediaPlayer> sound) {
		synchronized (loadingMusic) {
			loadingMusic.remove(sound);
		}
		onStopped(sound);
	}

	public synchronized SoundImpl<?> createSound(AssetFileDescriptor fd) {
		if (destroyed) {
			return createSoundError(new IllegalStateException(
					"Audio has been destroyed"));
		}
		synchronized (loadingSounds) {
			try {
				PooledSound sound = new PooledSound(pool.load(fd, 1));
				if (sound.soundId == 0) {
					sound.onLoadError(new Exception("Sound load failed [id=0]"));
					return sound;
				}
				loadingSounds.put(Integer.valueOf(sound.soundId), sound);
				return sound;
			} catch (Exception t) {
				return createSoundError(t);
			}
		}
	}

	public synchronized SoundImpl<?> createSound(FileDescriptor fd, long offset,
			long length) {
		if (destroyed) {
			return createSoundError(new IllegalStateException(
					"Audio has been destroyed"));
		}
		synchronized (loadingSounds) {
			try {
				PooledSound sound = new PooledSound(pool.load(fd, offset, length,
						1));
				if (sound.soundId == 0) {
					sound.onLoadError(new Exception("Sound load failed [id=0]"));
					return sound;
				}
				loadingSounds.put(Integer.valueOf(sound.soundId), sound);
				return sound;
			} catch (Exception t) {
				return createSoundError(t);
			}
		}
	}

	private static AssetFileDescriptor openFd(String fileName)
			throws IOException {
		return LSystem.getActivity().getAssets().openFd(fileName);
	}

	public synchronized SoundImpl<?> createSound(final String path) {
		AssetFileDescriptor fd = null;
		try {
			fd = openFd(path);
			return createSound(fd);
		} catch (IOException ioe) {
			return createSoundError(ioe);
		} finally {
			if (fd != null) {
				try {
					fd.close();
				} catch (IOException ignored) {
				}
			}
		}
	}

	public synchronized SoundImpl<?> createMusic(final String path) {
		if (destroyed) {
			return createSoundError(new IllegalStateException(
					"Audio has been destroyed"));
		}
		return new BigClip(this, new Resolver<MediaPlayer>() {
			@Override
			public void resolve(final AndroidSound<MediaPlayer> sound) {
				final MediaPlayer mp = new MediaPlayer();
				if (!addLoadingMusic(sound)) {
					mp.release();
					return;
				}
				LSystem.callScreenRunnable(new Runnable() {
					@Override
					public void run() {
						if (destroyed || sound.released) {
							removeLoadingMusic(sound);
							mp.release();
							return;
						}
						AssetFileDescriptor fd = null;
						try {
							fd = openFd(path);
							mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
							mp.setDataSource(fd.getFileDescriptor(),
									fd.getStartOffset(), fd.getLength());
							mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
								@Override
								public void onPrepared(final MediaPlayer mp) {
									if (removeLoadingMusic(sound)) {
										dispatchLoaded(sound, mp);
									} else {
										try {
											mp.release();
										} catch (Throwable ignored) {
										}
									}
								}
							});
							mp.setOnErrorListener(new OnErrorListener() {

								@Override
								public boolean onError(MediaPlayer mp,
										int what, int extra) {
									removeLoadingMusic(sound);
									String errmsg = "MediaPlayer prepare failure [what="
											+ what + ", x=" + extra + "]";
									if (!destroyed && !sound.released) {
										dispatchLoadError(sound, new Exception(
												errmsg));
									}
									try {
										mp.release();
									} catch (Throwable ignored) {
									}
									return true;
								}
							});
							mp.prepareAsync();
						} catch (Exception e) {
							removeLoadingMusic(sound);
							try {
								mp.release();
							} catch (Throwable ignored) {
							}
							if (!destroyed && !sound.released) {
								dispatchLoadError(sound, e);
							}
						} finally {
							if (fd != null) {
								try {
									fd.close();
								} catch (IOException ignored) {
								}
							}
						}
					}
				});
			}
		});
	}

	public synchronized void onPause() {
		if (destroyed) {
			return;
		}
		pool.autoPause();
		HashSet<AndroidSound<?>> wasPlaying;
		synchronized (playing) {
			wasPlaying = new HashSet<AndroidSound<?>>(playing);
		}
		for (AndroidSound<?> sound : wasPlaying) {
			sound.onPause();
		}
	}

	public synchronized void onResume() {
		if (destroyed) {
			return;
		}
		pool.autoResume();
		HashSet<AndroidSound<?>> wasPlaying;
		synchronized (playing) {
			wasPlaying = new HashSet<AndroidSound<?>>(playing);
			playing.clear();
		}
		for (AndroidSound<?> sound : wasPlaying) {
			sound.onResume();
		}
	}

	public synchronized void onDestroy() {
		if (destroyed) {
			return;
		}
		destroyed = true;
		HashSet<AndroidSound<?>> wasPlaying;
		synchronized (playing) {
			wasPlaying = new HashSet<AndroidSound<?>>(playing);
			playing.clear();
		}
		for (AndroidSound<?> sound : wasPlaying) {
			sound.release();
		}
		HashSet<AndroidSound<MediaPlayer>> wasLoadingMusic;
		synchronized (loadingMusic) {
			wasLoadingMusic = new HashSet<AndroidSound<MediaPlayer>>(
					loadingMusic);
			loadingMusic.clear();
		}
		for (AndroidSound<MediaPlayer> sound : wasLoadingMusic) {
			sound.release();
		}
		synchronized (loadingSounds) {
			loadingSounds.clear();
		}
		pool.release();
	}

	void onPlaying(AndroidSound<?> sound) {
		synchronized (playing) {
			playing.add(sound);
		}
	}

	void onStopped(AndroidSound<?> sound) {
		synchronized (playing) {
			playing.remove(sound);
		}
	}

	private SoundImpl<?> createSoundError(Throwable error) {
		PooledSound sound = new PooledSound(0);
		sound.onLoadError(error);
		return sound;
	}

}
