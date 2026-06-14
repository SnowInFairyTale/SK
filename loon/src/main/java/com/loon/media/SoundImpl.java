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
 * @email javachenpeng@yahoo.com
 * @version 0.3.3
 */
package com.loon.media;

import java.util.List;

import com.loon.core.Callback;
import com.loon.core.CallbackList;
import com.loon.utils.MathUtils;

public abstract class SoundImpl<I> implements Sound {
	
	
	protected List<Callback<Sound>> callbacks;
	protected Throwable error;
	protected boolean playing, looping, released;
	protected float volume = 1;
	protected float playVolume = 1;
	protected float playRate = 1;
	protected I impl;

	public synchronized void onLoaded(I impl) {
		if (released) {
			this.impl = impl;
			releaseImpl();
			this.impl = null;
			return;
		}
		this.impl = impl;
		callbacks = CallbackList.dispatchSuccessClear(callbacks, this);
		setVolumeImpl(volume);
		setLoopingImpl(looping);
		if (playing) {
			playImpl();
		}
	}

	public synchronized void onLoadError(Throwable error) {
		if (released) {
			return;
		}
		this.error = error;
		callbacks = CallbackList.dispatchFailureClear(callbacks, error);
	}

	@Override
	public synchronized boolean prepare() {
		if (released) {
			return false;
		}
		return (impl != null) ? prepareImpl() : false;
	}

	@Override
	public synchronized boolean isPlaying() {
		if (released) {
			return false;
		}
		return (impl != null) ? playingImpl() : playing;
	}

	@Override
	public synchronized boolean play() {
		return play(this.volume, 1f);
	}

	@Override
	public synchronized boolean play(float volume, float rate) {
		if (released) {
			return false;
		}
		this.playing = true;
		this.playVolume = MathUtils.clamp(volume, 0, 1);
		this.playRate = MathUtils.clamp(rate, 0.5f, 2f);
		if (impl != null) {
			return playImpl();
		} else {
			return false;
		}
	}

	@Override
	public synchronized void stop() {
		this.playing = false;
		if (released) {
			return;
		}
		if (impl != null) {
			stopImpl();
		}
	}

	@Override
	public synchronized void setLooping(boolean looping) {
		this.looping = looping;
		if (released) {
			return;
		}
		if (impl != null) {
			setLoopingImpl(looping);
		}
	}

	@Override
	public synchronized float volume() {
		return volume;
	}

	@Override
	public synchronized void setVolume(float volume) {
		this.volume = MathUtils.clamp(volume, 0, 1);
		if (released) {
			return;
		}
		if (impl != null) {
			setVolumeImpl(this.volume);
		}
	}

	@Override
	public synchronized void release() {
		if (released) {
			return;
		}
		released = true;
		playing = false;
		callbacks = null;
		if (impl != null) {
			releaseImpl();
			impl = null;
		}
	}

	@Override
	public final synchronized void addCallback(Callback<Sound> callback) {
		if (impl != null) {
			callback.onSuccess(this);
		} else if (error != null) {
			callback.onFailure(error);
		} else if (released) {
			callback.onFailure(new IllegalStateException(
					"Sound has been released"));
		} else {
			callbacks = CallbackList.createAdd(callbacks, callback);
		}
	}

	@Override
	protected void finalize() {
		release();
	}

	protected boolean prepareImpl() {
		return false;
	}

	protected boolean playingImpl() {
		return playing;
	}

	protected abstract boolean playImpl();

	protected abstract void stopImpl();

	protected abstract void setLoopingImpl(boolean looping);

	protected abstract void setVolumeImpl(float volume);

	protected abstract void releaseImpl();
}
