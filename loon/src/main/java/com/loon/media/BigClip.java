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

import android.media.MediaPlayer;

public class BigClip extends AndroidSound<MediaPlayer> {

	private final Audio audio;
	private final Audio.Resolver<MediaPlayer> resolver;
	private int position;

	public BigClip(Audio audio,
			Audio.Resolver<MediaPlayer> resolver) {
		this.audio = audio;
		this.resolver = resolver;
		resolve();
	}

	@Override
	public void onLoaded(MediaPlayer impl) {
		super.onLoaded(impl);
		if (this.impl != impl) {
			return;
		}
		impl.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				audio.onStopped(BigClip.this);
			}
		});
	}

	@Override
	protected boolean playingImpl() {
		return impl != null && impl.isPlaying();
	}

	@Override
	protected boolean playImpl() {
		if (impl == null) {
			return false;
		}
		try {
			audio.onPlaying(this);
			impl.seekTo(position);
			impl.start();
			position = 0;
			return true;
		} catch (IllegalStateException e) {
			audio.onStopped(this);
			return false;
		}
	}

	@Override
	protected void stopImpl() {
		audio.onStopped(this);
		if (impl != null) {
			try {
				if (impl.isPlaying()) {
					impl.pause();
				}
			} catch (IllegalStateException ignored) {
			}
		}
		position = 0;
	}

	@Override
	protected void setLoopingImpl(boolean looping) {
		if (impl != null) {
			impl.setLooping(looping);
		}
	}

	@Override
	protected void setVolumeImpl(float volume) {
		if (impl != null) {
			impl.setVolume(volume, volume);
		}
	}

	@Override
	protected void releaseImpl() {
		audio.onReleased(this);
		try {
			if (impl != null && impl.isPlaying()) {
				impl.stop();
			}
		} catch (IllegalStateException ignored) {
		}
		if (impl != null) {
			impl.release();
		}
	}

	@Override
	public void release() {
		audio.onReleased(this);
		super.release();
	}

	private void resolve() {
		resolver.resolve(BigClip.this);
	}

	@Override
	void onPause() {
		if (impl != null) {
			try {
				if (impl.isPlaying()) {
					position = impl.getCurrentPosition();
				}
			} catch (IllegalStateException ignored) {
			}
			impl.release();
			impl = null;
		}
	}

	@Override
	void onResume() {
		if (!released) {
			resolve();
		}
	}
}
